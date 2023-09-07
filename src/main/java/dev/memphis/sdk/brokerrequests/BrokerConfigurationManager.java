package dev.memphis.sdk.brokerrequests;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.memphis.sdk.MemphisException;
import dev.memphis.sdk.consumer.ConsumerOptions;
import io.nats.client.Connection;
import io.nats.client.Message;

import java.io.IOException;
import java.time.Duration;

/**
 * Manages requests to the broker that update configurations.  Examples include
 * creating and destroying consumers, producers, stations, and schemas.
 */
public class BrokerConfigurationManager {
    private static final String CREATE_CONSUMER_CHANNEL = "$memphis_consumer_creations";
    private static final Duration RESPONSE_WAIT_TIME = Duration.ofSeconds(5);

    private final Connection brokerConnection;
    private final String connectionId;
    private final String user;

    public BrokerConfigurationManager(Connection brokerConnection, String connectionId, String user) {
        this.brokerConnection = brokerConnection;
        this.connectionId = connectionId;
        this.user = user;
    }

    /**
     * Registers a new consumer with the broker.
     *
     * @param options Options for creating the consumer with
     */
    public PartitionsUpdate registerNewConsumer(ConsumerOptions options) throws MemphisException {
        CreateConsumerRequest request = new CreateConsumerRequest();
        request.consumerName = options.consumerName;
        request.consumersGroup = options.consumersGroup;
        request.stationName = options.stationName;
        request.maxAckTimeMs = options.maxAckTimeMs;
        request.maxMsgDeliveries = options.maxMsgDeliveries;
        request.startConsumeFromSequence = options.startConsumeFromSequence;
        request.lastMessages = options.lastMessages;
        request.connectionId = connectionId;
        request.username = user;

        ObjectMapper mapper = new ObjectMapper();

        try {
            byte[] serializedRequest = mapper.writeValueAsBytes(request);

            Message responseMsg = brokerConnection.request(CREATE_CONSUMER_CHANNEL,
                    serializedRequest, RESPONSE_WAIT_TIME);

            byte[] serializedResponse = responseMsg.getData();

            // response can be either string or JSON object.
            CreateConsumerResponse response = mapper.readValue(serializedResponse, CreateConsumerResponse.class);

            if(response.error != null) {
                throw new MemphisException("Error creating consumer: " + response.error);
            }

            return response.partitionsUpdate;

        } catch (InterruptedException | IOException e) {
            throw new MemphisException("Error creating consumer: " + e.getMessage());
        }
    }
}
