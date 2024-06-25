# Usage Documentation

### Setting up LSProtocol library
First step should always be setting up the library  
You can do this like that
```java
 new LightProtocol()
                .asDefault()
                .transport(LightProtocol.TransportType.NATIVE)
                .build();
```
or for cleaner looks
```java
new LightProtocol();
LightProtocol.builder()
    .transport(LightProtocol.TransportType.LEGACY)
    .build();
```
When using a new object ensure you are invoking `asDefault()`

### How to create a Server

You can create a simple server using this code

```java
var server = new MinecraftServer(NettyGroupBuilder.newBuilder().createPooledGroup(1));
server.setBaseHandler(getHandler());
server.bind(25565,(future -> {
    assert future.state() == Future.State.SUCCESS : "Server failed to bind";
}));
```
Server in this example will bind into port `25565`

### How to create a Client 
You can create a simple client using this code
```java
var server = new MinecraftClient(NettyGroupBuilder.newBuilder().createPooledGroup(1));
server.setBaseHandler(getHandler());
server.connect("127.0.0.1",25565,(future -> {
    assert future.state() == Future.State.SUCCESS : "Client failed to connect";
}));
```
Client in this example will connect to 127.0.0.1:25565

## Handlers

All of those examples above will need a handler  
**This library is not implementing any logic to the client/server**  

### Example Handler for Client

This handler will connect the client to the server using base protocol `1.20.4`
```java
    private static IConnectionHandler getHandler(){
        return new IConnectionHandler() {
            @Override
            public void active(Connection connection) {
                connection.sendPacket(new ClientHandshakePacket(765,"127.0.0.1",25565, (byte) 2));
                connection.sendPacket(new ClientLoginStartPacket("LSProtocol", UUID.randomUUID()));
                connection.setConnectionState(ConnectionState.LOGIN);
            }
            @Override
            public void packetReceived(Connection connection, Packet packet) {
                if(packet instanceof ServerLoginSetCompressionPacket compressionPacket){
                    connection.setCompressionThreshold(compressionPacket.getThreshold());
                }else if(packet instanceof ServerLoginSuccessPacket){
                    connection.sendPacket(new ClientLoginAcknowledgedPacket());
                    connection.setConnectionState(ConnectionState.CONFIG);

                }else if(packet instanceof ServerConfigFinishPacket){
                    connection.sendPacket(new ClientConfigFinishPacket());
                    connection.setConnectionState(ConnectionState.PLAY);
                }
            }
            @Override
            public void inactive(Connection connection) {
                System.out.printf("%s Inactive%n",connection.getId());
            }

            @Override
            public void exception(Connection connection, Throwable throwable) {
                throwable.printStackTrace();
            }
        };
    }
```


### Example Handler for Server
This handler will allow for client to connect into empty world.  
For world to work properly on 1.20.4 clients, you need to provide ConfigRegistryData NBT Tag, That tag can be found in the test resources directory
If you want to know how to spawn a player look at [wiki.vg/login_sequence](https://wiki.vg/Protocol_FAQ#What.27s_the_normal_login_sequence_for_a_client.3F)

```java
public static IConnectionHandler getHandler(Tag configTag){
        return new IConnectionHandler() {
            @Override
            public void active(Connection connection) {
                System.out.printf("%s Active%n",connection.getId());
            }

            @Override
            public void packetReceived(Connection connection, Packet packet) {
                switch(packet){
                    case ClientHandshakePacket handshakePacket -> connection.setConnectionState(handshakePacket.getNextState() == 2 ? ConnectionState.LOGIN : ConnectionState.STATUS);

                    case ClientLoginStartPacket loginStartPacket -> {
                        connection.sendPacket(new ServerLoginSetCompressionPacket(256));
                        connection.setCompressionThreshold(256); // SET COMPRESSION
                        connection.sendPacket(new ServerLoginSuccessPacket(loginStartPacket.getPlayerUUID(),loginStartPacket.getUsername(), Set.of()));
                    }

                    case ClientLoginAcknowledgedPacket ignored -> {
                        connection.setConnectionState(ConnectionState.CONFIG);
                        connection.sendPacket(new ServerConfigRegistryDataPacket(configTag));
                        connection.sendPacket(new ServerConfigFinishPacket());
                    }

                    case ClientConfigFinishPacket configFinishPacket -> {
                        connection.setConnectionState(ConnectionState.PLAY);
                        connection.sendPacket(new ServerPlayLoginPacket(
                                0,
                                false,
                                List.of("minecraft:overworld"),
                                20,
                                12,
                                12,
                                false,
                                true,
                                true,
                                "minecraft:overworld",
                                "overworld",
                                0L,
                                (byte) 1,
                                (byte) 1,
                                true,
                                false,
                                "minecraft:overworld",
                                new Position(0,0,0),
                                1));
                        connection.sendPacket(new ServerPlaySynchronizePlayerPositionPacket((double) 0, 500.0, (double) 0, 90F, 30F, (byte) 0,0));
                        connection.sendPacket(new ServerPlayGameEventPacket((short) 13,0f));
                        connection.sendPacket(new ServerPlaySetCenterChunkPacket(0,0));
                    }

                    default -> {}
                }
            }

            @Override
            public void inactive(Connection connection) {
                System.out.printf("%s Inactive%n",connection.getId());
            }

            @Override
            public void exception(Connection connection, Throwable throwable) {
                throwable.printStackTrace();
            }
        };
    }
```

# Need Help?

We're here to assist you! If you have any questions about using the library, encounter issues, or have suggestions for improvement, don't hesitate to reach out.

### Here are some ways we can help:
- **Questions:** Feel free to open an issue and ask how to achieve something specific with the library.
- **Problems:** If you encounter any difficulties, please open an issue to describe the problem and we'll be happy to assist.
- **Improvements:** We value your feedback! If you have suggestions on how to make the documentation clearer or more helpful, please open an issue and share your thoughts.
