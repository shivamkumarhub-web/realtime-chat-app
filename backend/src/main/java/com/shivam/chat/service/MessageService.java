package com.shivam.chat.service;

import com.shivam.chat.entity.ChatMessage;
import com.shivam.chat.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {
    private final ChatMessageRepository messageRepository;

    public MessageService(ChatMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional
    public ChatMessage saveMessage(ChatMessage message) {
        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getMessagesByRoom(String roomId) {
        return messageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
    }
}