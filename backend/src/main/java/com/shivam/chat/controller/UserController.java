package com.shivam.chat.controller;

import com.shivam.chat.service.PresenceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    private final PresenceService presenceService;

    public UserController(PresenceService presenceService) { this.presenceService = presenceService; }

    @GetMapping("/online")
    public ResponseEntity<Map<String, Set<String>>> getOnlineUsers() {
        return ResponseEntity.ok(Map.of("onlineUsers", presenceService.getOnlineUsers()));
    }
}