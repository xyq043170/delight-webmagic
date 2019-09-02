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

 Date: 16/05/2019 17:21:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for delight_video_result
-- ----------------------------
DROP TABLE IF EXISTS `delight_video_result`;
CREATE TABLE `delight_video_result`  (
  `video_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '视频名称',
  `video_num` int(10) NULL DEFAULT NULL COMMENT '视频第几期',
  `local_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '视频本地保存路径',
  `pic_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '视频截图本地保存路径',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '保存时间',
  PRIMARY KEY (`video_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
