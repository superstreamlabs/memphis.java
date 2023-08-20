package dev.memphis.sdk;


import dev.memphis.sdk.consumer.MemphisCallbackConsumer;
import io.nats.client.JetStreamApiException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class TestRunner {
    private static final Logger logger = LogManager.getLogger(TestRunner.class);

    public static void main(String[] args) throws MemphisException, InterruptedException, JetStreamApiException, IOException {
        ClientOptions.Builder builder = new ClientOptions.Builder();

        builder.host("atlas");
        builder.username("root");
        builder.password("memphis");

        ClientOptions options = builder.build();

        logger.error("Creating connection");
        MemphisConnection connection = new MemphisConnection(options);
        logger.error("connected");

        logger.error("creating consumer");
        MemphisCallbackConsumer consumer = connection.createCallbackConsumer("todo-cdc-events", "", msgs -> System.out.println(msgs.size()));

        logger.error("consuming messages");
        consumer.run();

        logger.error("closing connection");
        connection.close();
        logger.error("disconnected");
    }
}
