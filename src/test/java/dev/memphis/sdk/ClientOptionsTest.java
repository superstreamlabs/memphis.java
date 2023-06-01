package dev.memphis.sdk;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientOptionsTest {
    @Test
    public void authenticateWithPassword() throws MemphisConnectException {
        ClientOptions opts = new ClientOptions.Builder()
                .username("username")
                .password("password")
                .host("host")
                .build();
        assertEquals(opts.port, 6666);
        assertTrue(opts.reconnect);
        assertEquals(opts.reconnectInterval, Duration.ofMillis(1500));
        assertEquals(opts.timeout, Duration.ofMillis(1500));
        assertEquals(opts.maxReconnects, 3);
        assertInstanceOf(ClientOptions.Password.class, opts.authenticationMethod);
    }

    @Test
    public void authenticateWithToken() throws MemphisConnectException {
        ClientOptions opts = new ClientOptions.Builder()
                .username("username")
                .connectionToken("token")
                .host("host")
                .build();
        assertEquals(opts.port, 6666);
        assertTrue(opts.reconnect);
        assertEquals(opts.reconnectInterval, Duration.ofMillis(1500));
        assertEquals(opts.timeout, Duration.ofMillis(1500));
        assertEquals(opts.maxReconnects, 3);
        assertInstanceOf(ClientOptions.ConnectionToken.class, opts.authenticationMethod);
    }

    @Test
    public void missingAuthentication() {
        ClientOptions.Builder builder = new ClientOptions.Builder()
                .host("host")
                .username("username");

        assertThrows(MemphisConnectException.class,
                builder::build,
                "Expected ClientOptions.Builder.build() to throw MemphisConnectionException if no password or connection token was provided");
    }

    @Test
    public void missingHost() {
        ClientOptions.Builder builder = new ClientOptions.Builder()
                .password("password")
                .username("username");

        assertThrows(MemphisConnectException.class,
                builder::build,
                "Expected ClientOptions.Builder.build() to throw MemphisConnectionException if no host was provided");
    }

    @Test
    public void missingUsername() {
        ClientOptions.Builder builder = new ClientOptions.Builder()
                .password("password")
                .host("host");

        assertThrows(MemphisConnectException.class,
                builder::build,
                "Expected ClientOptions.Builder.build() to throw MemphisConnectionException if no username was provided");
    }
}