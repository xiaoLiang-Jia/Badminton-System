package com.badminton.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.badminton.dto.ConversationDTO;
import com.badminton.dto.MessageDTO;
import com.badminton.entity.Message;

import java.util.List;

public interface MessageService extends IService<Message> {

    /**
     * 发送消息
     */
    MessageDTO sendMessage(Long senderId, Long receiverId, String content);

    /**
     * 获取会话列表
     */
    List<ConversationDTO> getConversations(Long userId);

    /**
     * 获取与某人的聊天记录
     */
    List<MessageDTO> getMessages(Long userId, Long targetId);

    /**
     * 标记消息为已读
     */
    void markAsRead(Long userId, Long targetId);

    /**
     * 获取未读消息数
     */
    int getUnreadCount(Long userId);
}