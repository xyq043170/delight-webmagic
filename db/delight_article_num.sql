/*
 Navicat Premium Data Transfer

 Source Server         : 测试192.168.1.110
 Source Server Type    : MySQL
 Source Server Version : 50720
 Source Host           : 192.168.1.110:3306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 50720
 File Encoding         : 65001

 Date: 10/05/2019 11:27:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for delight_article_num
-- ----------------------------
DROP TABLE IF EXISTS `delight_article_num`;
CREATE TABLE `delight_article_num`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `channel` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '渠道',
  `article_num` int(10) NULL DEFAULT NULL COMMENT '文章发布的期数',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
