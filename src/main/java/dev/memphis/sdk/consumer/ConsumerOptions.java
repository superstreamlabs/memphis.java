package dev.memphis.sdk.consumer;

import dev.memphis.sdk.MemphisException;

/**
 * Options used when creating consumers.
 */
public class ConsumerOptions {
    public final String consumerName;
    public final String stationName;
    public final String consumersGroup;
    public final int maxAckTimeMs;
    public final int maxMsgDeliveries;
    public final int startConsumeFromSequence;
    public final int lastMessages;

    private ConsumerOptions(Builder b) {
        consumerName = b.consumerName;
        stationName = b.stationName;
        consumersGroup = b.consumersGroup;
        maxAckTimeMs = b.maxAckTimeMs;
        maxMsgDeliveries = b.maxMsgDeliveries;
        startConsumeFromSequence = b.startConsumeFromSequence;
        lastMessages = b.lastMessages;
    }

    /**
     * Fluent-style interface for building ConsumerOptions.
     */
    public static class Builder {
        private String consumerName;
        private String stationName;
        private String consumersGroup = null;
        private int maxAckTimeMs = 5000;
        private int maxMsgDeliveries = 10;
        private Integer startConsumeFromSequence = null;
        private Integer lastMessages = null;

        public Builder consumerName(String consumerName) {
            this.consumerName = consumerName;
            return this;
        }

        public Builder stationName(String stationName) {
            this.stationName = stationName;
            return this;
        }

        public Builder consumersGroup(String consumersGroup) {
            this.consumersGroup = consumersGroup;
            return this;
        }

        public Builder maxAckTimeMs(int maxAckTimeMs) {
            this.maxAckTimeMs = maxAckTimeMs;
            return this;
        }

        public Builder maxMsgDeliveries(int maxMsgDeliveries) {
            this.maxMsgDeliveries = maxMsgDeliveries;
            return this;
        }

        public Builder startConsumeFromSequence(int startConsumeFromSequence) {
            this.startConsumeFromSequence = startConsumeFromSequence;
            return this;
        }

        public Builder lastMessages(int lastMessages) {
            this.lastMessages = lastMessages;
            return this;
        }

        public ConsumerOptions build() throws MemphisException {
            if(stationName == null) {
                throw new MemphisException("Station name must be set.");
            }

            if(consumerName == null) {
                throw new MemphisException("Consumer name must be set.");
            }

            if(consumersGroup == null) {
                consumersGroup = consumerName;
            }

            if(startConsumeFromSequence != null && lastMessages != null) {
                throw new MemphisException("Cannot set both StartConsumeFromSequence and LastMessages.");
            } else if(startConsumeFromSequence == null && lastMessages == null) {
                // default to first message in station
                startConsumeFromSequence = 1;
                lastMessages = -1;
            } else if(startConsumeFromSequence != null) {
                lastMessages = -1;
            } else {
                startConsumeFromSequence = 1;
            }

            return new ConsumerOptions(this);
        }
    }
}
