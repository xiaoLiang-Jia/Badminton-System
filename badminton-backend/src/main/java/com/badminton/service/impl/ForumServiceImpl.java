package com.badminton.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.badminton.common.BusinessException;
import com.badminton.common.Constants;
import com.badminton.dto.ForumCreateRequest;
import com.badminton.entity.Comment;
import com.badminton.entity.Forum;
import com.badminton.entity.ForumLike;
import com.badminton.entity.User;
import com.badminton.mapper.CommentMapper;
import com.badminton.mapper.ForumLikeMapper;
import com.badminton.mapper.ForumMapper;
import com.badminton.mapper.UserMapper;
import com.badminton.service.ForumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForumServiceImpl extends ServiceImpl<ForumMapper, Forum> implements ForumService {

    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    private final ForumLikeMapper forumLikeMapper;

    @Override
    public Page<Forum> getForumList(Integer pageNum, Integer pageSize, Integer category, Integer status) {
        Page<Forum> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Forum> wrapper = new LambdaQueryWrapper<>();

        if (category != null) {
            wrapper.eq(Forum::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(Forum::getStatus, status);
        } else {
            wrapper.eq(Forum::getStatus, Constants.FORUM_STATUS_PUBLISHED);
        }

        wrapper.orderByDesc(Forum::getCreateTime);
        Page<Forum> result = this.page(page, wrapper);

        // 填充用户信息和评论数
        result.getRecords().forEach(forum -> {
            User user = userMapper.selectById(forum.getUserId());
            forum.setUser(user);

            // 获取评论数
            LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
            commentWrapper.eq(Comment::getForumId, forum.getId());
            forum.setCommentCount(commentMapper.selectCount(commentWrapper).intValue());
        });

        return result;
    }

    @Override
    public Forum getForumById(Long id, Long currentUserId) {
        Forum forum = this.getById(id);
        if (forum == null) {
            throw new BusinessException("帖子不存在");
        }

        // 增加浏览量
        forum.setViews(forum.getViews() + 1);
        this.updateById(forum);

        // 填充用户信息
        forum.setUser(userMapper.selectById(forum.getUserId()));

        // 检查是否点赞
        if (currentUserId != null) {
            LambdaQueryWrapper<ForumLike> likeWrapper = new LambdaQueryWrapper<>();
            likeWrapper.eq(ForumLike::getForumId, id)
                    .eq(ForumLike::getUserId, currentUserId);
            forum.setLiked(forumLikeMapper.selectCount(likeWrapper) > 0);
        }

        // 获取评论数
        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(Comment::getForumId, forum.getId());
        forum.setCommentCount(commentMapper.selectCount(commentWrapper).intValue());

        return forum;
    }

    @Override
    @Transactional
    public Forum publishForum(Long userId, ForumCreateRequest request) {
        Forum forum = new Forum();
        forum.setUserId(userId);
        forum.setTitle(request.getTitle());
        forum.setContent(request.getContent());
        forum.setImageUrl(request.getImageUrl());
        forum.setCategory(request.getCategory());
        forum.setLikes(0);
        forum.setViews(0);
        forum.setStatus(Constants.FORUM_STATUS_PUBLISHED);

        this.save(forum);
        forum.setUser(userMapper.selectById(userId));
        return forum;
    }

    @Override
    @Transactional
    public Forum updateForum(Long userId, Forum forum) {
        Forum existingForum = this.getById(forum.getId());
        if (existingForum == null) {
            throw new BusinessException("帖子不存在");
        }
        if (!existingForum.getUserId().equals(userId)) {
            throw new BusinessException("无权修改该帖子");
        }

        this.updateById(forum);
        return this.getForumById(forum.getId(), userId);
    }

    @Override
    @Transactional
    public void deleteForum(Long userId, Long id) {
        Forum forum = this.getById(id);
        if (forum == null) {
            throw new BusinessException("帖子不存在");
        }
        if (!forum.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该帖子");
        }

        // 软删除
        forum.setStatus(Constants.FORUM_STATUS_DELETED);
        this.updateById(forum);
    }

    @Override
    @Transactional
    public void likeForum(Long userId, Long id) {
        Forum forum = this.getById(id);
        if (forum == null) {
            throw new BusinessException("帖子不存在");
        }

        // 检查是否已点赞
        LambdaQueryWrapper<ForumLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ForumLike::getForumId, id)
                .eq(ForumLike::getUserId, userId);
        if (forumLikeMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("您已点赞过该帖子");
        }

        // 添加点赞记录
        ForumLike like = new ForumLike();
        like.setForumId(id);
        like.setUserId(userId);
        forumLikeMapper.insert(like);

        // 更新点赞数
        forum.setLikes(forum.getLikes() + 1);
        this.updateById(forum);
    }

    @Override
    @Transactional
    public void unlikeForum(Long userId, Long id) {
        LambdaQueryWrapper<ForumLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ForumLike::getForumId, id)
                .eq(ForumLike::getUserId, userId);
        ForumLike like = forumLikeMapper.selectOne(wrapper);

        if (like == null) {
            throw new BusinessException("您未点赞过该帖子");
        }

        forumLikeMapper.deleteById(like);

        // 更新点赞数
        Forum forum = this.getById(id);
        forum.setLikes(Math.max(0, forum.getLikes() - 1));
        this.updateById(forum);
    }

    @Override
    public List<Comment> getForumComments(Long forumId) {
        // 获取顶级评论
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getForumId, forumId)
                .isNull(Comment::getParentId)
                .orderByAsc(Comment::getCreateTime);
        List<Comment> comments = commentMapper.selectList(wrapper);

        // 填充用户信息和回复
        comments.forEach(comment -> {
            comment.setUser(userMapper.selectById(comment.getUserId()));

            // 获取回复
            LambdaQueryWrapper<Comment> replyWrapper = new LambdaQueryWrapper<>();
            replyWrapper.eq(Comment::getParentId, comment.getId())
                    .orderByAsc(Comment::getCreateTime);
            List<Comment> replies = commentMapper.selectList(replyWrapper);
            replies.forEach(reply -> reply.setUser(userMapper.selectById(reply.getUserId())));
            comment.setReplies(replies);
        });

        return comments;
    }

    @Override
    @Transactional
    public Comment addComment(Long userId, Long forumId, String content, Long parentId) {
        Forum forum = this.getById(forumId);
        if (forum == null) {
            throw new BusinessException("帖子不存在");
        }

        Comment comment = new Comment();
        comment.setForumId(forumId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setParentId(parentId);

        commentMapper.insert(comment);
        comment.setUser(userMapper.selectById(userId));

        return comment;
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该评论");
        }
        commentMapper.deleteById(id);
    }
}
