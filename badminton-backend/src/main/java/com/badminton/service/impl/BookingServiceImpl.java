package com.badminton.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.badminton.common.BusinessException;
import com.badminton.common.Constants;
import com.badminton.dto.BookingCreateRequest;
import com.badminton.entity.Booking;
import com.badminton.entity.Court;
import com.badminton.entity.Payment;
import com.badminton.entity.User;
import com.badminton.mapper.BookingMapper;
import com.badminton.mapper.CourtMapper;
import com.badminton.mapper.PaymentMapper;
import com.badminton.mapper.UserMapper;
import com.badminton.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl extends ServiceImpl<BookingMapper, Booking> implements BookingService {

    private final CourtMapper courtMapper;
    private final UserMapper userMapper;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public Booking createBooking(Long userId, BookingCreateRequest request) {
        // 获取场地信息
        Court court = courtMapper.selectById(request.getCourtId());
        if (court == null) {
            throw new BusinessException("场地不存在");
        }
        if (court.getStatus() != Constants.COURT_STATUS_AVAILABLE) {
            throw new BusinessException("该场地暂不可预约");
        }

        // 解析日期和时间
        LocalDate bookingDate = LocalDate.parse(request.getBookingDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime startTime = LocalTime.parse(request.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime endTime = LocalTime.parse(request.getEndTime(), DateTimeFormatter.ofPattern("HH:mm"));

        // 检查时间是否有效
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new BusinessException("结束时间必须大于开始时间");
        }

        // 检查场地是否已被预约
        if (!checkCourtAvailable(request.getCourtId(), request.getBookingDate(), request.getStartTime(), request.getEndTime())) {
            throw new BusinessException("该时段已被预约");
        }

        // 计算价格
        long hours = Duration.between(startTime, endTime).toHours();
        BigDecimal totalPrice = court.getPrice().multiply(BigDecimal.valueOf(hours));

        // 创建预约
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setCourtId(request.getCourtId());
        booking.setBookingDate(bookingDate);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setStatus(Constants.BOOKING_STATUS_PENDING);
        booking.setTotalPrice(totalPrice);
        booking.setRemark(request.getRemark());

        this.save(booking);

        // 关联查询
        booking.setCourt(court);
        User user = userMapper.selectById(userId);
        booking.setUser(user);

        return booking;
    }

    @Override
    public Page<Booking> getUserBookings(Long userId, Integer pageNum, Integer pageSize, Integer status) {
        Page<Booking> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Booking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Booking::getUserId, userId);
        if (status != null) {
            wrapper.eq(Booking::getStatus, status);
        }
        wrapper.orderByDesc(Booking::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public Booking getBookingById(Long id) {
        Booking booking = this.getById(id);
        if (booking != null) {
            booking.setCourt(courtMapper.selectById(booking.getCourtId()));
            booking.setUser(userMapper.selectById(booking.getUserId()));
        }
        return booking;
    }

    @Override
    @Transactional
    public void cancelBooking(Long userId, Long id) {
        Booking booking = this.getById(id);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }
        if (!booking.getUserId().equals(userId)) {
            throw new BusinessException("无权取消该预约");
        }
        if (booking.getStatus() != Constants.BOOKING_STATUS_PENDING) {
            throw new BusinessException("该预约无法取消");
        }

        booking.setStatus(Constants.BOOKING_STATUS_CANCELLED);
        this.updateById(booking);
    }

    @Override
    @Transactional
    public Booking payBooking(Long userId, Long id, Integer paymentMethod) {
        Booking booking = this.getById(id);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }
        if (!booking.getUserId().equals(userId)) {
            throw new BusinessException("无权支付该预约");
        }
        if (booking.getStatus() != Constants.BOOKING_STATUS_PENDING) {
            throw new BusinessException("该预约已支付");
        }

        User user = userMapper.selectById(userId);

        // 处理支付方式
        if (paymentMethod == Constants.PAYMENT_METHOD_BALANCE) {
            // 余额支付
            if (user.getBalance().compareTo(booking.getTotalPrice()) < 0) {
                throw new BusinessException("余额不足");
            }
            // 扣减余额
            user.setBalance(user.getBalance().subtract(booking.getTotalPrice()));
            userMapper.updateById(user);
        }

        // 创建支付记录
        Payment payment = new Payment();
        payment.setBookingId(booking.getId());
        payment.setUserId(userId);
        payment.setAmount(booking.getTotalPrice());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(Constants.PAYMENT_STATUS_PAID);
        payment.setPaymentTime(LocalDateTime.now());
        paymentMapper.insert(payment);

        // 更新预约状态
        booking.setStatus(Constants.BOOKING_STATUS_PAID);
        this.updateById(booking);

        return this.getBookingById(id);
    }

    @Override
    @Transactional
    public void completeBooking(Long id) {
        Booking booking = this.getById(id);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }
        if (booking.getStatus() != Constants.BOOKING_STATUS_PAID) {
            throw new BusinessException("只有已支付的预约才能完成");
        }
        booking.setStatus(Constants.BOOKING_STATUS_COMPLETED);
        this.updateById(booking);
    }

    @Override
    public Map<String, Object> getBookingStats() {
        Map<String, Object> stats = new HashMap<>();

        // 总预约数
        long totalBookings = this.count();
        stats.put("totalBookings", totalBookings);

        // 待支付数
        LambdaQueryWrapper<Booking> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(Booking::getStatus, Constants.BOOKING_STATUS_PENDING);
        stats.put("pendingBookings", this.count(pendingWrapper));

        // 已支付数
        LambdaQueryWrapper<Booking> paidWrapper = new LambdaQueryWrapper<>();
        paidWrapper.eq(Booking::getStatus, Constants.BOOKING_STATUS_PAID);
        stats.put("paidBookings", this.count(paidWrapper));

        // 已完成数
        LambdaQueryWrapper<Booking> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(Booking::getStatus, Constants.BOOKING_STATUS_COMPLETED);
        stats.put("completedBookings", this.count(completedWrapper));

        // 今日预约数
        LambdaQueryWrapper<Booking> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(Booking::getBookingDate, LocalDate.now());
        stats.put("todayBookings", this.count(todayWrapper));

        return stats;
    }

    @Override
    public boolean checkCourtAvailable(Long courtId, String bookingDate, String startTime, String endTime) {
        LocalDate date = LocalDate.parse(bookingDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));

        // 检查是否有冲突的预约
        LambdaQueryWrapper<Booking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Booking::getCourtId, courtId)
                .eq(Booking::getBookingDate, date)
                .in(Booking::getStatus, Constants.BOOKING_STATUS_PENDING, Constants.BOOKING_STATUS_PAID)
                .and(w -> w
                        .le(Booking::getStartTime, start)
                        .ge(Booking::getEndTime, end)
                        .or()
                        .and(w1 -> w1
                                .le(Booking::getStartTime, start)
                                .gt(Booking::getEndTime, start)
                                .lt(Booking::getEndTime, end))
                        .or()
                        .and(w2 -> w2
                                .gt(Booking::getStartTime, start)
                                .lt(Booking::getStartTime, end)
                                .le(Booking::getEndTime, end))
                        .or()
                        .and(w3 -> w3
                                .gt(Booking::getStartTime, start)
                                .lt(Booking::getEndTime, end))
                );

        return this.count(wrapper) == 0;
    }
}
