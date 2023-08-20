package dev.memphis.sdk;

import java.time.Duration;

public class ClientOptions {
    public final String host;
    public final String username;
    public final AuthenticationMethod authenticationMethod;
    public final int port;
    public final boolean reconnect;
    public final int maxReconnects;
    public final Duration reconnectInterval;
    public final Duration timeout;
    public final SSLConfiguration sslConfiguration;
    public final Duration maxWaitTime;
    public final int batchSize;
    public final Duration pullInterval;
    public final int accountId;

    private ClientOptions(Builder b) {
        this.host = b.host;
        this.username = b.username;
        this.authenticationMethod = b.authenticationMethod;
        this.port = b.port;
        this.reconnect = b.reconnect;
        this.maxReconnects = b.maxReconnects;
        this.reconnectInterval = b.reconnectInterval;
        this.timeout = b.timeout;
        this.sslConfiguration = b.sslConfiguration;
        this.maxWaitTime = b.maxWaitTime;
        this.batchSize = b.batchSize;
        this.pullInterval = b.pullInterval;
        this.accountId = b.accountId;
    }

    /**
     * Allows modeling of mutually-exclusive authentication methods
     * in a type-safe way.
     */
    public static class AuthenticationMethod {
        private final String s;
        protected AuthenticationMethod(String s) {
            this.s = s;
        }

        public String getString() {
            return s;
        }

        public static AuthenticationMethod fromPassword(String password) {
            return new Password(password);
        }

        public static AuthenticationMethod fromConnectionToken(String token) {
            return new ConnectionToken(token);
        }
    }

    public static class Password extends AuthenticationMethod {
        public Password(String password) {
            super(password);
        }
    }

    public static class ConnectionToken extends AuthenticationMethod {
        public ConnectionToken(String connectionToken) {
            super(connectionToken);
        }
    }

    /**
     * Bundles the TLS key, certificate, and certificate authority file paths
     * for type safety.
     */
    public static class SSLConfiguration {
        final private String keyFile;
        final private String certFile;
        final private String caFile;

        /**
         *
         * @param keyFile TLS key file path
         * @param certFile TLS certificate file path
         * @param caFile TLS ca file path
         */
        public SSLConfiguration(String keyFile, String certFile, String caFile) {
            this.keyFile = keyFile;
            this.certFile = certFile;
            this.caFile = caFile;
        }

        public String getKeyFile() {
            return keyFile;
        }

        public String getCertFile() {
            return certFile;
        }

        public String getCaFile() {
            return caFile;
        }
    }

    public static class Builder {
        private String host;
        private String username;
        private AuthenticationMethod authenticationMethod;
        private SSLConfiguration sslConfiguration;
        private int port = 6666;
        private boolean reconnect = true;
        private int maxReconnects = 3;
        private Duration reconnectInterval = Duration.ofMillis(1500);
        private Duration timeout = Duration.ofMillis(1500);
        private Duration maxWaitTime = Duration.ofMillis(5000);
        private int batchSize = 10;
        private Duration pullInterval = Duration.ofMillis(1000);
        // on-premise version defaults to account id of 1
        private int accountId = 1;

        /***
         *
         * @return a ClientOptions object created with the help of this Builder
         * @throws MemphisConnectException if either TLS key file, TLS certificate file or TLS ca file is missing
         */
        public ClientOptions build() throws MemphisConnectException {
            if(authenticationMethod == null) {
                throw new MemphisConnectException("Must provide a password or connection token.");
            } else if(!(authenticationMethod instanceof Password) && !(authenticationMethod instanceof ConnectionToken)) {
                throw new MemphisConnectException("Unsupported authentication type.");
            }

            if(host == null) {
                throw new MemphisConnectException("Must provide a host");
            }

            if(username == null) {
                throw new MemphisConnectException("Must provide a username");
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
         * @param password is a password associated with the given user
         * @return the Builder object for chaining purpose
         */
        public Builder password(String password) {
            this.authenticationMethod = AuthenticationMethod.fromPassword(password);
            return this;
        }

        /***
         *
         * @param token is a connection token associated with the given user
         * @return the Builder object for chaining purpose
         */
        public Builder connectionToken(String token) {
            this.authenticationMethod = AuthenticationMethod.fromConnectionToken(token);
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
            this.maxReconnects = max_reconnect;
            return this;
        }

        /***
         *
         * @param reconnectInterval duration between reconnection attempts, defaults to 1500 ms
         * @return the Builder object for chaining purpose
         */
        public Builder reconnectInterval(Duration reconnectInterval) {
            this.reconnectInterval = reconnectInterval;
            return this;
        }

        /***
         *
         * @param timeout connection timeout duration, defaults to 1500 ms
         * @return the Builder object for chaining purpose
         */
        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /***
         *
         * @param sslConfiguration SSLConfiguration object
         * @return the Builder object for chaining purpose
         */
        public Builder SSLConfiguration(SSLConfiguration sslConfiguration) {
            this.sslConfiguration = sslConfiguration;
            return this;
        }

        /***
         *
         * @param maxWaitTime amount of time to wait when polling the broker
         * @return the Builder object for chaining purpose
         */
        public Builder maxWaitTime(Duration maxWaitTime) {
            this.maxWaitTime = maxWaitTime;
            return this;
        }

        /***
         *
         * @param pullInterval amount of time to wait in between polling the broker
         * @return the Builder object for chaining purpose
         */
        public Builder pullInterval(Duration pullInterval) {
            this.pullInterval = pullInterval;
            return this;
        }

        /***
         *
         * @param batchSize maximum number of messages to grab from the broker per batch
         * @return the Builder object for chaining purpose
         */
        public Builder batchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        /***
         *
         * @param accountId account id
         * @return the Builder object for chaining purpose
         */
        public Builder accountId(int accountId) {
            this.accountId = accountId;
            return this;
        }
    }
}
