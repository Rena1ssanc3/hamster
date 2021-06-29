package me.rena1s3.hamster.netty.router.serializer.impl;

import me.rena1s3.hamster.netty.router.MessageFrame;
import me.rena1s3.hamster.netty.router.serializer.MessageFrameSerializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class DefaultMessageFrameSerializer implements MessageFrameSerializer {

    @Override
    public byte[] serialize(MessageFrame messageFrame) {
        String senderAddress = messageFrame.getSenderAddress();
        String receiverAddress = messageFrame.getReceiverAddress();
        byte[] senderAddressBytes = senderAddress.getBytes(StandardCharsets.UTF_8);
        byte[] receiverAddressBytes = receiverAddress.getBytes(StandardCharsets.UTF_8);
        byte[] contentBytes = messageFrame.getContent();
        int senderAddressBytesLength = senderAddressBytes.length;
        int receiverAddressBytesLength = receiverAddressBytes.length;
        int contentBytesLength = contentBytes.length;
        int prepareLength = 12 + senderAddressBytesLength + receiverAddressBytesLength + contentBytesLength;
        byte[] result = new byte[prepareLength];
        byte[] senderAddressBytesLengthBytes = parseIntToByteArray(senderAddressBytesLength);
        byte[] receiverAddressBytesLengthBytes = parseIntToByteArray(receiverAddressBytesLength);
        byte[] contentBytesLengthBytes = parseIntToByteArray(contentBytesLength);
        writeBytes(result, senderAddressBytesLengthBytes, receiverAddressBytesLengthBytes, contentBytesLengthBytes, senderAddressBytes, receiverAddressBytes, contentBytes);
        return result;
    }

    private byte[] parseIntToByteArray(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    private int readIntFromByteArray(byte[] bytes) {
        return ByteBuffer.allocate(4).put(bytes).rewind().getInt();
    }

    @Override
    public MessageFrame deserialize(byte[] bytes) {
        byte[][] split = readBytes(bytes, 4, 4, 4);
        int senderAddressLength = readIntFromByteArray(split[0]);
        int receiverAddressLength = readIntFromByteArray(split[1]);
        int contentLength = readIntFromByteArray(split[2]);
        split = readBytes(bytes, 4, 4, 4, senderAddressLength, receiverAddressLength, contentLength);
        String senderAddress = new String(split[3]);
        String receiverAddress = new String(split[4]);
        byte[] contentBytes = split[5];
        MessageFrame messageFrame = new MessageFrame();
        messageFrame.setSenderAddress(senderAddress);
        messageFrame.setReceiverAddress(receiverAddress);
        messageFrame.setContent(contentBytes);
        return messageFrame;
    }

    private void writeBytes(byte[] dest, byte[]... src) {
        int offset = 0;
        for (byte[] field : src) {
            for (byte b : field) {
                dest[offset] = b;
                offset++;
            }
        }
    }

    private byte[][] readBytes(byte[] src, int... lens) {
        byte[][] res = new byte[lens.length][];
        int offset = 0;
        int seq = 0;
        for (int len : lens) {
            byte[] part = new byte[len];
            for (int i = 0; i < len; i++) {
                part[i] = src[offset];
                offset++;
            }
            res[seq] = part;
            seq++;
        }
        return res;
    }

}
