package dev.memphis.sdk;

import dev.memphis.sdk.consumer.ConsumerOptions;
import dev.memphis.sdk.producer.ProducerOptions;
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

        ProducerOptions pOpts = new ProducerOptions.Builder()
                .stationName(stationName)
                .producerName("JavaProducer")
                .build();

        var producer = connection.createProducer(pOpts);
        byte[] msgText = "This is a test.".getBytes();

        for(int i = 0; i < numMessages; i++) {
            producer.produce(msgText);
        }

        ConsumerOptions cOpts = new ConsumerOptions.Builder()
                .consumerName("JavaConsumer")
                .stationName(stationName)
                .build();

        var consumer = connection.createSyncConsumer(cOpts);

        var messages = consumer.fetch();

        for(var msg : messages) {
            msg.ack();
        }

        consumer.destroy();

        connection.close();

        assertTrue(messages.size() >= numMessages);
    }
}
