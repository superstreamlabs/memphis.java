package dev.memphis.sdk;

import dev.memphis.sdk.consumer.MemphisConsumerCallback;
import dev.memphis.sdk.consumer.MemphisCallbackConsumer;
import dev.memphis.sdk.consumer.MemphisBatchConsumer;
import dev.memphis.sdk.producer.MemphisProducer;
import io.nats.client.Connection;
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
    private final ClientOptions opts;
    private final String connectionId;

    public MemphisConnection(ClientOptions opts) throws MemphisConnectException {
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
        } catch (Exception e) {
            throw new MemphisConnectException("Error occurred while connecting to Memphis: " + e.getMessage());
        }
    }

    /**
     * Closes the connection if connected
     * @throws InterruptedException
     */
    public void close() throws InterruptedException {
        if(isConnected()) {
            brokerConnection.close();
        }
    }

    /**
     * Checks the connection status.
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return brokerConnection.getStatus() == Connection.Status.CONNECTED;
    }

    public MemphisProducer createProducer(String stationName, String producerName) throws MemphisConnectException {
        return new MemphisProducer(brokerConnection, stationName, producerName, connectionId);
    }

    /**
     * Creates a consumer that consumes messages over this connection.
     * The consumer takes a callback function used to process the messages.
     * The consumer implements the Runnable interface so that it can be
     * executed in a separate thread, if desired.
     * @param stationName name of the Memphis station
     * @param consumerGroup name of the consumer group to assign consumer to
     * @param callbackFunction callback function that is called on each batch of messages
     * @return an instance of MemphisCallbackConsumer
     */
    public MemphisCallbackConsumer createCallbackConsumer(String stationName, String consumerGroup, MemphisConsumerCallback callbackFunction) throws MemphisException {
        return new MemphisCallbackConsumer(brokerConnection, stationName, consumerGroup, callbackFunction, opts);
    }

    /**
     * Creates a consumer that consumes messages over this connection.
     * The consumer returns a list of messages when fetch() is called.
     * The consumer implements the Runnable interface so that it can be
     * executed in a separate thread, if desired.
     * @param stationName name of the Memphis station
     * @param consumerGroup name of the consumer group to assign consumer to
     * @return an instance of MemphisSynchronousConsumer
     */
    public MemphisBatchConsumer createBatchConsumer(String stationName, String consumerGroup) throws MemphisException {
        return new MemphisBatchConsumer(brokerConnection, stationName, consumerGroup, opts);
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
