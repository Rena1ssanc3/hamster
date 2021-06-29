package me.rena1s3.hamster.netty.router.serializer.impl;

import com.alibaba.fastjson.JSONObject;
import me.rena1s3.hamster.netty.router.MessageFrame;
import me.rena1s3.hamster.netty.router.serializer.MessageFrameSerializer;

public class DefaultJsonSerializer implements MessageFrameSerializer {

    @Override
    public byte[] serialize(MessageFrame messageFrame) {
        return JSONObject.toJSONBytes(messageFrame);
    }

    @Override
    public MessageFrame deserialize(byte[] bytes) {
        return JSONObject.parseObject(bytes, MessageFrame.class);
    }

}
