package com.badminton.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationDTO {
    private Long id;
    private Long userId;
    private Long targetId;
    private String targetUsername;
    private String targetRealName;
    private String targetAvatar;
    private String lastMessage;
    private LocalDateTime lastTime;
    private Integer unreadCount;
}