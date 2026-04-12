/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 90400 (9.4.0)
 Source Host           : localhost:3306
 Source Schema         : lebo

 Target Server Type    : MySQL
 Target Server Version : 90400 (9.4.0)
 File Encoding         : 65001

 Date: 11/03/2026 13:47:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类 ID',
    `name` varchar(255) NOT NULL DEFAULT '' COMMENT '分类名称',
    `gmt_created` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    `gmt_modified` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类表' AUTO_INCREMENT=1;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `open_id` varchar(255) NOT NULL COMMENT '微信 openid',
    `session_key` varchar(255) DEFAULT NULL COMMENT '微信会话密钥',
    `nick_name` varchar(255) NOT NULL COMMENT '用户昵称',
    `union_id` varchar(255) DEFAULT NULL COMMENT '微信unionid',
    `password` varchar(255) DEFAULT NULL COMMENT '登录密码',
    `avatar` varchar(255) NOT NULL DEFAULT '' COMMENT '头像地址',
    `gender` tinyint NOT NULL DEFAULT '0' COMMENT '性别 0:保密 1:男 2:女',
    `city` varchar(255) DEFAULT NULL COMMENT '城市',
    `age` int DEFAULT NULL COMMENT '年龄',
    `birthday` varchar(255) DEFAULT NULL COMMENT '生日',
    `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
    `phone` varchar(255) DEFAULT NULL COMMENT '手机号',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '用户状态 0:正常 1:不活跃 2:暂停 3:封禁 4:注销',
    `last_login` datetime(6) DEFAULT NULL COMMENT '最后登录时间',
    `gmt_created` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    `gmt_modified` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除 0:未删除 1:已删除',
    `is_super` tinyint NOT NULL DEFAULT '0' COMMENT '是否为商户 0:普通用户 1:商户',
    PRIMARY KEY (`open_id`),
    UNIQUE KEY `uk_nick_name` (`nick_name`),
    KEY `idx_status` (`status`),
    KEY `idx_phone` (`phone`),
    KEY `idx_gmt_created` (`gmt_created`),
    CONSTRAINT `user_chk_1` CHECK ((`status` between 0 and 4)),
    CONSTRAINT `user_chk_2` CHECK ((`gender` between 0 and 2))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';

-- ----------------------------
-- Table structure for user_point
-- ----------------------------
DROP TABLE IF EXISTS `user_point`;
CREATE TABLE `user_point` (
    `id` varchar(255) NOT NULL COMMENT '用户积分ID',
    `open_id` varchar(255) NOT NULL DEFAULT '' COMMENT '用户openid',
    `current_point` bigint NOT NULL DEFAULT '0' COMMENT '当前可用积分',
    `accumulated_point` bigint NOT NULL DEFAULT '0' COMMENT '累计获得积分',
    `is_deleted` int NOT NULL DEFAULT '0' COMMENT '逻辑删除 0:未删除 1:已删除',
    `gmt_created` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    `gmt_modified` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_open_id` (`open_id`),
    KEY `idx_current_point` (`current_point`),
    CONSTRAINT `user_point_chk_1` CHECK ((`current_point` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户积分账户表';

-- ----------------------------
-- Table structure for point_record
-- ----------------------------
DROP TABLE IF EXISTS `point_record`;
CREATE TABLE `point_record` (
    `id` varchar(255) NOT NULL COMMENT '积分记录ID',
    `open_id` varchar(255) NOT NULL DEFAULT '' COMMENT '用户openid',
    `order_id` varchar(255) NOT NULL DEFAULT '' COMMENT '关联订单ID',
    `description` varchar(255) DEFAULT '' COMMENT '积分变动描述',
    `change_amount` bigint NOT NULL DEFAULT '0' COMMENT '变动积分数量',
    `before_amount` bigint NOT NULL DEFAULT '0' COMMENT '变动前积分',
    `after_amount` bigint NOT NULL DEFAULT '0' COMMENT '变动后积分',
    `gmt_created` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_open_id` (`open_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_gmt_created` (`gmt_created`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户积分变动记录表';

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id` varchar(255) NOT NULL COMMENT '商品ID',
    `name` varchar(255) NOT NULL DEFAULT '' COMMENT '商品名称',
    `description` varchar(1024) DEFAULT '' COMMENT '商品描述',
    `image` varchar(255) DEFAULT NULL COMMENT '商品图片路径',
    `tags` varchar(255) DEFAULT NULL COMMENT '商品标签JSON数组',
    `unit` varchar(8) DEFAULT '个' COMMENT '商品单位',
    `category_id` bigint NOT NULL COMMENT '分类ID',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '商品状态 0:在售 1:下架 2:删除',
    `sale_price` int NOT NULL DEFAULT '0' COMMENT '销售价格（单位：元）',
    `storage` bigint NOT NULL DEFAULT '0' COMMENT '库存数量',
    `cost_price` int NOT NULL DEFAULT '0' COMMENT '成本价格（单位：元）',
    `point` int NOT NULL DEFAULT '0' COMMENT '购买商品获得积分',
    `is_point_convert` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否支持积分兑换 0:否 1:是',
    `credits_exchange` bigint DEFAULT NULL COMMENT '兑换所需积分',
    `gmt_created` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    `gmt_modified` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除 0:未删除 1:已删除',
    PRIMARY KEY (`id`),
    FULLTEXT INDEX `ft_name_description` (`name`, `description`) WITH PARSER ngram,
    KEY `idx_category_status` (`category_id`, `status`),
    KEY `idx_status_gmt_created` (`status`, `gmt_created`),
    KEY `idx_name` (`name`),
    KEY `idx_gmt_created` (`gmt_created`),
    CONSTRAINT `product_chk_1` CHECK ((`status` between 0 and 2)),
    CONSTRAINT `product_chk_2` CHECK ((`storage` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品信息表';

-- ----------------------------
-- Table structure for in_out_product_record
-- ----------------------------
DROP TABLE IF EXISTS `in_out_product_record`;
CREATE TABLE `in_out_product_record` (
    `id` varchar(255) NOT NULL COMMENT '记录ID，格式：INOUT_yyyyMMddHHmmss + 4位序列号',
    `product_name` varchar(255) DEFAULT '' COMMENT '商品名称',
    `product_id` varchar(255) NOT NULL DEFAULT '' COMMENT '商品ID',
    `description` varchar(500) DEFAULT '' COMMENT '出入库描述',
    `number` bigint NOT NULL DEFAULT '0' COMMENT '出入库数量（正数）',
    `remain_number` bigint NOT NULL DEFAULT '0' COMMENT '出入库后剩余数量',
    `time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '出入库时间',
    `type` tinyint NOT NULL COMMENT '出入库类型 1:入库 2:出库',
    `operator_id` varchar(255) DEFAULT '' COMMENT '操作人ID',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_type` (`type`),
    KEY `idx_time` (`time`),
    KEY `idx_product_time` (`product_id`, `time`),
    CONSTRAINT `in_out_product_record_chk_1` CHECK ((`type` between 1 and 2))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品出入库记录表';

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    `id` varchar(255) NOT NULL COMMENT '订单ID',
    `open_id` varchar(255) NOT NULL DEFAULT '' COMMENT '用户openid',
    `home_number` varchar(255) DEFAULT NULL COMMENT '房间号',
    `total_amount` bigint NOT NULL DEFAULT '0' COMMENT '订单总金额（单位：元）',
    `point_number` bigint NOT NULL DEFAULT '0' COMMENT '获得积分数量',
    `pay_amount` bigint NOT NULL DEFAULT '0' COMMENT '实际支付金额（单位：元）',
    `pay_type` tinyint NOT NULL DEFAULT '0' COMMENT '支付方式 0:未知 1:微信 2:支付宝 3:积分',
    `pay_option` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态 0:待支付 1:已支付 2:支付失败',
    `pay_time` datetime(6) DEFAULT NULL COMMENT '支付时间',
    `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '订单创建时间',
    `end_time` datetime(6) DEFAULT NULL COMMENT '订单结束时间',
    `gmt_created` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    `gmt_modified` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_open_id_create_time` (`open_id`, `create_time`),
    KEY `idx_pay_type_option` (`pay_type`, `pay_option`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `order_chk_1` CHECK ((`pay_type` between 0 and 5)),
    CONSTRAINT `order_chk_2` CHECK ((`pay_option` between 0 and 2))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id` varchar(255) NOT NULL COMMENT '订单项 ID',
    `order_id` varchar(255) NOT NULL DEFAULT '' COMMENT '订单 ID',
    `product_id` varchar(255) NOT NULL DEFAULT '' COMMENT '商品 ID',
    `product_name` varchar(255) NOT NULL DEFAULT '' COMMENT '商品名称',
    `one_price` int NOT NULL DEFAULT '0' COMMENT '单价（单位：元）',
    `quantity` bigint NOT NULL DEFAULT '0' COMMENT '购买数量',
    `total_amount` bigint NOT NULL DEFAULT '0' COMMENT '总金额（单位：元）',
    `gmt_created` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单商品关联表';

-- ----------------------------
-- Table structure for history_operation
-- ----------------------------
DROP TABLE IF EXISTS `history_operation`;
CREATE TABLE `history_operation` (
    `id` varchar(255) NOT NULL COMMENT '操作记录ID，格式：HIS_yyyyMMddHHmmss + 4位序列号',
    `content` varchar(500) NOT NULL DEFAULT '' COMMENT '操作内容',
    `type` tinyint NOT NULL COMMENT '操作类型 0:商品添加 1:商品修改 2:商品删除 3:手动备份 4:自动备份 5:后台登录 6:分类添加 7:分类修改 8:分类删除',
    `operator_id` varchar(255) DEFAULT '' COMMENT '操作人 ID',
    `time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_time` (`time`),
    KEY `idx_operator_id` (`operator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='历史操作记录表';

-- ----------------------------
-- Table structure for user_sign_in
-- ----------------------------
DROP TABLE IF EXISTS `user_sign_in`;
CREATE TABLE `user_sign_in` (
    `id` varchar(255) NOT NULL COMMENT '签到记录ID',
    `open_id` varchar(255) NOT NULL COMMENT '用户openid',
    `last_sign_date` date DEFAULT NULL COMMENT '最近一次签到日期',
    `continuous_days` int NOT NULL DEFAULT '1' COMMENT '连续签到天数',
    `gmt_created` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    `gmt_modified` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_open_id` (`open_id`),
    KEY `idx_last_sign_date` (`last_sign_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户签到记录表';

-- ----------------------------
-- Table structure for award_record
-- ----------------------------
DROP TABLE IF EXISTS `award_record`;
CREATE TABLE `award_record` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '抽奖记录ID',
    `create_date` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '抽奖日期',
    `user_openid` varchar(64) NOT NULL COMMENT '用户openid',
    `award_goods` varchar(200) NOT NULL COMMENT '中奖物品',
    PRIMARY KEY (`id`),
    KEY `idx_user_openid` (`user_openid`),
    KEY `idx_create_date` (`create_date`),
    KEY `idx_user_date` (`user_openid`, `create_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='抽奖记录表';

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- 插入商品分类数据
-- ----------------------------
INSERT IGNORE INTO `category` (`id`, `name`, `gmt_created`, `gmt_modified`) VALUES
    (1, '电子产品', NOW(6), NOW(6)),
    (2, '服装鞋包', NOW(6), NOW(6)),
    (3, '食品饮料', NOW(6), NOW(6)),
    (4, '家居生活', NOW(6), NOW(6)),
    (5, '美妆护肤', NOW(6), NOW(6)),
    (6, '运动户外', NOW(6), NOW(6)),
    (7, '图书文具', NOW(6), NOW(6)),
    (8, '母婴用品', NOW(6), NOW(6)),
    (9, '宠物用品', NOW(6), NOW(6)),
    (10, '汽车用品', NOW(6), NOW(6));

-- ----------------------------
-- 插入商品数据 (30条)
-- ----------------------------
INSERT INTO `product` (
    `id`, `name`, `description`, `image`, `tags`, `unit`,
    `category_id`, `status`, `sale_price`, `storage`, `cost_price`,
    `point`, `is_point_convert`, `credits_exchange`, `gmt_created`, `gmt_modified`, `is_deleted`
) VALUES
-- 电子产品
('prod_iphone15_001', 'Apple iPhone 15 Pro', 'Apple iPhone 15 Pro 256GB 原色钛金属 支持移动联通电信5G 双卡双待手机', 'product/default.png', '["苹果","手机","5G"]', '台', 1, 0, 8999, 50, 7500, 899, b'0', NULL, NOW(6), NOW(6), b'0'),
('prod_mate60_002', '华为 Mate 60 Pro', '华为 HUAWEI Mate 60 Pro 卫星通话 12GB+512GB 雅川青', 'product/default.png', '["华为","手机","卫星通信"]', '台', 1, 0, 6999, 30, 5800, 699, b'1', 50000, NOW(6), NOW(6), b'0'),
('prod_mi14_003', '小米14 Ultra', '小米14 Ultra 徕卡光学 Summilux镜头 16GB+1TB 黑色', 'product/default.png', '["小米","手机","徕卡"]', '台', 1, 0, 6499, 40, 5200, 649, b'0', NULL, NOW(6), NOW(6), b'0'),
('prod_macbook_004', 'Apple MacBook Air 13', 'Apple MacBook Air 13.6英寸 8核M2芯片 8GB 256GB 午夜色', 'product/default.png', '["苹果","笔记本","办公"]', '台', 1, 0, 8999, 20, 7500, 899, b'0', NULL, NOW(6), NOW(6), b'0'),
('prod_ipad_005', 'iPad Pro 12.9英寸', 'iPad Pro 12.9英寸 M2芯片 512GB Wi-Fi版 银色', 'product/default.png', '["苹果","平板","学习"]', '台', 1, 0, 9299, 25, 8000, 929, b'1', 68000, NOW(6), NOW(6), b'0'),
('prod_sony_006', 'Sony WH-1000XM5', 'Sony索尼 WH-1000XM5 头戴式无线降噪耳机 铂金银', 'product/bluetooth.png', '["索尼","耳机","降噪"]', '个', 1, 0, 1999, 100, 1500, 199, b'1', 15000, NOW(6), NOW(6), b'0'),
-- 服装鞋包
('prod_nike_007', 'Nike Air Force 1', 'Nike空军一号 低帮板鞋 纯白 男女同款', 'product/shoe.png', '["耐克","运动鞋","板鞋"]', '双', 2, 0, 799, 200, 500, 79, b'0', NULL, NOW(6), NOW(6), b'0'),
('prod_adidas_008', 'Adidas Ultra Boost', 'Adidas阿迪达斯 Ultra Boost 21 跑鞋 黑色', 'product/shoe.png', '["阿迪达斯","跑鞋","boost"]', '双', 2, 0, 1099, 150, 700, 109, b'1', 8500, NOW(6), NOW(6), b'0'),
('prod_uniqlo_009', '优衣库 纯棉T恤', '优衣库 男装/女装 纯棉圆领T恤 多色可选', 'product/shirt.png', '["优衣库","T恤","纯棉"]', '件', 2, 0, 79, 500, 40, 7, b'0', NULL, NOW(6), NOW(6), b'0'),
('prod_northface_010', 'The North Face 冲锋衣', '北面 1996 Retro Nuptse 羽绒服 冲锋衣 黑色', 'product/default.png', '["北面","冲锋衣","户外"]', '件', 2, 0, 2399, 60, 1600, 239, b'0', NULL, NOW(6), NOW(6), b'0'),
('prod_longchamp_011', 'Longchamp 饺子包', 'Longchamp珑骧 Le Pliage系列 饺子包 短柄中号 红色', 'product/default.png', '["珑骧","包包","通勤"]', '个', 2, 0, 899, 80, 600, 89, b'1', 6800, NOW(6), NOW(6), b'0'),
-- 食品饮料
('prod_snacks_012', '三只松鼠 坚果大礼包', '三只松鼠 每日坚果 混合坚果 750g/30包', 'product/snacks.png', '["三只松鼠","坚果","零食"]', '盒', 3, 0, 129, 300, 80, 12, b'1', 980, NOW(6), NOW(6), b'0'),
('prod_starbucks_013', '星巴克 咖啡豆', '星巴克 深度烘焙 咖啡豆 200g 哥伦比亚', 'product/coffee.png', '["星巴克","咖啡","咖啡豆"]', '袋', 3, 0, 89, 200, 50, 8, b'0', NULL, NOW(6), NOW(6), b'0'),
('prod_mengniu_014', '蒙牛 特仑苏', '蒙牛 特仑苏 纯牛奶 250ml*12盒 礼盒装', 'product/default.png', '["蒙牛","牛奶","特仑苏"]', '箱', 3, 0, 68, 400, 45, 6, b'1', 520, NOW(6), NOW(6), b'0'),
-- 家居生活
('prod_muji_015', 'MUJI 香薰机', '无印良品 超声波香薰机 大号 定时功能 白色', 'product/default.png', '["无印良品","香薰","加湿"]', '个', 4, 0, 398, 80, 200, 39, b'1', 3200, NOW(6), NOW(6), b'0'),
('prod_chair_016', '网易严选 人体工学椅', '网易严选 人体工学椅 电脑椅 办公椅 黑色', 'product/default.png', '["网易严选","椅子","办公"]', '把', 4, 0, 899, 40, 550, 89, b'0', NULL, NOW(6), NOW(6), b'0'),
('prod_robot_017', '米家 扫地机器人', '米家 扫拖机器人 3C 智能扫地机 拖地机', 'product/default.png', '["小米","扫地机","智能"]', '台', 4, 0, 1299, 35, 900, 129, b'1', 9800, NOW(6), NOW(6), b'0'),
-- 美妆护肤
('prod_sk2_018', 'SK-II 神仙水', 'SK-II 护肤精华露 神仙水 230ml', 'product/default.png', '["SK-II","神仙水","精华"]', '瓶', 5, 0, 1299, 60, 900, 129, b'0', NULL, NOW(6), NOW(6), b'0'),
('prod_loreal_019', 'L\'Oreal 防晒霜', '欧莱雅 小金管 防晒霜 30ml 隔离紫外线', 'product/default.png', '["欧莱雅","防晒","隔离"]', '支', 5, 0, 119, 300, 70, 11, b'1', 890, NOW(6), NOW(6), b'0'),
('prod_estee_020', 'Estee Lauder 小棕瓶', '雅诗兰黛 特润修护肌活精华露 小棕瓶 50ml', 'product/default.png', '["雅诗兰黛","精华","小棕瓶"]', '瓶', 5, 0, 799, 80, 550, 79, b'0', NULL, NOW(6), NOW(6), b'0'),
-- 运动户外
('prod_yonex_021', 'Yonex 羽毛球拍', '尤尼克斯 羽毛球拍 天斧99 专业级 4U5', 'product/default.png', '["尤尼克斯","羽毛球拍","天斧"]', '支', 6, 0, 1299, 30, 900, 129, b'1', 9500, NOW(6), NOW(6), b'0'),
('prod_lulu_022', 'Lululemon 瑜伽垫', 'Lululemon 瑜伽垫 5mm 防滑 紫色', 'product/default.png', '["Lululemon","瑜伽垫","健身"]', '张', 6, 0, 499, 100, 300, 49, b'1', 3800, NOW(6), NOW(6), b'0'),
('prod_decathlon_023', '迪卡侬 帐篷', '迪卡侬 户外帐篷 速开 防雨 3-4人 蓝色', 'product/default.png', '["迪卡侬","帐篷","露营"]', '个', 6, 0, 399, 40, 250, 39, b'0', NULL, NOW(6), NOW(6), b'0'),
-- 图书文具
('prod_santi_024', '三体 全集', '刘慈欣 科幻小说 三体全集 套装3册', 'product/default.png', '["刘慈欣","科幻","三体"]', '套', 7, 0, 89, 200, 50, 8, b'1', 680, NOW(6), NOW(6), b'0'),
('prod_lamy_025', 'LAMY 钢笔', 'LAMY凌美 Safari狩猎者 钢笔 F尖 磨砂黑', 'product/default.png', '["LAMY","钢笔","文具"]', '支', 7, 0, 299, 150, 180, 29, b'0', NULL, NOW(6), NOW(6), b'0'),
('prod_moleskine_026', 'Moleskine 笔记本', 'Moleskine 经典笔记本 硬面 A5 横线 黑色', 'product/default.png', '["Moleskine","笔记本","手账"]', '本', 7, 0, 189, 120, 100, 18, b'1', 1450, NOW(6), NOW(6), b'0'),
-- 母婴用品
('prod_pampers_027', 'Pampers 纸尿裤', '帮宝适 一级帮 纸尿裤 L58片 男女通用', 'product/default.png', '["帮宝适","纸尿裤","母婴"]', '包', 8, 0, 99, 300, 70, 9, b'0', NULL, NOW(6), NOW(6), b'0'),
('prod_gb_028', '好孩子 婴儿车', '好孩子 婴儿推车 高景观 可坐躺 轻便折叠', 'product/default.png', '["好孩子","婴儿车","推车"]', '辆', 8, 0, 899, 30, 600, 89, b'1', 6800, NOW(6), NOW(6), b'0'),
-- 宠物用品
('prod_royal_029', '皇家 猫粮', '皇家 成猫猫粮 室内家猫 2kg 去毛球配方', 'product/default.png', '["皇家","猫粮","宠物"]', '袋', 9, 0, 189, 150, 120, 18, b'1', 1480, NOW(6), NOW(6), b'0'),
-- 汽车用品
('prod_360_030', '360 行车记录仪', '360 行车记录仪 K980 4K高清 前后双录 停车监控', 'product/default.png', '["360","行车记录仪","汽车"]', '台', 10, 0, 599, 80, 400, 59, b'0', NULL, NOW(6), NOW(6), b'0');

-- ----------------------------
-- 插入老板用户数据（仅用于开发测试）
-- ----------------------------
INSERT INTO `user` (
    `open_id`, `session_key`, `nick_name`, `union_id`, `password`,
    `avatar`, `gender`, `city`, `age`, `birthday`, `email`, `phone`,
    `status`, `last_login`, `gmt_created`, `gmt_modified`, `is_deleted`, `is_super`
) VALUES (
    'boss_openid_001',
    'boss_session_key_001',
    'boss',
    NULL,
    '123456',
    '/avatar/boss.jpg',
    1,
    '北京',
    30,
    '1994-01-01',
    'boss@lebo.com',
    '13888888888',
    0,
    NULL,
    NOW(6),
    NOW(6),
    0,
    1
);

-- ----------------------------
-- 插入老板的积分账户
-- ----------------------------
INSERT INTO `user_point` (
    `id`, `open_id`, `current_point`, `accumulated_point`,
    `is_deleted`, `gmt_created`, `gmt_modified`
) VALUES (
    'point_boss_001',
    'boss_openid_001',
    10000,  -- 初始积分
    10000,  -- 累计积分
    0,
    NOW(6),
    NOW(6)
);

-- ----------------------------
-- Table structure for information
-- ----------------------------
DROP TABLE IF EXISTS `information`;
CREATE TABLE `information` (
    `id` varchar(255) NOT NULL COMMENT '主键ID',
    `information_id` varchar(255) NOT NULL COMMENT '消息ID，用于商户查询自己发送的消息时，对于发给多个用户的消息记作一条消息',
    `open_id` varchar(255) NOT NULL DEFAULT '' COMMENT '客户ID',
    `name` varchar(255) DEFAULT NULL COMMENT '客户名称',
    `subject` varchar(255) NOT NULL DEFAULT '' COMMENT '消息主题',
    `content` varchar(1024) NOT NULL DEFAULT '' COMMENT '消息内容',
    `type` tinyint NOT NULL DEFAULT 0 COMMENT '消息类型：0-通知消息，1-系统消息，2-个人消息',
    `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    `gmt_created` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    `gmt_modified` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_information_id` (`information_id`),
    KEY `idx_open_id` (`open_id`),
    KEY `idx_type` (`type`),
    KEY `idx_gmt_created` (`gmt_created`),
    KEY `idx_open_id_gmt_created` (`open_id`, `gmt_created`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息通知表';
