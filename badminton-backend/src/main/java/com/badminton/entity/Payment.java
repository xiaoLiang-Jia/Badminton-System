package com.badminton.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment")
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long bookingId;

    private Long courseId;

    private Long userId;

    private BigDecimal amount;

    private Integer paymentMethod;

    private String tradeNo;

    private Integer status;

    private LocalDateTime paymentTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 关联对象
    @TableField(exist = false)
    private Booking booking;

    @TableField(exist = false)
    private Course course;
}
