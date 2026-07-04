package com.badminton.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.badminton.common.BusinessException;
import com.badminton.common.Constants;
import com.badminton.entity.Booking;
import com.badminton.entity.Court;
import com.badminton.mapper.BookingMapper;
import com.badminton.mapper.CourtMapper;
import com.badminton.service.CourtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourtServiceImpl extends ServiceImpl<CourtMapper, Court> implements CourtService {

    private final BookingMapper bookingMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final int OPEN_HOUR = 9;
    private static final int CLOSE_HOUR = 22;
    private static final int TIME_SLOT_HOURS = 1;

    @Override
    public Page<Court> getCourtList(Integer pageNum, Integer pageSize, Integer type, Integer status) {
        Page<Court> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Court> wrapper = new LambdaQueryWrapper<>();

        if (type != null) {
            wrapper.eq(Court::getType, type);
        }
        if (status != null) {
            wrapper.eq(Court::getStatus, status);
        }

        wrapper.orderByAsc(Court::getId);
        return this.page(page, wrapper);
    }

    @Override
    public Court getCourtById(Long id) {
        // 先从Redis获取
        try {
            String key = "court:" + id;
            Court cachedCourt = (Court) redisTemplate.opsForValue().get(key);
            if (cachedCourt != null) {
                return cachedCourt;
            }

            Court court = this.getById(id);
            if (court != null) {
                // 存入Redis
                redisTemplate.opsForValue().set(key, court, 1, TimeUnit.HOURS);
            }
            return court;
        } catch (Exception e) {
            // Redis不可用时，直接从数据库获取
            return this.getById(id);
        }
    }

    @Override
    public List<Court> getAvailableCourts() {
        LambdaQueryWrapper<Court> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Court::getStatus, Constants.COURT_STATUS_AVAILABLE);
        wrapper.orderByAsc(Court::getId);
        return this.list(wrapper);
    }

    @Override
    public List<Map<String, Object>> getAvailableTimeSlots(Long courtId, LocalDate date) {
        // 查询该场地当天的所有预约
        LambdaQueryWrapper<Booking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Booking::getCourtId, courtId)
                .eq(Booking::getBookingDate, date)
                .in(Booking::getStatus, Constants.BOOKING_STATUS_PAID, Constants.BOOKING_STATUS_PENDING);
        List<Booking> bookings = bookingMapper.selectList(wrapper);

        // 已预约的时间段
        Set<String> bookedSlots = bookings.stream()
                .map(b -> b.getStartTime().toString())
                .collect(Collectors.toSet());

        // 生成所有可用时段
        List<Map<String, Object>> timeSlots = new ArrayList<>();
        for (int hour = OPEN_HOUR; hour < CLOSE_HOUR; hour++) {
            String timeStr = String.format("%02d:00", hour);
            Map<String, Object> slot = new HashMap<>();
            slot.put("time", timeStr);
            slot.put("available", !bookedSlots.contains(timeStr));
            timeSlots.add(slot);
        }

        return timeSlots;
    }

    @Override
    @Transactional
    public Court addCourt(Court court) {
        court.setStatus(Constants.COURT_STATUS_AVAILABLE);
        this.save(court);
        return court;
    }

    @Override
    @Transactional
    public Court updateCourt(Court court) {
        if (court.getId() == null) {
            throw new BusinessException("场地ID不能为空");
        }
        Court existingCourt = this.getById(court.getId());
        if (existingCourt == null) {
            throw new BusinessException("场地不存在");
        }
        this.updateById(court);
        // 删除缓存
        try {
            redisTemplate.delete("court:" + court.getId());
        } catch (Exception e) {
            // Redis不可用时忽略
        }
        return this.getById(court.getId());
    }

    @Override
    @Transactional
    public void deleteCourt(Long id) {
        Court court = this.getById(id);
        if (court == null) {
            throw new BusinessException("场地不存在");
        }
        // 检查是否有未完成的预约
        LambdaQueryWrapper<Booking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Booking::getCourtId, id)
                .in(Booking::getStatus, Constants.BOOKING_STATUS_PENDING, Constants.BOOKING_STATUS_PAID);
        long count = bookingMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该场地存在未完成的预约，无法删除");
        }
        this.removeById(id);
        try {
            redisTemplate.delete("court:" + id);
        } catch (Exception e) {
            // Redis不可用时忽略
        }
    }
}
