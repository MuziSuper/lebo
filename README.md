# Lebo商户管理平台小程序

## 项目前景提要
随着现代信息科技的发展，越来越多的个体户跟不上时代的的脚步，信息化不足导致客流量增幅不高，甚至出现下滑的情况。因此市面上出现了很多商户管理的微信小程序，商家可以通过购买该小程序使用权，不断发展和维系更多的顾客，从而接轨时代的发展。Lebo商户管理平台小程序应运而生，主要是为了服务商家，为其提供选购平台、用户管理、商品登记、订单统计、消息发布等功能，从而更好的为用户服务。

## 项目概述
Lebo商户管理平台小程序，基于微信小程序开发，主要功能有：用户管理、商品管理、订单管理、消息管理、统计管理等功能；

## 前端概述
前端基于 Taro 4.x + Vue3 小程序框架开发，使用 webpack 构建，并配合京东官方的 NutUI 组件库以及 Vue-Router 进行路由管理。
主要有 index 首页页面、profile 个人信息页面、shop 商城页面、message 消息页面、statistics 统计页面、goods-detail 商品详情页面、order-detail 订单详情页面

## 后端概述
后端基于 SpringBoot 3.x + MyBatisPlus + Redis + MySQL 开发，使用 Pear 框架进行接口生成与权限、缓存等功能

## 模块说明

### 商城

#### 模块简述
商城模块主要功能有：商品展示、购物车、商品详情、订单详情等功能。

#### 相关API

GET /shop/list?pageNum=1&pageSize=10 
描述：获取商品列表
参数：pageNum - 页码, pageSize - 每页数量
返回：商品列表List<Map<String, Object>>

GET /shop/category/list
描述：获取商品分类列表
返回：商品分类列表<Map<String, Object>>

POST /shop/add
描述：添加商品
参数：商品信息 Product
返回：商品Id

POST /shop/update?productId=1
描述：修改商品
参数：商品信息 Product
返回：布尔值

POST /shop/delete?productId=1
描述：删除商品
参数：productId - 商品ID
返回：布尔值

GET /shop/detail?productId=1
描述：获取商品信息
参数：productId - 商品ID
返回：商品信息 Product

GET /order/list?userId=1&pageNum=1&pageSize=10
描述：获取基础订单列表
参数：userId - 用户ID, pageNum - 页码, pageSize - 每页数量
返回：订单列表List<Order>

GET /order/info?/userId=1&orderId=1
描述：获取订单基础信息
参数：userId - 用户ID, orderId - 订单ID
返回：订单基础信息 Order

GET /order/detail?/orderId=1
描述：获取订单详情
参数：orderId - 订单ID
返回：订单详情OrderItem

POST /order/add/userId=1
描述：添加订单
参数：userId - 用户ID, Order - 订单信息
返回：订单Id

POST /order/update/orderId=1
描述：修改订单状态
参数：orderId - 订单ID, Order - 订单信息
返回：布尔值


