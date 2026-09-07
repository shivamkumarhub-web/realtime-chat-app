package com.shivam.chat.event;

import com.shivam.chat.service.PresenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

/**
 * Handles WebSocket disconnect events to update user presence.
 */
@Component
public class WebSocketDisconnectHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketDisconnectHandler.class);
    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketDisconnectHandler(PresenceService presenceService, SimpMessagingTemplate messagingTemplate) {
        this.presenceService = presenceService; this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttrs = accessor.getSessionAttributes();
        if (sessionAttrs != null) {
            String username = (String) sessionAttrs.get("username");
            if (username != null) {
                logger.info("User disconnected: {}", username);
                presenceService.userLeft(username);
                messagingTemplate.convertAndSend("/topic/presence", Map.of("onlineUsers", presenceService.getOnlineUsers()));
            }
        }
    }
}