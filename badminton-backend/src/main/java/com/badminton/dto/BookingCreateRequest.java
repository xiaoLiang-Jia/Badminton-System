package com.badminton.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class BookingCreateRequest {
    @NotNull(message = "场地ID不能为空")
    private Long courtId;

    @NotBlank(message = "预约日期不能为空")
    private String bookingDate;

    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    @NotBlank(message = "结束时间不能为空")
    private String endTime;

    private String remark;
}
