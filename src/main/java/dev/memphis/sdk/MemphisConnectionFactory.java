package dev.memphis.sdk;

import dev.memphis.sdk.connection.MemphisConnection;
import dev.memphis.sdk.consumer.Consumer;
import dev.memphis.sdk.producer.Producer;

public class MemphisConnectionFactory implements MemphisConnection {

    @Override
    public void connect() {
        // TODO: Not yet implemented
    }

    @Override
    public void disconnect() {
        // TODO: Not yet implemented
    }

    @Override
    public Producer createProducer() {
        // TODO: Not yet implemented
        return null;
    }

    @Override
    public Consumer createConsumer() {
        // TODO: Not yet implemented
        return null;
    }

    @Override
    public String createUniqueProducerSuffix() {
        // TODO: Not yet implemented
        return null;
    }

    @Override
    public String createUniqueConsumerSuffix() {
        // TODO: Not yet implemented
        return null;
    }
}
