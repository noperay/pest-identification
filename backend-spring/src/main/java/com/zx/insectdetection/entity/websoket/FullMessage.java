package com.zx.insectdetection.entity.websoket;

import lombok.Data;


@Data
public class FullMessage {
    private Integer channelId;
    private Message message;
}
