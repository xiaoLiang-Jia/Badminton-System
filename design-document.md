# 羽毛球平台设计与实现 - 设计文档

## 1 系统架构设计

### 1.1 整体架构

本系统采用前后端分离的B/S架构，具体架构如下：

```
┌─────────────────────────────────────────────────────────────────┐
│                        客户端层 (Browser)                       │
│                    Vue3 + Element UI 前端应用                   │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼ HTTP/HTTPS
┌─────────────────────────────────────────────────────────────────┐
│                        应用服务层 (Spring Boot)                   │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌────────────┐ │
│  │  用户模块   │ │  场地模块   │ │  预约模块   │ │  课程模块  │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └────────────┘ │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐               │
│  │  论坛模块   │ │  消息模块   │ │  系统模块   │               │
│  └─────────────┘ └─────────────┘ └─────────────┘               │
└─────────────────────────────────────────────────────────────────┘
                                │
                    ┌───────────┴───────────┐
                    ▼                       ▼
        ┌───────────────────┐   ┌───────────────────┐
        │   数据存储层      │   │   缓存层          │
        │   MySQL 8.0       │   │   Redis          │
        └───────────────────┘   └───────────────────┘
```

### 1.2 技术选型

| 层次 | 技术选型 | 说明 |
|------|---------|------|
| 前端框架 | Vue 3 | 组合式API (Composition API) |
| UI组件库 | Element Plus | Vue 3 适配版本 |
| 状态管理 | Pinia | Vue 3 推荐的状态管理库 |
| 前端路由 | Vue Router 4 | SPA路由管理 |
| 后端框架 | Spring Boot 2.7.x | Java企业级开发框架 |
| ORM框架 | MyBatis-Plus | 简化MyBatis开发 |
| 数据库 | MySQL 8.0 | 关系型数据库 |
| 缓存 | Redis | 高性能缓存 |
| 安全框架 | Spring Security + JWT | 认证与授权 |
| 日志框架 | Lombok | 简化日志记录 |

### 1.3 项目结构

#### 后端项目结构 (badminton-backend)
```
badminton-backend/
├── src/main/java/com/badminton/
│   ├── config/           # 配置类
│   │   ├── RedisConfig.java
│   │   ├── SecurityConfig.java
│   │   └── CorsConfig.java
│   ├── controller/       # 控制器层
│   │   ├── UserController.java
│   │   ├── CourtController.java
│   │   ├── BookingController.java
│   │   ├── CourseController.java
│   │   ├── ForumController.java
│   │   └── AdminController.java
│   ├── service/         # 业务逻辑层
│   │   ├── impl/
│   │   └── interfaces/
│   ├── mapper/          # 数据访问层
│   │   └── impl/
│   ├── entity/          # 实体类
│   ├── dto/             # 数据传输对象
│   ├── common/         # 公共组件
│   │   ├── Result.java  # 统一响应
│   │   ├── ExceptionHandler.java
│   │   └── Constants.java
│   └── utils/           # 工具类
├── src/main/resources/
│   ├── application.yml
│   └── mapper/          # MyBatis映射文件
└── pom.xml
```

#### 前端项目结构 (badminton-frontend)
```
badminton-frontend/
├── src/
│   ├── api/             # API请求封装
│   │   ├── index.js
│   │   ├── user.js
│   │   ├── court.js
│   │   ├── booking.js
│   │   └── course.js
│   ├── assets/         # 静态资源
│   ├── components/     # 公共组件
│   ├── layouts/        # 布局组件
│   ├── router/         # 路由配置
│   ├── stores/         # Pinia状态管理
│   ├── views/         # 页面组件
│   │   ├── user/       # 用户端页面
│   │   ├── coach/      # 教练端页面
│   │   └── admin/      # 管理端页面
│   ├── App.vue
│   └── main.js
├── index.html
├── vite.config.js
└── package.json
```

---

## 2 数据库设计

### 2.1 数据库ER图

```
┌──────────────┐       ┌──────────────┐
│    user      │       │   court      │
├──────────────┤       ├──────────────┤
│ id           │       │ id           │
│ username     │       │ name         │
│ password     │       │ location     │
│ real_name    │       │ type         │
│ phone        │       │ price        │
│ email        │       │ status       │
│ avatar       │       │ capacity     │
│ role         │──────<│ description  │
│ balance      │       │ open_time    │
│ create_time  │       │ image_url    │
│ update_time  │       │ create_time  │
└──────────────┘       │ update_time  │
        │              └──────────────┘
        │                     │
        │                     │
        ▼                     ▼
┌──────────────┐       ┌──────────────┐
│   booking    │       │   course     │
├──────────────┤       ├──────────────┤
│ id           │       │ id           │
│ user_id      │──────>│ coach_id     │────<│ user │
│ court_id     │──────>│ court_id     │────<│ court│
│ booking_date │       │ name         │
│ start_time   │       │ description  │
│ end_time     │       │ price        │
│ status       │       │ max_students │
│ total_price  │       │ current_num  │
│ create_time  │       │ schedule     │
│ update_time  │       │ status       │
└──────────────┘       │ image_url    │
        │              │ create_time  │
        │              └──────────────┘
        │                     │
        │                     ▼
        │              ┌──────────────┐
        │              │ course_student│
        ├──────────────┤ ├──────────────┤
        │              │ id            │
        ▼              │ course_id     │────<│ course│
┌──────────────┐       │ user_id       │────<│ user │
│   payment    │       │ status        │
├──────────────┤       │ create_time   │
│ id           │       └──────────────┘
│ booking_id   │──────>│
│ amount       │
│ payment_method│
│ payment_time │
│ status       │
└──────────────┘

┌──────────────┐       ┌──────────────┐
│    forum     │       │   comment    │
├──────────────┤       ├──────────────┤
│ id           │       │ id           │
│ user_id      │──────>│ forum_id     │────<│ forum │
│ title        │       │ user_id      │────<│ user │
│ content      │       │ content      │
│ image_url    │       │ parent_id    │
│ likes        │       │ create_time  │
│ views        │       └──────────────┘
│ category     │
│ status       │
│ create_time  │
│ update_time  │
└──────────────┘
```

### 2.2 数据库表结构

#### 2.2.1 用户表 (user)
```sql
CREATE TABLE `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `role` TINYINT NOT NULL DEFAULT 1 COMMENT '角色: 1-普通用户 2-教练 3-管理员',
    `balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

#### 2.2.2 场地表 (court)
```sql
CREATE TABLE `court` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '场地ID',
    `name` VARCHAR(100) NOT NULL COMMENT '场地名称',
    `location` VARCHAR(255) NOT NULL COMMENT '场地位置',
    `type` TINYINT NOT NULL COMMENT '场地类型: 1-室内木地 2-室内地胶 3-室外',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格(元/小时)',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-不可用 1-可用',
    `capacity` INT DEFAULT 4 COMMENT '容纳人数',
    `description` TEXT COMMENT '场地描述',
    `open_time` VARCHAR(100) COMMENT '营业时间，如: 09:00-22:00',
    `image_url` VARCHAR(500) COMMENT '场地图片',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场地表';
```

#### 2.2.3 预约表 (booking)
```sql
CREATE TABLE `booking` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `court_id` BIGINT NOT NULL COMMENT '场地ID',
    `booking_date` DATE NOT NULL COMMENT '预约日期',
    `start_time` TIME NOT NULL COMMENT '开始时间',
    `end_time` TIME NOT NULL COMMENT '结束时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-待支付 2-已支付 3-已完成 4-已取消 5-已退款',
    `total_price` DECIMAL(10,2) NOT NULL COMMENT '总价',
    `remark` VARCHAR(500) COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`court_id`) REFERENCES `court`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';
```

#### 2.2.4 课程表 (course)
```sql
CREATE TABLE `course` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    `coach_id` BIGINT NOT NULL COMMENT '教练ID',
    `court_id` BIGINT COMMENT '上课场地',
    `name` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `description` TEXT COMMENT '课程描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '课程价格',
    `max_students` INT DEFAULT 10 COMMENT '最大人数',
    `current_num` INT DEFAULT 0 COMMENT '当前报名人数',
    `duration` INT COMMENT '课时时长(分钟)',
    `schedule` VARCHAR(500) COMMENT '课程安排，JSON格式',
    `level` TINYINT COMMENT '难度等级: 1-初级 2-中级 3-高级',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-已下架 1-可报名',
    `image_url` VARCHAR(500) COMMENT '课程图片',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`coach_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`court_id`) REFERENCES `court`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';
```

#### 2.2.5 课程报名表 (course_student)
```sql
CREATE TABLE `course_student` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `user_id` BIGINT NOT NULL COMMENT '学员ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-已报名 2-已完成 3-已取消',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程报名表';
```

#### 2.2.6 论坛帖子表 (forum)
```sql
CREATE TABLE `forum` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '发布者ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '内容',
    `image_url` VARCHAR(1000) COMMENT '图片，多个用逗号分隔',
    `category` TINYINT DEFAULT 1 COMMENT '分类: 1-技术讨论 2-约球 3-比赛心得 4-器材交流 5-新手求助',
    `likes` INT DEFAULT 0 COMMENT '点赞数',
    `views` INT DEFAULT 0 COMMENT '浏览量',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-待审核 1-已发布 2-已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛帖子表';
```

#### 2.2.7 评论表 (comment)
```sql
CREATE TABLE `comment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    `forum_id` BIGINT NOT NULL COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '评论者ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `parent_id` BIGINT COMMENT '父评论ID，用于回复',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`forum_id`) REFERENCES `forum`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';
```

#### 2.2.8 支付表 (payment)
```sql
CREATE TABLE `payment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付ID',
    `booking_id` BIGINT COMMENT '预约ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    `payment_method` TINYINT COMMENT '支付方式: 1-微信 2-支付宝 3-余额',
    `trade_no` VARCHAR(100) COMMENT '第三方交易号',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-待支付 2-已支付 3-已退款',
    `payment_time` DATETIME COMMENT '支付时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`booking_id`) REFERENCES `booking`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付表';
```

---

## 3 API接口设计

### 3.1 统一响应格式

```json
// 成功响应
{
    "code": 200,
    "message": "操作成功",
    "data": {}
}

// 失败响应
{
    "code": 400,
    "message": "错误信息",
    "data": null
}
```

### 3.2 用户模块接口

| 接口路径 | 方法 | 说明 | 权限 |
|---------|------|------|------|
| /api/user/register | POST | 用户注册 | 公开 |
| /api/user/login | POST | 用户登录 | 公开 |
| /api/user/info | GET | 获取当前用户信息 | 需认证 |
| /api/user/info/{id} | GET | 获取指定用户信息 | 需认证 |
| /api/user/update | PUT | 更新用户信息 | 需认证 |
| /api/user/changePassword | POST | 修改密码 | 需认证 |
| /api/user/balance | GET | 获取余额 | 需认证 |
| /api/user/ recharge | POST | 充值 | 需认证 |

### 3.3 场地模块接口

| 接口路径 | 方法 | 说明 | 权限 |
|---------|------|------|------|
| /api/court/list | GET | 场地列表 | 公开 |
| /api/court/{id} | GET | 场地详情 | 公开 |
| /api/court/available | GET | 可预约场地 | 需认证 |
| /api/court/add | POST | 添加场地 | 管理员 |
| /api/court/update | PUT | 更新场地 | 管理员 |
| /api/court/delete/{id} | DELETE | 删除场地 | 管理员 |

### 3.4 预约模块接口

| 接口路径 | 方法 | 说明 | 权限 |
|---------|------|------|------|
| /api/booking/create | POST | 创建预约 | 需认证 |
| /api/booking/list | GET | 预约列表 | 需认证 |
| /api/booking/{id} | GET | 预约详情 | 需认证 |
| /api/booking/cancel/{id} | POST | 取消预约 | 需认证 |
| /api/booking/pay/{id} | POST | 支付预约 | 需认证 |
| /api/booking/complete/{id} | POST | 完成预约 | 管理员 |
| /api/booking/stats | GET | 预约统计 | 管理员 |

### 3.5 课程模块接口

| 接口路径 | 方法 | 说明 | 权限 |
|---------|------|------|------|
| /api/course/list | GET | 课程列表 | 公开 |
| /api/course/{id} | GET | 课程详情 | 公开 |
| /api/course/enroll | POST | 报名课程 | 需认证 |
| /api/course/cancel/{id} | POST | 取消报名 | 需认证 |
| /api/course/my-courses | GET | 我的课程 | 需认证 |
| /api/course/add | POST | 添加课程 | 教练/管理员 |
| /api/course/update | PUT | 更新课程 | 教练/管理员 |
| /api/course/delete/{id} | DELETE | 删除课程 | 教练/管理员 |

### 3.6 论坛模块接口

| 接口路径 | 方法 | 说明 | 权限 |
|---------|------|------|------|
| /api/forum/list | GET | 帖子列表 | 公开 |
| /api/forum/{id} | GET | 帖子详情 | 公开 |
| /api/forum/publish | POST | 发布帖子 | 需认证 |
| /api/forum/update | PUT | 更新帖子 | 需认证 |
| /api/forum/delete/{id} | DELETE | 删除帖子 | 需认证 |
| /api/forum/like/{id} | POST | 点赞帖子 | 需认证 |
| /api/forum/comment | POST | 发表评论 | 需认证 |
| /api/forum/comments/{id} | GET | 帖子评论列表 | 公开 |

### 3.7 管理模块接口

| 接口路径 | 方法 | 说明 | 权限 |
|---------|------|------|------|
| /api/admin/users | GET | 用户列表 | 管理员 |
| /api/admin/user/{id}/status | PUT | 修改用户状态 | 管理员 |
| /api/admin/stats | GET | 系统统计 | 管理员 |

---

## 4 前端页面设计

### 4.1 路由结构

```
/                           # 首页（重定向到 /home）
/login                      # 登录页
/register                   # 注册页
/home                       # 用户首页（场地列表）
/courts                     # 场地预定页
/courses                    # 课程列表页
/course/:id                 # 课程详情页
/forum                      # 论坛首页
/forum/:id                  # 论坛详情页
/user/profile               # 个人中心
/user/bookings              # 我的预约
/user/courses               # 我的课程
/user/balance               # 余额充值
/admin                      # 管理后台首页
/admin/courts               # 场地管理
/admin/users                # 用户管理
/admin/courses              # 课程管理
/admin/forum                # 论坛管理
/admin/stats                # 数据统计
```

### 4.2 页面设计

#### 4.2.1 首页 /home
- 顶部：Logo、导航菜单、用户信息
- 轮播图：展示优惠活动或热门课程
- 快捷入口：场地预约、课程报名、社区论坛
- 推荐场地：热门场地卡片展示
- 推荐课程：热门课程列表

#### 4.2.2 场地列表页 /courts
- 筛选条件：场地类型、价格区间、可用时间
- 场地卡片：图片、名称、位置、价格、状态
- 点击卡片进入场地详情/预约

#### 4.2.3 预约流程
1. 选择场地 → 2. 选择日期时间 → 3. 确认订单 → 4. 支付

#### 4.2.4 课程列表页 /courses
- 分类筛选：全部、私教、团体课
- 难度筛选：初级、中级、高级
- 课程卡片：教练头像、课程名称、价格、人数

#### 4.2.5 论坛页 /forum
- 分类标签：技术讨论、约球、比赛心得等
- 帖子列表：标题、作者、点赞数、评论数、发布时间
- 发布按钮：发帖功能

---

## 5 核心功能流程设计

### 5.1 用户登录流程
```
用户输入账号密码
    ↓
前端发送登录请求
    ↓
后端验证用户名密码
    ↓
生成JWT Token
    ↓
返回Token和用户信息
    ↓
前端存储Token到LocalStorage
    ↓
跳转到首页
```

### 5.2 场地预约流程
```
用户选择场地和日期
    ↓
查看可用时间段
    ↓
选择时间段并确认
    ↓
创建预约订单（待支付状态）
    ↓
选择支付方式
    ↓
调用支付接口
    ↓
支付成功，更新订单状态
    ↓
发送预约成功通知
```

### 5.3 高并发处理
- 使用Redis缓存场地可用时段
- 分布式锁防止重复预约
- 乐观锁处理并发更新
- 限流策略防止恶意请求

---

## 6 安全设计

### 6.1 认证与授权
- JWT Token认证
- Token有效期24小时
- _refresh_token用于刷新access_token
- 基于角色的权限控制(RBAC)

### 6.2 数据安全
- 密码BCrypt加密存储
- 敏感数据传输使用HTTPS
- SQL注入防护（MyBatis参数绑定）
- XSS防护（前端转义）

### 6.3 接口安全
- 请求频率限制
- 签名验证
- 敏感操作日志记录

---

## 7 缓存设计

### 7.1 Redis缓存策略
| 缓存数据 | 过期时间 | 说明 |
|---------|---------|------|
| 用户Token | 24小时 | JWT黑名单 |
| 场地列表 | 1小时 | 热点数据 |
| 场地时段 | 5分钟 | 高频访问 |
| 课程列表 | 10分钟 | 低频更新 |
| 论坛帖子 | 5分钟 | 高频访问 |
| 系统配置 | 24小时 | 低频更新 |

---

## 8 部署架构

### 8.1 开发环境
- 后端：Spring Boot内置Tomcat
- 前端：Vite开发服务器
- 数据库：本地MySQL
- 缓存：本地Redis

### 8.2 生产环境（建议）
- 后端：Docker容器 + Nginx
- 前端：Nginx静态部署
- 数据库：云MySQL
- 缓存：云Redis
- CDN：静态资源加速

---

## 9 开发计划

### 第一阶段：基础框架搭建
1. 创建Spring Boot项目，配置依赖
2. 创建Vue3项目，配置Element Plus
3. 实现用户注册登录功能

### 第二阶段：核心功能开发
1. 场地管理模块
2. 预约管理模块
3. 课程管理模块

### 第三阶段：社区功能开发
1. 论坛模块
2. 评论功能

### 第四阶段：管理后台
1. 用户管理
2. 数据统计

### 第五阶段：测试与部署
1. 功能测试
2. 性能优化
3. 部署上线
