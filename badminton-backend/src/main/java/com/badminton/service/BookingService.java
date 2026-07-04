package com.badminton.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.badminton.dto.BookingCreateRequest;
import com.badminton.entity.Booking;

import java.math.BigDecimal;
import java.util.Map;

public interface BookingService extends IService<Booking> {

    /**
     * 创建预约
     */
    Booking createBooking(Long userId, BookingCreateRequest request);

    /**
     * 获取用户预约列表
     */
    Page<Booking> getUserBookings(Long userId, Integer pageNum, Integer pageSize, Integer status);

    /**
     * 获取预约详情
     */
    Booking getBookingById(Long id);

    /**
     * 取消预约
     */
    void cancelBooking(Long userId, Long id);

    /**
     * 支付预约
     */
    Booking payBooking(Long userId, Long id, Integer paymentMethod);

    /**
     * 完成预约（管理员）
     */
    void completeBooking(Long id);

    /**
     * 获取预约统计
     */
    Map<String, Object> getBookingStats();

    /**
     * 检查场地是否可预约
     */
    boolean checkCourtAvailable(Long courtId, String bookingDate, String startTime, String endTime);
}
