# Lebo 接口文档

## 目录

- [Lebo 接口文档](#lebo-接口文档)
  - [目录](#目录)
  - [用户管理接口](#用户管理接口)
    - [微信小程序登录](#微信小程序登录)
    - [商户后台登录](#商户后台登录)
    - [微信小程序注册](#微信小程序注册)
    - [更新用户信息](#更新用户信息)
    - [获取当前用户信息](#获取当前用户信息)
    - [用户注销](#用户注销)
    - [用户列表](#用户列表)
  - [订单管理接口](#订单管理接口)
    - [创建订单](#创建订单)
    - [提交订单（确认支付）](#提交订单确认支付)
    - [取消订单](#取消订单)
    - [获取订单详情](#获取订单详情)
    - [用户获取订单列表](#用户获取订单列表)
    - [商家获取订单列表](#商家获取订单列表)
    - [商家确认接单结束](#商家确认接单结束)
    - [商家拒绝接单](#商家拒绝接单)
  - [商品管理接口](#商品管理接口)
    - [获取商品列表](#获取商品列表)
    - [添加商品](#添加商品)
    - [更新商品信息](#更新商品信息)
    - [商品入库出库](#商品入库出库)
    - [删除商品](#删除商品)
    - [获取出入库大盘数据](#获取出入库大盘数据)
  - [商品类目接口](#商品类目接口)
    - [创建商品类目](#创建商品类目)
    - [更新商品类目](#更新商品类目)
    - [删除商品类目](#删除商品类目)
    - [获取商品类目列表](#获取商品类目列表)
  - [商品出入库记录接口](#商品出入库记录接口)
    - [获取出入库记录列表](#获取出入库记录列表)
  - [历史操作记录接口](#历史操作记录接口)
    - [获取历史操作记录列表](#获取历史操作记录列表)
  - [用户积分接口](#用户积分接口)
    - [积分兑换商品](#积分兑换商品)
  - [用户积分记录接口](#用户积分记录接口)
    - [获取用户积分记录列表](#获取用户积分记录列表)
  - [用户签到接口](#用户签到接口)
    - [每日签到](#每日签到)
    - [获取今日签到状态](#获取今日签到状态)
  - [抽奖接口](#抽奖接口)
    - [更新奖项配置](#更新奖项配置)
    - [获取奖项列表](#获取奖项列表)
    - [抽奖](#抽奖)
  - [文件管理接口](#文件管理接口)
    - [上传文件](#上传文件)
    - [下载/查看文件](#下载查看文件)
  - [数据备份接口](#数据备份接口)
    - [导出备份数据](#导出备份数据)
    - [导入备份数据](#导入备份数据)
  - [消息通知接口](#消息通知接口)
    - [发送消息](#发送消息)
    - [客户获取消息列表](#客户获取消息列表)
    - [商家获取消息列表](#商家获取消息列表)
  - [WebSocket 接口](#websocket-接口)
    - [连接地址](#连接地址)
    - [消息格式](#消息格式)
    - [事件类型](#事件类型)
    - [心跳机制](#心跳机制)
    - [业务通知流程](#业务通知流程)
      - [1. 用户支付成功通知商户](#1-用户支付成功通知商户)
      - [2. 商户确认接单通知用户](#2-商户确认接单通知用户)
      - [3. 商户拒绝接单通知用户](#3-商户拒绝接单通知用户)
    - [前端集成示例](#前端集成示例)
  - [错误码说明](#错误码说明)
  - [订单状态枚举](#订单状态枚举)
  - [商品状态枚举](#商品状态枚举)
  - [支付方式枚举](#支付方式枚举)
  - [ID生成规则](#id生成规则)

---

## 用户管理接口

**基础路径**: `/user`

### 微信小程序登录

通过微信登录 code 换取用户 openid、session_key 和 unionid，并更新数据库中该用户的登录信息。

- **URL**: `/user/login`
- **Method**: `POST`
- **认证**: 无需认证

**URL 参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| jscode | String | 是 | 微信登录凭证（由小程序端调用 wx.login() 获取） |

**请求示例**:
```
POST /user/login?jscode=微信登录凭证
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否登录成功 |

---

### 商户后台登录

商户后台管理网站的登录接口。

- **URL**: `/user/bossLogin`
- **Method**: `POST`
- **认证**: 无需认证

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| nickName | String | 是 | 商户用户名 |
| password | String | 是 | 商户密码 |

**请求示例**:

```json
{
  "nickName": "商户用户名",
  "password": "商户密码"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否登录成功 |

---

### 微信小程序注册

通过微信登录 code 换取用户 openid、session_key 和 unionid，并携带额外信息更新数据库中该用户的登录信息。

- **URL**: `/user/register`
- **Method**: `POST`
- **认证**: 无需认证

**URL 参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| jscode | String | 是 | 微信登录凭证（由小程序端调用 wx.login() 获取） |

**请求体** (可选):

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| nickName | String | 否 | 用户昵称 |
| avatarUrl | String | 否 | 用户头像URL |
| gender | Integer | 否 | 性别：0-保密，1-男，2-女 |

**请求示例**:

```json
{
  "nickName": "用户昵称",
  "avatarUrl": "头像URL",
  "gender": 1
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否注册成功 |

---

### 更新用户信息

更新当前登录用户的信息，调用后可再次调用 `/user/info` 获取最新数据。

- **URL**: `/user/update`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| nickName | String | 否 | 用户昵称 |
| avatar | String | 否 | 头像URL |
| gender | Integer | 否 | 性别：0-保密，1-男，2-女 |
| city | String | 否 | 城市 |
| age | Integer | 否 | 年龄 |
| birthday | String | 否 | 生日（格式：yyyy-MM-dd） |
| email | String | 否 | 邮箱 |
| phone | String | 否 | 手机号 |
| status | AccountStatusEnum | 否 | 状态枚举 |

**请求示例**:

```json
{
  "nickName": "用户昵称",
  "avatar": "头像URL",
  "gender": 1,
  "city": "北京",
  "age": 25,
  "birthday": "2000-01-01",
  "email": "user@example.com",
  "phone": "13800138000"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否更新成功 |

---

### 获取当前用户信息

获取当前登录用户的基本信息。用户首次登录后，客户端会保存 token，在 token 有效期内再次进入小程序时，可调用此接口自动获取用户信息。

- **URL**: `/user/info`
- **Method**: `GET`
- **认证**: 需要认证（Authorization token）

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "nickName": "用户昵称",
    "avatar": "头像URL",
    "accumulatedPoint": 1000,
    "gender": 1,
    "city": "北京",
    "email": "user@example.com",
    "age": 25,
    "phone": "13800138000",
    "birthday": "2000-01-01",
    "statusCode": 0
  }
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| nickName | String | 用户昵称 |
| avatar | String | 头像URL |
| accumulatedPoint | Long | 累计获得积分 |
| gender | Integer | 性别：0-保密，1-男，2-女 |
| city | String | 城市 |
| email | String | 邮箱 |
| age | Integer | 年龄 |
| phone | String | 手机号 |
| birthday | String | 生日 |
| statusCode | Integer | 账户状态：0-正常，1-不活跃，2-暂停，3-封禁，4-注销 |

---

### 用户注销

注销当前用户登录状态。服务端更新用户的登录状态，客户端接收到成功响应后需要清除保存在缓存中的 token，并跳转到登录页面。

- **URL**: `/user/logout`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否注销成功 |

---

### 用户列表

获取用户列表，支持按昵称模糊查询、手机号尾号四位查询、状态查询、性别查询。

- **URL**: `/user/list`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| nickName | String | 否 | 昵称（模糊查询） |
| phoneSuffix | String | 否 | 手机号尾号四位 |
| status | Integer | 否 | 状态：0-正常，1-不活跃，2-暂停，3-封禁，4-注销 |
| gender | Integer | 否 | 性别：0-保密，1-男，2-女 |

**请求示例**:

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "nickName": "张",
  "phoneSuffix": "1234",
  "status": 0,
  "gender": 1
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "openId": "oXXXXXXXXXXXXXX",
        "nickName": "张三",
        "gender": 1,
        "city": "北京",
        "age": 25,
        "birthday": "2000-01-01",
        "email": "zhangsan@example.com",
        "phone": "13800138000",
        "status": 0,
        "currentPoint": 1000,
        "lastLogin": "2025-03-20T10:30:00",
        "gmtCreated": "2025-01-01T08:00:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| records | Array | 用户列表 |
| records[].openId | String | 用户openId |
| records[].nickName | String | 昵称 |
| records[].gender | Integer | 性别：0-保密，1-男，2-女 |
| records[].city | String | 城市 |
| records[].age | Integer | 年龄 |
| records[].birthday | String | 生日 |
| records[].email | String | 邮箱 |
| records[].phone | String | 手机号 |
| records[].status | Integer | 状态：0-正常，1-不活跃，2-暂停，3-封禁，4-注销 |
| records[].currentPoint | Long | 当前可用积分 |
| records[].lastLogin | String | 上次登录时间（ISO 8601格式） |
| records[].gmtCreated | String | 创建时间（ISO 8601格式） |
| total | Long | 总记录数 |
| size | Long | 每页数量 |
| current | Long | 当前页码 |
| pages | Long | 总页数 |

---

## 订单管理接口

**基础路径**: `/order`

### 创建订单

用户在前端选购商品确认后创建订单，携带选购商品信息，填充订单商品信息、订单状态为未支付、订单创建时间、订单金额、订单用户ID，返回订单ID。

**订单超时机制**：
- 订单创建后5分钟内未支付，系统自动将订单状态改为支付失败
- 用户主动取消支付也会将订单状态改为支付失败

- **URL**: `/order/create`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| homeNumber | String | 否 | 房间号 |
| productOutDTOList | Array | 是 | 购买商品列表 |

**productOutDTOList 数组元素**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | String | 是 | 商品ID |
| number | Long | 是 | 购买数量（正数） |
| description | String | 否 | 商品描述 |

**请求示例**:

```json
{
  "homeNumber": "101",
  "productOutDTOList": [
    {
      "productId": "PRO_202503221530300001",
      "number": 2,
      "description": "备注信息"
    },
    {
      "productId": "PRO_202503221530300002",
      "number": 1
    }
  ]
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": "ORD_202503221530300001"
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | String | 订单ID，格式：ORD_yyyyMMddHHmmss + 4位序列号 |

---

### 提交订单（确认支付）

用户在五分钟内点击确认支付，传入支付方式，修改订单状态为已支付、填充支付方式、实际支付金额、支付时间。

**业务逻辑**:
- 验证订单状态（只有未支付的订单才能支付）
- 更新订单状态为已支付
- 填充支付方式、实际支付金额、支付时间
- 从超时队列中移除该订单
- **WebSocket 通知**: 向所有在线商户发送 `ORDER_PAID` 事件，通知商户有新订单

- **URL**: `/order/submit`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | String | 是 | 订单ID |
| orderOptionCode | Integer | 是 | 支付方式代码：0-线下收款，1-微信，2-支付宝 |

**请求示例**:

```json
{
  "orderId": "ORD_202503221530300001",
  "orderOptionCode": 1
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": "ORD_202503221530300001"
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | String | 订单ID |

---

### 取消订单

用户在订单确认支付页面点击取消，修改订单状态为支付失败，填充订单结束时间、实际支付金额为0。

**业务逻辑**:
- 验证订单状态（只有未支付的订单才能取消）
- 更新订单状态为支付失败
- 从超时队列中移除该订单

- **URL**: `/order/cancel`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**URL 参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | String | 是 | 订单ID |

**请求示例**:
```
POST /order/cancel?orderId=ORD_202503221530300001
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否取消成功 |

---

### 获取订单详情

获取订单的详细信息，包括订单商品列表、数量、创建时间、订单状态、订单总金额、用户昵称。

- **URL**: `/order/detail`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**URL 参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | String | 是 | 订单ID |

**请求示例**:
```
POST /order/detail?orderId=ORD_202503221530300001
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "ORD_202503221530300001",
    "nickName": "用户昵称",
    "homeNumber": "101",
    "totalAmount": 10000,
    "pointNumber": 100,
    "payAmount": 10000,
    "payType": 2,
    "payOption": 1,
    "payTime": "2024-01-01T12:00:00",
    "createTime": "2024-01-01T11:55:00",
    "endTime": null,
    "orderItemVOS": [
      {
        "productId": "PRO_202503221530300001",
        "productName": "商品名称",
        "onePrice": 100,
        "quantity": 10,
        "totalAmount": 1000
      }
    ]
  }
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 订单ID |
| nickName | String | 下单用户昵称 |
| homeNumber | String | 房间号 |
| totalAmount | Long | 订单总金额（单位：分） |
| pointNumber | Long | 获得积分数量 |
| payAmount | Long | 实际支付金额（单位：分） |
| payType | Integer | 支付状态：1-未支付，2-已支付，3-支付失败，4-已退款，5-已结束 |
| payOption | Integer | 支付方式：0-线下收款，1-微信，2-支付宝 |
| payTime | String | 支付时间（ISO 8601格式） |
| createTime | String | 订单创建时间（ISO 8601格式） |
| endTime | String | 订单结束时间（ISO 8601格式），未结束为null |
| orderItemVOS | Array | 订单商品列表 |

**orderItemVOS 数组元素**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| productId | String | 商品ID |
| productName | String | 商品名称 |
| onePrice | Integer | 商品单价（单位：分） |
| quantity | Long | 购买数量 |
| totalAmount | Long | 该商品总金额（单位：分） |

---

### 用户获取订单列表

用户获取自己的订单列表，可按订单状态筛选。

- **URL**: `/order/orderInfolist`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderTypeCode | String | 否 | 订单状态代码 |

**请求示例**:

```json
{
  "orderTypeCode": "2"
}
```

**订单状态代码说明**:

| 代码 | 说明 |
|------|------|
| 1 | 未支付 |
| 2 | 已支付 |
| 3 | 支付失败 |
| 4 | 已退款 |
| 5 | 已结束 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "ORD_202503221530300001",
      "nickName": "用户昵称",
      "homeNumber": "101",
      "totalAmount": 10000,
      "pointNumber": 100,
      "payAmount": 10000,
      "payType": 2,
      "payOption": 1,
      "payTime": "2024-01-01T12:00:00",
      "createTime": "2024-01-01T11:55:00",
      "endTime": null
    }
  ]
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 订单ID |
| nickName | String | 下单用户昵称 |
| homeNumber | String | 房间号 |
| totalAmount | Long | 订单总金额（单位：分） |
| pointNumber | Long | 获得积分数量 |
| payAmount | Long | 实际支付金额（单位：分） |
| payType | Integer | 支付状态：1-未支付，2-已支付，3-支付失败，4-已退款，5-已结束 |
| payOption | Integer | 支付方式：0-线下收款，1-微信，2-支付宝 |
| payTime | String | 支付时间（ISO 8601格式） |
| createTime | String | 订单创建时间（ISO 8601格式） |
| endTime | String | 订单结束时间（ISO 8601格式），未结束为null |

---

### 商家获取订单列表

商家获取订单列表，支持多种筛选条件和分页查询。

- **URL**: `/order/bossOrderInfolist`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | String | 否 | 订单号（模糊搜索） |
| orderTypeCode | String | 否 | 订单状态代码 |
| orderOptionCode | String | 否 | 支付方式代码 |
| orderCreateTime | String | 否 | 订单创建时间区间，格式：startTime,endTime |
| orderEndTime | String | 否 | 订单结束时间区间，格式：startTime,endTime |
| openId | String | 否 | 用户openId（精确匹配） |
| pageNum | Integer | 否 | 页码，不传或传null则不分页 |
| pageSize | Integer | 否 | 每页数量，不传或传null则不分页 |

**请求示例**:

分页查询：
```json
{
  "orderId": "ORD_202503",
  "orderTypeCode": "2",
  "orderOptionCode": "1",
  "orderCreateTime": "2024-01-01T00:00:00,2024-12-31T23:59:59",
  "orderEndTime": "",
  "openId": "oXXXXXXXXXXXXXX",
  "pageNum": 1,
  "pageSize": 10
}
```

不分页查询（返回全部数据）：
```json
{
  "orderId": "ORD_202503",
  "orderTypeCode": "2"
}
```

**响应示例**:

分页查询响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": "ORD_202503221530300001",
        "nickName": "用户昵称",
        "homeNumber": "101",
        "totalAmount": 10000,
        "pointNumber": 100,
        "payAmount": 10000,
        "payType": 5,
        "payOption": 1,
        "payTime": "2024-01-01T12:00:00",
        "createTime": "2024-01-01T11:55:00",
        "endTime": "2024-01-01T12:30:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| records | Array | 订单列表 |
| total | Long | 总记录数 |
| size | Long | 每页数量 |
| current | Long | 当前页码 |
| pages | Long | 总页数 |

**records 数组元素**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 订单ID |
| nickName | String | 下单用户昵称 |
| homeNumber | String | 房间号 |
| totalAmount | Long | 订单总金额（单位：分） |
| pointNumber | Long | 获得积分数量 |
| payAmount | Long | 实际支付金额（单位：分） |
| payType | Integer | 支付状态：1-未支付，2-已支付，3-支付失败，4-已退款，5-已结束 |
| payOption | Integer | 支付方式：0-线下收款，1-微信，2-支付宝 |
| payTime | String | 支付时间（ISO 8601格式） |
| createTime | String | 订单创建时间（ISO 8601格式） |
| endTime | String | 订单结束时间（ISO 8601格式），未结束为null |

---

### 商家确认接单结束

商家确认接单，对订单内的商品进行出库操作、用户积分钱包加积分，订单填充实际支付金额、结束时间，更新订单状态为已结束。

**业务逻辑**:
1. 验证订单状态（只有已支付的订单才能结束）
2. 对订单内的商品进行出库操作
3. 计算订单总积分，计入用户积分钱包
4. 填充实际支付金额、结束时间
5. 更新订单状态为已结束
6. **WebSocket 通知**: 向订单用户发送 `ORDER_ACCEPT` 事件，通知用户订单已确认

- **URL**: `/order/over`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**URL 参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | String | 是 | 订单ID |

**请求示例**:
```
POST /order/over?orderId=ORD_202503221530300001
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否操作成功 |

---

### 商家拒绝接单

商家拒绝接单，填充订单实际支付金额为0，结束时间，支付状态改为已退款，商品库存退回。

**业务逻辑**:
1. 验证订单状态（只有已支付的订单才能拒绝）
2. 获取订单项列表，退回商品库存
3. 更新订单状态为已退款，最终支付价格设为0
4. **WebSocket 通知**: 向订单用户发送 `ORDER_REJECT` 事件，通知用户订单已拒绝

- **URL**: `/order/reject`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**URL 参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | String | 是 | 订单ID |

**请求示例**:
```
POST /order/reject?orderId=ORD_202503221530300001
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否操作成功 |

---

## 商品管理接口

**基础路径**: `/product`

### 获取商品列表

获取商品列表，支持关键词、类目、状态、积分兑换等筛选条件，支持分页查询。

- **URL**: `/product/list`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体** (可选):

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，不传或传null则不分页 |
| pageSize | Integer | 否 | 每页数量，不传或传null则不分页 |
| keyword | String | 否 | 商品关键词（模糊搜索名称和描述） |
| categoryId | Long | 否 | 商品类目ID |
| status | Integer | 否 | 商品状态代码 |
| isPointConvert | Boolean | 否 | 是否可积分兑换 |

**请求示例**:

分页查询：
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "keyword": "商品关键词",
  "categoryId": 1,
  "status": 0,
  "isPointConvert": true
}
```

不分页查询（返回全部数据）：
```json
{
  "keyword": "商品关键词",
  "status": 0
}
```

**商品状态说明**:

| 代码 | 说明 |
|------|------|
| 0 | 上架（SELL） |
| 1 | 下架（SOLD_OUT） |
| 2 | 删除（DELETED） |

**响应示例**:

分页查询响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": "PRO_202503221530300001",
        "name": "商品名称",
        "description": "商品描述",
        "image": "商品图片URL",
        "tags": "[\"标签1\", \"标签2\"]",
        "unit": "件",
        "categoryId": 1,
        "status": "SELL",
        "salePrice": 100,
        "storage": 1000,
        "point": 10,
        "isPointConvert": false,
        "creditsExchange": 1000
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| records | Array | 商品列表 |
| total | Long | 总记录数 |
| size | Long | 每页数量 |
| current | Long | 当前页码 |
| pages | Long | 总页数 |

**records 数组元素**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 商品ID，格式：PRO_yyyyMMddHHmmss + 4位序列号 |
| name | String | 商品名称 |
| description | String | 商品描述 |
| image | String | 商品图片URL |
| tags | String | 商品标签（JSON数组字符串） |
| unit | String | 商品单位 |
| categoryId | Long | 商品类目ID |
| status | String | 商品状态枚举值：SELL/SOLD_OUT/DELETED |
| salePrice | Integer | 销售价格（单位：分） |
| storage | Long | 库存数量 |
| point | Integer | 单位商品产生积分 |
| isPointConvert | Boolean | 是否可积分兑换 |
| creditsExchange | Long | 兑换所需积分 |

---

### 添加商品

添加新商品。

- **URL**: `/product/add`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 是 | 商品名称 |
| description | String | 否 | 商品描述 |
| image | String | 否 | 商品图片URL |
| tags | String | 否 | 商品标签（JSON数组字符串） |
| unit | String | 否 | 商品单位（默认：个） |
| categoryId | Long | 是 | 商品类目ID |
| status | Integer | 否 | 商品状态（默认：0-上架） |
| salePrice | Integer | 是 | 销售价格（单位：分） |
| storage | Long | 是 | 库存数量 |
| costPrice | Integer | 否 | 成本价格（单位：分） |
| point | Integer | 否 | 单位商品产生积分（默认：0） |
| isPointConvert | Boolean | 否 | 是否可积分兑换（默认：false） |
| creditsExchange | Long | 否 | 兑换所需积分 |

**请求示例**:

```json
{
  "name": "商品名称",
  "description": "商品描述",
  "image": "商品图片URL",
  "tags": "[\"标签1\", \"标签2\"]",
  "unit": "件",
  "categoryId": 1,
  "salePrice": 100,
  "storage": 1000,
  "costPrice": 50,
  "point": 10,
  "isPointConvert": false,
  "creditsExchange": 1000
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否添加成功 |

---

### 更新商品信息

更新商品基础信息。

- **URL**: `/product/update`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 是 | 商品ID |
| name | String | 否 | 商品名称 |
| description | String | 否 | 商品描述 |
| image | String | 否 | 商品图片URL |
| tags | String | 否 | 商品标签（JSON数组字符串） |
| unit | String | 否 | 商品单位 |
| categoryId | Long | 否 | 商品类目ID |
| salePrice | Integer | 否 | 销售价格（单位：分） |
| costPrice | Integer | 否 | 成本价格（单位：分） |
| point | Integer | 否 | 单位商品产生积分 |
| isPointConvert | Boolean | 否 | 是否可积分兑换 |
| creditsExchange | Long | 否 | 兑换所需积分 |

**请求示例**:

```json
{
  "id": "PRO_202503221530300001",
  "name": "商品名称",
  "description": "商品描述",
  "image": "商品图片URL",
  "tags": "[\"标签1\", \"标签2\"]",
  "unit": "件",
  "categoryId": 1,
  "salePrice": 100,
  "costPrice": 50,
  "point": 10,
  "isPointConvert": false,
  "creditsExchange": 1000
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否更新成功 |

---

### 商品入库出库

商户操作单商品的入库或出库。

- **URL**: `/product/inout`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | String | 是 | 商品ID |
| number | Long | 是 | 出入库数量（正数=入库，负数=出库） |
| description | String | 否 | 出入库描述 |

**请求示例**:

```json
{
  "productId": "PRO_202503221530300001",
  "number": 100,
  "description": "采购入库"
}
```

> 注：number 为正数表示入库，负数表示出库

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否操作成功 |

---

### 删除商品

删除指定商品（逻辑删除）。

- **URL**: `/product/delete`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**URL 参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 是 | 商品ID |

**请求示例**:
```
POST /product/delete?id=PRO_202503221530300001
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否删除成功 |

---

### 获取出入库大盘数据

获取昨日零点到今日零点的出入库数据量，以及当前库存总数量和金额。

- **URL**: `/product/dashboard`
- **Method**: `GET`
- **认证**: 需要认证（Authorization token）

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "yesterdayTotalInNumber": 100,
    "yesterdayTotalOutNumber": 50,
    "currentlyTotalStock": 500,
    "currentLyTotalAmount": 25000
  }
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| yesterdayTotalInNumber | Long | 昨日入库总数量 |
| yesterdayTotalOutNumber | Long | 昨日出库总数量 |
| currentlyTotalStock | Long | 当前库存总数量（包括在售和停售状态的商品） |
| currentLyTotalAmount | Long | 当前库存总金额（库存 × 成本价，单位：分） |

---

## 商品类目接口

**基础路径**: `/category`

### 创建商品类目

创建新的商品类目。

- **URL**: `/category/create`
- **Method**: `POST` / `GET`
- **认证**: 需要认证（Authorization token）

**URL 参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryName | String | 是 | 商品类目名称 |

**请求示例**:
```
POST /category/create?categoryName=新分类名称
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否创建成功 |

---

### 更新商品类目

根据类目 ID 修改类目名称。

- **URL**: `/category/update`
- **Method**: `POST` / `GET`
- **认证**: 需要认证（Authorization token）

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 商品类目ID |
| name | String | 是 | 类目名称 |

**请求示例**:

```json
{
  "id": 1,
  "name": "类目名称"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否更新成功 |

---

### 删除商品类目

删除商品类目。需要检查类目下是否有商品，有商品则不能删除。

- **URL**: `/category/delete`
- **Method**: `POST` / `GET`
- **认证**: 需要认证（Authorization token）

**URL 参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 商品类目ID |

**请求示例**:
```
POST /category/delete?id=1
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否删除成功 |

---

### 获取商品类目列表

获取商品类目列表，支持分类名称模糊查询。当传入分页参数（pageNum 和 pageSize）时返回分页数据，不传分页参数时返回全部数据。

- **URL**: `/category/list`
- **Method**: `POST` / `GET`
- **认证**: 需要认证（Authorization token）

**请求体** (可选):

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码（需与 pageSize 同时传入才生效） |
| pageSize | Integer | 否 | 每页数量（需与 pageNum 同时传入才生效） |
| name | String | 否 | 分类名称（模糊查询） |

**请求示例**:

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "name": "分类名称"
}
```

**分页机制说明**:
- 当 `pageNum` 和 `pageSize` **都**传入且大于 0 时，返回分页数据
- 当 `pageNum` 或 `pageSize` 任一不传或小于等于 0 时，返回全部数据（仍为分页格式）

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "类目名称",
        "gmtCreated": "2024-01-01T12:00:00",
        "gmtModified": "2024-01-01T12:00:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| records | Array | 类目列表 |
| total | Long | 总记录数 |
| size | Long | 每页数量 |
| current | Long | 当前页码 |
| pages | Long | 总页数 |

**records 数组元素**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 类目ID（自增） |
| name | String | 类目名称 |
| gmtCreated | String | 创建时间（ISO 8601格式） |
| gmtModified | String | 修改时间（ISO 8601格式） |

---

## 商品出入库记录接口

**基础路径**: `/inOutProductRecord`

### 获取出入库记录列表

获取所有商品出入库记录列表（分页），支持多种筛选条件。

- **URL**: `/inOutProductRecord/getInOutRecordList`
- **Method**: `POST` / `GET`
- **认证**: 需要认证（Authorization token）

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 商品ID |
| productName | String | 否 | 商品名称（模糊查询） |
| type | Integer | 否 | 出入库类型：1-入库，2-出库 |
| startTime | String | 否 | 开始时间（LocalDateTime格式：yyyy-MM-ddTHH:mm:ss） |
| endTime | String | 否 | 结束时间（LocalDateTime格式：yyyy-MM-ddTHH:mm:ss） |
| pageNum | Integer | 否 | 页码（默认1） |
| pageSize | Integer | 否 | 每页数量（默认10） |

**请求示例**:

```json
{
  "id": "PRO_202503221530300001",
  "productName": "商品名称",
  "type": 1,
  "startTime": "2024-01-01T00:00:00",
  "endTime": "2024-12-31T23:59:59",
  "pageNum": 1,
  "pageSize": 10
}
```

**出入库类型说明**:

| 类型码 | 说明 |
|--------|------|
| 1 | 入库 |
| 2 | 出库 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": "INOUT_202503221530300001",
        "productId": "PRO_202503221530300001",
        "productName": "商品名称",
        "description": "采购入库",
        "number": 100,
        "remainNumber": 1000,
        "type": 1,
        "time": "2024-01-01T12:00:00",
        "operatorId": "openid_xxx"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| records | Array | 出入库记录列表 |
| total | Long | 总记录数 |
| size | Long | 每页数量 |
| current | Long | 当前页码 |
| pages | Long | 总页数 |

**records 数组元素**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 记录ID，格式：INOUT_yyyyMMddHHmmss + 4位序列号 |
| productId | String | 商品ID |
| productName | String | 商品名称 |
| description | String | 出入库描述 |
| number | Long | 出入库数量 |
| remainNumber | Long | 出入库后剩余数量 |
| type | Integer | 出入库类型：1-入库，2-出库 |
| time | String | 出入库时间（ISO 8601格式） |
| operatorId | String | 操作人ID |

---

## 历史操作记录接口

**基础路径**: `/historyOperation`

### 获取历史操作记录列表

获取商户操作历史记录列表（分页），支持多种筛选条件。

**操作类型说明**:

| 类型码 | 说明 |
|--------|------|
| 0 | 商品添加 |
| 1 | 商品修改 |
| 2 | 商品删除 |
| 3 | 手动导出备份 |
| 4 | 自动备份 |
| 5 | 后台系统登录 |
| 6 | 商品分类添加 |
| 7 | 商品分类修改 |
| 8 | 商品分类删除 |
| 9 | 导入备份数据 |

- **URL**: `/historyOperation/getHistoryOperationList`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| type | Integer | 否 | 操作类型（见上表） |
| pageNum | Integer | 否 | 页码（默认1） |
| pageSize | Integer | 否 | 每页数量（默认10） |
| startTime | String | 否 | 开始时间（格式：yyyy-MM-dd HH:mm:ss） |
| endTime | String | 否 | 结束时间（格式：yyyy-MM-dd HH:mm:ss） |
| content | String | 否 | 操作内容（模糊查询） |
| userName | String | 否 | 操作人ID（模糊查询） |

**请求示例**:

```json
{
  "type": 0,
  "pageNum": 1,
  "pageSize": 10,
  "startTime": "2024-01-01 00:00:00",
  "endTime": "2024-12-31 23:59:59",
  "content": "操作内容关键词",
  "userName": "openid"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": "HIS_202503221530300001",
        "content": "添加商品：测试商品A",
        "type": 0,
        "operatorId": "openid_xxx",
        "time": "2024-01-01T12:00:00"
      }
    ],
    "total": 50,
    "size": 10,
    "current": 1,
    "pages": 5
  }
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| records | Array | 历史操作记录列表 |
| total | Long | 总记录数 |
| size | Long | 每页数量 |
| current | Long | 当前页码 |
| pages | Long | 总页数 |

**records 数组元素**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 记录ID，格式：HIS_yyyyMMddHHmmss + 4位序列号 |
| content | String | 操作内容描述 |
| type | Integer | 操作类型 |
| operatorId | String | 操作人ID |
| time | String | 操作时间（ISO 8601格式） |

---

## 用户积分接口

**基础路径**: `/point`

### 积分兑换商品

用户使用积分兑换商品，支持批量兑换多个商品。

- **URL**: `/point/convert`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| items | Array | 是 | 商品列表 |
| description | String | 否 | 积分兑换描述 |
| openId | String | 否 | 用户openid（可选，从token中获取） |

**items 数组元素**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | String | 是 | 商品ID |
| number | Long | 是 | 兑换数量 |

**请求示例**:

```json
{
  "items": [
    {
      "productId": "PRO_202506021103020153",
      "number": 2
    },
    {
      "productId": "PRO_202506021103020154",
      "number": 1
    }
  ],
  "description": "积分兑换商品"
}
```

**业务逻辑**:

1. **参数校验**：验证请求参数是否完整
2. **商品校验**：
   - 检查商品是否存在
   - 检查商品是否在售
   - 检查商品是否支持积分兑换
   - 检查商品库存是否充足
3. **积分计算**：计算所需总积分 = Σ(商品兑换积分 × 兑换数量)
4. **积分校验**：检查用户当前积分是否足够
5. **执行兑换**：
   - 扣除商品库存（批量出库）
   - 创建商品出库记录
   - 创建积分变动记录
   - 扣除用户积分
   - 发送个人消息通知用户兑换成功

**事务处理**:
- 使用 `@Transactional` 注解保证原子性
- 隔离级别：`REPEATABLE_READ`
- 悲观锁：使用 `FOR UPDATE` 锁定商品记录

**消息通知内容**:
兑换成功后，系统自动发送类型为 2（个人消息）的消息给用户，包含：
- 兑换的商品列表
- 每个商品消耗的积分
- 总消耗积分
- 剩余积分

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否兑换成功 |

**错误情况**:

| 错误信息 | 说明 |
|----------|------|
| 用户未登录 | 用户未通过JWT认证 |
| 兑换商品列表不能为空 | 未提供兑换商品信息 |
| 商品不存在: xxx | 指定的商品ID不存在 |
| 以下商品不在售: xxx | 商品已下架 |
| 以下商品不可积分兑换: xxx | 商品未开启积分兑换功能 |
| 以下商品库存不足: xxx | 商品库存不够 |
| 用户积分钱包不存在 | 用户积分钱包未创建 |
| 用户积分不足，当前积分: xxx，所需积分: xxx | 用户当前积分不足以兑换 |

---

## 用户积分记录接口

**基础路径**: `/pointRecord`

### 获取用户积分记录列表

获取用户积分变动记录列表（分页），支持按用户openId、创建时间范围筛选。

- **URL**: `/pointRecord/list`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| openId | String | 否 | 用户openid |
| orderId | String | 否 | 关联订单ID |
| startTime | String | 否 | 创建时间开始（LocalDateTime格式：yyyy-MM-ddTHH:mm:ss） |
| endTime | String | 否 | 创建时间结束（LocalDateTime格式：yyyy-MM-ddTHH:mm:ss） |
| pageNum | Integer | 否 | 页码（默认1） |
| pageSize | Integer | 否 | 每页数量（默认10） |

**请求示例**:

```json
{
  "openId": "oXXXXXXXXXXXXXX",
  "startTime": "2024-01-01T00:00:00",
  "endTime": "2024-12-31T23:59:59",
  "pageNum": 1,
  "pageSize": 10
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": "uuid-xxx",
        "orderId": "ORD_202503221530300001",
        "description": "订单完成获得积分",
        "changeAmount": 100,
        "beforeAmount": 500,
        "afterAmount": 600,
        "gmtCreated": "2024-01-01T12:00:00"
      }
    ],
    "total": 50,
    "size": 10,
    "current": 1,
    "pages": 5
  }
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| records | Array | 积分记录列表 |
| total | Long | 总记录数 |
| size | Long | 每页数量 |
| current | Long | 当前页码 |
| pages | Long | 总页数 |

**records 数组元素**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 积分记录ID |
| orderId | String | 关联订单ID（可为空） |
| description | String | 积分变动描述（可为空） |
| changeAmount | Long | 变动积分数量（正数=增加，负数=减少） |
| beforeAmount | Long | 变动前积分 |
| afterAmount | Long | 变动后积分 |
| gmtCreated | String | 创建时间（ISO 8601格式） |

---

## 用户签到接口

**基础路径**: `/signin`

### 每日签到

用户每日签到获取积分。

**签到规则**:
- 基础签到获得10积分
- 连续签到3天及以上额外获得10积分
- 断签后连续天数重置为1

- **URL**: `/signin`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求参数**: 无

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否签到成功 |

---

### 获取今日签到状态

获取用户今日是否已签到。

- **URL**: `/signin/status`
- **Method**: `GET`
- **认证**: 需要认证（Authorization token）

**请求参数**: 无

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | true-今日已签到，false-今日未签到 |

---

## 抽奖接口

**基础路径**: `/awards`

### 更新奖项配置

更新抽奖系统的奖项配置，所有奖项概率总和必须等于100%。

**奖项规则**:
- 概率最低为0.0001%
- 概率总和必须等于100%
- 积分奖品（isPoint=true）的goods字段必须为正整数
- "未中奖"也需要作为一个奖项配置

- **URL**: `/awards/update`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）
- **Content-Type**: `application/json`

**请求体参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| - | Array | 是 | 奖项列表 |

**奖项对象字段**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| probability | BigDecimal | 是 | 中奖概率（精确到小数点后4位，如30.0001） |
| goods | String | 是 | 奖品名称或积分数（积分奖品时为正整数字符串） |
| isPoint | Boolean | 否 | 是否为积分奖品，默认false |

**请求示例**:

```json
[
  {
    "probability": 30.00,
    "goods": "一等奖",
    "isPoint": false
  },
  {
    "probability": 20.00,
    "goods": "二等奖",
    "isPoint": false
  },
  {
    "probability": 10.00,
    "goods": "50",
    "isPoint": true
  },
  {
    "probability": 40.00,
    "goods": "未中奖",
    "isPoint": false
  }
]
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否更新成功 |

---

### 获取奖项列表

获取当前抽奖系统的奖项配置列表。

- **URL**: `/awards/list`
- **Method**: `GET`
- **认证**: 需要认证（Authorization token）

**请求参数**: 无

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "probability": 30.00,
      "goods": "一等奖",
      "isPoint": false
    },
    {
      "probability": 20.00,
      "goods": "二等奖",
      "isPoint": false
    },
    {
      "probability": 10.00,
      "goods": "50",
      "isPoint": true
    },
    {
      "probability": 40.00,
      "goods": "未中奖",
      "isPoint": false
    }
  ]
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Array | 奖项列表 |

**奖项对象字段**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| probability | BigDecimal | 中奖概率 |
| goods | String | 奖品名称或积分数 |
| isPoint | Boolean | 是否为积分奖品 |

---

### 抽奖

用户进行抽奖，返回中奖结果。

**抽奖规则**:
- 用户必须完成今日签到才能参与抽奖
- 每个用户每天只能参与一次抽奖
- 中奖积分奖品会自动发放到用户钱包
- 中奖实物奖品会创建消息通知
- 未中奖不创建消息通知

- **URL**: `/awards/draw`
- **Method**: `GET`
- **认证**: 需要认证（Authorization token）

**请求参数**: 无

**响应示例（中奖实物）**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "probability": 30.00,
    "goods": "一等奖",
    "isPoint": false
  }
}
```

**响应示例（中奖积分）**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "probability": 10.00,
    "goods": "50",
    "isPoint": true
  }
}
```

**响应示例（未中奖）**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "probability": 40.00,
    "goods": "未中奖",
    "isPoint": false
  }
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Object | 中奖奖项对象 |

**奖项对象字段**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| probability | BigDecimal | 中奖概率 |
| goods | String | 奖品名称或积分数 |
| isPoint | Boolean | 是否为积分奖品，true表示积分数，false表示实物奖品 |

**错误响应示例**:

```json
{
  "code": 400,
  "message": "请先完成今日签到后再参与抽奖",
  "data": null
}
```

```json
{
  "code": 400,
  "message": "今日已参与过抽奖，请明天再来",
  "data": null
}
```

---

## 文件管理接口

**基础路径**: 无（根路径）

### 上传文件

上传文件到服务器。

- **URL**: `/upload`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）
- **Content-Type**: `multipart/form-data`

**表单参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | File | 是 | 上传的文件 |
| pictureCategory | String | 是 | 文件分类目录（子目录名），只允许字母、数字、下划线和连字符 |

**请求示例 (form-data)**:
```
file: [二进制文件]
pictureCategory: "product"
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": "/files/product/filename.png"
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | String | 上传后的服务器文件访问路径 |

---

### 下载/查看文件

代理下载或查看资源文件。

- **URL**: `/files/{filename}` 或 `/files/{download}/{filename}`
- **Method**: `GET`
- **认证**: 无需认证

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| filename | String | 是 | 文件名 |
| download | String | 否 | 不为空时触发下载 |

**查询参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pictureCategory | String | 是 | 文件分类目录（子目录名） |

**支持的文件类型**:

| 扩展名 | Content-Type |
|--------|--------------|
| pdf | application/pdf |
| png | image/png |
| gif | image/gif |
| jpg | image/jpg |
| jpeg | image/jpeg |
| ofd | application/ofd |
| zip | application/zip |
| xlsx | application/vnd.openxmlformats-officedocument.spreadsheetml.sheet |
| 其他 | application/octet-stream |

**示例**:
- 查看文件: `GET /files/abc.png?pictureCategory=icon`
- 下载文件: `GET /files/download/abc.png?pictureCategory=icon`

**响应**:
- 成功时返回文件流
- 失败时返回错误信息

---

## 数据备份接口

**基础路径**: `/backup`

### 导出备份数据

导出全量业务数据，包括商品、类目、订单、订单项、用户、用户积分、出入库记录、积分记录。数据将以 JSON 格式压缩为 ZIP 文件下载。

**业务逻辑**:
1. 查询所有业务数据表
2. 序列化为格式化的 JSON
3. 压缩为 ZIP 文件
4. 记录历史操作（type=3，手动备份数据）

- **URL**: `/backup/export`
- **Method**: `GET`
- **认证**: 需要认证（Authorization token）

**响应**:
- Content-Type: `application/zip`
- 文件名格式: `backup_yyyyMMdd_HHmmss.zip`

**备份数据结构**:

```json
{
  "products": [...],
  "categories": [...],
  "orders": [...],
  "orderItems": [...],
  "users": [...],
  "userPoints": [...],
  "inOutProductRecords": [...],
  "pointRecords": [...],
  "userSignIns": [...]
}
```

**响应示例**:

成功时直接返回 ZIP 文件流，浏览器自动下载。

---

### 导入备份数据

上传 ZIP 备份文件，将数据覆盖写入数据库。

**业务逻辑**:
1. 校验上传文件格式
2. 解压 ZIP 提取 JSON
3. 反序列化验证数据格式
4. 在事务中执行：
   - 清空现有数据表
   - 批量插入备份数据
5. 记录历史操作（type=9，导入备份数据）

**安全性保障**:
- 导入前先验证备份数据格式
- 整个导入过程在事务中执行
- 如果导入失败，事务自动回滚，原有数据不受影响

- **URL**: `/backup/import`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）
- **Content-Type**: `multipart/form-data`

**表单参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | File | 是 | ZIP 格式的备份文件 |

**请求示例 (form-data)**:
```
file: [ZIP文件]
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**错误响应示例**:

```json
{
  "code": 400,
  "message": "请上传ZIP格式的备份文件",
  "data": null
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否导入成功 |

---

## 消息通知接口

**基础路径**: `/information`

### 发送消息

商户发送消息给指定客户或所有客户。

- **URL**: `/information/sendMessage`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| openIds | Array[String] | 否 | 客户ID列表，为空则发送给全部客户 |
| subject | String | 是 | 消息主题 |
| content | String | 是 | 消息内容 |
| type | Integer | 是 | 消息类型：0-通知消息，1-系统消息，2-个人消息 |

**请求示例**:

```json
{
  "openIds": ["openid_001", "openid_002"],
  "subject": "新活动通知",
  "content": "尊敬的客户，我们即将推出新的优惠活动，敬请期待！",
  "type": 0
}
```

**消息类型说明**:

| 类型值 | 说明 |
|--------|------|
| 0 | 通知消息，商家活动等 |
| 1 | 系统消息，系统维护或更新等 |
| 2 | 个人消息，客户订单信息或商户定点通信等 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | Boolean | 是否发送成功 |

---

### 客户获取消息列表

客户获取自己接收到的消息分页列表。

- **URL**: `/information/messageList`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 分页页号，默认1 |
| pageSize | Integer | 否 | 分页数量，默认10 |
| type | Integer | 否 | 消息类型筛选 |

**请求示例**:

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "type": 0
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "type": 0,
        "subject": "新活动通知",
        "content": "尊敬的客户，我们即将推出新的优惠活动，敬请期待！",
        "gmtCreated": "2024-01-01T12:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| records | Array | 消息列表 |
| total | Long | 总记录数 |
| size | Long | 每页大小 |
| current | Long | 当前页码 |
| pages | Long | 总页数 |

**records 数组元素**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| type | Integer | 消息类型 |
| subject | String | 消息主题 |
| content | String | 消息内容 |
| gmtCreated | String | 创建时间（ISO 8601格式） |

---

### 商家获取消息列表

商家获取发送的消息分页列表，支持多种筛选条件。

- **URL**: `/information/bossMessageList`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 分页页号，默认1 |
| pageSize | Integer | 否 | 分页数量，默认10 |
| type | Integer | 否 | 消息类型筛选 |
| likeSubject | String | 否 | 消息主题模糊查询 |
| startGmtCreated | String | 否 | 创建时间开始点（ISO 8601格式） |
| endGmtCreated | String | 否 | 创建时间结束点（ISO 8601格式） |
| deleted | Integer | 否 | 逻辑删除筛选：0-未删除，1-已删除 |

**请求示例**:

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "type": 0,
  "likeSubject": "活动",
  "startGmtCreated": "2024-01-01T00:00:00",
  "endGmtCreated": "2024-12-31T23:59:59",
  "deleted": 0
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "type": 0,
        "subject": "新活动通知",
        "content": "尊敬的客户，我们即将推出新的优惠活动，敬请期待！",
        "gmtCreated": "2024-01-01T12:00:00",
        "deleted": 0,
        "openIds": ["openid_001", "openid_002", "openid_003"]
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| records | Array | 消息列表 |
| total | Long | 总记录数 |
| size | Long | 每页大小 |
| current | Long | 当前页码 |
| pages | Long | 总页数 |

**records 数组元素**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| type | Integer | 消息类型 |
| subject | String | 消息主题 |
| content | String | 消息内容 |
| gmtCreated | String | 创建时间（ISO 8601格式） |
| deleted | Integer | 逻辑删除状态：0-未删除，1-已删除 |
| openIds | Array[String] | 接收消息的用户ID列表 |

---

## WebSocket 接口

系统提供 WebSocket 双工通信接口，用于实时推送订单状态变更通知。

### 连接地址

- **WebSocket URL**: `ws://{host}/ws/order`
- **认证方式**: JWT Token 通过 URL 参数传递

**连接示例**:
```
ws://localhost:8080/ws/order?token=your_jwt_token
```

**认证流程**:
1. 客户端发起 WebSocket 连接请求，携带 JWT Token
2. 服务端通过 `JwtHandshakeInterceptor` 验证 Token 有效性
3. 验证通过后建立连接，验证失败返回 401 错误
4. 商户连接时额外验证 `isSuper` 字段是否为 1

### 消息格式

所有 WebSocket 消息采用统一的 JSON 格式：

```json
{
  "type": "事件类型",
  "timestamp": 1710000000000,
  "requestId": "唯一请求ID",
  "data": {}
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| type | String | 事件类型枚举值 |
| timestamp | Long | 消息时间戳（毫秒） |
| requestId | String | 唯一请求ID，用于消息追踪 |
| data | Object | 业务数据，可为 null |

### 事件类型

| 事件类型 | 枚举值 | 方向 | 说明 |
|----------|--------|------|------|
| PING | 心跳请求 | 客户端→服务端 | 客户端定时发送心跳检测 |
| PONG | 心跳响应 | 服务端→客户端 | 服务端响应心跳 |
| ORDER_PAID | 客户已支付订单 | 服务端→商户端 | 用户支付成功后通知商户 |
| ORDER_ACCEPT | 商家确认接单 | 服务端→客户端 | 商户确认接单后通知用户 |
| ORDER_REJECT | 商家拒绝接单 | 服务端→客户端 | 商户拒绝接单后通知用户 |

### 心跳机制

客户端应定时发送心跳消息以保持连接活跃：

**心跳请求（客户端发送）**:
```json
{
  "type": "PING",
  "timestamp": 1710000000000,
  "requestId": "req_123456",
  "data": null
}
```

**心跳响应（服务端返回）**:
```json
{
  "type": "PONG",
  "timestamp": 1710000000001,
  "requestId": "req_123456",
  "data": null
}
```

**建议心跳间隔**: 30秒

### 业务通知流程

#### 1. 用户支付成功通知商户

**触发时机**: 用户调用 `/order/submit` 接口支付成功后

**通知消息**:
```json
{
  "type": "ORDER_PAID",
  "timestamp": 1710000000000,
  "requestId": "req_abc123",
  "data": null
}
```

**商户端处理**:
- 收到 `ORDER_PAID` 消息后，调用 `/order/bossOrderInfolist` 接口刷新订单列表
- 筛选已支付状态的订单展示在界面上

#### 2. 商户确认接单通知用户

**触发时机**: 商户调用 `/order/over` 接口确认接单后

**通知消息**:
```json
{
  "type": "ORDER_ACCEPT",
  "timestamp": 1710000000000,
  "requestId": "req_def456",
  "data": null
}
```

**用户端处理**:
- 收到 `ORDER_ACCEPT` 消息后，调用 `/order/detail` 或 `/order/orderInfolist` 接口刷新订单状态
- 可选：弹出提示"订单已确认"

#### 3. 商户拒绝接单通知用户

**触发时机**: 商户调用 `/order/reject` 接口拒绝接单后

**通知消息**:
```json
{
  "type": "ORDER_REJECT",
  "timestamp": 1710000000000,
  "requestId": "req_ghi789",
  "data": null
}
```

**用户端处理**:
- 收到 `ORDER_REJECT` 消息后，调用 `/order/detail` 或 `/order/orderInfolist` 接口刷新订单状态
- 可选：弹出提示"订单已拒绝，将进行退款"

### 前端集成示例

**JavaScript 客户端示例**:

```javascript
// 建立 WebSocket 连接
const token = localStorage.getItem('token');
const ws = new WebSocket(`ws://localhost:8080/ws/order?token=${token}`);

// 连接成功
ws.onopen = () => {
  console.log('WebSocket 连接已建立');
  // 启动心跳
  startHeartbeat();
};

// 接收消息
ws.onmessage = (event) => {
  const message = JSON.parse(event.data);
  
  switch (message.type) {
    case 'PONG':
      console.log('心跳响应');
      break;
    case 'ORDER_PAID':
      // 商户端：刷新订单列表
      fetchOrderList();
      break;
    case 'ORDER_ACCEPT':
      // 用户端：订单已确认
      showNotification('订单已确认');
      refreshOrderDetail();
      break;
    case 'ORDER_REJECT':
      // 用户端：订单已拒绝
      showNotification('订单已拒绝，将进行退款');
      refreshOrderDetail();
      break;
  }
};

// 心跳函数
function startHeartbeat() {
  setInterval(() => {
    if (ws.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify({
        type: 'PING',
        timestamp: Date.now(),
        requestId: generateRequestId(),
        data: null
      }));
    }
  }, 30000);
}
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 401 | 未授权（token 无效或已过期） |
| 403 | 禁止访问 |
| 400 | 请求参数错误 |
| 500 | 服务器内部错误 |

## 订单状态枚举

| 代码 | 名称 | 说明 |
|------|------|------|
| 1 | NONPAYMENT | 未支付 |
| 2 | PAID | 已支付 |
| 3 | FAILURE | 支付失败 |
| 4 | REFUNDED | 已退款 |
| 5 | OVER | 已结束 |

## 商品状态枚举

| 代码 | 名称 | 说明 |
|------|------|------|
| 0 | ON_SHELF | 上架 |
| 1 | OFF_SHELF | 下架 |
| 2 | DELETED | 删除 |

## 支付方式枚举

| 代码 | 说明 |
|------|------|
| 0 | 线下收款 |
| 1 | 微信支付 |
| 2 | 支付宝支付 |

## ID生成规则

| 实体类型 | ID前缀 | 格式示例 | 说明 |
|----------|--------|----------|------|
| 订单 | ORD | ORD_202503221530300001 | ORD_yyyyMMddHHmmss + 4位序列号 |
| 商品 | PRO | PRO_202503221530300001 | PRO_yyyyMMddHHmmss + 4位序列号 |
| 用户积分 | UP | UP_202503221530300001 | UP_yyyyMMddHHmmss + 4位序列号 |
| 出入库记录 | INOUT | INOUT_202503221530300001 | INOUT_yyyyMMddHHmmss + 4位序列号 |
| 历史操作 | HIS | HIS_202503221530300001 | HIS_yyyyMMddHHmmss + 4位序列号 |

---

*文档最后更新时间: 2026-03-31*
