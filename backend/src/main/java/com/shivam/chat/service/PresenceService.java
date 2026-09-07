package com.shivam.chat.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks online users in memory using a thread-safe set.
 */
@Service
public class PresenceService {
    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    public void userJoined(String username) {
        onlineUsers.add(username);
    }

    public void userLeft(String username) {
        onlineUsers.remove(username);
    }

    public boolean isOnline(String username) {
        return onlineUsers.contains(username);
    }

    public Set<String> getOnlineUsers() {
        return Set.copyOf(onlineUsers);
    }
}