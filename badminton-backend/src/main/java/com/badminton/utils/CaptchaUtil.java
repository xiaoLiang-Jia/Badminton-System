package com.badminton.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

public class CaptchaUtil {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    /**
     * 生成验证码图片，返回Base64编码的图片和验证码
     */
    public static CaptchaResult generateCaptcha() {
        // 创建图片
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 生成随机验证码
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }

        // 绘制验证码
        Font font = new Font("Arial", Font.BOLD, 24);
        g.setFont(font);
        for (int i = 0; i < CODE_LENGTH; i++) {
            Color color = new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150));
            g.setColor(color);
            int x = 20 + i * 25;
            int y = 25 + random.nextInt(10);
            g.drawString(String.valueOf(code.charAt(i)), x, y);
        }

        // 绘制干扰线
        for (int i = 0; i < 5; i++) {
            Color color = new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200));
            g.setColor(color);
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        g.dispose();

        // 转换为Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
            String base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
            return new CaptchaResult(code.toString(), base64Image);
        } catch (IOException e) {
            throw new RuntimeException("生成验证码失败", e);
        }
    }

    public static class CaptchaResult {
        private String code;
        private String image;

        public CaptchaResult(String code, String image) {
            this.code = code;
            this.image = image;
        }

        public String getCode() {
            return code;
        }

        public String getImage() {
            return image;
        }
    }
}
