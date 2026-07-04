package com.badminton.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.badminton.entity.Court;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CourtService extends IService<Court> {

    /**
     * 获取场地列表（分页）
     */
    Page<Court> getCourtList(Integer pageNum, Integer pageSize, Integer type, Integer status);

    /**
     * 获取场地详情
     */
    Court getCourtById(Long id);

    /**
     * 获取可用场地列表
     */
    List<Court> getAvailableCourts();

    /**
     * 获取场地可用时段
     */
    List<Map<String, Object>> getAvailableTimeSlots(Long courtId, LocalDate date);

    /**
     * 添加场地
     */
    Court addCourt(Court court);

    /**
     * 更新场地
     */
    Court updateCourt(Court court);

    /**
     * 删除场地
     */
    void deleteCourt(Long id);
}
