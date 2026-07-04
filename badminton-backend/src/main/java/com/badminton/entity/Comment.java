package com.badminton.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long forumId;

    private Long userId;

    private String content;

    private Long parentId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 关联对象
    @TableField(exist = false)
    private User user;

    @TableField(exist = false)
    private Comment parentComment;

    @TableField(exist = false)
    private java.util.List<Comment> replies;
}
