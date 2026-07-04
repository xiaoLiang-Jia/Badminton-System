package com.badminton.controller;

import com.badminton.common.Result;
import com.badminton.dto.ConversationDTO;
import com.badminton.dto.MessageDTO;
import com.badminton.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 获取会话列表
     */
    @GetMapping("/conversations")
    public Result<List<ConversationDTO>> getConversations(Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        List<ConversationDTO> conversations = messageService.getConversations(userId);
        return Result.success(conversations);
    }

    /**
     * 获取与某人的聊天记录
     */
    @GetMapping("/{userId}")
    public Result<List<MessageDTO>> getMessages(Authentication authentication, @PathVariable Long userId) {
        Long currentUserId = (Long) authentication.getDetails();
        List<MessageDTO> messages = messageService.getMessages(currentUserId, userId);
        return Result.success(messages);
    }

    /**
     * 发送消息
     */
    @PostMapping
    public Result<MessageDTO> sendMessage(Authentication authentication, @RequestBody Map<String, Object> request) {
        Long senderId = (Long) authentication.getDetails();
        Long receiverId = Long.valueOf(request.get("receiverId").toString());
        String content = request.get("content").toString();

        MessageDTO message = messageService.sendMessage(senderId, receiverId, content);
        return Result.success("发送成功", message);
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/read/{userId}")
    public Result<?> markAsRead(Authentication authentication, @PathVariable Long userId) {
        Long currentUserId = (Long) authentication.getDetails();
        messageService.markAsRead(currentUserId, userId);
        return Result.success("已标记为已读");
    }

    /**
     * 获取未读消息数
     */
    @GetMapping("/unread")
    public Result<Integer> getUnreadCount(Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        int count = messageService.getUnreadCount(userId);
        return Result.success(count);
    }
}