package com.badminton.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("forum")
public class Forum implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String content;

    private String imageUrl;

    private Integer category;

    private Integer likes;

    private Integer views;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 关联对象
    @TableField(exist = false)
    private User user;

    @TableField(exist = false)
    private Boolean liked;  // 当前用户是否点赞

    @TableField(exist = false)
    private Integer commentCount;  // 评论数
}
