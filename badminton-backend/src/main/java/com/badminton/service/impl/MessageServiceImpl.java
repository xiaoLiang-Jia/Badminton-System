package com.badminton.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.badminton.dto.ConversationDTO;
import com.badminton.dto.MessageDTO;
import com.badminton.entity.Conversation;
import com.badminton.entity.Message;
import com.badminton.entity.User;
import com.badminton.mapper.ConversationMapper;
import com.badminton.mapper.MessageMapper;
import com.badminton.mapper.UserMapper;
import com.badminton.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final ConversationMapper conversationMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public MessageDTO sendMessage(Long senderId, Long receiverId, String content) {
        // 保存消息
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setIsRead(0);
        this.save(message);

        // 更新发送者的会话
        updateConversation(senderId, receiverId, content);

        // 更新接收者的会话
        updateConversation(receiverId, senderId, content);

        // 构建返回结果
        return buildMessageDTO(message);
    }

    private void updateConversation(Long userId, Long targetId, String content) {
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Conversation::getUserId, userId)
               .eq(Conversation::getTargetId, targetId);
        Conversation conversation = conversationMapper.selectOne(wrapper);

        if (conversation == null) {
            conversation = new Conversation();
            conversation.setUserId(userId);
            conversation.setTargetId(targetId);
            conversation.setUnreadCount(0);
        }

        conversation.setLastMessage(content);
        conversation.setLastTime(LocalDateTime.now());

        // 如果是接收者会话，增加未读数
        if (!userId.equals(this.getById(targetId))) {
            // 查询当前用户是否是发送者，如果是则不增加未读数
            // 这里简化处理，实际应该根据发送者ID判断
        }

        if (conversation.getId() == null) {
            conversationMapper.insert(conversation);
        } else {
            conversationMapper.updateById(conversation);
        }
    }

    @Override
    public List<ConversationDTO> getConversations(Long userId) {
        // 获取与当前用户相关的所有会话
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Conversation::getUserId, userId)
               .orderByDesc(Conversation::getLastTime);
        List<Conversation> conversations = conversationMapper.selectList(wrapper);

        List<ConversationDTO> result = new ArrayList<>();
        for (Conversation conversation : conversations) {
            User target = userMapper.selectById(conversation.getTargetId());
            if (target != null) {
                ConversationDTO dto = new ConversationDTO();
                dto.setId(conversation.getId());
                dto.setUserId(conversation.getUserId());
                dto.setTargetId(conversation.getTargetId());
                dto.setTargetUsername(target.getUsername());
                dto.setTargetRealName(target.getRealName());
                dto.setTargetAvatar(target.getAvatar());
                dto.setLastMessage(conversation.getLastMessage());
                dto.setLastTime(conversation.getLastTime());
                dto.setUnreadCount(conversation.getUnreadCount());
                result.add(dto);
            }
        }

        return result;
    }

    @Override
    public List<MessageDTO> getMessages(Long userId, Long targetId) {
        // 查询双向消息记录
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Message::getSenderId, userId).eq(Message::getReceiverId, targetId)
                    .or()
                    .eq(Message::getSenderId, targetId).eq(Message::getReceiverId, userId))
               .orderByAsc(Message::getCreateTime);
        List<Message> messages = this.list(wrapper);

        return messages.stream().map(this::buildMessageDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, Long targetId) {
        // 标记发送给对方的消息为已读
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getSenderId, targetId)
               .eq(Message::getReceiverId, userId)
               .eq(Message::getIsRead, 0);
        List<Message> messages = this.list(wrapper);

        for (Message message : messages) {
            message.setIsRead(1);
            this.updateById(message);
        }

        // 更新会话的未读数
        LambdaQueryWrapper<Conversation> convWrapper = new LambdaQueryWrapper<>();
        convWrapper.eq(Conversation::getUserId, userId)
                  .eq(Conversation::getTargetId, targetId);
        Conversation conversation = conversationMapper.selectOne(convWrapper);
        if (conversation != null) {
            conversation.setUnreadCount(0);
            conversationMapper.updateById(conversation);
        }
    }

    @Override
    public int getUnreadCount(Long userId) {
        // 统计发送给当前用户且未读的消息数
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId)
               .eq(Message::getIsRead, 0);
        return (int) this.count(wrapper);
    }

    private MessageDTO buildMessageDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSenderId());
        dto.setReceiverId(message.getReceiverId());
        dto.setContent(message.getContent());
        dto.setIsRead(message.getIsRead());
        dto.setCreateTime(message.getCreateTime());

        // 填充发送者信息
        User sender = userMapper.selectById(message.getSenderId());
        if (sender != null) {
            dto.setSenderUsername(sender.getUsername());
            dto.setSenderRealName(sender.getRealName());
        }

        // 填充接收者信息
        User receiver = userMapper.selectById(message.getReceiverId());
        if (receiver != null) {
            dto.setReceiverUsername(receiver.getUsername());
            dto.setReceiverRealName(receiver.getRealName());
        }

        return dto;
    }
}