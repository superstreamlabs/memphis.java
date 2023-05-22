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
        private int port = 6666;
        private boolean reconnect = true;
        private int max_reconnect = 3;
        private int reconnect_interval_ms = 1500;
        private int timeout_ms = 1500;
        private String key_file = "";
        private String cert_file = "";
        private String ca_file = "";

        /***
         *
         * @return a ClientOptions object created with the help of this Builder
         * @throws MemphisConnectException if either TLS key file, TLS certificate file or TLS ca file is missing
         */
        public ClientOptions build() throws MemphisConnectException {
            if(!key_file.equals("") || !cert_file.equals("")) {
                if(key_file.equals(""))
                    throw new MemphisConnectException("Must provide a TLS key file");
                if(cert_file.equals(""))
                    throw new MemphisConnectException("Must provide a TLS cert file");
                if(ca_file.equals(""))
                    throw new MemphisConnectException("Must provide a TLS ca file");
            }

            return new ClientOptions(this);
        }

        /***
         *
         * @param host memphis host address
         * @return the Builder object for chaining purpose
         */
        public Builder host(String host) {
            this.host = host;
            return this;
        }

        /***
         *
         * @param username user of type root/application
         * @return the Builder object for chaining purpose
         */
        public Builder username(String username) {
            this.username = username;
            return this;
        }

        /***
         *
         * @param broker_token connection token, obtained at the time of application type user creation
         * @return the Builder object for chaining purpose
         */
        public Builder token(String broker_token) {
            this.broker_token = broker_token;
            return this;
        }

        /***
         *
         * @param port memphis host port, defaults to 6666
         * @return the Builder object for chaining purpose
         */
        public Builder port(int port) {
            this.port = port;
            return this;
        }

        /***
         *
         * @param reconnect whether to try to reconnect if connection is lost, defaults to true
         * @return the Builder object for chaining purpose
         */
        public Builder reconnect(boolean reconnect) {
            this.reconnect = reconnect;
            return this;
        }

        /***
         *
         * @param max_reconnect maximum reconnection attempts that should be made, defaults to 3
         * @return the Builder object for chaining purpose
         */
        public Builder maxReconnect(int max_reconnect) {
            this.max_reconnect = max_reconnect;
            return this;
        }

        /***
         *
         * @param reconnect_interval duration in milliseconds between reconnection attempts, defaults to 1500 ms
         * @return the Builder object for chaining purpose
         */
        public Builder reconnectInterval(int reconnect_interval) {
            this.reconnect_interval_ms = reconnect_interval;
            return this;
        }

        /***
         *
         * @param timeout connection timeout duration in milliseconds, defaults to 1500 ms
         * @return the Builder object for chaining purpose
         */
        public Builder timeout(int timeout) {
            this.timeout_ms = timeout;
            return this;
        }

        /***
         *
         * @param key_file TLS key file path
         * @return the Builder object for chaining purpose
         */
        public Builder keyFile(String key_file) {
            this.key_file = key_file;
            return this;
        }


        /***
         *
         * @param cert_file TLS certificate file path
         * @return the Builder object for chaining purpose
         */
        public Builder certFile(String cert_file) {
            this.cert_file = cert_file;
            return this;
        }

        /***
         *
         * @param ca_file TLS ca file path
         * @return the Builder object for chaining purpose
         */
        public Builder caFile(String ca_file) {
            this.ca_file = ca_file;
            return this;
        }
    }
}
