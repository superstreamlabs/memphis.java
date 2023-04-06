<div align="center">

  ![Memphis light logo](https://github.com/memphisdev/memphis-broker/blob/master/logo-white.png?raw=true#gh-dark-mode-only)

</div>

<div align="center">

  ![Memphis light logo](https://github.com/memphisdev/memphis-broker/blob/master/logo-black.png?raw=true#gh-light-mode-only)

</div>

<div align="center">
<h4>Simple as RabbitMQ, Robust as Apache Kafka, and Perfect for microservices.</h4>

<img width="750" alt="Memphis UI" src="https://user-images.githubusercontent.com/70286779/204081372-186aae7b-a387-4253-83d1-b07dff69b3d0.png"><br>


  <a href="https://landscape.cncf.io/?selected=memphis"><img width="200" alt="CNCF Silver Member" src="https://github.com/cncf/artwork/raw/master/other/cncf-member/silver/white/cncf-member-silver-white.svg#gh-dark-mode-only"></a>

</div>

<div align="center">

  <img width="200" alt="CNCF Silver Member" src="https://github.com/cncf/artwork/raw/master/other/cncf-member/silver/color/cncf-member-silver-color.svg#gh-light-mode-only">

</div>

 <p align="center">
  <a href="https://sandbox.memphis.dev/" target="_blank">Sandbox</a> - <a href="https://memphis.dev/docs/">Docs</a> - <a href="https://twitter.com/Memphis_Dev">Twitter</a> - <a href="https://www.youtube.com/channel/UCVdMDLCSxXOqtgrBaRUHKKg">YouTube</a>
</p>

<p align="center">
<a href="https://discord.gg/WZpysvAeTf"><img src="https://img.shields.io/discord/963333392844328961?color=6557ff&label=discord" alt="Discord"></a>
<a href="https://github.com/memphisdev/memphis-broker/issues?q=is%3Aissue+is%3Aclosed"><img src="https://img.shields.io/github/issues-closed/memphisdev/memphis-broker?color=6557ff"></a> 
<a href="https://github.com/memphisdev/memphis-broker/blob/master/CODE_OF_CONDUCT.md"><img src="https://img.shields.io/badge/Code%20of%20Conduct-v1.0-ff69b4.svg?color=ffc633" alt="Code Of Conduct"></a> 
<a href="https://docs.memphis.dev/memphis/release-notes/releases/v0.4.2-beta"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/memphisdev/memphis-broker?color=61dfc6"></a>
<img src="https://img.shields.io/github/last-commit/memphisdev/memphis-broker?color=61dfc6&label=last%20commit">
</p>

**[Memphis](https://memphis.dev)** is a next-generation alternative to traditional message brokers.<br><br>
A simple, robust, and durable cloud-native message broker wrapped with<br>
an entire ecosystem that enables cost-effective, fast, and reliable development of modern queue-based use cases.<br><br>
Memphis enables the building of modern queue-based applications that require<br>
large volumes of streamed and enriched data, modern protocols, zero ops, rapid development,<br>
extreme cost reduction, and a significantly lower amount of dev time for data-oriented developers and data engineers.

## Installation

```sh
$ gradle install memphis-dev
```

## Importing

```java
import com.memphis.Memphis;
```

### Connecting to Memphis

First, we need to create Memphis `object` and then connect with Memphis by using `Memphis.connect`.

```java
ClientOptions opts = new ClientOptions.Builder()
    .host("<memphis-host>")
    .username("<application-username>")
    .token("<broker-token>")
    .port("<port>")
    .reconnect(true)
    .maxReconnect(3)
    .reconnectInterval(1500)
    .timeOut(1500)
    .keyFile("<key-client.pem>")
    .certFile("<cert-client.pem>")
    .caFile("<rootCA.pem>")
    .build();

MemphisConnection memphisConnection = Memphis.connect(opts).get();
```

Once connected, the entire functionalities offered by Memphis are available.

### Disconnecting from Memphis

To disconnect from Memphis, call `close()` on the memphis object.

```java
memphisConnection.close().get();
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
memphisConnection.detachSchema("<station-name>").get()
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
Prodcuer producer = memphisConnection.createProducer("<station-name>", "<producer-name>", false).get();
```

### Producing a message
Without creating a producer.
In cases where extra performance is needed the recommended way is to create a producer first
and produce messages by using the produce function of it
```java
memphisConnection.produce(
    "<station-name>", // station_name
    "<producer-name>", // producer_name
    message, // bytearray / protobuf class (schema validated station - protobuf) or bytearray/dict (schema validated station - json schema) or string/bytearray/graphql.language.ast.DocumentNode (schema validated station - graphql schema)
    false, // generate_random_suffix - defaults to false
    15, // ack_wait_sec
    headers, // headers - default to {}
    false, // async_produce - defaults to false
    "123" // msg_id
).get();
```

with creating a producer
```java
producer.produce(
  message, // bytearray / protobuf class (schema validated station - protobuf) or bytearray/dict (schema validated station - json schema) or string/bytearray/graphql.language.ast.DocumentNode (schema validated station - graphql schema)
  ack_wait_sec) // defaults to 15
```
### Add headers

```java
headers= new Headers()
headers.add("key", "value")
producer.produce(
  message, // bytearray / protobuf class (schema validated station - protobuf) or bytearray/dict (schema validated station - json schema) or string/bytearray/graphql.language.ast.DocumentNode (schema validated station - graphql schema)
  headers) // default to {}
  .get() 
```

### Async produce
Meaning your application won't wait for broker acknowledgement - use only in case you are tolerant for data loss

```java
producer.produce(
  message, // bytearray / protobuf class (schema validated station - protobuf) or bytearray/dict (schema validated station - json schema) or string/bytearray/graphql.language.ast.DocumentNode (schema validated station - graphql schema)
  headers={}, async_produce=True).get()
```

### Message ID
Stations are idempotent by default for 2 minutes (can be configured), Idempotency achieved by adding a message id

```java
producer.produce((
  message, // bytes / protobuf class (schema validated station - protobuf) or bytes/dict (schema validated station - json schema)
  headers, // defaulet {}
  async_produce, //should be True to make async call
  msg_id).get()
```

### Destroying a Producer

```java
producer.destroy().get()
```

### Creating a Consumer

```java
Consumer consumer = memphisConnection.createConsumer(
          "<station-name>",
          "<consumer-name>",
          "<group-name>",// defaults to the consumer name
          pull_interval_ms, // defaults to 1000
          batch_size, // defaults to 10
          batch_max_time_to_wait_ms, // defaults to 5000
          max_ack_time_ms, // defaults to 30000
          max_msg_deliveries, // defaults to 10
          generate_random_suffix,
          start_consume_from_sequence, // start consuming from a specific sequence. defaults to 1
          last_messages // consume the last N messages, defaults to -1 (all messages in the station)
).get()
```

### Setting a context for message handler function

```java
Map<String, Object> context = new HashMap<>();
context.put("key", "value");
consumer.setContext(context);
```

### Processing messages

Once all the messages in the station were consumed the msg_handler will receive error: `Memphis: TimeoutError`.

```java
public void msgHandler(List<Message> msgs, Exception error, Object context) {
    for (Message msg : msgs) {
        System.out.println("message: " + msg.getData());
        msg.ack().get();
    }
    if (error != null) {
        System.out.println(error);
    }
}

consumer.consume(this::msgHandler).get();
```

### Fetch a single batch of messages
```java
messagesFuture = memphisConnection.fetchMessages("<station-name>", "<consumer-name>", "<group-name>", 10, 5000, 30000, 10, false, 1, -1).get();
```

### Fetch a single batch of messages after creating a consumer
```java
List<Message> msgs = consumer.fetch(10); // fetches 10 messages from the station
```


### Acknowledge a message

Acknowledge a message indicates the Memphis server to not re-send the same message again to the same consumer / consumers group

```java
message.ack().get()
```

### Get headers 
Get headers per message

```java
message.getHeaders()
```

### Get message sequence number
Get message sequence number

```java
msg.getSequenceNumber()
```

### Destroying a Consumer

```java
consumer.destroy().get()
```


### Check connection status

```java
memphisConnection.isConnected()
```
