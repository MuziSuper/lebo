# Lebo 接口文档

## 目录

- [用户管理接口](#用户管理接口)
  - [微信小程序登录](#微信小程序登录)
  - [商户后台登录](#商户后台登录)
  - [微信小程序注册](#微信小程序注册)
  - [更新用户信息](#更新用户信息)
  - [获取当前用户信息](#获取当前用户信息)
  - [用户注销](#用户注销)
- [订单管理接口](#订单管理接口)
  - [创建订单](#创建订单)
  - [提交订单（确认支付）](#提交订单确认支付)
  - [取消订单](#取消订单)
  - [获取订单详情](#获取订单详情)
  - [用户获取订单列表](#用户获取订单列表)
  - [商家获取订单列表](#商家获取订单列表)
  - [商家确认订单结束](#商家确认订单结束)
- [商品管理接口](#商品管理接口)
  - [获取商品列表](#获取商品列表)
  - [添加商品](#添加商品)
  - [更新商品信息](#更新商品信息)
  - [商品入库出库](#商品入库出库)
  - [删除商品](#删除商品)
- [商品类目接口](#商品类目接口)
  - [创建商品类目](#创建商品类目)
  - [更新商品类目](#更新商品类目)
  - [删除商品类目](#删除商品类目)
  - [获取商品类目列表](#获取商品类目列表)
- [商品出入库记录接口](#商品出入库记录接口)
  - [获取出入库记录列表](#获取出入库记录列表)
- [文件管理接口](#文件管理接口)
  - [上传文件](#上传文件)
  - [下载/查看文件](#下载查看文件)

---

## 用户管理接口

**基础路径**: `/user`

### 微信小程序登录

通过微信登录 code 换取用户 openid、session_key 和 unionid，并更新数据库中该用户的登录信息。

- **URL**: `/user/login`
- **Method**: `POST`
- **认证**: 无需认证

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| jscode | String | 是 | 微信登录凭证（由小程序端调用 wx.login() 获取） |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

### 商户后台登录

商户后台管理网站的登录接口。

- **URL**: `/user/bossLogin`
- **Method**: `POST`
- **认证**: 无需认证

**请求体**:

```json
{
  "username": "商户用户名",
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

---

### 微信小程序注册

通过微信登录 code 换取用户 openid、session_key 和 unionid，并携带额外信息更新数据库中该用户的登录信息。

- **URL**: `/user/register`
- **Method**: `POST`
- **认证**: 无需认证

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| jscode | String | 是 | 微信登录凭证（由小程序端调用 wx.login() 获取） |

**请求体** (可选):

```json
{
  "inviteCode": "邀请码"
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

---

### 更新用户信息

更新当前登录用户的信息，调用后可再次调用 `/user/info` 获取最新数据。

- **URL**: `/user/update`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

```json
{
  "nickname": "用户昵称",
  "avatar": "头像URL",
  "phone": "手机号码"
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
    "openid": "用户openid",
    "nickname": "用户昵称",
    "avatar": "头像URL",
    "phone": "手机号码"
  }
}
```

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

---

## 订单管理接口

**基础路径**: `/order`

### 创建订单

用户点击支付创建订单，订单状态为未支付。前端已经对商品销售进行限制，无需确认商品是否足够。

- **URL**: `/order/create`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求参数**: 无

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": "订单ID"
}
```

---

### 提交订单（确认支付）

用户点击确认支付，服务端更新订单支付时间，并进行商品出库。

**业务逻辑**:
- 如果检测订单商品库存不足，则状态更新为支付失败，返回报错原因
- 如果订单确认支付时间超过5分钟，则状态更新为支付失败，返回报错原因
- 如果订单商品充足，则状态更新为已支付，填充支付时间

- **URL**: `/order/submit`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

```json
{
  "orderId": "订单ID",
  "orderOptionCode": "支付方式代码",
  "productInOutDTOList": [
    {
      "productId": "商品ID",
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
  "data": "订单ID"
}
```

---

### 取消订单

用户在订单确认支付页面点击取消，修改订单状态为支付失败，完善订单结束时间以及最终支付价格。

- **URL**: `/order/cancel`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | Long | 是 | 订单ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

### 获取订单详情

获取订单的详细信息，包括订单商品列表。

- **URL**: `/order/detail`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | Long | 是 | 订单ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "订单ID",
    "openId": "用户openid",
    "totalAmount": 10000,
    "payAmount": 10000,
    "payType": 2,
    "payOption": 1,
    "payTime": "2024-01-01T12:00:00",
    "createTime": "2024-01-01T11:55:00",
    "endTime": null,
    "orderItems": [
      {
        "id": "订单项ID",
        "orderId": "订单ID",
        "productId": "商品ID",
        "productName": "商品名称",
        "onePrice": 100,
        "quantity": 10,
        "totalAmount": 1000
      }
    ]
  }
}
```

---

### 用户获取订单列表

用户获取自己的订单列表，可按订单状态筛选。

- **URL**: `/order/orderInfolist`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderTypeCode | String | 否 | 订单状态代码 |

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
      "id": "订单ID",
      "openId": "用户openid",
      "totalAmount": 10000,
      "payType": 2,
      "createTime": "2024-01-01T11:55:00"
    }
  ]
}
```

---

### 商家获取订单列表

商家获取订单列表，支持多种筛选条件。

- **URL**: `/order/bossOrderInfolist`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | String | 否 | 订单号（模糊搜索） |
| orderTypeCode | String | 否 | 订单状态代码 |
| orderOptionCode | String | 否 | 支付方式代码 |
| orderCreateTime | String | 否 | 订单创建时间区间，格式：`yyyy-MM-ddTHH:mm:ss,yyyy-MM-ddTHH:mm:ss` |
| orderEndTime | String | 否 | 订单结束时间区间，格式：`yyyy-MM-ddTHH:mm:ss,yyyy-MM-ddTHH:mm:ss` |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "订单ID",
      "openId": "用户openid",
      "totalAmount": 10000,
      "payType": 5,
      "payOption": 1,
      "createTime": "2024-01-01T11:55:00",
      "endTime": "2024-01-01T12:30:00"
    }
  ]
}
```

---

### 商家确认订单结束

商家确认订单结束，修改订单状态为已结束，完善订单结束时间，并将订单对应总积分计入用户积分钱包。

**业务逻辑**:
1. 验证订单状态（只有已支付的订单才能结束）
2. 获取订单项列表，计算订单总积分
3. 更新订单状态为已结束
4. 将订单总积分计入用户积分钱包

- **URL**: `/order/over`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | String | 是 | 订单ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

## 商品管理接口

**基础路径**: `/product`

### 获取商品列表

获取商品列表（分页），支持关键词、类目、状态等筛选条件。

- **URL**: `/product/list`
- **Method**: `GET`
- **认证**: 需要认证（Authorization token）

**请求体** (可选):

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "keyword": "商品关键词",
  "categoryId": 1,
  "statusCode": 0
}
```

**商品状态说明**:

| 代码 | 说明 |
|------|------|
| 0 | 上架 |
| 1 | 下架 |
| 2 | 删除 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": "商品ID",
        "name": "商品名称",
        "description": "商品描述",
        "image": "商品图片URL",
        "tags": "[\"标签1\", \"标签2\"]",
        "unit": "件",
        "categoryId": 1,
        "status": 0,
        "salePrice": 100,
        "storage": 1000,
        "costPrice": 50,
        "point": 10,
        "isPointConvert": false
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

---

### 添加商品

添加新商品。

- **URL**: `/product/add`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

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
  "isPointConvert": false
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

---

### 更新商品信息

更新商品基础信息。

- **URL**: `/product/update`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

```json
{
  "id": "商品ID",
  "name": "商品名称",
  "description": "商品描述",
  "image": "商品图片URL",
  "tags": "[\"标签1\", \"标签2\"]",
  "unit": "件",
  "categoryId": 1,
  "salePrice": 100,
  "costPrice": 50,
  "point": 10,
  "isPointConvert": false
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

---

### 商品入库出库

商户操作单商品的入库或出库。

- **URL**: `/product/inout`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求体**:

```json
{
  "productId": "商品ID",
  "number": 100
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

---

### 删除商品

删除指定商品（逻辑删除）。

- **URL**: `/product/delete`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 商品ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

## 商品类目接口

**基础路径**: `/category`

### 创建商品类目

创建新的商品类目。

- **URL**: `/category/create`
- **Method**: `POST`/`GET`
- **认证**: 需要认证（Authorization token）

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryName | String | 是 | 商品类目名称 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

### 更新商品类目

根据类目 ID 修改类目名称。

- **URL**: `/category/update`
- **Method**: `POST`/`GET`
- **认证**: 需要认证（Authorization token）

**请求体**:

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

---

### 删除商品类目

删除商品类目。需要检查类目下是否有商品，有商品则不能删除。

- **URL**: `/category/delete`
- **Method**: `POST`/`GET`
- **认证**: 需要认证（Authorization token）

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryId | Long | 是 | 商品类目ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

### 获取商品类目列表

获取所有商品类目列表。

- **URL**: `/category/list`
- **Method**: `POST`/`GET`
- **认证**: 需要认证（Authorization token）

**请求参数**: 无

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "类目名称"
    }
  ]
}
```

---

## 商品出入库记录接口

**基础路径**: `/inOutProductRecord`

### 获取出入库记录列表

获取所有商品出入库记录列表，支持多种筛选条件。

- **URL**: `/inOutProductRecord/getInOutRecordList`
- **Method**: `POST`/`GET`
- **认证**: 需要认证（Authorization token）

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startTime | String | 否 | 开始时间 |
| endTime | String | 否 | 结束时间 |
| productName | String | 否 | 商品名称 |
| type | Integer | 否 | 出入库类型 |
| productId | String | 否 | 商品ID |

**响应示例**:

```json
[
  {
    "id": "记录ID",
    "productId": "商品ID",
    "productName": "商品名称",
    "number": 100,
    "type": 1,
    "createTime": "2024-01-01T12:00:00"
  }
]
```

---

## 文件管理接口

### 上传文件

上传文件到服务器。

- **URL**: `/upload`
- **Method**: `POST`
- **认证**: 需要认证（Authorization token）
- **Content-Type**: `multipart/form-data`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | File | 是 | 上传的文件 |
| pictureCategory | String | 是 | 文件分类目录（子目录名），只允许字母、数字、下划线和连字符 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": "/files/icon/filename.png"
}
```

---

### 下载/查看文件

代理下载或查看资源文件。

- **URL**: `/files/{filename}` 或 `/files/{download}/{filename}`
- **Method**: `GET`
- **认证**: 无需认证

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| filename | String | 是 | 文件名（路径参数） |
| download | String | 否 | 不为空时触发下载（路径参数） |
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

---

*文档最后更新时间: 2026-03-13*
