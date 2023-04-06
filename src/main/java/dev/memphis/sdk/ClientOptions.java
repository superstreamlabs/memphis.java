package dev.memphis.sdk;

public class ClientOptions {
    final String host;
    final String username;
    final String broker_token;
    final int port;
    final boolean reconnect;
    final int max_reconnect;
    final int reconnect_interval_ms;
    final int timeout_ms;
    final String key_file;
    final String cert_file;
    final String ca_file;

    private ClientOptions(Builder b) {
        this.host = b.host;
        this.username = b.username;
        this.broker_token = b.broker_token;
        this.port = b.port;
        this.reconnect = b.reconnect;
        this.max_reconnect = b.max_reconnect;
        this.reconnect_interval_ms = b.reconnect_interval_ms;
        this.timeout_ms = b.timeout_ms;
        this.key_file = b.key_file;
        this.cert_file = b.cert_file;
        this.ca_file = b.ca_file;
    }

    public static class Builder {
        private String host;
        private String username;
        private String broker_token;

        // Connection parameters with default values
        private int port = 6666;
        private boolean reconnect = true;
        private int max_reconnect = 3;
        private int reconnect_interval_ms = 1500;
        private int timeout_ms = 1500;
        private String key_file = "";
        private String cert_file = "";
        private String ca_file = "";

        public ClientOptions build() {
            return new ClientOptions(this);
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder token(String broker_token) {
            this.broker_token = broker_token;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder reconnect(boolean reconnect) {
            this.reconnect = reconnect;
            return this;
        }

        public Builder maxReconnect(int max_reconnect) {
            this.max_reconnect = max_reconnect;
            return this;
        }
        public Builder reconnectInterval(int reconnect_interval) {
            this.reconnect_interval_ms = reconnect_interval;
            return this;
        }

        public Builder timeout(int timeout) {
            this.timeout_ms = timeout;
            return this;
        }

        public Builder keyFile(String key_file) {
            this.key_file = key_file;
            return this;
        }

        public Builder certFile(String cert_file) {
            this.cert_file = cert_file;
            return this;
        }

        public Builder caFile(String ca_file) {
            this.ca_file = ca_file;
            return this;
        }
    }

}
