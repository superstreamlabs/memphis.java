package dev.memphis.sdk;


import dev.memphis.sdk.consumer.ConsumerOptions;
import dev.memphis.sdk.consumer.MemphisBatchConsumer;
import io.nats.client.JetStreamApiException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class TestRunner {
    private static final Logger logger = LogManager.getLogger(TestRunner.class);

    public static void main(String[] args) throws MemphisException, InterruptedException, JetStreamApiException, IOException {
        ClientOptions.Builder builder = new ClientOptions.Builder();

        builder.host("atlas");
        builder.username("root");
        builder.password("memphis");
        builder.maxWaitTime(Duration.ofSeconds(1));

        ClientOptions options = builder.build();

        logger.error("Creating connection");
        MemphisConnection connection = new MemphisConnection(options);
        logger.error("connected");

        logger.error("creating consumer");
        ConsumerOptions opts = new ConsumerOptions
                .Builder()
                .stationName("test-station")
                .consumerName("test-consumer2")
                .build();
        MemphisBatchConsumer consumer = connection.createBatchConsumer(opts);

        logger.error("consuming messages");
        for(MemphisMessage msg : consumer.fetch()) {
            System.out.println(new String(msg.getData(), StandardCharsets.UTF_8));
            msg.ack();
        }

        logger.error("closing connection");
        connection.close();
        logger.error("disconnected");
    }
}
