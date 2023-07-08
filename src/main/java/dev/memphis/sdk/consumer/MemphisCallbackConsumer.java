package dev.memphis.sdk.consumer;

import dev.memphis.sdk.ClientOptions;
import dev.memphis.sdk.MemphisMessage;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.JetStreamSubscription;
import io.nats.client.Message;
import io.nats.client.PullSubscribeOptions;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MemphisCallbackConsumer implements Runnable {
    private static final String STATION_SUFFIX = ".final";

    private final String stationName;
    private final String consumerGroup;
    private final JetStream jetStreamContext;
    private final MemphisConsumerCallback callbackFunction;
    private final Duration maxWaitTime;
    private final int batchSize;
    private final Duration pullInterval;

    public MemphisCallbackConsumer(JetStream jetStreamContext, String stationName, String consumerGroup, MemphisConsumerCallback callbackFunction, ClientOptions opts) {
        this.stationName = stationName;
        this.consumerGroup = consumerGroup;
        this.jetStreamContext = jetStreamContext;
        this.callbackFunction = callbackFunction;
        this.maxWaitTime = opts.maxWaitTime;
        this.batchSize = opts.batchSize;
        this.pullInterval = opts.pullInterval;
    }

    public void run() {
        PullSubscribeOptions pullOptions = PullSubscribeOptions.builder()
                .durable(this.consumerGroup)
                .build();
        JetStreamSubscription sub;
        try {
            sub = jetStreamContext.subscribe(stationName + STATION_SUFFIX, pullOptions);
        } catch (IOException | JetStreamApiException e) {
            throw new RuntimeException(e);
        }

        while(true) {
            List<MemphisMessage> memphisMessages = new ArrayList<>();
            for(Message msg : sub.fetch(batchSize, maxWaitTime)) {
                memphisMessages.add(new MemphisMessage(msg, consumerGroup));
            }
            callbackFunction.accept(memphisMessages);
            try {
                Thread.sleep(pullInterval.toMillis());
            } catch(InterruptedException e) {
                break;
            }
        }
    }
}
