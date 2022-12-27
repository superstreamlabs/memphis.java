package dev.memphis.sdk.connection;

import dev.memphis.sdk.consumer.Consumer;
import dev.memphis.sdk.producer.Producer;

public interface MemphisConnection {

    void connect();
    void disconnect();
    Producer createProducer();
    Consumer createConsumer();
    String createUniqueProducerSuffix();
    String createUniqueConsumerSuffix();
}
