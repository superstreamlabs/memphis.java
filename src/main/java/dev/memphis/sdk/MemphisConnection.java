package dev.memphis.sdk;

import dev.memphis.sdk.consumer.Consumer;
import dev.memphis.sdk.producer.Producer;
import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.Nats;
import io.nats.client.Options;
import nl.altindag.ssl.SSLFactory;
import nl.altindag.ssl.util.PemUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Future;

public class MemphisConnection {

    MemphisConnection(ClientOptions opts) throws MemphisConnectException {
        UUID uuid = UUID.randomUUID();

        Options.Builder natsConnOptsBuilder = new Options.Builder()
                .server(opts.host + ":" + opts.port)
                .connectionName(uuid + "::" + opts.username)
                .token(opts.broker_token.toCharArray())
                .maxReconnects(opts.reconnect ? opts.max_reconnect : 0)
                .reconnectWait(Duration.ofDays(opts.reconnect_interval_ms))
                .connectionTimeout(Duration.ofDays(opts.timeout_ms));

        //Third party library approach, allows using pem files
        X509ExtendedKeyManager keyManager = PemUtils.loadIdentityMaterial(opts.cert_file, opts.key_file);
        X509ExtendedTrustManager trustManager = PemUtils.loadTrustMaterial(opts.ca_file);

        SSLFactory sslFactory = SSLFactory.builder()
                .withIdentityMaterial(keyManager)
                .withTrustMaterial(trustManager)
                .build();
        SSLContext ssl_ctx = sslFactory.getSslContext();
        natsConnOptsBuilder = natsConnOptsBuilder.sslContext(ssl_ctx);

        Options natsConnOptions = natsConnOptsBuilder.build();

        try {
            Connection brokerConnection = Nats.connect(natsConnOptions);
            JetStream jetStreamContext = brokerConnection.jetStream();
        } catch (Exception e) {
            throw new MemphisConnectException("Error occurred while connecting to Memphis");
        }

        //Todo: return Memphis
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
