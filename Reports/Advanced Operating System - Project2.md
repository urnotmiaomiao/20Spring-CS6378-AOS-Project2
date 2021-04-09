# Advanced Operating System - Project2

## Implementation

### Start

To start clients and servers:

```bash
# run client on dc30-dc34
cd Client
java -jar AClient.jar
```

```bash
# run server on dc20-dc26
cd Server
java -jar AServer.jar
```

### Object  Manipulation 

1. To create an object, enter following command in a client node:

```bash
create object_name
```

2. Update object function is written as appending the message content to the object.

   To update an object, enter following command in a client node:

```bash
update object_name
```

3. To delete an object, enter following command in a client node:

```bash
delete object_name
```

4. To read an object, enter following command in a client node:

```bash
get object_name
```

### Channel  Disable

1. To break channel with server i in a client, enter following command in a client node:

```bash
break i	# i is an Integer
```

2. To break channel with server i in a server, enter following command in a server node:

```bash
break i	s # i is an Integer
```

3. To break channel with client i in a client, enter following command in a server node:

```bash
break i	c # i is an Integer
```



## Correctness

### Hash Value

The hash value of an object is settled when a message is constructed.

In case the function `hashCode() `could return negative hash value: 

```java
this.objectHashValue = objectName.hashCode() & Integer.MAX_VALUE;
```

### Voting Protocol

Once a server node receives a request message, it would add the message in `BlockingQueue<Message> MsgQueueClient`, which would sort the message according to timestamp and client ID.

Server would refresh the queue to get its current peek message, and server would process the message only when it receives a same message from at least one other server. 

After server processing the message, it will remove it from the queue and also remove the message received from other server.

If server can not get a message from other servers, the message would be deleted after timeout, according to timestamp.

```java
if(MsgHMServer.containsKey(message.toKey())) {
    MsgHMServer.remove(message.toKey());
    try {
        MsgQueueClient.take();
    } catch (InterruptedException e) { e.printStackTrace(); }
    processMessage(u, message);
    con.sendMsgClient(u, message, message.getClientID());
}else if(atomTS.getTimestamp() - message.getTimestamp()>=50) {
    /* remove the message from Queue after timeout*/
    try {
        MsgQueueClient.take();
    } catch (InterruptedException e) { e.printStackTrace(); }
}
```

### Channel Disconnecting

To avoid breaking TCP connection and exceptions caused by channel breaking,  I set an Array of boolean values to determine if a channel is active. These boolean values are initialized as true.

```java
/* in Client's Connection class */
private boolean[] isActive = new boolean[7];	// if a TCP channel to server_i is active
```

```java
/* in Server's Connection class */
private boolean[] isActive = new boolean[7];	// if a TCP channel to server_i is active
private boolean[] isActiveClient = new boolean[5];	// if a TCP channel to client_i is active
```

Before sending message to server_i, client has to check if the channel is active.

For example, client_j wants to break a channel with server_i. Client_j needs to enter `break i` in command line, i is the index of server_i. Client_j will first send break message to server_i, then set `isActive[i]` as false. After receiving a break message, server_i would set `isActiveClient[j]` as false.

In this situation, the message receiving functions are still active, but both message sending functions are turned off. So there would be no message in transit through this channel, which will work as disabled.

### Safety and Liveness

#### Disorder

Using timestamp to avoid a message being executed in different order on different servers.

Timestamp will be increased when message is sent and will be renewed as the largest timestamp when a new message is received.

Since client and server are communicating using TCP, messages sent by the same client would be executed in same order.

If a message sent by different clients reach the same server with same timestamp, their client ID will be used to determine which one will be executed first. 

#### Liveness

A message could be executed only when server receives a same message from its neighbor. 

So if a client can not reach at least two servers that are needed for an object, the message would not be processed.

If three servers that are needed can not talk to each other, then the message can not be processed neither.



## Design Document

### Common Class

##### AtomTimeStamp.class

A class of atom time stamp. 

##### Message.class

Message class implements `Comparable<Message>` and `Serializable`, so that message could be stored in `BlockingQueue` and message could be sent and received as Object.

##### Node.class

Define Node class.

### Client 

##### Main.class

##### Utility.class

Get config information from document `config.properties`.

Save server nodes in HashMap.

##### Connection.class

Get and save connections between servers.

Define functions to manage connections.

##### Protocol.class

Define protocol of client, how to react after receiving message from server, and how to run commands entered in command line.

##### ThreadHandleServerIS.class

### Server

##### Main.class

##### Utility.class

Get config information from document `config.properties`.

Save client nodes and server nodes in HashMap.

Define functions of object manipulation, including create, update, delete, read.

##### Connection.class

Get and save connections between servers and clients.

Define functions to manage connections.

##### Protocol.class

Define protocol of server, how to react after receiving message from server or client, and how to run commands entered in command line.

##### ThreadHandleServerIS.class

##### ThreadHandleClientIS.class

##### ThreadQueueProcess.class

##### ThreadSocketListen.class

