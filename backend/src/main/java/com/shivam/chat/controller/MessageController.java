package com.shivam.chat.controller;

import com.shivam.chat.entity.ChatMessage;
import com.shivam.chat.service.MessageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages")
@SecurityRequirement(name = "Bearer Authentication")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) { this.messageService = messageService; }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ChatMessage>> getMessagesByRoom(@PathVariable String roomId) {
        return ResponseEntity.ok(messageService.getMessagesByRoom(roomId));
    }
}