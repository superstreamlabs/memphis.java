package dev.memphis.sdk;

import dev.memphis.sdk.brokerrequests.BrokerConfigurationManager;
import dev.memphis.sdk.consumer.ConsumerOptions;
import dev.memphis.sdk.consumer.MemphisConsumerCallback;
import dev.memphis.sdk.consumer.MemphisAsyncConsumer;
import dev.memphis.sdk.consumer.MemphisSyncConsumer;
import dev.memphis.sdk.producer.ProducerOptions;
import dev.memphis.sdk.producer.MemphisProducer;
import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import nl.altindag.ssl.SSLFactory;
import nl.altindag.ssl.util.PemUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

public class MemphisConnection {

    private final Connection brokerConnection;
    private final ClientOptions opts;
    private final String connectionId;
    private final BrokerConfigurationManager manager;

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
            // To support multi-tenancy, user ids are of the form
            // username$accountId.  This serves several purposes
            // including namespacing usernames and controlling access
            String user = opts.username + "$" + opts.accountId;
            natsConnOptsBuilder = natsConnOptsBuilder.userInfo(user,
                    opts.authenticationMethod.getString());
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

            manager = new BrokerConfigurationManager(brokerConnection, connectionId, opts.username);
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


    /**
     * Creates a producer for sending messages to a station.
     * @param producerOptions Configuration parameters for creating producer
     * @return an instance of
     * @throws MemphisConnectException
     */
    public MemphisProducer createProducer(ProducerOptions producerOptions) throws MemphisException {
        List<Integer> partitions = manager.registerNewProducer(producerOptions).partitionsList;
        return new MemphisProducer(brokerConnection, connectionId, producerOptions, partitions);
    }

    /**
     * Creates an asynchronous consumer that consumes messages over this connection.
     * The consumer takes a callback function used to process the messages.
     * The callback is executed in a background thread when new messages
     * arrive.
     * @param consumerOptions Configuration parameters for creating consumer
     * @param callbackFunction callback function that is called on each batch of messages
     * @return an instance of MemphisAsyncConsumer
     */
    public MemphisAsyncConsumer createAsyncConsumer(ConsumerOptions consumerOptions, MemphisConsumerCallback callbackFunction) throws MemphisException {
        List<Integer> partitions = manager.registerNewConsumer(consumerOptions).partitionsList;
        return new MemphisAsyncConsumer(brokerConnection, opts, consumerOptions, partitions, callbackFunction);
    }

    /**
     * Creates a synchronous consumer that consumes messages over this connection.
     * The consumer returns a list of messages when fetch() is called.
     * The consumer will either wait until it receives messages or
     * the wait time is reached.
     * @param consumerOptions Configuration parameters for creating consumer
     * @return an instance of MemphisSyncConsumer
     */
    public MemphisSyncConsumer createSyncConsumer(ConsumerOptions consumerOptions) throws MemphisException {
        List<Integer> partitions = manager.registerNewConsumer(consumerOptions).partitionsList;
        return new MemphisSyncConsumer(brokerConnection, opts, consumerOptions, partitions);
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
