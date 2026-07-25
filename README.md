# 害虫识别交流网站

## 项目简介

这是一个面向农业场景的害虫识别与交流平台。用户可以上传害虫图片，系统会调用 YOLO 模型进行目标检测，返回害虫类别、置信度和带检测框的结果图，并保存识别历史。项目还提供文章社区、用户关注、频道交流、私信、后台管理和数据统计等功能，适合用于害虫识别展示、农业知识分享和检测记录管理。

## 核心功能

- 害虫图片识别：支持多图上传，调用 Flask 推理服务识别害虫类别并返回置信度。
- 识别结果管理：保存识别后的图片、类别、置信度和识别时间，支持用户查看与删除历史记录。
- 文章社区：支持文章发布、封面上传、分类筛选、搜索、阅读、点赞、收藏和评论。
- 用户系统：支持注册、登录、JWT 鉴权、个人资料、头像、密码修改、关注与粉丝统计。
- 实时交流：基于 WebSocket/STOMP 实现频道聊天和用户私信。
- 后台管理：支持用户、管理员、文章、分类、评论、点赞、收藏、频道、聊天记录和识别记录管理。
- 数据看板：统计注册用户、文章数量、识别数量、频道消息数量，并展示害虫类别占比和平均置信度图表。
- 云端文件存储：图片上传到阿里云 OSS，前端、Spring 后端和 Flask 推理服务围绕图片 URL 协同工作。

## 技术栈

### 前端

- Vue 3
- Vite
- Vue Router
- Pinia
- Element Plus
- Ant Design Vue
- Axios
- STOMP WebSocket
- wangEditor
- ECharts

### Spring Boot 后端

- Java 8
- Spring Boot 2.6.13
- Spring Web
- Spring WebSocket
- Spring Data JPA
- MyBatis Plus
- MySQL
- Redis
- JWT
- Lombok
- 阿里云 OSS SDK

### Flask 推理服务

- Python
- Flask
- Ultralytics YOLO
- PyTorch
- Pillow
- Requests
- oss2

## 项目架构

```text
pest-detection/
├── frontend/                  # Vue 3 前端项目
│   ├── src/api/               # Axios 接口封装
│   ├── src/router/            # 前端路由
│   ├── src/stores/            # Pinia 状态管理
│   ├── src/views/             # 前台与后台页面
│   └── src/assets/            # 图片、样式、图标字体等静态资源
├── backend-spring/            # Spring Boot 业务后端
│   ├── src/main/java/.../controller/  # REST API 与 WebSocket 控制器
│   ├── src/main/java/.../service/     # 业务逻辑
│   ├── src/main/java/.../mapper/      # MyBatis 数据访问
│   ├── src/main/java/.../entity/      # 实体类
│   └── src/main/resources/            # 应用配置与静态资源
└── backend-flask/             # Flask + YOLO 推理服务
    ├── YOLOv11检测.py          # 图片识别接口
    ├── oss_util.py            # 推理结果图上传 OSS
    ├── result.py              # 统一响应结构
    ├── 筛选置信度图片.py       # 高置信度图片筛选脚本
    └── 模型/                  # YOLO 权重文件
```

## 识别流程

1. 用户在前端“害虫识别”页面选择图片。
2. 前端通过 `/api/file/upload` 将原图上传到 Spring Boot 后端。
3. Spring Boot 将图片上传到阿里云 OSS，并返回图片 URL。
4. 前端把图片 URL 发送到 Flask 服务的 `/flask/detect`。
5. Flask 下载图片，调用 YOLO 模型识别害虫，并生成带检测框的结果图。
6. Flask 将结果图上传到 OSS，返回结果图 URL、害虫类别和置信度。
7. 前端调用 `/api/detect/insertDetectInfo`，将识别结果保存到 Spring Boot 后端数据库。
8. 用户可以在“识别历史”中查看或删除历史记录。

## 快速开始

### 1. 准备环境

需要提前安装：

- Node.js 和 npm
- Java 8
- Maven
- Python
- MySQL
- Redis

还需要准备阿里云 OSS 的 Bucket、Endpoint、AccessKeyId 和 AccessKeySecret。

### 2. 配置 Spring Boot 后端

编辑：

```text
backend-spring/src/main/resources/application.properties
```

配置 MySQL、Redis 和 OSS：

```properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/你的数据库名?serverTimezone=UTC
spring.datasource.username=你的数据库用户名
spring.datasource.password=你的数据库密码

aliyun.oss.end-point=你的 OSS Endpoint
aliyun.oss.access-key-id=你的 AccessKeyId
aliyun.oss.access-key-secret=你的 AccessKeySecret
aliyun.oss.bucket-name=你的 Bucket 名称

spring.redis.host=你的 Redis 地址
spring.redis.port=你的 Redis 端口
```

启动 Spring Boot：

```bash
cd backend-spring
mvn spring-boot:run
```

默认服务地址：

```text
http://localhost:8080
```

### 3. 配置 Flask 推理服务

编辑：

```text
backend-flask/oss_util.py
```

填写 OSS 配置。然后确认模型路径与本地权重文件一致。当前推理脚本中默认读取：

```python
MODEL_PATH = '模型/50+120/best.pt'
```

仓库中已包含多个训练轮次的权重目录，例如：

```text
backend-flask/模型/50轮/best.pt
backend-flask/模型/50+120轮/best.pt
backend-flask/模型/50+120+100轮/best.pt
backend-flask/模型/50+120+100+130轮/best.pt
```

如果直接运行，请把 `MODEL_PATH` 修改为实际存在的模型文件路径。

安装依赖并启动 Flask：

```bash
cd backend-flask
pip install -r requirements.txt
python YOLOv11检测.py
```

默认服务地址：

```text
http://localhost:5000
```

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端开发代理配置在 `frontend/vite.config.js`：

```text
/api   -> http://localhost:8080
/flask -> http://localhost:5000
```

默认前端地址通常为：

```text
http://localhost:5173
```

## 主要接口模块

- `/user`：注册、登录、用户信息、头像、密码、关注、管理员管理。
- `/file`：图片上传到 OSS。
- `/detect`：检测结果保存、查询、删除和后台检测记录列表。
- `/article`：文章、评论、点赞、收藏、阅读量。
- `/category`：文章分类管理。
- `/channel`：频道创建、订阅、启用、禁用和删除。
- `/chat`：频道消息、私信列表、私信记录和 WebSocket 消息处理。
- `/ws`：WebSocket/STOMP 连接端点。

## 支持识别的害虫类别

Flask 推理服务内置了英文类别到中文类别的映射，覆盖稻纵卷叶螟、二化螟、褐飞虱、白背飞虱、稻水象甲、玉米螟、蚜虫、小麦吸浆虫、甜菜夜蛾、蝗虫、蓟马、葡萄根瘤蚜、温室白粉虱、柑橘凤蝶、柑橘潜叶蛾、橘蚜、茶黄蓟马、芒果切叶象甲、叶蝉科等多种常见农业害虫。

## 注意事项

- `application.properties` 和 `oss_util.py` 中的数据库、Redis、OSS 配置建议改为环境变量或本地私有配置，避免真实密钥上传到公开仓库。
- 当前仓库没有单独提供数据库初始化 SQL，运行前需要根据实体类和 Mapper 中的表名准备 MySQL 表结构。
- Flask 依赖中包含 PyTorch CUDA 版本，CPU 或不同 CUDA 环境需要按实际机器调整安装方式。
- 前端 WebSocket 默认连接 `ws://localhost:8080/ws`，部署到服务器时需要同步修改前端配置和反向代理。

## License

本项目使用 MIT License，详情见 `LICENSE`。

---

# Pest Detection Community Platform

## Overview

This is an agricultural pest detection and community platform. Users can upload pest images, run YOLO-based object detection, receive pest categories, confidence scores, and annotated result images, and keep detection history. The project also includes article sharing, user follows, channel chat, private messaging, admin management, and dashboard statistics.

## Core Features

- Pest image detection: upload multiple images and call the Flask inference service to detect pest categories and confidence scores.
- Detection history: store annotated images, pest names, confidence values, and detection time; users can view or delete their records.
- Article community: publish articles, upload covers, filter by category, search, read, like, collect, and comment.
- User system: registration, login, JWT authentication, profile editing, avatar upload, password update, follows, and fan counts.
- Real-time communication: channel chat and private messages based on WebSocket/STOMP.
- Admin panel: manage users, administrators, articles, categories, comments, likes, collects, channels, chat records, and detection records.
- Data dashboard: show user count, article count, detection count, channel message count, pest category distribution, and average confidence charts.
- Cloud file storage: images are uploaded to Alibaba Cloud OSS and shared between the frontend, Spring Boot backend, and Flask inference service through image URLs.

## Tech Stack

### Frontend

- Vue 3
- Vite
- Vue Router
- Pinia
- Element Plus
- Ant Design Vue
- Axios
- STOMP WebSocket
- wangEditor
- ECharts

### Spring Boot Backend

- Java 8
- Spring Boot 2.6.13
- Spring Web
- Spring WebSocket
- Spring Data JPA
- MyBatis Plus
- MySQL
- Redis
- JWT
- Lombok
- Alibaba Cloud OSS SDK

### Flask Inference Service

- Python
- Flask
- Ultralytics YOLO
- PyTorch
- Pillow
- Requests
- oss2

## Architecture

```text
pest-detection/
├── frontend/                  # Vue 3 frontend
│   ├── src/api/               # Axios API wrappers
│   ├── src/router/            # Frontend routes
│   ├── src/stores/            # Pinia stores
│   ├── src/views/             # User-facing and admin pages
│   └── src/assets/            # Images, styles, icon fonts, and static assets
├── backend-spring/            # Spring Boot business backend
│   ├── src/main/java/.../controller/  # REST APIs and WebSocket controllers
│   ├── src/main/java/.../service/     # Business logic
│   ├── src/main/java/.../mapper/      # MyBatis data access
│   ├── src/main/java/.../entity/      # Entity classes
│   └── src/main/resources/            # App config and static resources
└── backend-flask/             # Flask + YOLO inference service
    ├── YOLOv11检测.py          # Image detection API
    ├── oss_util.py            # Upload annotated results to OSS
    ├── result.py              # Unified response wrapper
    ├── 筛选置信度图片.py       # High-confidence image filtering script
    └── 模型/                  # YOLO weight files
```

## Detection Workflow

1. The user selects images on the pest detection page.
2. The frontend uploads original images to the Spring Boot backend through `/api/file/upload`.
3. Spring Boot uploads images to Alibaba Cloud OSS and returns image URLs.
4. The frontend sends image URLs to the Flask endpoint `/flask/detect`.
5. Flask downloads each image, runs YOLO detection, and generates an annotated result image.
6. Flask uploads the annotated image to OSS and returns the result image URL, pest category, and confidence score.
7. The frontend calls `/api/detect/insertDetectInfo` to save detection results in the Spring Boot database.
8. Users can view or delete saved records on the detection history page.

## Quick Start

### 1. Prerequisites

Install the following:

- Node.js and npm
- Java 8
- Maven
- Python
- MySQL
- Redis

You also need an Alibaba Cloud OSS Bucket, Endpoint, AccessKeyId, and AccessKeySecret.

### 2. Configure the Spring Boot Backend

Edit:

```text
backend-spring/src/main/resources/application.properties
```

Configure MySQL, Redis, and OSS:

```properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/your_database?serverTimezone=UTC
spring.datasource.username=your_database_username
spring.datasource.password=your_database_password

aliyun.oss.end-point=your_oss_endpoint
aliyun.oss.access-key-id=your_access_key_id
aliyun.oss.access-key-secret=your_access_key_secret
aliyun.oss.bucket-name=your_bucket_name

spring.redis.host=your_redis_host
spring.redis.port=your_redis_port
```

Start Spring Boot:

```bash
cd backend-spring
mvn spring-boot:run
```

Default backend URL:

```text
http://localhost:8080
```

### 3. Configure the Flask Inference Service

Edit:

```text
backend-flask/oss_util.py
```

Fill in your OSS configuration. Then make sure the model path matches an existing weight file. The current inference script reads:

```python
MODEL_PATH = '模型/50+120/best.pt'
```

The repository contains several trained weight directories, such as:

```text
backend-flask/模型/50轮/best.pt
backend-flask/模型/50+120轮/best.pt
backend-flask/模型/50+120+100轮/best.pt
backend-flask/模型/50+120+100+130轮/best.pt
```

If you run the service directly, update `MODEL_PATH` to a path that exists locally.

Install dependencies and start Flask:

```bash
cd backend-flask
pip install -r requirements.txt
python YOLOv11检测.py
```

Default inference URL:

```text
http://localhost:5000
```

### 4. Start the Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend development proxies are configured in `frontend/vite.config.js`:

```text
/api   -> http://localhost:8080
/flask -> http://localhost:5000
```

The default frontend URL is usually:

```text
http://localhost:5173
```

## Main API Modules

- `/user`: registration, login, user profile, avatar, password, follows, and administrator management.
- `/file`: image upload to OSS.
- `/detect`: save, query, delete, and manage detection records.
- `/article`: articles, comments, likes, collects, and view counts.
- `/category`: article category management.
- `/channel`: channel creation, subscription, enable, disable, and deletion.
- `/chat`: channel messages, private chat list, private messages, and WebSocket message handling.
- `/ws`: WebSocket/STOMP connection endpoint.

## Supported Pest Categories

The Flask inference service includes an English-to-Chinese class mapping. It covers many common agricultural pests, including rice leaf roller, yellow rice borer, brown plant hopper, white backed plant hopper, rice water weevil, corn borer, aphids, wheat blossom midge, beet army worm, locusts, thrips, grape phylloxera, greenhouse whitefly, citrus swallowtail, citrus leaf miner, citrus aphid, yellow tea thrips, mango leaf-cutting weevil, and leafhopper families.

## Notes

- Database, Redis, and OSS settings in `application.properties` and `oss_util.py` should be moved to environment variables or private local config before publishing real credentials.
- The repository does not include a standalone database initialization SQL file. Prepare the MySQL schema according to entity classes and table names used in Mapper SQL.
- The Flask dependency list includes a CUDA PyTorch build. CPU-only or different CUDA environments should adjust the installation commands accordingly.
- The frontend WebSocket client connects to `ws://localhost:8080/ws` by default. Update the frontend config and reverse proxy rules when deploying to a server.

## License

This project is licensed under the MIT License. See `LICENSE` for details.
