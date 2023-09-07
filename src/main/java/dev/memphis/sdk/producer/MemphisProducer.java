package dev.memphis.sdk.producer;

import dev.memphis.sdk.MemphisConnectException;
import dev.memphis.sdk.MemphisException;
import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.api.PublishAck;
import io.nats.client.impl.Headers;
import io.nats.client.impl.NatsMessage;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A simple synchronous producer.
 */
public class MemphisProducer {
    private static final String STATION_SUFFIX = ".final";

    private final JetStream jetStreamContext;
    private final String stationName;
    private final String connectionId;
    private final String producerName;
    private final PartitionIterator partIter;
    private final ConcurrentLinkedQueue<NatsMessage> msgQueue;
    private final ProducerRunnable backgroundProducer;
    private final Thread backgroundProducerThread;
    private final int queueSize;

    private static class PartitionIterator implements Iterator<Integer> {
        private final List<Integer> partitions;
        private int nextIdx;

        public PartitionIterator(List<Integer> partitions) {
            this.partitions = partitions;
            nextIdx = 0;
        }


        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Integer next() {
            int nextPart = partitions.get(nextIdx);
            nextIdx = (nextIdx + 1) % partitions.size();
            return nextPart;
        }
    }

    /**
     * Runs as a background thread that sends messages in the queue.
     * Used to implement non-blocking sending.
     */
    private static class ProducerRunnable implements Runnable {
        private final JetStream jetStreamContext;
        private volatile boolean canceled = false;
        private final ConcurrentLinkedQueue<NatsMessage> msgQueue;
        private volatile boolean hasError = false;
        private String errorMsg = "";

        public ProducerRunnable(JetStream jetStreamContext, ConcurrentLinkedQueue<NatsMessage> msgQueue) {
            this.jetStreamContext = jetStreamContext;
            this.msgQueue = msgQueue;
        }

        @Override
        public void run() {
            while(!canceled) {
                if(msgQueue.isEmpty()) {
                    try{
                        this.wait(100);
                    } catch(InterruptedException e) {
                        // ignore exception
                    }
                } else {
                    NatsMessage msg = msgQueue.peek();

                    try {
                        PublishAck ack = jetStreamContext.publish(msg);
                        msgQueue.poll();
                    } catch(Exception e) {
                        errorMsg = "Error occurred while connecting to Memphis: " + e.getMessage();
                        canceled = true;
                        hasError = true;
                    }
                }
            }
        }

        public void cancel() {
            this.canceled = true;
        }

        public boolean isCanceled() {
            return canceled;
        }

        public boolean hasError() {
            return hasError;
        }

        public String getErrorMsg() {
            return errorMsg;
        }
    }

    public MemphisProducer(Connection connection, String connectionId, ProducerOptions producerOptions, List<Integer> partitions) throws MemphisConnectException {
        try {
            this.jetStreamContext = connection.jetStream();
        } catch(IOException e) {
            throw new MemphisConnectException(e.getMessage());
        }
        this.stationName = producerOptions.stationName;
        this.connectionId = connectionId;
        this.producerName = producerOptions.producerName.toLowerCase();
        partIter = new PartitionIterator(partitions);
        msgQueue = new ConcurrentLinkedQueue<>();
        this.queueSize = producerOptions.queueSize;

        // It probably isn't safe to let two threads use the same
        // JetStream object...
        backgroundProducer = new ProducerRunnable(this.jetStreamContext, msgQueue);
        backgroundProducerThread = new Thread(backgroundProducer);
    }

    /**
     * Send message to the station synchronously.
     * @param msg A byte array constituting the body of the message.
     * @return PublishAck object providing information about success or failure
     * @throws MemphisException if a problem is encountered.
     */
    public PublishAck produce(byte[] msg) throws MemphisException {
        var headers = new Headers();
        headers.put("$memphis_connectionId", connectionId);
        headers.put("$memphis_producedBy", producerName);

        int partNum = partIter.next();
        String partitionName = stationName + "$" + partNum + STATION_SUFFIX;

        var natsMsg = NatsMessage.builder()
                .subject(partitionName)
                .data(msg)
                .headers(headers)
                .build();

        PublishAck ack = null;
        try {
            ack = jetStreamContext.publish(natsMsg);
        } catch(Exception e) {
            throw new MemphisException("Error occurred while connecting to Memphis: " + e.getMessage());
        }

        return ack;
    }

    /**
     * Adds message to an internal queue to be sent by a background thread.
     * If the queue is full, this call blocks.
     * @param msg A byte array constituting the body of the message.
     * @throws MemphisException if a problem is encountered.
     */
    public void produceNonblocking(byte[] msg) throws MemphisException {
        var headers = new Headers();
        headers.put("$memphis_connectionId", connectionId);
        headers.put("$memphis_producedBy", producerName);

        int partNum = partIter.next();
        String partitionName = stationName + "$" + partNum + STATION_SUFFIX;

        var natsMsg = NatsMessage.builder()
                .subject(partitionName)
                .data(msg)
                .headers(headers)
                .build();

        // let the queue drain to half of its maximum size
        if (msgQueue.size() >= queueSize) {
            while(msgQueue.size() >= queueSize / 2) {
                try {
                    this.wait(100);
                } catch (InterruptedException e) {
                    // ignore exception
                }
            }
        }

        msgQueue.add(natsMsg);
    }

    public void stop() {
        this.backgroundProducer.cancel();
        try {
            this.backgroundProducerThread.join();
        } catch (InterruptedException e) {
            // don't care if we're interrupted
        }
    }
}
