package dev.memphis.sdk.consumer;

import dev.memphis.sdk.ClientOptions;
import dev.memphis.sdk.MemphisConnectException;
import dev.memphis.sdk.MemphisException;
import dev.memphis.sdk.MemphisMessage;
import io.nats.client.*;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * A consumer that fetches batches of messages synchronously.
 */
public class MemphisBatchConsumer {
    private static final String STATION_SUFFIX = ".final";

    private final String consumerGroup;
    private final Duration maxWaitTime;
    private final int batchSize;
    private final JetStreamSubscription sub;
    private final Connection connection;

    public MemphisBatchConsumer(Connection brokerConnection, String stationName, String consumerGroup, ClientOptions opts) throws MemphisException {
        this.connection = brokerConnection;
        this.consumerGroup = consumerGroup.toLowerCase();
        this.maxWaitTime = opts.maxWaitTime;
        this.batchSize = opts.batchSize;

        PullSubscribeOptions pullOptions = PullSubscribeOptions.builder()
                .durable(this.consumerGroup)
                .build();
        try {
            var context = connection.jetStream();
            sub = context.subscribe(stationName + STATION_SUFFIX, pullOptions);
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
        for(Message msg : sub.fetch(batchSize, maxWaitTime)) {
            memphisMessages.add(new MemphisMessage(msg, consumerGroup));
        }

        return memphisMessages;
    }

    /**
     * Destroy the consumer object.
     */
    public void destroy() {
        sub.unsubscribe();
    }
}
