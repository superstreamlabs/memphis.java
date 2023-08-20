package dev.memphis.sdk.consumer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data model for a request made to the $memphis_consumer_creations
 * channel to create a new consumer.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CreateConsumerRequest {
    @JsonProperty("consumer_name")
    public String consumerName;

    @JsonProperty("station_name")
    public String stationName;

    @JsonProperty("connection_id")
    public String connectionId;

    @JsonProperty("consumer_type")
    public String consumerType = "application";

    @JsonProperty("consumers_group")
    public String consumersGroup;

    @JsonProperty("max_ack_time_ms")
    public Integer maxAckTimeMs;

    @JsonProperty("max_msg_deliveries")
    public Integer maxMsgDeliveries;

    @JsonProperty("start_consumer_from_sequence")
    public Integer startConsumeFromSequence;

    @JsonProperty("last_messages")
    public Integer lastMessages;

    @JsonProperty("req_version")
    public Integer requestVersion;

    @JsonProperty("username")
    public String username;

    public CreateConsumerRequest() {

    }
}
