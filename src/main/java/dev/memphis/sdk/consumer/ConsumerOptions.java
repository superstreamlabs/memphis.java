package dev.memphis.sdk.consumer;

public class ConsumerOptions {
    public String consumerName;
    public String stationName;
    public String consumersGroup = null;
    public Integer maxAckTimeMs;
    public Integer maxMsgDeliveries;
    public Integer startConsumeFromSequence;
    public Integer lastMessages;
}
