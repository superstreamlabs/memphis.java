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
 * A consumer that fetches batches of messages synchronously.
 */
public class MemphisBatchConsumer {
    private static final String STATION_SUFFIX = ".final";
    private final List<Integer> partitions;
    private final Map<Integer, JetStreamSubscription> subscriptions = new HashMap<>();
    private final Map<Integer, Thread> keepAliveThreads = new HashMap<>();
    private final Map<Integer, ConsumerKeepAlive> keepAlives = new HashMap<>();

    private final String consumerGroup;
    private final Duration maxWaitTime;
    private final int batchSize;

    public MemphisBatchConsumer(Connection brokerConnection, ClientOptions clientOptions, ConsumerOptions consumerOptions, List<Integer> partitions) throws MemphisException {
        this.partitions = partitions;
        this.consumerGroup = consumerOptions.consumersGroup;
        this.maxWaitTime = clientOptions.maxWaitTime;
        this.batchSize = clientOptions.batchSize;

        PullSubscribeOptions pullOptions = PullSubscribeOptions.builder()
                .durable(this.consumerGroup)
                .build();

        try {
            var context = brokerConnection.jetStream();
            for(Integer partition : partitions) {
                String internalName = Utils.getInternalName(consumerOptions.stationName);
                String completeStationName = internalName + "$" + partition;
                JetStreamSubscription sub = context.subscribe(completeStationName + STATION_SUFFIX, pullOptions);
                subscriptions.put(partition, sub);

                ConsumerKeepAlive keepAlive = new ConsumerKeepAlive(brokerConnection.jetStreamManagement(), completeStationName, consumerOptions.consumersGroup, Duration.ofSeconds(10));
                Thread keepAliveThread = new Thread(keepAlive);
                keepAliveThread.start();
                keepAliveThreads.put(partition, keepAliveThread);
                keepAlives.put(partition, keepAlive);
            }
        } catch (IOException | JetStreamApiException e) {
            throw new MemphisException(e.getMessage());
        }
    }

    /**
     * Fetches a batch of messages synchronously.
     * @return a list of MemphisMessage objects
     * @throws MemphisException
     */
    public List<MemphisMessage> fetch() throws MemphisException {
        List<MemphisMessage> memphisMessages = new ArrayList<>();
        try {
            for (Integer partNumber : partitions) {
                JetStreamSubscription sub = subscriptions.get(partNumber);
                for (Message msg : sub.fetch(batchSize, maxWaitTime)) {
                    memphisMessages.add(new MemphisMessage(msg, consumerGroup));
                }
            }
        } catch(IllegalStateException e) {
            throw new MemphisException(e.getMessage());
        }

        return memphisMessages;
    }

    /**
     * Destroy the consumer object.
     */
    public void destroy() {
        for(Integer partNumber : partitions) {
            subscriptions.get(partNumber).unsubscribe();
            keepAlives.get(partNumber).cancel();
        }

        try {
            for(Integer partNumber : partitions) {
                keepAliveThreads.get(partNumber).join();
            }
        } catch(InterruptedException e) {

        }
    }
}
