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
}
