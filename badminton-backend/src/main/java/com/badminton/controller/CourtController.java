package com.badminton.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.badminton.common.Constants;
import com.badminton.common.Result;
import com.badminton.entity.Court;
import com.badminton.service.CourtService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/court")
@RequiredArgsConstructor
public class CourtController {

    private final CourtService courtService;

    /**
     * 获取场地列表
     */
    @GetMapping("/list")
    public Result<Page<Court>> getCourtList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        Page<Court> page = courtService.getCourtList(pageNum, pageSize, type, status);
        return Result.success(page);
    }

    /**
     * 获取场地详情
     */
    @GetMapping("/{id}")
    public Result<Court> getCourtById(@PathVariable Long id) {
        Court court = courtService.getCourtById(id);
        if (court == null) {
            return Result.notFound();
        }
        return Result.success(court);
    }

    /**
     * 获取可用场地列表
     */
    @GetMapping("/available")
    public Result<List<Court>> getAvailableCourts() {
        List<Court> courts = courtService.getAvailableCourts();
        return Result.success(courts);
    }

    /**
     * 获取场地可用时段
     */
    @GetMapping("/available-time")
    public Result<List<Map<String, Object>>> getAvailableTimeSlots(
            @RequestParam Long courtId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Map<String, Object>> timeSlots = courtService.getAvailableTimeSlots(courtId, date);
        return Result.success(timeSlots);
    }

    /**
     * 添加场地
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Court> addCourt(@Validated @RequestBody Court court) {
        Court result = courtService.addCourt(court);
        return Result.success("添加成功", result);
    }

    /**
     * 更新场地
     */
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Court> updateCourt(@Validated @RequestBody Court court) {
        Court result = courtService.updateCourt(court);
        return Result.success("更新成功", result);
    }

    /**
     * 删除场地
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteCourt(@PathVariable Long id) {
        courtService.deleteCourt(id);
        return Result.success("删除成功");
    }
}
