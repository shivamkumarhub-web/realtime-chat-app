package com.shivam.chat.controller;

import com.shivam.chat.dto.ChatMessageDto;
import com.shivam.chat.entity.ChatMessage;
import com.shivam.chat.service.MessageService;
import com.shivam.chat.service.PresenceService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * WebSocket controller handling STOMP messages for chat.
 * Messages sent to /app/chat.* are routed here.
 */
@Controller
public class ChatController {

    private final MessageService messageService;
    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(MessageService messageService, PresenceService presenceService,
                          SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService; this.presenceService = presenceService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/room/general")
    public ChatMessageDto sendMessage(@Payload ChatMessageDto messageDto) {
        // Persist message
        ChatMessage entity = new ChatMessage();
        entity.setSender(messageDto.getSender());
        entity.setContent(messageDto.getContent());
        entity.setRoomId(messageDto.getRoomId() != null ? messageDto.getRoomId() : "general");
        entity.setType(ChatMessage.MessageType.CHAT);
        messageService.saveMessage(entity);
        return messageDto;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/room/general")
    public ChatMessageDto addUser(@Payload ChatMessageDto message, SimpMessageHeaderAccessor headerAccessor) {
        // Add user to session
        if (headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("username", message.getSender());
        }
        presenceService.userJoined(message.getSender());
        message.setContent(message.getSender() + " joined the chat");
        message.setType("JOIN");

        // Broadcast presence update
        messagingTemplate.convertAndSend("/topic/presence", Map.of("onlineUsers", presenceService.getOnlineUsers()));

        return message;
    }
}