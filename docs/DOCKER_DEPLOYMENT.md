# Lebo 后端项目 Docker 部署指南

## 快速开始

### 方式一: 使用 Docker Compose 一键部署(推荐)

1. **克隆项目并进入目录**
   ```bash
   cd /path/to/lebo
   ```

2. **配置环境变量**
   ```bash
   # 复制环境变量模板
   cp .env.example .env

   # 编辑 .env 文件,填入实际的配置信息
   vi .env
   ```

   **必须配置的变量:**
   - `WECHAT_APP_ID`: 微信小程序 AppID
   - `WECHAT_APP_SECRET`: 微信小程序 AppSecret

3. **构建并启动服务**
   ```bash
   # 构建镜像并启动所有服务
   docker-compose up --build -d

   # 查看日志
   docker-compose logs -f lebo-app

   # 查看所有服务状态
   docker-compose ps
   ```

4. **访问应用**
   - 应用地址: http://localhost:8080
   - MySQL: localhost:3306
   - Redis: localhost:6379
   - Kafka: localhost:9092

### 方式二: 只部署应用,使用外部数据库

如果你已有运行中的 MySQL、Redis 和 Kafka 服务:

1. **配置环境变量**
   ```bash
   # 复制环境变量模板
   cp .env.example .env

   # 编辑 .env 文件,修改外部服务的连接信息
   vi .env

   # 例如:
   # MYSQL_HOST=your_mysql_host
   # MYSQL_PORT=3306
   # MYSQL_USERNAME=your_mysql_user
   # MYSQL_PASSWORD=your_mysql_password
   # REDIS_HOST=your_redis_host
   # REDIS_PORT=6379
   # REDIS_PASSWORD=your_redis_password
   # KAFKA_BROKERS=your_kafka_broker:9092
   ```

2. **修改 docker-compose.yml**
   ```yaml
   # 注释掉或删除 mysql、redis、kafka 服务
   # 只保留 lebo-app 服务
   ```

3. **构建并启动应用**
   ```bash
   docker-compose up --build -d
   ```

## 常用命令

### 构建镜像
```bash
# 使用 Docker Compose
docker-compose build

# 使用 Docker 直接构建
docker build -t lebo:latest .
```

### 启动和停止
```bash
# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose down

# 停止并删除所有卷(会删除数据!)
docker-compose down -v

# 重启服务
docker-compose restart
```

### 查看日志
```bash
# 查看应用日志
docker-compose logs -f lebo-app

# 查看所有服务日志
docker-compose logs -f
```

### 进入容器
```bash
# 进入应用容器
docker-compose exec lebo-app sh

# 进入数据库容器
docker-compose exec mysql mysql -uroot -p
```

### 更新应用
```bash
# 拉取最新代码后重新构建
git pull
docker-compose up --build -d
```

## 生产环境部署建议

1. **修改数据库密码**
   ```bash
   # 在 .env 文件中修改为强密码
   MYSQL_PASSWORD=your_strong_password_here
   ```

2. **配置 JVM 参数**
   ```bash
   # 根据服务器配置调整内存
   JAVA_OPTS=-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200
   ```

3. **数据持久化**
   ```yaml
   # 在 docker-compose.yml 中已配置数据卷
   # 数据会保存在 Docker 管理的卷中,容器重启不会丢失
   ```

4. **健康检查**
   ```bash
   # Dockerfile 中已配置健康检查
   # 可以通过 docker inspect 查看健康状态
   docker inspect --format='{{.State.Health.Status}}' lebo-backend
   ```

5. **安全建议**
   - 不要将 `.env` 文件提交到版本控制
   - 使用强密码
   - 限制容器端口暴露(只暴露必要端口)
   - 定期更新基础镜像

## 故障排查

### 问题 1: 应用无法连接数据库
```bash
# 检查数据库服务是否运行
docker-compose ps mysql

# 查看数据库日志
docker-compose logs mysql

# 测试数据库连接
docker-compose exec lebo-app sh
ping mysql
```

### 问题 2: 启动失败
```bash
# 查看详细日志
docker-compose logs lebo-app

# 检查环境变量是否正确配置
docker-compose exec lebo-app env
```

### 问题 3: 内存不足
```bash
# 编辑 .env 文件,减少 JVM 内存配置
JAVA_OPTS=-Xms256m -Xmx512m
```

## 端口说明

| 服务 | 内部端口 | 外部端口 | 说明 |
|------|---------|---------|------|
| 应用 | 8080 | 8080 | Spring Boot 应用 |
| MySQL | 3306 | 3306 | 数据库 |
| Redis | 6379 | 6379 | 缓存 |
| Kafka | 9092, 29092 | 9092 | 消息队列 |
| Zookeeper | 2181 | 2181 | Kafka 依赖 |

## 环境变量说明

详见 `.env.example` 文件中的详细说明。

## 技术栈

- **Java**: 21 (JDK 21)
- **Spring Boot**: 3.3.4
- **数据库**: MySQL 8.0
- **缓存**: Redis 7
- **消息队列**: Kafka 2.13 (Confluent Platform 7.5.0)
- **构建工具**: Maven 3.9
- **容器**: Docker & Docker Compose

## 注意事项

1. 首次启动 MySQL 可能需要较长时间等待初始化
2. 健康检查启动时间设置为 60 秒,确保应用有足够时间启动
3. 配置文件中的敏感信息请妥善保管
4. 生产环境建议使用外部数据库和相关服务,不要使用 compose 中的有状态服务

## 许可证

Copyright © 2026 MuziSuper. All rights reserved.
