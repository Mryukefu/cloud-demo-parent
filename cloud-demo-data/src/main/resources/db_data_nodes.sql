/*
Navicat MySQL Data Transfer

Source Server         : 腾讯云
Source Server Version : 80023
Source Host           : 42.192.81.74:3306
Source Database       : study

Target Server Type    : MYSQL
Target Server Version : 80023
File Encoding         : 65001

Date: 2021-04-18 20:57:35
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for db_data_nodes
-- ----------------------------
DROP TABLE IF EXISTS `db_data_nodes`;
CREATE TABLE `db_data_nodes` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `date_source_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '数据库名称',
  `type` tinyint DEFAULT NULL COMMENT '1：按照年 2：按季度3:按照月份 4:按照主键取模',
  `logic_table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '逻辑表名称',
  `generator_column_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '表主键',
  `sharding_columns` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '分片键',
  `create_table_template` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '建表sql模板',
  `create_num` int DEFAULT NULL COMMENT '建表数量',
  `expression` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '分表表达式',
  `algorithm_expression` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '分表算法表达式',
  `class_name_sharding_strategy` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '分表规则配置策略',
  `class_name_sharding_algorithm` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '分表算法实现类',
  `state` tinyint DEFAULT NULL COMMENT '1 正常 2 删除',
  `create_time` int DEFAULT '0' COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='sharding 分库配置表';

SET FOREIGN_KEY_CHECKS=1;
