# 乐播智能商铺客户服务平台 - 后端服务

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.7-blue.svg)](https://baomidou.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📖 项目简介

**乐播智能商铺客户服务平台**（Lebo）是一个面向个体商户的智能化客户管理与营销平台后端服务系统。该平台旨在帮助传统个体商户实现数字化转型，通过微信小程序为商户提供商品管理、订单处理、客户关系维护、数据统计分析等功能，助力商户提升运营效率与客户粘性。

本项目采用现代化的微服务架构思想，基于 Spring Boot 3.x 构建，集成了用户认证、商品管理、订单处理、积分系统、消息推送、数据备份等核心业务模块，支持高并发场景下的稳定运行。

### 业务背景

随着移动互联网的普及，越来越多的个体商户面临信息化不足、客流量增长乏力等问题。传统的线下经营模式已难以满足现代消费者的需求。乐播平台应运而生，为商户提供：

- **数字化商品管理**：支持商品上下架、库存管理、分类管理等
- **智能化订单处理**：全流程订单跟踪，支持多种支付方式
- **客户关系维护**：用户画像、积分体系、签到奖励等营销工具
- **数据驱动决策**：实时统计报表、出入库大盘分析
- **即时消息触达**：WebSocket 实时消息推送，提升用户活跃度

## 🏗️ 技术架构

### 核心技术栈

| 技术组件 | 版本 | 说明 |
|---------|------|------|
| **Spring Boot** | 3.3.4 | 应用框架基础 |
| **Java** | 17 | 运行时环境 |
| **MyBatis-Plus** | 3.5.7 | ORM 框架，简化数据库操作 |
| **MySQL** | 8.0.33 | 关系型数据库 |
| **Redis** | - | 缓存与会话管理 |
| **Kafka** | - | 消息队列，异步任务处理 |
| **JWT (jjwt)** | 0.12.6 | 用户身份认证与授权 |
| **WebSocket** | - | 实时订单消息推送 |
| **Fastjson2** | 2.0.32 | JSON 序列化/反序列化 |
| **Lombok** | 1.18.42 | 代码简化工具 |
| **Apache HttpClient** | 4.5.14 | HTTP 客户端，用于微信接口调用 |
| **JaCoCo** | 0.8.12 | 代码覆盖率检测（目标 ≥95%） |
| **Maven** | 3.9.6 | 项目构建与依赖管理 |
| **Docker** | - | 容器化部署 |

**注意**：虽然 pom.xml 中包含 Redis 和 Kafka 依赖，但当前版本未实际启用这些组件（Redis 健康检查已禁用，Kafka 无相关配置）。

### 架构特点

- **分层架构设计**：严格遵循 Controller-Service-Mapper 三层架构，职责清晰
- **RESTful API 规范**：统一接口风格，便于前端对接与维护
- **JWT 无状态认证**：支持分布式部署，会话信息通过 JWT Token 管理
- **完善的异常处理**：全局异常拦截器，统一错误响应格式
- **高代码质量保障**：JaCoCo 代码覆盖率要求 ≥95%，确保核心逻辑测试覆盖
- **本地文件存储**：使用本地文件系统存储上传的文件，支持分类目录管理

## 📦 项目结构

```
lebo/
├── src/main/java/cn/muzisheng/lebo/
│   ├── LeboApplication.java          # 应用启动类
│   ├── api/                          # 控制器层（16个API接口）
│   │   ├── UserApi.java              # 用户管理接口
│   │   ├── ProductApi.java           # 商品管理接口
│   │   ├── OrderApi.java             # 订单管理接口
│   │   ├── CategoryApi.java          # 商品分类接口
│   │   ├── InformationApi.java       # 资讯公告接口
│   │   ├── SlideshowApi.java         # 轮播图管理接口
│   │   ├── AwardApi.java             # 抽奖活动接口
│   │   ├── UserPointApi.java         # 用户积分接口
│   │   ├── UserSignInApi.java        # 用户签到接口
│   │   ├── UserPointRecordApi.java   # 积分记录接口
│   │   ├── HistoryOperationApi.java  # 历史操作记录接口
│   │   ├── InOutProductRecordApi.java # 商品出入库记录接口
│   │   ├── WxCloudStorageApi.java    # 微信云存储接口
│   │   ├── BackupApi.java            # 数据备份接口
│   │   ├── GeneralApi.java           # 通用功能接口
│   │   └── ExceptionApi.java         # 异常测试接口
│   ├── service/                      # 业务逻辑层
│   │   ├── impl/                     # 服务实现类
│   │   ├── UserService.java
│   │   ├── ProductService.java
│   │   ├── OrderService.java
│   │   ├── CategoryService.java
│   │   ├── InformationService.java
│   │   ├── UserPointService.java
│   │   ├── UserSignInService.java
│   │   ├── AwardRecordService.java
│   │   ├── PointRecordService.java
│   │   ├── OrderItemService.java
│   │   ├── StorageService.java
│   │   ├── WXService.java            # 微信接口服务
│   │   ├── WxCloudStorageService.java
│   │   ├── BackupService.java
│   │   ├── HistoryOperationService.java
│   │   └── InOutProductRecordService.java
│   ├── mapper/                       # 数据访问层（MyBatis Mapper）
│   ├── entity/                       # 数据库实体类
│   ├── dto/                          # 数据传输对象（请求参数）
│   ├── vo/                           # 视图对象（响应数据）
│   ├── param/                        # 查询参数封装
│   ├── model/                        # 通用模型（如 Result 统一响应）
│   ├── config/                       # 配置类（Redis、WebSocket、JWT等）
│   ├── interceptor/                  # 拦截器（JWT认证拦截）
│   ├── handler/                      # 处理器（全局异常处理等）
│   ├── exception/                    # 自定义异常类
│   ├── constant/                     # 常量定义
│   ├── properties/                   # 配置属性绑定类
│   └── utils/                        # 工具类（JWT、加密、日期等）
├── src/test/java/                    # 单元测试代码
├── docs/                             # 项目文档
├── logs/                             # 日志文件目录
├── pom.xml                           # Maven 配置文件
├── Dockerfile                        # Docker 镜像构建文件
├── settings.xml                      # Maven 私服配置
├── 后端系分文档.md                   # 后端系统设计文档
├── API接口文档.md                    # API 接口详细文档
├── 前后端接口联调规范.md             # 接口联调规范
└── README.md                         # 项目说明文档
```

## 🚀 核心功能模块

### 1. 用户管理系统 (`UserApi`)

**功能描述**：提供微信小程序用户的完整生命周期管理

**主要接口**：
- `POST /user/login` - 微信小程序登录（通过 jscode 换取 openid/session_key）
- `POST /user/register` - 用户注册（支持邀请码等额外信息）
- `POST /user/bossLogin` - 商户后台管理登录
- `GET /user/info` - 获取当前登录用户信息（需 JWT Token）
- `POST /user/update` - 更新用户资料（昵称、头像、手机号等）
- `POST /user/logout` - 用户注销（清除服务端登录状态）
- `POST /user/list` - 分页查询用户列表（支持昵称、手机号、状态等多条件筛选）
- `POST /user/listByOpenIds` - 批量查询用户信息（通过 openid 列表）

**技术亮点**：
- 集成微信官方登录接口，自动处理 session_key 过期刷新
- JWT Token 认证机制，Token 有效期可配置
- 支持商户后台与普通用户双角色登录体系

### 2. 商品管理系统 (`ProductApi`)

**功能描述**：提供商品的 CRUD 操作及库存管理

**主要接口**：
- `POST /product/list` - 分页查询商品列表（支持关键词搜索、分类筛选、状态过滤）
- `POST /product/add` - 新增商品（名称、描述、价格、库存、图片等）
- `POST /product/update` - 更新商品信息
- `POST /product/delete` - 删除商品（软删除）
- `POST /product/inout` - 商品出入库操作（自动记录出入库流水）
- `GET /product/dashboard` - 出入库数据大盘（昨日至今日的出入库量、当前库存总量及金额）

**技术亮点**：
- 支持商品多条件复合查询，性能优化使用 MyBatis-Plus 分页插件
- 出入库操作原子性保证，防止并发库存异常
- 自动记录每次出入库操作，形成完整的库存流水账
- 数据大盘接口提供实时经营数据分析

### 3. 订单管理系统 (`OrderApi`)

**功能描述**：完整的订单生命周期管理，从创建到结算的全流程

**主要接口**：
- `POST /order/create` - 创建订单（校验库存、计算总价、生成订单号）
- `POST /order/submit` - 确认支付（更新订单状态、记录支付方式与时间）
- `POST /order/cancel` - 取消订单（释放库存、标记支付失败）
- `POST /order/over` - 商家确认订单完成（更新结束时间、最终支付金额）
- `POST /order/reject` - 商家拒绝接单（订单退款、库存回滚）
- `POST /order/orderInfolist` - 用户查询自己的订单列表（按状态筛选）
- `POST /order/bossOrderInfolist` - 商家查询订单列表（支持时间区间、支付方式、订单ID等多条件分页查询）
- `POST /order/detail` - 查询订单详情（包含订单项明细）

**技术亮点**：
- 订单状态机设计：待支付 → 已支付 → 已完成/已退款
- 创建订单时校验库存充足性，防止超卖
- 拒绝接单时自动回滚库存，保证数据一致性
- 支持商家多维度订单筛选，便于经营管理

### 4. 商品分类管理 (`CategoryApi`)

**主要接口**：
- `POST /category/list` - 查询商品分类列表
- `POST /category/add` - 新增分类
- `POST /category/update` - 更新分类信息
- `POST /category/delete` - 删除分类

### 5. 消息通知系统 (`InformationApi`)

**功能描述**：商户向客户发送消息通知，支持批量发送和消息管理

**主要接口**：
- `POST /information/sendMessage` - 发送消息给客户（支持批量选择客户或发送给所有客户）
- `POST /information/messageList` - 客户查询本人接收的消息列表（分页，支持按消息类型筛选）
- `POST /information/bossMessageList` - 商家查询已发送的消息列表（分页，支持主题模糊查询、时间范围筛选）
- `POST /information/look` - 标记消息为已读
- `POST /information/delete` - 批量删除消息

### 6. 轮播图管理 (`SlideshowApi`)

**功能描述**：管理小程序首页轮播图，集成微信云存储

**主要接口**：
- `POST /slideshow/upload` - 上传轮播图图片到微信云存储
- `GET /slideshow/fileIds` - 获取所有轮播图文件 ID 列表
- `DELETE /slideshow` - 删除指定轮播图（同时删除云端文件和本地记录）

**技术亮点**：
- 集成微信云托管对象存储，自动生成 CDN 链接
- 文件 ID 持久化到数据库，便于管理
- 删除操作同步清理云端和本地数据

### 7. 积分与签到系统

#### 7.1 用户积分 (`UserPointApi`)

**主要接口**：
- `POST /point/convert` - 积分兑换商品（扣减用户积分）

#### 7.2 用户签到 (`UserSignInApi`)

**主要接口**：
- `POST /signin` - 每日签到
- `GET /signin/status` - 查询今日签到状态

**签到规则**：
- 基础签到获得 10 积分
- 连续签到 3 天及以上额外获得 10 积分
- 断签后连续天数重置为 1

#### 7.3 积分记录 (`UserPointRecordApi`)

**主要接口**：
- `POST /userpointrecord/list` - 分页查询积分变动记录（收入/支出明细）

**技术亮点**：
- 签到奖励算法：基础积分 + 连续签到 bonus
- 积分流水完整记录，支持审计追溯
- 事务保证积分扣减与业务操作的原子性

### 8. 抽奖活动系统 (`AwardApi`)

**主要接口**：
- `GET /awards/list` - 获取奖品列表
- `GET /awards/draw` - 参与抽奖
- `POST /awards/update` - 更新奖品配置（管理员接口）

**技术亮点**：
- 概率抽奖算法，支持权重配置
- 中奖记录持久化，支持兑奖核销

### 9. 微信云存储集成 (`WxCloudStorageApi`)

**功能描述**：集成微信云托管对象存储服务，用于图片、文件上传

**主要接口**：
- `POST /wxcloud/upload` - 上传文件到微信云存储
- `GET /wxcloud/download` - 获取文件下载链接
- `DELETE /wxcloud/delete` - 删除云端文件

**技术亮点**：
- 支持大文件分片上传
- 自动生成 CDN 加速链接
- 文件类型白名单校验，防止恶意上传

### 10. 数据备份系统 (`BackupApi`)

**主要接口**：
- `POST /backup/create` - 手动触发数据备份
- `GET /backup/list` - 查询备份记录列表
- `GET /backup/download` - 下载备份文件

**技术亮点**：
- 定时任务自动备份（可通过配置调整频率）
- 备份文件加密存储
- 支持备份恢复操作

### 11. 历史操作记录 (`HistoryOperationApi`)

**主要接口**：
- `POST /history/list` - 查询用户操作历史记录

**功能描述**：记录关键业务操作（如登录、下单、修改资料等），用于安全审计与行为分析

### 12. 商品出入库记录 (`InOutProductRecordApi`)

**主要接口**：
- `POST /inoutrecord/list` - 分页查询出入库流水记录

**功能描述**：完整记录每次商品库存变动，支持财务对账与库存审计

### 13. 通用功能 (`GeneralApi`)

**主要接口**：
- `GET /files/{filename}` - 下载或查看服务器文件（支持分类目录）
- `POST /upload` - 上传文件到服务器本地存储（支持分类目录）

**功能描述**：提供文件上传下载服务，支持图片、PDF、Excel 等多种文件格式，防止目录遍历攻击

### 14. 异常测试接口 (`ExceptionApi`)

**功能描述**：用于测试全局异常处理机制，生产环境应禁用

## 🔧 开发环境搭建

### 前置要求

- **JDK**: 17 或以上版本
- **Maven**: 3.6+ 
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Kafka**: 2.8+（可选，用于异步消息）
- **IDE**: IntelliJ IDEA（推荐）或 Eclipse

### 快速启动

#### 1. 克隆项目

```bash
git clone <repository-url>
cd lebo
```

#### 2. 配置数据库

创建 MySQL 数据库并导入初始化脚本：

```sql
CREATE DATABASE lebo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lebo;
-- 导入 docs/schema.sql 或其他初始化脚本
```

修改 `src/main/resources/application.yml` 中的数据库连接配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lebo?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

#### 3. 配置 Redis

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password  # 如有密码
      database: 0
```

#### 4. 配置微信相关参数

```yaml
wechat:
  appid: your_wechat_appid
  secret: your_wechat_secret
  cloud-storage:
    env-id: your_cloud_env_id
```

#### 5. 编译与运行

```bash
# 编译项目
mvn clean package -DskipTests

# 运行应用
java -jar target/lebo.jar

# 或使用 Maven 插件直接运行
mvn spring-boot:run
```

应用启动后，默认监听端口为 **80**（可在 `application.properties` 中通过 `server.port` 配置修改）。

#### 6. 验证服务

访问健康检查接口：

```bash
curl http://localhost:80/actuator/health
```

预期返回：

```json
{
  "status": "UP"
}
```

### Docker 部署

#### 构建镜像

```bash
docker build -t lebo-backend:latest .
```

#### 运行容器

```bash
docker run -d \
  --name lebo-backend \
  -p 80:80 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/lebo \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e SPRING_REDIS_HOST=host.docker.internal \
  lebo-backend:latest
```

**注意**：Dockerfile 已配置 HTTPS 证书信任，适用于微信云托管等需要调用外部 HTTPS 接口的场景。

## 📊 代码质量与测试

### 代码覆盖率要求

项目使用 JaCoCo 进行代码覆盖率检测，要求核心 API 层的代码覆盖率 **≥95%**：

- **指令覆盖率（INSTRUCTION）**：≥95%
- **行覆盖率（LINE）**：≥95%

### 运行测试

```bash
# 运行所有单元测试
mvn test

# 生成覆盖率报告
mvn verify

# 查看覆盖率报告
# 报告位置：target/site/jacoco/index.html
```

### 强制覆盖率检查

在 `verify` 阶段，JaCoCo 会自动执行覆盖率检查，如果未达到 95% 阈值，构建将失败。这确保了核心业务逻辑的充分测试覆盖。

## 🌐 API 文档

详细的 API 接口文档请参考：

- **[API接口文档.md](./API接口文档.md)** - 完整的接口定义、请求参数、响应示例
- **[前后端接口联调规范.md](./前后端接口联调规范.md)** - 接口联调注意事项与规范

### 统一响应格式

所有 API 接口均返回统一的 JSON 格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**字段说明**：
- `code`: 响应状态码（200 表示成功，其他为错误码）
- `message`: 响应消息描述
- `data`: 业务数据（可能为空对象 `{}`、数组 `[]` 或 null，具体取决于接口）

### 认证机制

除登录、注册等公开接口外，其他接口均需要在 HTTP Header 中携带 JWT Token：

```
Authorization: Bearer <your_jwt_token>
```

Token 通过 `/user/login` 接口获取，有效期默认为 7 天（可配置）。

## 📝 相关文档

- **[后端系分文档.md](./后端系分文档.md)** - 后端系统详细设计文档
- **[前端系统设计文档.md](./前端系统设计文档.md)** - 前端小程序架构说明
- **[幸运大转盘组件文档.md](./幸运大转盘组件文档.md)** - 抽奖功能详细设计
- **[微信云托管对象存储部署指南.md](./微信云托管对象存储部署指南.md)** - 云存储集成指南

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request 来改进本项目。

### 开发规范

1. **代码风格**：遵循阿里巴巴 Java 开发手册
2. **提交规范**：使用语义化提交信息（feat/fix/docs/style/refactor/test/chore）
3. **分支管理**：
   - `main` - 主分支，保持稳定可发布状态
   - `develop` - 开发分支，日常开发在此进行
   - `feature/*` - 功能分支，从 develop 检出
   - `hotfix/*` - 热修复分支，从 main 检出

### 提交 PR 前检查清单

- [ ] 代码已通过本地编译
- [ ] 单元测试全部通过（`mvn test`）
- [ ] 代码覆盖率满足要求（`mvn verify`）
- [ ] 无 Lint 错误或警告
- [ ] 更新了相关文档

## 📄 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

## 👥 作者

- **开发者**：煲崽
- **邮箱**：lxh523245@digital-engine.com
- **所属机构**：Digital Engine

## 🙏 致谢

感谢以下开源项目为本项目提供的技术支持：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis-Plus](https://baomidou.com/)
- [Lombok](https://projectlombok.org/)
- [Fastjson2](https://github.com/alibaba/fastjson2)
- [JWT](https://jwt.io/)

---

**最后更新时间**：2026-05-09