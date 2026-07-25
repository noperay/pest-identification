package com.zx.insectdetection.service.Impl;

import com.zx.insectdetection.entity.chat.Chat;
import com.zx.insectdetection.entity.chat.ChatMessage;
import com.zx.insectdetection.mapper.ChatMapper;
import com.zx.insectdetection.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private ChatMapper chatMapper;
    @Override
    public List<Chat> getUserChats(Integer userId) {
        return chatMapper.getUserChats(userId);
    }

    @Override
    public void addChat(Chat chat) {
        chat.setCreateTime(LocalDateTime.now());
        chatMapper.addChat(chat);
    }

    @Override
    public void deleteChat(Integer chatId) {
        chatMapper.deleteChat(chatId);
    }

    @Override
    public void addUsersMessage(ChatMessage chatMessage) {
        chatMessage.setCreateTime(LocalDateTime.now());
        chatMapper.addUsersMessage(chatMessage);
    }

    @Override
    public void deleteUsersMessage(Integer chatId) {
        chatMapper.deleteUsersMessage(chatId);
    }

    @Override
    public List<ChatMessage> getUsersMessages(Integer chatId) {
        return chatMapper.getUsersMessages(chatId);
    }
}
