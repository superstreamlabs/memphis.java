package dev.memphis.sdk;

import dev.memphis.sdk.consumer.MemphisCallbackFunction;
import dev.memphis.sdk.consumer.MemphisConsumer;
import dev.memphis.sdk.producer.MemphisProducer;
import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.Nats;
import io.nats.client.Options;
import nl.altindag.ssl.SSLFactory;
import nl.altindag.ssl.util.PemUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.util.UUID;
import java.util.concurrent.Future;

public class MemphisConnection {

    private final Connection brokerConnection;
    private final JetStream jetStreamContext;
    private final ClientOptions opts;
    private final String connectionId;

    MemphisConnection(ClientOptions opts) throws MemphisConnectException {
        this.opts = opts;

        UUID uuid = UUID.randomUUID();
        connectionId = uuid + "::" + opts.username;

        Options.Builder natsConnOptsBuilder = new Options.Builder()
                .server(opts.host + ":" + opts.port)
                .connectionName(connectionId)
                .maxReconnects(opts.reconnect ? opts.maxReconnects : 0)
                .reconnectWait(opts.reconnectInterval)
                .connectionTimeout(opts.timeout);

        if(opts.authenticationMethod instanceof ClientOptions.Password) {
            natsConnOptsBuilder = natsConnOptsBuilder.userInfo(opts.username, opts.authenticationMethod.getString());
        } else if(opts.authenticationMethod instanceof ClientOptions.ConnectionToken) {
            natsConnOptsBuilder = natsConnOptsBuilder.token(opts.authenticationMethod.getString());
        }

        //Third party library approach, allows using pem files
        if(opts.sslConfiguration != null) {
            X509ExtendedKeyManager keyManager = PemUtils.loadIdentityMaterial(opts.sslConfiguration.getCertFile(),
                    opts.sslConfiguration.getKeyFile());
            X509ExtendedTrustManager trustManager = PemUtils.loadTrustMaterial(opts.sslConfiguration.getCaFile());

            SSLFactory sslFactory = SSLFactory.builder()
                    .withIdentityMaterial(keyManager)
                    .withTrustMaterial(trustManager)
                    .build();
            SSLContext ssl_ctx = sslFactory.getSslContext();
            natsConnOptsBuilder = natsConnOptsBuilder.sslContext(ssl_ctx);
        }

        Options natsConnOptions = natsConnOptsBuilder.build();

        try {
            this.brokerConnection = Nats.connect(natsConnOptions);
            this.jetStreamContext = this.brokerConnection.jetStream();
        } catch (Exception e) {
            throw new MemphisConnectException("Error occurred while connecting to Memphis: " + e.getMessage());
        }
    }

    public void close() throws InterruptedException {
        this.brokerConnection.close();
    }

    public boolean isConnected() {
        return false;
    }

    public Future<Void> produce() {
        return null;
    }

    public MemphisProducer createProducer(String stationName, String producerName) {
        return new MemphisProducer(jetStreamContext, stationName, producerName, connectionId);
    }

    /**
     * Creates a consumer that consumes messages over this connection.
     * The consumer implements the Runnable interface so that it can be
     * executed in a separate thread, if desired.
     * @param stationName name of the Memphis station
     * @param consumerGroup name of the consumer group
     * @param callbackFunction callback function that is called on each batch of messages
     * @return an instance of MemphisConsumer
     */
    public MemphisConsumer createConsumer(String stationName, String consumerGroup, MemphisCallbackFunction callbackFunction) {
        return new MemphisConsumer(jetStreamContext, stationName, consumerGroup, callbackFunction, opts);
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
