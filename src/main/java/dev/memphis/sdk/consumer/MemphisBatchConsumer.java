package dev.memphis.sdk.consumer;

import dev.memphis.sdk.ClientOptions;
import dev.memphis.sdk.MemphisException;
import dev.memphis.sdk.MemphisMessage;
import io.nats.client.*;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MemphisBatchConsumer {
    private static final String STATION_SUFFIX = ".final";

    private final String stationName;
    private final String consumerGroup;
    private final JetStream jetStreamContext;
    private final Duration maxWaitTime;
    private final int batchSize;

    public MemphisBatchConsumer(JetStream jetStreamContext, String stationName, String consumerGroup, ClientOptions opts) {
        this.stationName = stationName;
        this.consumerGroup = consumerGroup;
        this.jetStreamContext = jetStreamContext;
        this.maxWaitTime = opts.maxWaitTime;
        this.batchSize = opts.batchSize;
    }

    public List<MemphisMessage> fetch() throws MemphisException {
        PullSubscribeOptions pullOptions = PullSubscribeOptions.builder()
                .durable(this.consumerGroup)
                .build();
        JetStreamSubscription sub;
        try {
            sub = jetStreamContext.subscribe(stationName + STATION_SUFFIX, pullOptions);
        } catch (IOException | JetStreamApiException e) {
            throw new MemphisException(e.getMessage());
        }

        List<MemphisMessage> memphisMessages = new ArrayList<>();
        for(Message msg : sub.fetch(batchSize, maxWaitTime)) {
            memphisMessages.add(new MemphisMessage(msg, consumerGroup));
        }

        return memphisMessages;
    }
}
