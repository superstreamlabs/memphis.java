package dev.memphis.sdk;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegrationTest {

    @Test
    public void synchronousProduceThenConsume() throws MemphisException, InterruptedException {
        var stationName = "integrationtest";
        var numMessages = 5;

        var options = new ClientOptions.Builder()
                .host("localhost")
                .username("root")
                .password("memphis")
                .build();

        var connection = new MemphisConnection(options);

        var producer = connection.createProducer(stationName, "JavaProducer");
        byte[] msgText = "This is a test.".getBytes();

        for(int i = 0; i < numMessages; i++) {
            producer.produce(msgText);
        }

        var consumer = connection.createBatchConsumer(stationName, "test-group");

        var messages = consumer.fetch();

        for(var msg : messages) {
            msg.ack();
        }

        connection.close();

        assertTrue(messages.size() >= numMessages);
    }
}
