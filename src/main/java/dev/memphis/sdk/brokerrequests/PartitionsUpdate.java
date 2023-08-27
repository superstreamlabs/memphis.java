package dev.memphis.sdk.brokerrequests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PartitionsUpdate {
    @JsonProperty("partitions_list")
    public ArrayList<Integer> partitionsList;
}
