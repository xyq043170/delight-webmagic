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

 Date: 16/05/2019 17:21:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for delight_video
-- ----------------------------
DROP TABLE IF EXISTS `delight_video`;
CREATE TABLE `delight_video`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `local_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '本地存储路径',
  `video_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '视频名称',
  `paly_time` int(10) NOT NULL DEFAULT 0 COMMENT '视频播放时间单位秒',
  `flag` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标志位预留',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '保存时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2502 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
