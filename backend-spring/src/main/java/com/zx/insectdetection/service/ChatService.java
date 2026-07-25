package com.zx.insectdetection.service;

import com.zx.insectdetection.entity.chat.Chat;
import com.zx.insectdetection.entity.chat.ChatMessage;

import java.util.List;

public interface ChatService {
    List<Chat> getUserChats(Integer userId);

    void addChat(Chat chat);

    void deleteChat(Integer chatId);

    void addUsersMessage(ChatMessage chatMessage);

    void deleteUsersMessage(Integer chatId);

    List<ChatMessage> getUsersMessages(Integer chatId);
}
