package dev.memphis.sdk;

import dev.memphis.sdk.consumer.MemphisCallbackConsumer;
import dev.memphis.sdk.consumer.MemphisSynchronousConsumer;
import dev.memphis.sdk.producer.MemphisProducer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        var connection = Memphis.connect(options);

        var producer = connection.createProducer(stationName, "JavaProducer");
        byte[] msg = "This is a test.".getBytes();

        for(int i = 0; i < numMessages; i++) {
            producer.produce(msg);
        }

        var consumer = connection.createSynchronousConsumer(stationName, "group");

        var messages = consumer.fetch();

        assertEquals(numMessages, messages.size());

        connection.close();
    }
}
