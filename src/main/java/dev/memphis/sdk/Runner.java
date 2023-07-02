package dev.memphis.sdk;

import dev.memphis.sdk.producer.MemphisProducer;

public class Runner {
    public static void main(String[] args) throws MemphisException {
        ClientOptions options = new ClientOptions.Builder()
                .host("localhost")
                .username("root")
                .password("memphis")
                .build();

        MemphisConnection connection = Memphis.connect(options);

        MemphisProducer producer = connection.createProducer("thisisatest", "JavaProducer");
        byte[] msg = "This is a test.".getBytes();

        for(int i = 0; i < 5; i++) {
            producer.produce(msg);
        }

        System.out.println("Reached end.");
    }
}
