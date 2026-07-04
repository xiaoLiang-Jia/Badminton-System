/*
SQLyog Ultimate v12.08 (64 bit)
MySQL - 8.0.44 : Database - badminton_db
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`badminton_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `badminton_db`;

/*Table structure for table `booking` */

DROP TABLE IF EXISTS `booking`;

CREATE TABLE `booking` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '棰勭害ID',
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛ID',
  `court_id` bigint NOT NULL COMMENT '鍦哄湴ID',
  `booking_date` date NOT NULL COMMENT '棰勭害鏃ユ湡',
  `start_time` time NOT NULL COMMENT '寮??鏃堕棿',
  `end_time` time NOT NULL COMMENT '缁撴潫鏃堕棿',
  `status` tinyint DEFAULT '1' COMMENT '鐘舵?: 1-寰呮敮浠?2-宸叉敮浠?3-宸插畬鎴?4-宸插彇娑?5-宸查?娆',
  `total_price` decimal(10,2) NOT NULL COMMENT '鎬讳环',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_court_id` (`court_id`),
  KEY `idx_booking_date` (`booking_date`),
  KEY `idx_status` (`status`),
  CONSTRAINT `booking_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `booking_ibfk_2` FOREIGN KEY (`court_id`) REFERENCES `court` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='棰勭害琛';

/*Data for the table `booking` */

/*Table structure for table `comment` */

DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '璇勮?ID',
  `forum_id` bigint NOT NULL COMMENT '甯栧瓙ID',
  `user_id` bigint NOT NULL COMMENT '璇勮?鑰匢D',
  `content` text NOT NULL COMMENT '璇勮?鍐呭?',
  `parent_id` bigint DEFAULT NULL COMMENT '鐖惰瘎璁篒D锛岀敤浜庡洖澶',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_forum_id` (`forum_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`forum_id`) REFERENCES `forum` (`id`),
  CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璇勮?琛';

/*Data for the table `comment` */

/*Table structure for table `conversation` */

DROP TABLE IF EXISTS `conversation`;

CREATE TABLE `conversation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_id` bigint NOT NULL COMMENT '对方用户ID',
  `last_message` text COMMENT '最后一条消息',
  `last_time` datetime DEFAULT NULL COMMENT '最后消息时间',
  `unread_count` int DEFAULT '0' COMMENT '未读数',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='会话表';

/*Data for the table `conversation` */

insert  into `conversation`(`id`,`user_id`,`target_id`,`last_message`,`last_time`,`unread_count`) values (1,11,12,'111','2026-04-09 16:27:51',0),(2,12,11,'111','2026-04-09 16:27:51',0);

/*Table structure for table `course` */

DROP TABLE IF EXISTS `course`;

CREATE TABLE `course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '璇剧▼ID',
  `coach_id` bigint NOT NULL COMMENT '鏁欑粌ID',
  `court_id` bigint DEFAULT NULL COMMENT '涓婅?鍦哄湴',
  `name` varchar(100) NOT NULL COMMENT '璇剧▼鍚嶇О',
  `description` text COMMENT '璇剧▼鎻忚堪',
  `price` decimal(10,2) NOT NULL COMMENT '璇剧▼浠锋牸',
  `max_students` int DEFAULT '10' COMMENT '鏈?ぇ浜烘暟',
  `current_num` int DEFAULT '0' COMMENT '褰撳墠鎶ュ悕浜烘暟',
  `duration` int DEFAULT '60' COMMENT '璇炬椂鏃堕暱(鍒嗛挓)',
  `schedule` varchar(500) DEFAULT NULL COMMENT '璇剧▼瀹夋帓锛孞SON鏍煎紡',
  `level` tinyint DEFAULT '1' COMMENT '闅惧害绛夌骇: 1-鍒濈骇 2-涓?骇 3-楂樼骇',
  `status` tinyint DEFAULT '1' COMMENT '鐘舵?: 0-宸蹭笅鏋?1-鍙?姤鍚',
  `image_url` varchar(500) DEFAULT NULL COMMENT '璇剧▼鍥剧墖',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  `lesson_count` int DEFAULT '0' COMMENT '课时数',
  `curriculum` text COMMENT '课程大纲(JSON)',
  `target_audience` varchar(500) DEFAULT NULL COMMENT '适合人群',
  `graduation_standard` varchar(1000) DEFAULT NULL COMMENT '结业标准',
  `training_focus` varchar(500) DEFAULT NULL COMMENT '训练重点',
  `frequency` varchar(100) DEFAULT NULL COMMENT '训练频率',
  PRIMARY KEY (`id`),
  KEY `idx_coach_id` (`coach_id`),
  KEY `idx_status` (`status`),
  KEY `court_id` (`court_id`),
  CONSTRAINT `course_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `user` (`id`),
  CONSTRAINT `course_ibfk_2` FOREIGN KEY (`court_id`) REFERENCES `court` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璇剧▼琛';

/*Data for the table `course` */

insert  into `course`(`id`,`coach_id`,`court_id`,`name`,`description`,`price`,`max_students`,`current_num`,`duration`,`schedule`,`level`,`status`,`image_url`,`create_time`,`update_time`,`lesson_count`,`curriculum`,`target_audience`,`graduation_standard`,`training_focus`,`frequency`) values (1,20,1,'羽毛球入门基础班','专为羽毛球零基础学员设计，通过8次课掌握基础握拍、站位、发球、击球技术，学期结束后能够与教练进行简单的对打练习。','1280.00',8,2,90,'每周二、四 19:00-20:30',1,1,'/uploads/badminton-1.jpg','2026-03-24 15:05:56','2026-04-13 17:58:00',8,'[{\"lesson\":1,\"title\":\"握拍与站位\",\"content\":\"学习正手握拍、反手握拍；基本站位姿势与移动准备\"},\n{\"lesson\":2,\"title\":\"发球技术\",\"content\":\"正手发高远球、反手发网前球\"},\n{\"lesson\":3,\"title\":\"高远球技术\",\"content\":\"正手击高远球的动作要领、挥拍轨迹\"},\n{\"lesson\":4,\"title\":\"吊球技术\",\"content\":\"正手吊球技术、落点控制\"},\n{\"lesson\":5,\"title\":\"杀球入门\",\"content\":\"正手杀球基础动作、发力技巧\"},\n{\"lesson\":6,\"title\":\"网前技术\",\"content\":\"搓球、推球、勾对角基础\"},\n{\"lesson\":7,\"title\":\"步法基础\",\"content\":\"上网步法、后退步法、两侧移动步法\"},\n{\"lesson\":8,\"title\":\"综合练习\",\"content\":\"半场对打练习、总结与考核\"}]','适合从未接触过羽毛球或基础薄弱的初学者','能够正确握拍和站位；能独立完成正手发高远球；能进行简单的半场对打','重点培养球感、基础动作规范性、步法协调性','每周2次，共4周'),(2,20,1,'羽毛球进阶提高班','面向有一定基础的学员，通过12次课强化技术动作，掌握进攻防守转换，形成比赛意识。','1180.00',8,0,90,'每周三、五 19:00-20:30',2,1,'/uploads/badminton-2.jpg','2026-03-24 15:05:56','2026-04-13 17:58:01',12,'[{\"lesson\":1,\"title\":\"高远球强化\",\"content\":\"高远球深度与弧度控制、发力协调性\"},\n{\"lesson\":2,\"title\":\"吊球与劈吊\",\"content\":\"吊球落点控制、劈吊技术动作\"},\n{\"lesson\":3,\"title\":\"杀球进阶\",\"content\":\"重杀、点杀、劈杀技术\"},\n{\"lesson\":4,\"title\":\"网前技术提升\",\"content\":\"网前假动作、抢网技术\"},\n{\"lesson\":5,\"title\":\"反手技术\",\"content\":\"反手高远球、反手吊球、反手杀球\"},\n{\"lesson\":6,\"title\":\"接杀技术\",\"content\":\"接杀挡网、接杀抽球\"},\n{\"lesson\":7,\"title\":\"平抽快挡\",\"content\":\"平抽球技术、双打中平抽快挡应用\"},\n{\"lesson\":8,\"title\":\"发球与接发球\",\"content\":\"发球变化、接发球抢攻\"},\n{\"lesson\":9,\"title\":\"防守反击\",\"content\":\"防守转攻的时机把握与技术运用\"},\n{\"lesson\":10,\"title\":\"进攻组织\",\"content\":\"杀吊结合、进攻节奏控制\"},\n{\"lesson\":11,\"title\":\"战术意识\",\"content\":\"单打基本战术、线路意识\"},\n{\"lesson\":12,\"title\":\"比赛实战\",\"content\":\"计分比赛、战术运用、综合考核\"}]','适合已掌握基础技术，能够进行稳定对打的学员','技术动作规范完整；具备基本的进攻和防守能力；能参与业余比赛','技术精细化、战术意识培养、实战能力提升','每周2次，共6周'),(3,20,1,'羽毛球竞技特训班','面向高水平学员，通过16次课进行技术精细化训练，构建完整战术体系，提升竞技水平。','1680.00',6,0,120,'每周六、日 14:00-16:00',3,1,'/uploads/badminton-3.jpg','2026-03-24 15:05:56','2026-04-13 17:58:04',16,'[{\"lesson\":1,\"title\":\"技术诊断与评估\",\"content\":\"全面技术评估、制定个性化训练计划\"},\n{\"lesson\":2,\"title\":\"杀球速度与变化\",\"content\":\"提升杀球速度、落点精准度\"},\n{\"lesson\":3,\"title\":\"吊球迷惑性\",\"content\":\"假动作吊球、延迟吊球\"},\n{\"lesson\":4,\"title\":\"网前技术高级\",\"content\":\"网前细腻手感、创造性假动作\"},\n{\"lesson\":5,\"title\":\"反手进阶\",\"content\":\"反手突击、反手杀球一致性\"},\n{\"lesson\":6,\"title\":\"接杀高级\",\"content\":\"接杀弹后场、接杀反抽对角\"},\n{\"lesson\":7,\"title\":\"假动作与一致性\",\"content\":\"击球一致性、假动作运用\"},\n{\"lesson\":8,\"title\":\"步法精进\",\"content\":\"快速启动、步伐节奏优化\"},\n{\"lesson\":9,\"title\":\"体能训练\",\"content\":\"专项体能、耐力与爆发力\"},\n{\"lesson\":10,\"title\":\"单打战术体系\",\"content\":\"控制与反控制战术、四方球运用\"},\n{\"lesson\":11,\"title\":\"双打战术配合\",\"content\":\"轮换进攻、前后封网战术\"},\n{\"lesson\":12,\"title\":\"比赛心理\",\"content\":\"心理调节、关键分处理\"},\n{\"lesson\":13,\"title\":\"多球训练\",\"content\":\"高强度多球训练、反应速度\"},\n{\"lesson\":14,\"title\":\"实战对抗\",\"content\":\"模拟比赛、战术执行\"},\n{\"lesson\":15,\"title\":\"战术复盘\",\"content\":\"比赛录像分析、战术调整\"},\n{\"lesson\":16,\"title\":\"综合考核与总结\",\"content\":\"结业比赛、技术考核、成长总结\"}]','适合具备扎实技术基础，渴望提升竞技水平的学员','技术全面且精细；具备完整战术体系；能参加正式比赛并取得成绩','技术精细化、战术体系构建、心理素质培养、体能强化','每周2次，共8周');

/*Table structure for table `course_student` */

DROP TABLE IF EXISTS `course_student`;

CREATE TABLE `course_student` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `course_id` bigint NOT NULL COMMENT '璇剧▼ID',
  `user_id` bigint NOT NULL COMMENT '瀛﹀憳ID',
  `status` tinyint DEFAULT '1' COMMENT '鐘舵?: 1-宸叉姤鍚?2-宸插畬鎴?3-宸插彇娑',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `course_student_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`),
  CONSTRAINT `course_student_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璇剧▼鎶ュ悕琛';

/*Data for the table `course_student` */

insert  into `course_student`(`id`,`course_id`,`user_id`,`status`,`create_time`) values (4,1,21,1,NULL);

/*Table structure for table `court` */

DROP TABLE IF EXISTS `court`;

CREATE TABLE `court` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '鍦哄湴ID',
  `name` varchar(100) NOT NULL COMMENT '鍦哄湴鍚嶇О',
  `location` varchar(255) NOT NULL COMMENT '鍦哄湴浣嶇疆',
  `type` tinyint NOT NULL DEFAULT '1' COMMENT '鍦哄湴绫诲瀷: 1-瀹ゅ唴鏈ㄥ湴 2-瀹ゅ唴鍦拌兌 3-瀹ゅ?',
  `price` decimal(10,2) NOT NULL COMMENT '浠锋牸(鍏?灏忔椂)',
  `status` tinyint DEFAULT '1' COMMENT '鐘舵?: 0-涓嶅彲鐢?1-鍙?敤',
  `capacity` int DEFAULT '4' COMMENT '瀹圭撼浜烘暟',
  `description` text COMMENT '鍦哄湴鎻忚堪',
  `open_time` varchar(100) DEFAULT '09:00-22:00' COMMENT '钀ヤ笟鏃堕棿',
  `image_url` varchar(500) DEFAULT NULL COMMENT '鍦哄湴鍥剧墖',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍦哄湴琛';

/*Data for the table `court` */

insert  into `court`(`id`,`name`,`location`,`type`,`price`,`status`,`capacity`,`description`,`open_time`,`image_url`,`create_time`,`update_time`) values (1,'羽毛球场地A1','体育馆一楼A区',1,'30.00',1,4,'标准室内木地场地','09:00-22:00','/uploads/badminton-1.jpg','2026-03-16 11:17:46','2026-03-31 11:29:22'),(2,'羽毛球场地A2','体育馆一楼A区',1,'30.00',1,4,'标准室内木地场地','09:00-22:00','/uploads/badminton-2.jpg','2026-03-16 11:17:46','2026-03-31 11:29:22'),(3,'羽毛球场地B1','体育馆二楼B区',2,'25.00',1,4,'室内地胶场地','09:00-22:00','/uploads/badminton-3.jpg','2026-03-16 11:17:46','2026-03-31 11:29:22'),(4,'羽毛球场地B2','体育馆二楼B区',2,'25.00',1,4,'室内地胶场地','09:00-22:00','/uploads/badminton-4.jpg','2026-03-16 11:17:46','2026-03-31 11:29:22'),(5,'室外羽毛球场1','学校操场西侧',3,'15.00',1,4,'室外硬地场地','09:00-22:00','/uploads/badminton-5.jpg','2026-03-16 11:17:46','2026-03-31 11:29:22');

/*Table structure for table `forum` */

DROP TABLE IF EXISTS `forum`;

CREATE TABLE `forum` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '甯栧瓙ID',
  `user_id` bigint NOT NULL COMMENT '鍙戝竷鑰匢D',
  `title` varchar(200) NOT NULL COMMENT '鏍囬?',
  `content` text NOT NULL COMMENT '鍐呭?',
  `image_url` varchar(1000) DEFAULT NULL COMMENT '鍥剧墖锛屽?涓?敤閫楀彿鍒嗛殧',
  `category` tinyint DEFAULT '1' COMMENT '鍒嗙被: 1-鎶?湳璁ㄨ? 2-绾︾悆 3-姣旇禌蹇冨緱 4-鍣ㄦ潗浜ゆ祦 5-鏂版墜姹傚姪',
  `likes` int DEFAULT '0' COMMENT '鐐硅禐鏁',
  `views` int DEFAULT '0' COMMENT '娴忚?閲',
  `status` tinyint DEFAULT '1' COMMENT '鐘舵?: 0-寰呭?鏍?1-宸插彂甯?2-宸插垹闄',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  CONSTRAINT `forum_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璁哄潧甯栧瓙琛';

/*Data for the table `forum` */

/*Table structure for table `forum_like` */

DROP TABLE IF EXISTS `forum_like`;

CREATE TABLE `forum_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `forum_id` bigint NOT NULL COMMENT '甯栧瓙ID',
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_forum_user` (`forum_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `forum_like_ibfk_1` FOREIGN KEY (`forum_id`) REFERENCES `forum` (`id`),
  CONSTRAINT `forum_like_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璁哄潧鐐硅禐琛';

/*Data for the table `forum_like` */

/*Table structure for table `message` */

DROP TABLE IF EXISTS `message`;

CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `content` text NOT NULL COMMENT '消息内容',
  `is_read` tinyint DEFAULT '0' COMMENT '是否已读: 0-未读 1-已读',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_sender` (`sender_id`),
  KEY `idx_receiver` (`receiver_id`),
  KEY `idx_conversation` (`sender_id`,`receiver_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息表';

/*Data for the table `message` */

insert  into `message`(`id`,`sender_id`,`receiver_id`,`content`,`is_read`,`create_time`) values (1,11,12,'111',0,NULL);

/*Table structure for table `payment` */

DROP TABLE IF EXISTS `payment`;

CREATE TABLE `payment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '鏀?粯ID',
  `booking_id` bigint DEFAULT NULL COMMENT '棰勭害ID',
  `course_id` bigint DEFAULT NULL COMMENT '璇剧▼ID',
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛ID',
  `amount` decimal(10,2) NOT NULL COMMENT '鏀?粯閲戦?',
  `payment_method` tinyint DEFAULT NULL COMMENT '鏀?粯鏂瑰紡: 1-寰?俊 2-鏀?粯瀹?3-浣欓?',
  `trade_no` varchar(100) DEFAULT NULL COMMENT '绗?笁鏂逛氦鏄撳彿',
  `status` tinyint DEFAULT '1' COMMENT '鐘舵?: 1-寰呮敮浠?2-宸叉敮浠?3-宸查?娆',
  `payment_time` datetime DEFAULT NULL COMMENT '鏀?粯鏃堕棿',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_booking_id` (`booking_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`),
  CONSTRAINT `payment_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`),
  CONSTRAINT `payment_ibfk_3` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鏀?粯琛';

/*Data for the table `payment` */

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '鐢ㄦ埛ID',
  `username` varchar(50) NOT NULL COMMENT '鐢ㄦ埛鍚',
  `password` varchar(255) NOT NULL COMMENT '瀵嗙爜',
  `real_name` varchar(50) DEFAULT NULL COMMENT '鐪熷疄濮撳悕',
  `phone` varchar(20) DEFAULT NULL COMMENT '鎵嬫満鍙',
  `email` varchar(100) DEFAULT NULL COMMENT '閭??',
  `avatar` varchar(255) DEFAULT '/avatar/default.png' COMMENT '澶村儚URL',
  `role` tinyint NOT NULL DEFAULT '1' COMMENT '瑙掕壊: 1-鏅??鐢ㄦ埛 2-鏁欑粌 3-绠＄悊鍛',
  `balance` decimal(10,2) DEFAULT '0.00' COMMENT '璐︽埛浣欓?',
  `status` tinyint DEFAULT '1' COMMENT '鐘舵?: 0-绂佺敤 1-姝ｅ父',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_username` (`username`),
  KEY `idx_role` (`role`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鐢ㄦ埛琛';

/*Data for the table `user` */

insert  into `user`(`id`,`username`,`password`,`real_name`,`phone`,`email`,`avatar`,`role`,`balance`,`status`,`create_time`,`update_time`) values (19,'admin','$2b$10$BjN05mu7iCTruzuJnDXVBuBf7Fbo9xU3G3V8ZRW.fG6lE.U/JNixG','系统管理员','13800000000',NULL,'/avatar/default.png',3,'0.00',1,'2026-04-13 16:33:54','2026-04-13 18:01:15'),(20,'coach','$2b$10$0kzWSJ2IsLd.1SZTg3RlPOgYAYamoLtNJisM2Nj1A1glgMVOgZ5vO','肖教练','13800000001',NULL,'/avatar/default.png',2,'0.00',1,'2026-04-13 16:38:17','2026-04-13 18:01:15'),(21,'user','$2b$10$lrWHZBM7KQwXVXdCUEj4jO2QBq/xc0Jny9NlkxjPE7pA3xwepnGeO','普通用户','13800000002',NULL,'/avatar/default.png',1,'0.00',1,'2026-04-13 16:39:19','2026-04-13 18:01:15');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
