# hamster
## What is hamster
Project hamster is a simple message router. It's designed for learning how to use netty.  
## What hamster done
In this project, it implements an encoder and decoder base on a custom protocol. The custom protocol is so simple that it doesn't implement any verification, but it works. Client and server can communicate through this protocol.   
For various reason, server need to ensure the client connected is a valid one. So it provides you a chance to do authentication. The default authentication mechanism is very simple too, just need the client to shout "Hamster" out loud.  
As is a router, the client need to declare it's address. The channel registry is so simple that it even cannot handle the duplicate addresses. The router's function is just find out the receiver's address in message and deliver it.  
## How to use
    new HamsterServerStarter().start();
## And then
    ChannelFuture aliceChannelFuture = new HamsterClientStarter().setAddress("Alice").start();
    ChannelFuture bobChannelFuture = new HamsterClientStarter().setAddress("Bob").start();
    ChannelHandler printMessage = new ChannelInboundHandlerAdapter(){
        public void channelRead(ChannelHandlerContext ctx, Object msg){
            System.out.println("RECEIVE MESSAGE FROM "+ ((MessageFrame)msg).getSenderAddress() + " CONTENT:" + new String(((MessageFrame)msg).getContent()));
            }
        }
    bobChannelFuture.channel().pipeline().addLast(printMessage);
    MessageFrame aliceToBob = new MessageFrame();
    aliceToBob.setSenderAddress("Alice");
    aliceToBob.setReceiverAddress("Bob");
    aliceToBob.setContent("Hi I'm Alice. How are you, Bob?".getBytes(StandardCharsets.UTF_8));
    aliceChannelFuture.channel().writeAndFlush(aliceToBob);
