package me.rena1s3.hamster.netty.router;

import lombok.Data;

@Data
public class MessageFrame {

    private String senderAddress;

    private String receiverAddress;

    private byte[] content;

}
