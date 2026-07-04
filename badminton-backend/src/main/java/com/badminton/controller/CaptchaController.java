package com.badminton.controller;

import com.badminton.common.Result;
import com.badminton.utils.CaptchaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取验证码
     */
    @GetMapping
    public Result<CaptchaResponse> getCaptcha() {
        // 生成验证码
        CaptchaUtil.CaptchaResult captcha = CaptchaUtil.generateCaptcha();

        // 生成唯一标识
        String captchaId = UUID.randomUUID().toString().replace("-", "");

        // 存入Redis，5分钟有效
        redisTemplate.opsForValue().set("captcha:" + captchaId, captcha.getCode().toUpperCase(), 5, TimeUnit.MINUTES);

        // 返回给前端（验证码ID和图片）
        return Result.success(new CaptchaResponse(captchaId, captcha.getImage()));
    }

    /**
     * 验证验证码
     */
    public boolean verifyCaptcha(String captchaId, String code) {
        if (captchaId == null || code == null) {
            return false;
        }
        String key = "captcha:" + captchaId;
        String cachedCode = (String) redisTemplate.opsForValue().get(key);
        if (cachedCode == null) {
            return false;
        }
        // 验证后删除
        redisTemplate.delete(key);
        return cachedCode.equalsIgnoreCase(code);
    }

    public static class CaptchaResponse {
        private String captchaId;
        private String image;

        public CaptchaResponse(String captchaId, String image) {
            this.captchaId = captchaId;
            this.image = image;
        }

        public String getCaptchaId() {
            return captchaId;
        }

        public String getImage() {
            return image;
        }
    }
}
