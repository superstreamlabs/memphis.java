package dev.memphis.sdk.producer;

import dev.memphis.sdk.MemphisConnectException;
import dev.memphis.sdk.MemphisException;
import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.api.PublishAck;
import io.nats.client.impl.Headers;
import io.nats.client.impl.NatsMessage;

import java.io.IOException;

/**
 * A simple synchronous producer.
 * Created by calling MemphisConnection.createProducer(stationName, producerName).
 */
public class MemphisProducer {
    private static final String STATION_SUFFIX = ".final";

    private final JetStream jetStreamContext;
    private final String stationName;
    private final String connectionId;
    private final String producerName;

    public MemphisProducer(Connection connection, String stationName, String producerName, String connectionId) throws MemphisConnectException {
        try {
            this.jetStreamContext = connection.jetStream();
        } catch(IOException e) {
            throw new MemphisConnectException(e.getMessage());
        }
        this.stationName = stationName;
        this.connectionId = connectionId;
        this.producerName = producerName;
    }

    /**
     * Send message to the station synchronously.
     * @param msg A byte array constituting the body of the message.
     * @return PublishAck object providing information about success or failure
     * @throws MemphisException if a problem is encountered.
     */
    public PublishAck produce(byte[] msg) throws MemphisException {
        var headers = new Headers();
        headers.put("$memphis_connectionId", connectionId);
        headers.put("$memphis_producedBy", producerName);

        var natsMsg = NatsMessage.builder()
                .subject(stationName + STATION_SUFFIX)
                .data(msg)
                .headers(headers)
                .build();

        PublishAck ack = null;
        try {
            ack = jetStreamContext.publish(natsMsg);
        } catch(Exception e) {
            throw new MemphisException("Error occurred while connecting to Memphis: " + e.getMessage());
        }

        return ack;
    }
}
