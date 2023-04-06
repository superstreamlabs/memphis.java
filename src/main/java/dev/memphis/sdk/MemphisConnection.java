package dev.memphis.sdk;

import dev.memphis.sdk.consumer.Consumer;
import dev.memphis.sdk.producer.Producer;
import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.Options;

import java.util.concurrent.Future;

public class MemphisConnection {

    public MemphisConnection(Options natsConnOptions, Connection brokerConnection, JetStream jetStreamContext, String uuid) {

    }

    public boolean isConnected() {
        return false;
    }

    public Future<Void> produce() {
        return null;
    }

    public Future<Producer> createProducer() {
        return null;
    }

    public Future<Consumer> createConsumer() {
        return null;
    }

    public String createUniqueProducerSuffix() {
        return null;
    }

    public String createUniqueConsumerSuffix() {
        return null;
    }

    public Future<Station> createStation() {
        return null;
    }

    public Future<Void> attachSchema() {
        return null;
    }

    public Future<Void> detachStation() {
        return null;
    }
}
