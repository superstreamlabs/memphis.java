package dev.memphis.sdk.producer;

import dev.memphis.sdk.MemphisException;

/**
 * Options used when creating producers.
 */
public class ProducerOptions {
    public final String producerName;
    public final String stationName;

    private ProducerOptions(Builder b) {
        producerName = b.producerName;
        stationName = b.stationName;

    }

    /**
     * Fluent-style interface for building ProducerOptions.
     */
    public static class Builder {
        private String producerName;
        private String stationName;

        public Builder producerName(String producerName) {
            this.producerName = producerName;
            return this;
        }

        public Builder stationName(String stationName) {
            this.stationName = stationName;
            return this;
        }

        public ProducerOptions build() throws MemphisException {
            if(stationName == null) {
                throw new MemphisException("Station name must be set.");
            }

            if(producerName == null) {
                throw new MemphisException("Consumer name must be set.");
            }

            return new ProducerOptions(this);
        }
    }
}
