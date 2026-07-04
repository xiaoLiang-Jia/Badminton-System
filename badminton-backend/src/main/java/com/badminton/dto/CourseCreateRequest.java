package com.badminton.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CourseCreateRequest {
    @NotNull(message = "场地ID不能为空")
    private Long courtId;

    @NotBlank(message = "课程名称不能为空")
    private String name;

    private String description;

    @NotNull(message = "课程价格不能为空")
    private BigDecimal price;

    @NotNull(message = "最大人数不能为空")
    private Integer maxStudents;

    private Integer duration = 60;

    private String schedule;

    @NotNull(message = "难度等级不能为空")
    private Integer level;

    private String imageUrl;

    private Integer lessonCount;

    private String curriculum;

    private String targetAudience;

    private String graduationStandard;

    private String trainingFocus;

    private String frequency;
}
