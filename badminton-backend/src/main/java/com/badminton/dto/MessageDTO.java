package com.badminton.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private Long senderId;
    private String senderUsername;
    private String senderRealName;
    private Long receiverId;
    private String receiverUsername;
    private String receiverRealName;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;
}