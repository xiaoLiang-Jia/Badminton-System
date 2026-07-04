package com.badminton.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.badminton.common.Result;
import com.badminton.dto.ForumCreateRequest;
import com.badminton.entity.Comment;
import com.badminton.entity.Forum;
import com.badminton.service.ForumService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
@RequiredArgsConstructor
public class ForumController {

    private final ForumService forumService;

    /**
     * 获取帖子列表
     */
    @GetMapping("/list")
    public Result<Page<Forum>> getForumList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) Integer status) {
        Page<Forum> page = forumService.getForumList(pageNum, pageSize, category, status);
        return Result.success(page);
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/{id}")
    public Result<Forum> getForumById(@PathVariable Long id, Authentication authentication) {
        Long currentUserId = null;
        if (authentication != null) {
            currentUserId = (Long) authentication.getDetails();
        }
        Forum forum = forumService.getForumById(id, currentUserId);
        return Result.success(forum);
    }

    /**
     * 发布帖子
     */
    @PostMapping("/publish")
    public Result<Forum> publishForum(Authentication authentication,
                                        @Validated @RequestBody ForumCreateRequest request) {
        Long userId = (Long) authentication.getDetails();
        Forum forum = forumService.publishForum(userId, request);
        return Result.success("发布成功", forum);
    }

    /**
     * 更新帖子
     */
    @PutMapping("/update")
    public Result<Forum> updateForum(Authentication authentication,
                                       @Validated @RequestBody Forum forum) {
        Long userId = (Long) authentication.getDetails();
        Forum result = forumService.updateForum(userId, forum);
        return Result.success("更新成功", result);
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteForum(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getDetails();
        forumService.deleteForum(userId, id);
        return Result.success("删除成功");
    }

    /**
     * 点赞帖子
     */
    @PostMapping("/like/{id}")
    public Result<?> likeForum(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getDetails();
        forumService.likeForum(userId, id);
        return Result.success("点赞成功");
    }

    /**
     * 取消点赞
     */
    @PostMapping("/unlike/{id}")
    public Result<?> unlikeForum(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getDetails();
        forumService.unlikeForum(userId, id);
        return Result.success("取消成功");
    }

    /**
     * 获取帖子评论列表
     */
    @GetMapping("/comments/{id}")
    public Result<List<Comment>> getForumComments(@PathVariable Long id) {
        List<Comment> comments = forumService.getForumComments(id);
        return Result.success(comments);
    }

    /**
     * 发表评论
     */
    @PostMapping("/comment")
    public Result<Comment> addComment(Authentication authentication,
                                       @RequestParam Long forumId,
                                       @RequestParam String content,
                                       @RequestParam(required = false) Long parentId) {
        Long userId = (Long) authentication.getDetails();
        Comment comment = forumService.addComment(userId, forumId, content, parentId);
        return Result.success("评论成功", comment);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comment/{id}")
    public Result<?> deleteComment(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getDetails();
        forumService.deleteComment(userId, id);
        return Result.success("删除成功");
    }
}
