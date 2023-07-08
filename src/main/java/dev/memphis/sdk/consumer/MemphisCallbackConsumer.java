package dev.memphis.sdk.consumer;

import dev.memphis.sdk.ClientOptions;
import dev.memphis.sdk.MemphisException;
import dev.memphis.sdk.MemphisMessage;
import io.nats.client.*;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * This consumer will call the provided function when messages
 * are received.  This class implements the Runnable interface
 * so that it can be run in a separate thread if desired.
 */
public class MemphisCallbackConsumer implements Runnable {
    private static final String STATION_SUFFIX = ".final";

    private final String consumerGroup;
    private final MemphisConsumerCallback callbackFunction;
    private final Duration maxWaitTime;
    private final int batchSize;
    private final Duration pullInterval;
    private final JetStreamSubscription sub;
    private final Connection connection;

    public MemphisCallbackConsumer(Connection brokerConnection, String stationName, String consumerGroup, MemphisConsumerCallback callbackFunction, ClientOptions opts) throws MemphisException {
        this.connection = brokerConnection;
        this.consumerGroup = consumerGroup.toLowerCase();
        this.callbackFunction = callbackFunction;
        this.maxWaitTime = opts.maxWaitTime;
        this.batchSize = opts.batchSize;
        this.pullInterval = opts.pullInterval;

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
     * Run the callback processing loop.
     */
    public void run() {
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

    /**
     * Disconnect the consumer.
     */
    public void destroy() {
        sub.unsubscribe();
    }
}
