package com.badminton.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.badminton.dto.ForumCreateRequest;
import com.badminton.entity.Comment;
import com.badminton.entity.Forum;

import java.util.List;

public interface ForumService extends IService<Forum> {

    /**
     * 获取帖子列表
     */
    Page<Forum> getForumList(Integer pageNum, Integer pageSize, Integer category, Integer status);

    /**
     * 获取帖子详情
     */
    Forum getForumById(Long id, Long currentUserId);

    /**
     * 发布帖子
     */
    Forum publishForum(Long userId, ForumCreateRequest request);

    /**
     * 更新帖子
     */
    Forum updateForum(Long userId, Forum forum);

    /**
     * 删除帖子
     */
    void deleteForum(Long userId, Long id);

    /**
     * 点赞帖子
     */
    void likeForum(Long userId, Long id);

    /**
     * 取消点赞
     */
    void unlikeForum(Long userId, Long id);

    /**
     * 获取帖子评论列表
     */
    List<Comment> getForumComments(Long forumId);

    /**
     * 发表评论
     */
    Comment addComment(Long userId, Long forumId, String content, Long parentId);

    /**
     * 删除评论
     */
    void deleteComment(Long userId, Long id);
}
