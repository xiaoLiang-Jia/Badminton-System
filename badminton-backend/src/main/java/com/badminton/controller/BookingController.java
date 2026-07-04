package com.badminton.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.badminton.common.Result;
import com.badminton.dto.BookingCreateRequest;
import com.badminton.entity.Booking;
import com.badminton.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * 创建预约
     */
    @PostMapping("/create")
    public Result<Booking> createBooking(Authentication authentication,
                                          @Validated @RequestBody BookingCreateRequest request) {
        Long userId = (Long) authentication.getDetails();
        Booking booking = bookingService.createBooking(userId, request);
        return Result.success("预约创建成功", booking);
    }

    /**
     * 获取用户预约列表
     */
    @GetMapping("/list")
    public Result<Page<Booking>> getUserBookings(
            Authentication authentication,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        Long userId = (Long) authentication.getDetails();
        Page<Booking> page = bookingService.getUserBookings(userId, pageNum, pageSize, status);
        return Result.success(page);
    }

    /**
     * 获取预约详情
     */
    @GetMapping("/{id}")
    public Result<Booking> getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);
        if (booking == null) {
            return Result.notFound();
        }
        return Result.success(booking);
    }

    /**
     * 取消预约
     */
    @PostMapping("/cancel/{id}")
    public Result<?> cancelBooking(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getDetails();
        bookingService.cancelBooking(userId, id);
        return Result.success("取消成功");
    }

    /**
     * 支付预约
     */
    @PostMapping("/pay/{id}")
    public Result<Booking> payBooking(Authentication authentication,
                                       @PathVariable Long id,
                                       @RequestParam(defaultValue = "3") Integer paymentMethod) {
        Long userId = (Long) authentication.getDetails();
        Booking booking = bookingService.payBooking(userId, id, paymentMethod);
        return Result.success("支付成功", booking);
    }

    /**
     * 完成预约
     */
    @PostMapping("/complete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> completeBooking(@PathVariable Long id) {
        bookingService.completeBooking(id);
        return Result.success("操作成功");
    }

    /**
     * 获取预约统计
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getBookingStats() {
        Map<String, Object> stats = bookingService.getBookingStats();
        return Result.success(stats);
    }

    /**
     * 检查场地是否可预约
     */
    @GetMapping("/check")
    public Result<Boolean> checkCourtAvailable(
            @RequestParam Long courtId,
            @RequestParam String bookingDate,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        boolean available = bookingService.checkCourtAvailable(courtId, bookingDate, startTime, endTime);
        return Result.success(available);
    }
}
