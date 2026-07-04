package com.badminton.common;

public class Constants {

    // 用户角色
    public static final int ROLE_USER = 1;        // 普通用户
    public static final int ROLE_COACH = 2;       // 教练
    public static final int ROLE_ADMIN = 3;       // 管理员

    // 用户状态
    public static final int USER_STATUS_NORMAL = 1;   // 正常
    public static final int USER_STATUS_DISABLED = 0;  // 禁用

    // 场地状态
    public static final int COURT_STATUS_UNAVAILABLE = 0;  // 不可用
    public static final int COURT_STATUS_AVAILABLE = 1;     // 可用

    // 场地类型
    public static final int COURT_TYPE_INDOOR_WOOD = 1;     // 室内木地
    public static final int COURT_TYPE_INDOOR_PU = 2;       // 室内地胶
    public static final int COURT_TYPE_OUTDOOR = 3;        // 室外

    // 预约状态
    public static final int BOOKING_STATUS_PENDING = 1;    // 待支付
    public static final int BOOKING_STATUS_PAID = 2;        // 已支付
    public static final int BOOKING_STATUS_COMPLETED = 3;  // 已完成
    public static final int BOOKING_STATUS_CANCELLED = 4;  // 已取消
    public static final int BOOKING_STATUS_REFUNDED = 5;    // 已退款

    // 课程状态
    public static final int COURSE_STATUS_OFFLINE = 0;      // 已下架
    public static final int COURSE_STATUS_AVAILABLE = 1;    // 可报名

    // 课程难度等级
    public static final int COURSE_LEVEL_BEGINNER = 1;      // 初级
    public static final int COURSE_LEVEL_INTERMEDIATE = 2;  // 中级
    public static final int COURSE_LEVEL_ADVANCED = 3;      // 高级

    // 课程报名状态
    public static final int ENROLL_STATUS_ENROLLED = 1;     // 已报名
    public static final int ENROLL_STATUS_COMPLETED = 2;    // 已完成
    public static final int ENROLL_STATUS_CANCELLED = 3;    // 已取消

    // 论坛帖子状态
    public static final int FORUM_STATUS_PENDING = 0;       // 待审核
    public static final int FORUM_STATUS_PUBLISHED = 1;     // 已发布
    public static final int FORUM_STATUS_DELETED = 2;       // 已删除

    // 论坛分类
    public static final int FORUM_CATEGORY_TECH = 1;         // 技术讨论
    public static final int FORUM_CATEGORY_INVITE = 2;       // 约球
    public static final int FORUM_CATEGORY_MATCH = 3;        // 比赛心得
    public static final int FORUM_CATEGORY_EQUIPMENT = 4;   // 器材交流
    public static final int FORUM_CATEGORY_HELP = 5;         // 新手求助

    // 支付状态
    public static final int PAYMENT_STATUS_PENDING = 1;     // 待支付
    public static final int PAYMENT_STATUS_PAID = 2;         // 已支付
    public static final int PAYMENT_STATUS_REFUNDED = 3;     // 已退款

    // 支付方式
    public static final int PAYMENT_METHOD_WECHAT = 1;        // 微信
    public static final int PAYMENT_METHOD_ALIPAY = 2;       // 支付宝
    public static final int PAYMENT_METHOD_BALANCE = 3;      // 余额
}
