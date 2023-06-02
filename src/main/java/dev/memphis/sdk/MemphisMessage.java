package dev.memphis.sdk;

import io.nats.client.Message;

public class MemphisMessage {
    private final Message message;
    private final String consumerGroup;

    public MemphisMessage(Message message, String consumerGroup) {
        this.message = message;
        this.consumerGroup = consumerGroup;
    }

    public byte[] getData() {
        return message.getData();
    }

    public void ack() {
        message.ack();
    }
}
