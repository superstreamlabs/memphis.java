![Github (3)](https://github.com/memphisdev/memphis.java/assets/107035359/18151b8f-18a8-4285-a7a6-6d6cce721a6e)
<p align="center">
<a href="https://memphis.dev/discord"><img src="https://img.shields.io/discord/963333392844328961?color=6557ff&label=discord" alt="Discord"></a>
<a href="https://github.com/memphisdev/memphis/issues?q=is%3Aissue+is%3Aclosed"><img src="https://img.shields.io/github/issues-closed/memphisdev/memphis?color=6557ff"></a> 
  <img src="https://img.shields.io/npm/dw/memphis-dev?color=ffc633&label=installations">
<a href="https://github.com/memphisdev/memphis/blob/master/CODE_OF_CONDUCT.md"><img src="https://img.shields.io/badge/Code%20of%20Conduct-v1.0-ff69b4.svg?color=ffc633" alt="Code Of Conduct"></a> 
<img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/memphisdev/memphis?color=61dfc6">
<img src="https://img.shields.io/github/last-commit/memphisdev/memphis?color=61dfc6&label=last%20commit">
</p>

 <b><p align="center">
  <a href="https://memphis.dev/pricing/">Cloud</a> - <a href="github.com/memphisdev/memphis-dev-academy">Academy</a> - <a href="https://memphis.dev/docs/">Docs</a> - <a href="https://twitter.com/Memphis_Dev">X</a> - <a href="https://www.youtube.com/channel/UCVdMDLCSxXOqtgrBaRUHKKg">YouTube</a>
</p></b>

<div align="center">

  <h4>

**[Memphis.dev](https://memphis.dev)** is more than a broker. It's a new streaming stack.<br>
Memphis.dev is a highly scalable event streaming and processing engine.<br>

<img width="177" alt="cloud_native 2 (5)" src="https://github.com/memphisdev/memphis/assets/107035359/a20ea11c-d509-42bb-a46c-e388c8424101">

  </h4>
  
</div>

## Installation

```sh
$ gradle install memphis-dev
```

## Importing

```java
import com.memphis.Memphis;
```

### Connecting to Memphis

First, we need to create a connection to the broker using `MemphisConnection`.

```java
ClientOptions opts = new ClientOptions.Builder()
    .host("<memphis-host>")
    .username("<application-username>")
    .token("<broker-token>")
    .port("<port>")		// defaults to 6666
    .reconnect(true)	// defaults to true
    .maxReconnect(3)	// defaults to 3
    .reconnectInterval(1500)	// defaults to 1500 ms
    .timeOut(1500)		// defaults to 1500 ms
    .keyFile("<key-client.pem>")	// key_file, for TLS connection
    .certFile("<cert-client.pem>")	// cert_file, for TLS connection
    .caFile("<rootCA.pem>")		// ca_file, for TLS connection
    .build();

MemphisConnection memphisConnection = MemphisConnection(opts);
```

Once connected, the entire functionalities offered by Memphis are available.

### Disconnecting from Memphis

To disconnect from Memphis, call `close()` on the memphis object.

```java
memphisConnection.close();
```

### Creating a Station

_If a station already exists nothing happens, the new configuration will not be applied_

```java
Station station = memphisConnection.createStation(
    "<station-name>",
    "<schema-name>",
    Retention.MAX_MESSAGE_AGE_SECONDS, // MAX_MESSAGE_AGE_SECONDS/MESSAGES/BYTES. Defaults to MAX_MESSAGE_AGE_SECONDS
    604800, // defaults to 604800
    Storage.DISK, // Storage.DISK/Storage.MEMORY. Defaults to DISK
    1, // defaults to 1
    120000, // defaults to 2 minutes
    true, // defaults to true
    true, // defaults to true
    false // defaults to false
).get();
```

### Retention types

Memphis currently supports the following types of retention:

```java
RetentionTypes.MAX_MESSAGE_AGE_SECONDS
```

Means that every message persists for the value set in retention value field (in seconds)

```java
RetentionTypes.MESSAGES
```

Means that after max amount of saved messages (set in retention value), the oldest messages will be deleted

```java
RetentionTypes.BYTES
```

Means that after max amount of saved bytes (set in retention value), the oldest messages will be deleted


### Retention Values

The `retention values` are directly related to the `retention types` mentioned above, where the values vary according to the type of retention chosen.

All retention values are of type `Integer` but with different representations as follows:

`RetentionTypes.MAX_MESSAGE_AGE_SECONDS` is represented **in seconds**, `RetentionTypes.MESSAGES` in a **number of messages** and finally `RetentionTypes.BYTES` in a **number of bytes**.

After these limits are reached oldest messages will be deleted.

### Storage types

Memphis currently supports the following types of messages storage:

```java
StorageTypes.DISK
```

Means that messages persist on disk

```java
StorageTypes.MEMORY
```

Means that messages persist on the main memory

### Destroying a Station

Destroying a station will remove all its resources (producers/consumers)

```java
station.destroy().get()
```

### Attaching a Schema to an Existing Station

```java
memphisConnection.attachSchema("<schema-name>", "<station-name>").get();
```

### Detaching a Schema from Station

```java
memphisConnection.detachSchema("<station-name>").get();
```


### Produce and Consume messages

The most common client operations are `produce` to send messages and `consume` to
receive messages.

Messages are published to a station and consumed from it by creating a consumer.
Consumers are pull based and consume all the messages in a station unless you are using a consumers group, in this case messages are spread across all members in this group.

Memphis messages are payload agnostic. Payloads are `byte[]`.

In order to stop getting messages, you have to call `consumer.destroy()`. Destroy will terminate regardless
of whether there are messages in flight for the client.

### Creating a Producer

```java
ProducerOptions pOpts = new ProducerOptions.Builder()
        .stationName("<station-name>")
        .producerName("<producer-name>")
        .build();
MemphisProducer producer = memphisConnection.createProducer(pOpts);
```

### Producing a message and blocking until acknowledgment
```java
producer.produce(byte[] message);
```

### Producing a message without blocking
This method will add the message to an internal queue and return.
If the queue is full, this method will block until the queue has been
drained some.
```java
producer.produceNonblocking(byte[] message);
```

### Stopping a Producer

```java
producer.stop();
```

### Creating an Asynchronous Consumer
The asynchronous consumer executes a provided function on each batch of messages received in a background thread.

```java
ConsumerOptions opts = new ConsumerOptions.Builder()
        .consumerName("test-runner")
        .stationName("example-station")
        .build();

MemphisAsyncConsumer consumer = memphisConnection.createAsyncConsumer(
        opts,
        messages -> {
            for(MemphisMessage msg : messages) {
                System.out.println(new String(msg.getData(), StandardCharsets.UTF_8));
                msg.ack();
            }
        });
        
);
```

### Processing messages

```java
consumer.start();
```

### Stopping the consumer

```java
consumer.stop();
```

### Creating a Synchronous Consumer
The synchronous consumer checks for messages when its `fetch()` method is called.
The call blocks until messages are available or the wait timeout has been exceeded.

```java 
ConsumerOptions opts = new ConsumerOptions.Builder()
        .consumerName("test-runner")
        .stationName("example-station")
        .build();
MemphisSyncConsumer consumer = connection.createSyncConsumer(opts);
```

### Processing messages

```java
var messages = consumer.fetch();

for(var msg : messages) {
    msg.ack();
}
```

### Check connection status

```java
memphisConnection.isConnected();
```
