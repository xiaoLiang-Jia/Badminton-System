package com.badminton.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("course")
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long coachId;

    private Long courtId;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer maxStudents;

    private Integer currentNum;

    private Integer duration;

    private String schedule;

    private Integer level;

    private Integer status;

    private String imageUrl;

    private Integer lessonCount;

    private String curriculum;

    private String targetAudience;

    private String graduationStandard;

    private String trainingFocus;

    private String frequency;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 关联对象
    @TableField(exist = false)
    private User coach;

    @TableField(exist = false)
    private Court court;
}
