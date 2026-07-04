package com.badminton.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.badminton.dto.LoginRequest;
import com.badminton.dto.RegisterRequest;
import com.badminton.dto.UserUpdateRequest;
import com.badminton.entity.User;

import java.math.BigDecimal;

public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    User register(RegisterRequest request);

    /**
     * 用户登录
     */
    String login(LoginRequest request);

    /**
     * 获取当前用户信息
     */
    User getCurrentUser(Long userId);

    /**
     * 更新用户信息
     */
    User updateUser(Long userId, UserUpdateRequest request);

    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 充值余额
     */
    User recharge(Long userId, BigDecimal amount);

    /**
     * 检查用户名是否存在
     */
    boolean checkUsername(String username);

    /**
     * 根据用户名获取用户信息
     */
    User getCurrentUserByUsername(String username);

    /**
     * 获取用户列表
     */
    Page<User> getUserList(Integer pageNum, Integer pageSize, Integer role, Integer status);

    /**
     * 管理员更新用户信息
     */
    User adminUpdateUser(Long userId, java.util.Map<String, Object> request);

    /**
     * 管理员创建用户
     */
    User adminCreateUser(java.util.Map<String, Object> request);

    /**
     * 管理员删除用户
     */
    void adminDeleteUser(Long userId);
}
