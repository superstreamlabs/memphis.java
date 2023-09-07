package dev.memphis.sdk.brokerrequests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data model for a request made to the $memphis_producer_creations
 * channel to create a new producer.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CreateProducerRequest {
    @JsonProperty("name")
    public String producerName;

    @JsonProperty("station_name")
    public String stationName;

    @JsonProperty("connection_id")
    public String connectionId;

    @JsonProperty("producer_type")
    public String consumerType = "application";

    @JsonProperty("req_version")
    public Integer requestVersion = 3;

    @JsonProperty("username")
    public String username;
}
