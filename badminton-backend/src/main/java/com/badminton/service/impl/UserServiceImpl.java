package com.badminton.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.badminton.common.BusinessException;
import com.badminton.common.Constants;
import com.badminton.dto.LoginRequest;
import com.badminton.dto.RegisterRequest;
import com.badminton.dto.UserUpdateRequest;
import com.badminton.entity.User;
import com.badminton.mapper.UserMapper;
import com.badminton.service.UserService;
import com.badminton.utils.JwtUtil;
import com.badminton.utils.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        // 检查用户名是否存在
        if (checkUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已被注册
        if (request.getPhone() != null) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, request.getPhone());
            if (this.count(wrapper) > 0) {
                throw new BusinessException("手机号已被注册");
            }
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(Constants.ROLE_USER);
        user.setBalance(BigDecimal.ZERO);
        user.setStatus(Constants.USER_STATUS_NORMAL);
        user.setAvatar("/avatar/default.png");

        this.save(user);
        return user;
    }

    @Override
    public String login(LoginRequest request) {
        // 验证验证码
        String captchaKey = "captcha:" + request.getCaptchaId();
        String cachedCaptcha = (String) redisTemplate.opsForValue().get(captchaKey);
        if (cachedCaptcha == null) {
            throw new BusinessException("验证码已过期，请刷新");
        }
        if (!cachedCaptcha.equalsIgnoreCase(request.getCaptchaCode())) {
            throw new BusinessException("验证码错误");
        }
        // 验证成功后删除验证码
        redisTemplate.delete(captchaKey);

        // 查询用户（支持用户名或手机号登录）
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername())
               .or()
               .eq(User::getPhone, request.getUsername());
        User user = this.getOne(wrapper);

        if (user == null) {
            throw new BusinessException("账号或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == Constants.USER_STATUS_DISABLED) {
            throw new BusinessException("账号已被禁用");
        }

        // 验证密码
        if (!PasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 将Token存入Redis（用于黑名单验证）
        redisTemplate.opsForValue().set("token:" + user.getId(), token, 24, TimeUnit.HOURS);

        return token;
    }

    @Override
    public User getCurrentUser(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 不返回密码
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UserUpdateRequest request) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新用户信息
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getPhone() != null) {
            // 检查手机号是否被其他用户使用
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, request.getPhone()).ne(User::getId, userId);
            if (this.count(wrapper) > 0) {
                throw new BusinessException("手机号已被其他用户使用");
            }
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }

        this.updateById(user);
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!PasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 更新密码
        user.setPassword(PasswordEncoder.encode(newPassword));
        this.updateById(user);

        // 使旧Token失效
        redisTemplate.delete("token:" + userId);
    }

    @Override
    @Transactional
    public User recharge(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }

        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 增加余额
        user.setBalance(user.getBalance().add(amount));
        this.updateById(user);

        user.setPassword(null);
        return user;
    }

    @Override
    public boolean checkUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return this.count(wrapper) > 0;
    }

    @Override
    public User getCurrentUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = this.getOne(wrapper);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public Page<User> getUserList(Integer pageNum, Integer pageSize, Integer role, Integer status) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (role != null) {
            wrapper.eq(User::getRole, role);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }

        wrapper.orderByDesc(User::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public User adminUpdateUser(Long userId, Map<String, Object> request) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (request.containsKey("realName")) {
            user.setRealName((String) request.get("realName"));
        }
        if (request.containsKey("phone")) {
            user.setPhone((String) request.get("phone"));
        }
        if (request.containsKey("email")) {
            user.setEmail((String) request.get("email"));
        }
        if (request.containsKey("role")) {
            user.setRole((Integer) request.get("role"));
        }
        if (request.containsKey("status")) {
            user.setStatus((Integer) request.get("status"));
        }
        if (request.containsKey("balance")) {
            user.setBalance(new BigDecimal(request.get("balance").toString()));
        }

        this.updateById(user);
        return user;
    }

    @Override
    public User adminCreateUser(Map<String, Object> request) {
        String username = (String) request.get("username");
        String password = (String) request.get("password");

        if (username == null || username.isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        if (this.checkUsername(username)) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordEncoder.encode(password));
        user.setRealName((String) request.get("realName"));
        user.setPhone((String) request.get("phone"));
        user.setEmail((String) request.get("email"));
        user.setRole((Integer) request.getOrDefault("role", 1));
        user.setStatus(1);
        user.setBalance(BigDecimal.ZERO);

        this.save(user);
        return user;
    }

    @Override
    public void adminDeleteUser(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        this.removeById(userId);
    }
}
