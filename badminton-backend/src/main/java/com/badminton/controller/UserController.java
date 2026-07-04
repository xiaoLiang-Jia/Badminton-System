package com.badminton.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.badminton.common.Result;
import com.badminton.dto.LoginRequest;
import com.badminton.dto.RegisterRequest;
import com.badminton.dto.UserUpdateRequest;
import com.badminton.entity.User;
import com.badminton.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<User> register(@Validated @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        user.setPassword(null);
        return Result.success("注册成功", user);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginRequest request) {
        String token = userService.login(request);
        User user = userService.getCurrentUserByUsername(request.getUsername());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        return Result.success("登录成功", data);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<User> getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        User user = userService.getCurrentUser(userId);
        return Result.success(user);
    }

    /**
     * 获取指定用户信息
     */
    @GetMapping("/info/{id}")
    public Result<User> getUserInfo(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.notFound();
        }
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<User> updateUser(Authentication authentication,
                                   @Validated @RequestBody UserUpdateRequest request) {
        Long userId = (Long) authentication.getDetails();
        User user = userService.updateUser(userId, request);
        return Result.success("更新成功", user);
    }

    /**
     * 修改密码
     */
    @PostMapping("/changePassword")
    public Result<?> changePassword(Authentication authentication,
                                     @RequestParam String oldPassword,
                                     @RequestParam String newPassword) {
        Long userId = (Long) authentication.getDetails();
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功");
    }

    /**
     * 获取余额
     */
    @GetMapping("/balance")
    public Result<BigDecimal> getBalance(Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        User user = userService.getCurrentUser(userId);
        return Result.success(user.getBalance());
    }

    /**
     * 充值
     */
    @PostMapping("/recharge")
    public Result<User> recharge(Authentication authentication, @RequestParam BigDecimal amount) {
        Long userId = (Long) authentication.getDetails();
        User user = userService.recharge(userId, amount);
        return Result.success("充值成功", user);
    }

    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check/{username}")
    public Result<Boolean> checkUsername(@PathVariable String username) {
        boolean exists = userService.checkUsername(username);
        return Result.success(exists);
    }

    /**
     * 获取用户列表（管理员）
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) Integer status) {
        Page<User> page = userService.getUserList(pageNum, pageSize, role, status);
        // 隐藏密码
        page.getRecords().forEach(user -> user.setPassword(null));
        return Result.success(page);
    }

    /**
     * 管理员更新用户信息
     */
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> adminUpdateUser(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        User user = userService.adminUpdateUser(id, request);
        return Result.success("更新成功", user);
    }

    /**
     * 管理员创建用户
     */
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> adminCreateUser(@RequestBody Map<String, Object> request) {
        User user = userService.adminCreateUser(request);
        return Result.success("创建成功", user);
    }

    /**
     * 管理员删除用户
     */
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> adminDeleteUser(@PathVariable Long id) {
        userService.adminDeleteUser(id);
        return Result.success("删除成功");
    }
}
