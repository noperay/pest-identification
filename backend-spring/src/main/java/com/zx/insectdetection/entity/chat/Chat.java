package com.zx.insectdetection.entity.chat;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Chat {
    private Integer id;
    private Integer senderId;
    private Integer receiverId;
    private LocalDateTime createTime;
}
