-- 羽毛球平台数据库脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS badminton_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE badminton_db;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `avatar` VARCHAR(255) DEFAULT '/avatar/default.png' COMMENT '头像URL',
    `role` TINYINT NOT NULL DEFAULT 1 COMMENT '角色: 1-普通用户 2-教练 3-管理员',
    `balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`),
    INDEX `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 场地表
CREATE TABLE IF NOT EXISTS `court` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '场地ID',
    `name` VARCHAR(100) NOT NULL COMMENT '场地名称',
    `location` VARCHAR(255) NOT NULL COMMENT '场地位置',
    `type` TINYINT NOT NULL DEFAULT 1 COMMENT '场地类型: 1-室内木地 2-室内地胶 3-室外',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格(元/小时)',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-不可用 1-可用',
    `capacity` INT DEFAULT 4 COMMENT '容纳人数',
    `description` TEXT COMMENT '场地描述',
    `open_time` VARCHAR(100) DEFAULT '09:00-22:00' COMMENT '营业时间',
    `image_url` VARCHAR(500) COMMENT '场地图片',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场地表';

-- 预约表
CREATE TABLE IF NOT EXISTS `booking` (
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
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_court_id` (`court_id`),
    INDEX `idx_booking_date` (`booking_date`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`court_id`) REFERENCES `court`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- 课程表
CREATE TABLE IF NOT EXISTS `course` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    `coach_id` BIGINT NOT NULL COMMENT '教练ID',
    `court_id` BIGINT COMMENT '上课场地',
    `name` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `description` TEXT COMMENT '课程描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '课程价格',
    `max_students` INT DEFAULT 10 COMMENT '最大人数',
    `current_num` INT DEFAULT 0 COMMENT '当前报名人数',
    `duration` INT DEFAULT 60 COMMENT '课时时长(分钟)',
    `schedule` VARCHAR(500) COMMENT '课程安排，JSON格式',
    `level` TINYINT DEFAULT 1 COMMENT '难度等级: 1-初级 2-中级 3-高级',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-已下架 1-可报名',
    `image_url` VARCHAR(500) COMMENT '课程图片',
    `lesson_count` INT DEFAULT 0 COMMENT '课时数',
    `curriculum` TEXT COMMENT '课程大纲(JSON)',
    `target_audience` VARCHAR(500) COMMENT '适合人群',
    `graduation_standard` VARCHAR(1000) COMMENT '结业标准',
    `training_focus` VARCHAR(500) COMMENT '训练重点',
    `frequency` VARCHAR(100) COMMENT '训练频率',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_coach_id` (`coach_id`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`coach_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`court_id`) REFERENCES `court`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 课程报名表
CREATE TABLE IF NOT EXISTS `course_student` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `user_id` BIGINT NOT NULL COMMENT '学员ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-已报名 2-已完成 3-已取消',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_course_id` (`course_id`),
    INDEX `idx_user_id` (`user_id`),
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程报名表';

-- 论坛帖子表
CREATE TABLE IF NOT EXISTS `forum` (
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
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_category` (`category`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛帖子表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    `forum_id` BIGINT NOT NULL COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '评论者ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `parent_id` BIGINT COMMENT '父评论ID，用于回复',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_forum_id` (`forum_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_parent_id` (`parent_id`),
    FOREIGN KEY (`forum_id`) REFERENCES `forum`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 支付表
CREATE TABLE IF NOT EXISTS `payment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付ID',
    `booking_id` BIGINT COMMENT '预约ID',
    `course_id` BIGINT COMMENT '课程ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    `payment_method` TINYINT COMMENT '支付方式: 1-微信 2-支付宝 3-余额',
    `trade_no` VARCHAR(100) COMMENT '第三方交易号',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-待支付 2-已支付 3-已退款',
    `payment_time` DATETIME COMMENT '支付时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_booking_id` (`booking_id`),
    INDEX `idx_course_id` (`course_id`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`booking_id`) REFERENCES `booking`(`id`),
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付表';

-- 论坛点赞表
CREATE TABLE IF NOT EXISTS `forum_like` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `forum_id` BIGINT NOT NULL COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_forum_user` (`forum_id`, `user_id`),
    FOREIGN KEY (`forum_id`) REFERENCES `forum`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛点赞表';

-- 插入默认管理员账号 (密码: admin123，使用BCrypt加密)
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 3, 1);

-- 插入测试教练账号
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `role`, `status`) VALUES
('coach1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '肖教练', '13800138001', 2, 1);

-- 插入测试用户账号
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `role`, `status`, `balance`) VALUES
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '测试用户', '13800138002', 1, 1, 100.00);

-- 插入测试场地数据
INSERT INTO `court` (`name`, `location`, `type`, `price`, `status`, `capacity`, `description`, `image_url`) VALUES
('羽毛球场地A1', '体育馆一楼A区', 1, 30.00, 1, 4, '标准室内木地场地，配备专业照明', '/images/court/court1.jpg'),
('羽毛球场地A2', '体育馆一楼A区', 1, 30.00, 1, 4, '标准室内木地场地，配备专业照明', '/images/court/court2.jpg'),
('羽毛球场地B1', '体育馆二楼B区', 2, 25.00, 1, 4, '室内地胶场地，防滑舒适', '/images/court/court3.jpg'),
('羽毛球场地B2', '体育馆二楼B区', 2, 25.00, 1, 4, '室内地胶场地，防滑舒适', '/images/court/court4.jpg'),
('室外羽毛球场1', '学校操场西侧', 3, 15.00, 1, 4, '室外水泥场地，白天开放', '/images/court/court5.jpg');

-- 为已存在的数据库添加新课程字段
ALTER TABLE course ADD COLUMN lesson_count INT DEFAULT 0 COMMENT '课时数';
ALTER TABLE course ADD COLUMN curriculum TEXT COMMENT '课程大纲(JSON)';
ALTER TABLE course ADD COLUMN target_audience VARCHAR(500) COMMENT '适合人群';
ALTER TABLE course ADD COLUMN graduation_standard VARCHAR(1000) COMMENT '结业标准';
ALTER TABLE course ADD COLUMN training_focus VARCHAR(500) COMMENT '训练重点';
ALTER TABLE course ADD COLUMN frequency VARCHAR(100) COMMENT '训练频率';

-- 插入初级课程（零基础→能稳定对打）
INSERT INTO `course` (`coach_id`, `court_id`, `name`, `description`, `price`, `max_students`, `duration`, `schedule`, `level`, `status`, `image_url`, `lesson_count`, `curriculum`, `target_audience`, `graduation_standard`, `training_focus`, `frequency`) VALUES
(12, 1, '羽毛球入门基础班', '专为羽毛球零基础学员设计，通过8次课掌握基础握拍、站位、发球、击球技术，学期结束后能够与教练进行简单的对打练习。', 1280.00, 8, 90, '每周二、四 19:00-20:30', 1, 1, '/images/course/beginner.jpg', 8,
'[{"lesson":1,"title":"握拍与站位","content":"学习正手握拍、反手握拍；基本站位姿势与移动准备"},
{"lesson":2,"title":"发球技术","content":"正手发高远球、反手发网前球"},
{"lesson":3,"title":"高远球技术","content":"正手击高远球的动作要领、挥拍轨迹"},
{"lesson":4,"title":"吊球技术","content":"正手吊球技术、落点控制"},
{"lesson":5,"title":"杀球入门","content":"正手杀球基础动作、发力技巧"},
{"lesson":6,"title":"网前技术","content":"搓球、推球、勾对角基础"},
{"lesson":7,"title":"步法基础","content":"上网步法、后退步法、两侧移动步法"},
{"lesson":8,"title":"综合练习","content":"半场对打练习、总结与考核"}]',
'适合从未接触过羽毛球或基础薄弱的初学者',
'能够正确握拍和站位；能独立完成正手发高远球；能进行简单的半场对打',
'重点培养球感、基础动作规范性、步法协调性',
'每周2次，共4周');

-- 插入中级课程（能稳定对打→具备比赛意识）
INSERT INTO `course` (`coach_id`, `court_id`, `name`, `description`, `price`, `max_students`, `duration`, `schedule`, `level`, `status`, `image_url`, `lesson_count`, `curriculum`, `target_audience`, `graduation_standard`, `training_focus`, `frequency`) VALUES
(12, 1, '羽毛球进阶提高班', '面向有一定基础的学员，通过12次课强化技术动作，掌握进攻防守转换，形成比赛意识。', 1980.00, 8, 90, '每周三、五 19:00-20:30', 2, 1, '/images/course/intermediate.jpg', 12,
'[{"lesson":1,"title":"高远球强化","content":"高远球深度与弧度控制、发力协调性"},
{"lesson":2,"title":"吊球与劈吊","content":"吊球落点控制、劈吊技术动作"},
{"lesson":3,"title":"杀球进阶","content":"重杀、点杀、劈杀技术"},
{"lesson":4,"title":"网前技术提升","content":"网前假动作、抢网技术"},
{"lesson":5,"title":"反手技术","content":"反手高远球、反手吊球、反手杀球"},
{"lesson":6,"title":"接杀技术","content":"接杀挡网、接杀抽球"},
{"lesson":7,"title":"平抽快挡","content":"平抽球技术、双打中平抽快挡应用"},
{"lesson":8,"title":"发球与接发球","content":"发球变化、接发球抢攻"},
{"lesson":9,"title":"防守反击","content":"防守转攻的时机把握与技术运用"},
{"lesson":10,"title":"进攻组织","content":"杀吊结合、进攻节奏控制"},
{"lesson":11,"title":"战术意识","content":"单打基本战术、线路意识"},
{"lesson":12,"title":"比赛实战","content":"计分比赛、战术运用、综合考核"}]',
'适合已掌握基础技术，能够进行稳定对打的学员',
'技术动作规范完整；具备基本的进攻和防守能力；能参与业余比赛',
'技术精细化、战术意识培养、实战能力提升',
'每周2次，共6周');

-- 插入高级课程（竞技提升→专业比赛能力）
INSERT INTO `course` (`coach_id`, `court_id`, `name`, `description`, `price`, `max_students`, `duration`, `schedule`, `level`, `status`, `image_url`, `lesson_count`, `curriculum`, `target_audience`, `graduation_standard`, `training_focus`, `frequency`) VALUES
(12, 1, '羽毛球竞技特训班', '面向高水平学员，通过16次课进行技术精细化训练，构建完整战术体系，提升竞技水平。', 3280.00, 6, 120, '每周六、日 14:00-16:00', 3, 1, '/images/course/advanced.jpg', 16,
'[{"lesson":1,"title":"技术诊断与评估","content":"全面技术评估、制定个性化训练计划"},
{"lesson":2,"title":"杀球速度与变化","content":"提升杀球速度、落点精准度"},
{"lesson":3,"title":"吊球迷惑性","content":"假动作吊球、延迟吊球"},
{"lesson":4,"title":"网前技术高级","content":"网前细腻手感、创造性假动作"},
{"lesson":5,"title":"反手进阶","content":"反手突击、反手杀球一致性"},
{"lesson":6,"title":"接杀高级","content":"接杀弹后场、接杀反抽对角"},
{"lesson":7,"title":"假动作与一致性","content":"击球一致性、假动作运用"},
{"lesson":8,"title":"步法精进","content":"快速启动、步伐节奏优化"},
{"lesson":9,"title":"体能训练","content":"专项体能、耐力与爆发力"},
{"lesson":10,"title":"单打战术体系","content":"控制与反控制战术、四方球运用"},
{"lesson":11,"title":"双打战术配合","content":"轮换进攻、前后封网战术"},
{"lesson":12,"title":"比赛心理","content":"心理调节、关键分处理"},
{"lesson":13,"title":"多球训练","content":"高强度多球训练、反应速度"},
{"lesson":14,"title":"实战对抗","content":"模拟比赛、战术执行"},
{"lesson":15,"title":"战术复盘","content":"比赛录像分析、战术调整"},
{"lesson":16,"title":"综合考核与总结","content":"结业比赛、技术考核、成长总结"}]',
'适合具备扎实技术基础，渴望提升竞技水平的学员',
'技术全面且精细；具备完整战术体系；能参加正式比赛并取得成绩',
'技术精细化、战术体系构建、心理素质培养、体能强化',
'每周2次，共8周');
