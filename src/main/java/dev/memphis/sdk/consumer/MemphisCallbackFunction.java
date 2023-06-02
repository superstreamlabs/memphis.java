package dev.memphis.sdk.consumer;

import dev.memphis.sdk.MemphisMessage;

import java.util.List;

@FunctionalInterface
public interface MemphisCallbackFunction {
    void accept(List<MemphisMessage> messages);
}
