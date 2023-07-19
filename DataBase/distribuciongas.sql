/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 100425
 Source Host           : localhost:3306
 Source Schema         : distribuciongas

 Target Server Type    : MySQL
 Target Server Version : 100425
 File Encoding         : 65001

 Date: 18/07/2023 20:11:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for clientes
-- ----------------------------
DROP TABLE IF EXISTS `clientes`;
CREATE TABLE `clientes`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `idUsuario` int NULL DEFAULT NULL,
  `lat` double(11, 6) NULL DEFAULT NULL,
  `lng` double(11, 6) NULL DEFAULT NULL,
  `estado` varchar(100) CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fkUsuarioCliente`(`idUsuario` ASC) USING BTREE,
  CONSTRAINT `fkUsuarioCliente` FOREIGN KEY (`idUsuario`) REFERENCES `usuarios` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_spanish_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of clientes
-- ----------------------------

-- ----------------------------
-- Table structure for pedidos
-- ----------------------------
DROP TABLE IF EXISTS `pedidos`;
CREATE TABLE `pedidos`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `idCliente` int NULL DEFAULT NULL,
  `idRepartidor` int NULL DEFAULT NULL,
  `fecha` date NULL DEFAULT NULL,
  `hora` time NULL DEFAULT NULL,
  `subtotal` double(11, 2) NULL DEFAULT NULL,
  `iva` double(11, 2) NULL DEFAULT NULL,
  `total` double(11, 2) NULL DEFAULT NULL,
  `estado` varchar(20) CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL DEFAULT NULL,
  `comentario` varchar(255) CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL DEFAULT NULL,
  `origenLat` double(11, 6) NULL DEFAULT NULL,
  `origenLng` double(11, 6) NULL DEFAULT NULL,
  `destinoLat` double(11, 6) NULL DEFAULT NULL,
  `destinoLng` double(11, 6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fkClientePedido`(`idCliente` ASC) USING BTREE,
  INDEX `fkRepartidorPedido`(`idRepartidor` ASC) USING BTREE,
  CONSTRAINT `fkClientePedido` FOREIGN KEY (`idCliente`) REFERENCES `clientes` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fkRepartidorPedido` FOREIGN KEY (`idRepartidor`) REFERENCES `repartidores` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_spanish_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of pedidos
-- ----------------------------

-- ----------------------------
-- Table structure for pedidosds
-- ----------------------------
DROP TABLE IF EXISTS `pedidosds`;
CREATE TABLE `pedidosds`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `idPedido` int NULL DEFAULT NULL,
  `idProducto` int NULL DEFAULT NULL,
  `cantidad` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fkPedidoPedidods`(`idPedido` ASC) USING BTREE,
  INDEX `fkProductoPedidods`(`idProducto` ASC) USING BTREE,
  CONSTRAINT `fkPedidoPedidods` FOREIGN KEY (`idPedido`) REFERENCES `pedidos` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fkProductoPedidods` FOREIGN KEY (`idProducto`) REFERENCES `productos` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_spanish_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of pedidosds
-- ----------------------------

-- ----------------------------
-- Table structure for personas
-- ----------------------------
DROP TABLE IF EXISTS `personas`;
CREATE TABLE `personas`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombres` varchar(255) CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL DEFAULT NULL,
  `apellidos` varchar(255) CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL DEFAULT NULL,
  `cedula` varchar(20) CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL DEFAULT NULL,
  `telefono` varchar(20) CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL DEFAULT NULL,
  `direccion` varchar(255) CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL DEFAULT NULL,
  `estado` varchar(50) CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8 COLLATE = utf8_spanish_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of personas
-- ----------------------------
INSERT INTO `personas` VALUES (19, 'Rodolfo', 'Alonso', '1725270225', '2607038', '154456', 'administrador@hotmail.com', '1');

-- ----------------------------
-- Table structure for productos
-- ----------------------------
DROP TABLE IF EXISTS `productos`;
CREATE TABLE `productos`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `producto` varchar(100) CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL DEFAULT NULL,
  `descripcion` varchar(255) CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL DEFAULT NULL,
  `costo` double(11, 2) NULL DEFAULT NULL,
  `pvp` double(11, 2) NULL DEFAULT NULL,
  `imagen` text CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_spanish_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of productos
-- ----------------------------
INSERT INTO `productos` VALUES (1, 'Cilindro Azul', 'Cilindro GLP', 2.00, 2.50, 'cilindro.jpg');

-- ----------------------------
-- Table structure for repartidores
-- ----------------------------
DROP TABLE IF EXISTS `repartidores`;
CREATE TABLE `repartidores`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `idUsuario` int NULL DEFAULT NULL,
  `lat` double(11, 6) NULL DEFAULT NULL,
  `lng` double(11, 6) NULL DEFAULT NULL,
  `estado` varchar(100) CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fkRepartidorUsuario`(`idUsuario` ASC) USING BTREE,
  CONSTRAINT `fkRepartidorUsuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuarios` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_spanish_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of repartidores
-- ----------------------------

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `rol` varchar(20) CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL DEFAULT NULL,
  `descripcion` varchar(255) CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_spanish_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES (1, 'Administrador', 'admin');
INSERT INTO `roles` VALUES (2, 'Empleado', 'empleado');
INSERT INTO `roles` VALUES (3, 'Cliente', 'cliente');
INSERT INTO `roles` VALUES (4, 'Repartidor', 'repartidor');

-- ----------------------------
-- Table structure for usuarios
-- ----------------------------
DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE `usuarios`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `idPersona` int NULL DEFAULT NULL,
  `idRol` int NULL DEFAULT NULL,
  `usuario` varchar(255) CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL DEFAULT NULL,
  `clave` varchar(255) CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL DEFAULT NULL,
  `estado` varchar(100) CHARACTER SET utf8 COLLATE utf8_spanish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fkPersonaUsuario`(`idPersona` ASC) USING BTREE,
  INDEX `fkRolUsuario`(`idRol` ASC) USING BTREE,
  CONSTRAINT `fkPersonaUsuario` FOREIGN KEY (`idPersona`) REFERENCES `personas` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fkRolUsuario` FOREIGN KEY (`idRol`) REFERENCES `roles` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8 COLLATE = utf8_spanish_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of usuarios
-- ----------------------------
INSERT INTO `usuarios` VALUES (1, 19, 1, '123', '$2a$10$zK1kq0PPmGr0v/OpcMe63e6QUoM9f2b0qykzwFTQkWU2ER2qd5ZXS', 'ACTIVO');

SET FOREIGN_KEY_CHECKS = 1;
