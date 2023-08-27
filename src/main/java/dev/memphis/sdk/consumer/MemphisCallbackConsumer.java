package dev.memphis.sdk.consumer;

import dev.memphis.sdk.ClientOptions;
import dev.memphis.sdk.MemphisException;
import dev.memphis.sdk.MemphisMessage;
import dev.memphis.sdk.Utils;
import io.nats.client.*;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This consumer will call the provided function when messages
 * are received.  This class implements the Runnable interface
 * so that it can be run in a separate thread if desired.
 */
public class MemphisCallbackConsumer {
    private static final String STATION_SUFFIX = ".final";

    private final Map<Integer, Thread> subscriptionThreads = new HashMap<>();
    private final Map<Integer, ConsumerRunnable> subscriptions = new HashMap<>();
    private final Map<Integer, Thread> keepAliveThreads = new HashMap<>();
    private final Map<Integer, ConsumerKeepAlive> keepAlives = new HashMap<>();
    private final List<Integer> partitions;

    private class ConsumerRunnable implements Runnable {
        private final JetStreamSubscription sub;
        private volatile boolean canceled = false;
        private final Duration maxWaitTime;
        private final int batchSize;
        private final Duration pullInterval;
        private final String consumerGroup;
        private final MemphisConsumerCallback callback;

        public ConsumerRunnable(JetStreamSubscription sub, ClientOptions clientOptions, ConsumerOptions consumerOptions, MemphisConsumerCallback callbackFunction) {
            this.sub = sub;
            this.maxWaitTime = clientOptions.maxWaitTime;
            this.batchSize = clientOptions.batchSize;
            this.pullInterval = clientOptions.pullInterval;
            this.consumerGroup = consumerOptions.consumersGroup;
            this.callback = callbackFunction;
        }

        /**
         * Run the callback processing loop.
         */
        public void run() {
            while(!canceled) {
                List<MemphisMessage> memphisMessages = new ArrayList<>();
                for(Message msg : sub.fetch(batchSize, maxWaitTime)) {
                    memphisMessages.add(new MemphisMessage(msg, consumerGroup));
                }
                callback.accept(memphisMessages);
                try {
                    Thread.sleep(pullInterval.toMillis());
                } catch(InterruptedException e) {
                    break;
                }
            }
        }

        /**
         * Call the consumer processing.
         */
        public void cancel() {
            this.canceled = true;
        }

        public void unsubscribe() {
            this.sub.unsubscribe();
        }
    }

    public MemphisCallbackConsumer(Connection brokerConnection, ClientOptions clientOptions, ConsumerOptions consumerOptions, List<Integer> partitions, MemphisConsumerCallback callbackFunction) throws MemphisException {
        this.partitions = partitions;

        PullSubscribeOptions pullOptions = PullSubscribeOptions.builder()
                .durable(consumerOptions.consumersGroup)
                .build();

        try {
            var context = brokerConnection.jetStream();

            for(Integer partName : partitions) {
                String internalName = Utils.getInternalName(consumerOptions.stationName);
                String completeStationName = internalName + "$" + partName;
                JetStreamSubscription sub = context.subscribe(completeStationName + STATION_SUFFIX, pullOptions);
                ConsumerRunnable runnable = new ConsumerRunnable(sub, clientOptions, consumerOptions, callbackFunction);
                Thread consumerThread = new Thread(runnable);
                consumerThread.start();
                subscriptionThreads.put(partName, consumerThread);
                subscriptions.put(partName, runnable);

                ConsumerKeepAlive keepAlive = new ConsumerKeepAlive(brokerConnection.jetStreamManagement(), completeStationName, consumerOptions.consumersGroup, Duration.ofSeconds(10));
                Thread keepAliveThread = new Thread(keepAlive);
                keepAliveThread.start();
                keepAliveThreads.put(partName, keepAliveThread);
                keepAlives.put(partName, keepAlive);
            }
        } catch (IOException | JetStreamApiException e) {
            throw new MemphisException(e.getMessage());
        }
    }



    /**
     * Disconnect the consumer and release resources.
     */
    public void destroy() {
        for(Integer partName : partitions) {
            subscriptions.get(partName).cancel();
            keepAlives.get(partName).cancel();
        }

        try {
            for(Integer partName : partitions) {
                subscriptions.get(partName).unsubscribe();
                subscriptionThreads.get(partName).join();
                keepAliveThreads.get(partName).join();
            }
        } catch(InterruptedException e) {

        }
    }
}
