package dev.memphis.sdk.consumer;

import io.nats.client.Connection;
import io.nats.client.JetStreamApiException;
import io.nats.client.JetStreamManagement;

import java.io.IOException;
import java.time.Duration;

/**
 * Retrieves consumer info every `duration` ms.  Keeps the consumer
 * alive.
 */
class ConsumerKeepAlive implements Runnable {

    private final JetStreamManagement jsManagement;
    private final String stationName;
    private final String consumerGroup;
    private final Duration sleepPeriod;

    private boolean canceled = false;

    public ConsumerKeepAlive(JetStreamManagement jsManagement, String stationName, String consumerGroup, Duration sleepPeriod) {
        this.jsManagement = jsManagement;
        this.stationName = stationName;
        this.consumerGroup = consumerGroup;
        this.sleepPeriod = sleepPeriod;
    }

    @Override
    public void run() {
        try {
            while (!canceled) {
                Thread.sleep(sleepPeriod.toMillis());
                jsManagement.getConsumerInfo(stationName, consumerGroup);
            }
        } catch(IOException | JetStreamApiException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InterruptedException e) {
            // do nothing
        }
    }

    public void cancel() {
        this.canceled = true;
    }
}
