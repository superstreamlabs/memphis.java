package dev.memphis.sdk.producer;

import dev.memphis.sdk.MemphisException;

/**
 * Options used when creating producers.
 */
public class ProducerOptions {
    public final String producerName;
    public final String stationName;
    public final int queueSize;

    private ProducerOptions(Builder b) {
        producerName = b.producerName;
        stationName = b.stationName;
        this.queueSize = b.queueSize;
    }

    /**
     * Fluent-style interface for building ProducerOptions.
     */
    public static class Builder {
        private String producerName;
        private String stationName;
        private int queueSize = 1000;

        public Builder producerName(String producerName) {
            this.producerName = producerName;
            return this;
        }

        public Builder stationName(String stationName) {
            this.stationName = stationName;
            return this;
        }

        public Builder queueSize(int numMessages) {
            this.queueSize = numMessages;
            return this;
        }

        public ProducerOptions build() throws MemphisException {
            if(stationName == null) {
                throw new MemphisException("Station name must be set.");
            }

            if(producerName == null) {
                throw new MemphisException("Consumer name must be set.");
            }

            if(queueSize < 1) {
                throw new MemphisException("The buffer must be able to hold at least one message.");
            }

            return new ProducerOptions(this);
        }
    }
}
