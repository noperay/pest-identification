package com.zx.insectdetection.entity.chat;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessage {
    private Integer id;
    private Integer chatId;
    private Integer senderId;
    private String senderName;
    private Integer receiverId;
    private String receiverName;
    private String message;
    private LocalDateTime createTime;
}
