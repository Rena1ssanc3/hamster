package me.rena1s3.hamster.netty.router.serializer;

import me.rena1s3.hamster.netty.router.MessageFrame;

public interface MessageFrameSerializer {

    byte[] serialize(MessageFrame messageFrame);

    MessageFrame deserialize(byte[] bytes);

}
