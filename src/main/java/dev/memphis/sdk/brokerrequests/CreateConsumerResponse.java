package dev.memphis.sdk.brokerrequests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CreateConsumerResponse {
    @JsonProperty("error")
    public String error;

    @JsonProperty("partitions_update")
    public PartitionsUpdate partitionsUpdate;
}
