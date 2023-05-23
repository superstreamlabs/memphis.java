package dev.memphis.sdk;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientOptionsTest {
    @Test
    public void defaultClientOptions() throws MemphisConnectException {
        ClientOptions opts = new ClientOptions.Builder().build();
        assertEquals(opts.port, 6666);
        assertTrue(opts.reconnect);
        assertEquals(opts.reconnect_interval_ms, 1500);
        assertEquals(opts.timeout_ms, 1500);
        assertEquals(opts.max_reconnect, 3);
    }
}