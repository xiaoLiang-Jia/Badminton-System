# 羽毛球场地管理系统 - IDEA运行指南

## 项目技术栈

- **后端**: Spring Boot 2.7.18 + MyBatis Plus + MySQL + Redis
- **前端**: Vue 3 + Vite + Element Plus
- **JDK版本**: Java 11

## 前置条件

在运行项目前，确保本地已安装以下软件：

| 软件 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 11+ | Java开发环境 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.0+ | 缓存数据库 |
| Maven | 3.6+ | 后端依赖管理 |
| Node.js | 16+ | 前端运行环境 |
| IDEA | 2021+ | 开发IDE |

---

## 第一步：数据库配置

### 1.1 安装并启动MySQL

1. 下载安装 [MySQL 8.0](https://dev.mysql.com/downloads/mysql/)
2. 启动MySQL服务

### 1.2 创建数据库

```sql
CREATE DATABASE badminton_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 1.3 配置数据库连接

打开 `badminton-backend/src/main/resources/application.yml`，修改数据库配置：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/badminton_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root      # 修改为你的MySQL用户名
    password: 123456  # 修改为你的MySQL密码
```

---

## 第二步：Redis配置

### 2.1 安装并启动Redis

1. 下载 [Redis for Windows](https://github.com/tporadowski/redis/releases) 或使用 WSL/Linux
2. 启动Redis服务（默认端口6379）

### 2.2 配置Redis连接

打开 `application.yml`，修改Redis配置：

```yaml
spring:
  redis:
    host: localhost      # Redis服务器地址
    port: 6379          # Redis端口
    password: 123456    # Redis密码（如果有）
    database: 0
```

---

## 第三步：IDEA导入后端项目

### 3.1 打开项目

1. 打开IDEA
2. File -> Open
3. 选择 `C:/桌面/code/badminton-backend`
4. 点击OK，IDEA会自动识别为Maven项目

### 3.2 配置JDK

1. File -> Project Structure (Ctrl+Alt+Shift+S)
2. Project Settings -> Project
3. SDK选择JDK 11+（如果没有，点击Add SDK -> JDK）
4. Project language level选择11

### 3.3 等待Maven下载依赖

IDEA右侧Maven面板会自动下载依赖，或者点击：

```
View -> Tool Windows -> Maven
```

展开Lifecycle，双击 `install` 下载所有依赖。

---

## 第四步：运行后端项目

### 4.1 找到启动类

后端启动类位于：
```
src/main/java/com/badminton/BadmintonApplication.java
```

### 4.2 运行项目

1. 右键点击 `BadmintonApplication.java`
2. 选择 Run 'BadmintonApplication'
3. 或者使用快捷键 Shift+F10

### 4.3 验证后端启动

控制台出现以下日志表示启动成功：

```
Tomcat started on port(s): 8080 (http)
Started BadmintonApplication in x.xxx seconds
```

---

## 第五步：运行前端项目

### 5.1 使用IDEA前端支持

如果IDEA安装了Vue插件：

1. File -> Open
2. 选择 `C:/桌面/code/badminton-frontend`
3. IDEA会识别为Vue项目

### 5.2 使用命令行

1. 打开终端，进入前端目录：
```bash
cd C:/桌面/code/badminton-frontend
```

2. 安装依赖（如果node_modules不存在）：
```bash
npm install
```

3. 启动开发服务器：
```bash
npm run dev
```

### 5.3 修改API地址（可选）

如果前端API地址不是 `http://localhost:8080`，修改：
```
badminton-frontend/src/api/index.js
```

---

## 第六步：访问系统

| 服务 | 地址 |
|------|------|
| 后端API | http://localhost:8080 |
| 前端页面 | http://localhost:5173 |

---

## 常见问题

### Q1: 端口被占用

修改 `application.yml` 中的端口：
```yaml
server:
  port: 8081
```

### Q2: MySQL连接失败

1. 确认MySQL服务已启动
2. 检查用户名密码是否正确
3. 确认 `badminton_db` 数据库已创建

### Q3: Redis连接失败

1. 确认Redis服务已启动
2. 检查Redis密码配置

### Q4: 前端跨域问题

后端已配置CORS，如有问题检查：
```
src/main/java/com/badminton/config/CorsConfig.java
```

---

## 项目结构

```
C:/桌面/code/
├── badminton-backend/          # 后端 Spring Boot 项目
│   ├── src/main/java/com/badminton/
│   │   ├── controller/        # 控制器
│   │   ├── service/          # 业务逻辑
│   │   ├── mapper/           # 数据访问层
│   │   ├── entity/           # 实体类
│   │   ├── config/           # 配置类
│   │   ├── dto/              # 数据传输对象
│   │   ├── utils/            # 工具类
│   │   └── common/            # 公共类
│   ├── src/main/resources/
│   │   └── application.yml  # 应用配置
│   └── pom.xml               # Maven配置
│
├── badminton-frontend/         # 前端 Vue 3 项目
│   ├── src/
│   │   ├── api/             # API请求
│   │   ├── views/           # 页面组件
│   │   ├── router/          # 路由配置
│   │   └── stores/         # 状态管理
│   ├── index.html
│   └── package.json         # NPM配置
│
└── design-document.md        # 设计文档
```

---

## 默认账号

系统运行后，可使用以下方式注册新用户或联系管理员创建账号。