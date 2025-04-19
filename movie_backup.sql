-- MySQL dump 10.13  Distrib 8.0.41, for Linux (x86_64)
--
-- Host: localhost    Database: movie
-- ------------------------------------------------------
-- Server version	8.0.41-0ubuntu0.24.10.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `genre`
--

DROP TABLE IF EXISTS `genre`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `genre` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `genre`
--

LOCK TABLES `genre` WRITE;
/*!40000 ALTER TABLE `genre` DISABLE KEYS */;
INSERT INTO `genre` VALUES (1,_binary '\0','Phim hanh dong'),(2,_binary '\0','Phim hoat hinh'),(3,_binary '','Phim ma'),(4,_binary '','Phim natra');
/*!40000 ALTER TABLE `genre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorite`
--

DROP TABLE IF EXISTS `favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `movie_product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrxwxc0at6udlhpt4dnh45flkf` (`movie_product_id`),
  KEY `FKh3f2dg11ibnht4fvnmx60jcif` (`user_id`),
  CONSTRAINT `FKh3f2dg11ibnht4fvnmx60jcif` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKrxwxc0at6udlhpt4dnh45flkf` FOREIGN KEY (`movie_product_id`) REFERENCES `movie_product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorite`
--

LOCK TABLES `favorite` WRITE;
/*!40000 ALTER TABLE `favorite` DISABLE KEYS */;
INSERT INTO `favorite` VALUES (1,2,1),(2,1,1);
/*!40000 ALTER TABLE `favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invalidated_token`
--

DROP TABLE IF EXISTS `invalidated_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invalidated_token` (
  `id` varchar(255) NOT NULL,
  `expiry_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invalidated_token`
--

LOCK TABLES `invalidated_token` WRITE;
/*!40000 ALTER TABLE `invalidated_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `invalidated_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movie_category`
--

DROP TABLE IF EXISTS `movie_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movie_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` bigint NOT NULL,
  `movie_product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhkem46gi7yq1019e1j8hlvp9y` (`category_id`),
  KEY `FKl7s160aql6owkh6whwtxnerw9` (`movie_product_id`),
  CONSTRAINT `FKhkem46gi7yq1019e1j8hlvp9y` FOREIGN KEY (`category_id`) REFERENCES `genre` (`id`),
  CONSTRAINT `FKl7s160aql6owkh6whwtxnerw9` FOREIGN KEY (`movie_product_id`) REFERENCES `movie_product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movie_category`
--

LOCK TABLES `movie_category` WRITE;
/*!40000 ALTER TABLE `movie_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `movie_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movie_product`
--

DROP TABLE IF EXISTS `movie_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movie_product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `dislikes` int NOT NULL,
  `img_movie` varchar(255) DEFAULT NULL,
  `likes` int NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `views` int NOT NULL,
  `cattegoty_id` bigint NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsfkgqpv8l9j6w51aa1srprxb2` (`cattegoty_id`),
  CONSTRAINT `FKsfkgqpv8l9j6w51aa1srprxb2` FOREIGN KEY (`cattegoty_id`) REFERENCES `genre` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movie_product`
--

LOCK TABLES `movie_product` WRITE;
/*!40000 ALTER TABLE `movie_product` DISABLE KEYS */;
INSERT INTO `movie_product` VALUES (1,'hahahaha',2,'ahdjksahdjkahdjkahs',100,'Add your name in the body',3,1,NULL),(2,'hahahaha',0,'ahdjksahdjkahdjkahs',0,NULL,0,4,NULL);
/*!40000 ALTER TABLE `movie_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment` longtext,
  `created_at` datetime(6) DEFAULT NULL,
  `rating` int NOT NULL,
  `movie_product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6mfvta7fghany20jp13ex3ftc` (`movie_product_id`),
  KEY `FKiyf57dy48lyiftdrf7y87rnxi` (`user_id`),
  CONSTRAINT `FK6mfvta7fghany20jp13ex3ftc` FOREIGN KEY (`movie_product_id`) REFERENCES `movie_product` (`id`),
  CONSTRAINT `FKiyf57dy48lyiftdrf7y87rnxi` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES (1,'phim rat ok','2025-02-19 15:50:24.249984',3,1,18),(2,'phim hay vlon','2025-02-19 15:38:06.239277',3,1,17);
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES ('ADMIN','haha'),('USERS','hihi');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `gender` bit(1) NOT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `profile_picture_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Ha Noi','2003-11-04','thanh1',NULL,'',_binary '',NULL,'$2a$10$vx1mDUuyg9Y8uUUvcH0vIOLl/zWUlTXpuj/eUt6b413EA/jfC9ocS','0335395242','user/thanh.jpeg'),(2,NULL,NULL,'thanh2',NULL,'',_binary '\0',NULL,'$2a$10$gkrbSc3I6uYVzwY1t8tK2etAz9fzkAh5.xj4350pgLDTigJ0gnhfW',NULL,NULL),(3,NULL,NULL,'thanh3',NULL,'',_binary '\0',NULL,'$2a$10$qjJ/2ADotFONzfF8tBN5AeLF1VipyIDfkZHkvnXhufDZ4yW1egN8m',NULL,NULL),(4,NULL,NULL,'thanh4',NULL,'',_binary '\0',NULL,'$2a$10$/lyWF7LJN7qgVDZD6Qyk9esTXRCT/Ta0kqActZdSnV3JjcsTICDfy',NULL,NULL),(5,NULL,NULL,'thanh5',NULL,'Nguyen Tien Thanh',_binary '\0',NULL,'$2a$10$JuxnXDlw6cVIRakbPAbkl.7pD912/l3DHm2SdETnwOKzLfRpsVZHK',NULL,NULL),(6,NULL,NULL,'thanh6',NULL,'Nguyen Tien Thanh',_binary '\0',NULL,'$2a$10$1zqx.ZQ4FlNBcBPF0TTUd.TLxUOBFTX4DUctIzxuPDVIy0Tq5rJQi',NULL,NULL),(7,NULL,NULL,'thanh7',NULL,'Nguyen Tien Thanh',_binary '\0',NULL,'$2a$10$Fl9dBMvXou24KOy7jP4k7elrXzzk3j6hem.Ec34kGXIulxVOt/1wi',NULL,NULL),(8,NULL,NULL,'thanh8',NULL,'Nguyen Tien Thanh',_binary '\0',NULL,'$2a$10$MqnNAAL6JiVSnz.1.1LAZuKBEQzO7Ft8H1vvmOfz.rAMlHVcF2A..',NULL,NULL),(9,NULL,NULL,'thanh9',NULL,'Nguyen Tien Thanh',_binary '\0',NULL,'$2a$10$VuVqV.LF5QRelrQ.xOMFX.jvr6kUyL5qGRYf/OKBB1lMuiE6i0.wq',NULL,NULL),(10,NULL,NULL,'thanh11',NULL,'Nguyen Tien Thanh',_binary '\0',NULL,'$2a$10$hIkc/sh/BhP8FubEsGr7M.CaVOtK8rExtxd5ANSZM19ngVfzqMrX.',NULL,NULL),(11,NULL,NULL,'thanh12',NULL,'Nguyen Tien Thanh',_binary '\0',NULL,'$2a$10$.0beYq1pAyRYWBLsxQok4OuDKgrTj0ARGPsUZjtB/cohipnR9n/bK',NULL,NULL),(12,NULL,NULL,'thanh13',NULL,'Nguyen Tien Thanh',_binary '\0',NULL,'$2a$10$kUP1XfwVHDzY5FUOCE.FIO7H6AibuLueKVIIqTtnFRN/N7CDUctSC',NULL,NULL),(13,NULL,NULL,'thanh14',NULL,'Nguyen Tien Thanh',_binary '\0',NULL,'$2a$10$xdleZW.o4zQ.QHhHYbcJreWyb5EKlmH3vpQhRq1.HX56TUmM1xX7i',NULL,NULL),(14,NULL,NULL,'thanh15',NULL,'Nguyen Tien Thanh',_binary '\0',NULL,'$2a$10$HDeim4X870FNYyUm64CiEu3V9PcAn.9IjbeFgaBuCRe.Wx57qPXs.',NULL,NULL),(15,NULL,NULL,'thanh16',NULL,'Nguyen Tien Thanh',_binary '\0',NULL,'$2a$10$33dA4NjhWh9.9pfhjIi0d.pvPAy3oWqANc4plCytgrkp/u..NVLxi',NULL,NULL),(16,NULL,NULL,'thanh17',NULL,'Nguyen Tien Thanh',_binary '\0',NULL,'$2a$10$K.KOfVZQB1XZOmRN.30NCOmoH//cKD5v7o2.g9IbytkZHZCbG68c.',NULL,NULL),(17,NULL,NULL,'thanh18',NULL,'Nguyen Tien Thanh',_binary '\0',NULL,'$2a$10$deC2lg8SumNd5ElUH2M1TODhgcdzy9hESxZQKBr2gMJcoNbt7mKmK',NULL,NULL),(18,NULL,NULL,'thanh19',NULL,'Cao Tuan Anh',_binary '\0',NULL,'$2a$10$Wc31HG8GkufvvSyecnaHYOviqdnYL1yP.PaD7q1zYsXZ5KJhC1bR.',NULL,NULL),(19,NULL,NULL,'thanh20',NULL,'NGuyen Tien Thanh',_binary '\0',NULL,'$2a$10$yvYGVLsQ.xc5u1oJ2M1G0.J5gEmhyZ4uPzLd2sOD/8sbxTF6jDNaq',NULL,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `user_id` bigint NOT NULL,
  `role_name` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`,`role_name`),
  KEY `FKn6r4465stkbdy93a9p8cw7u24` (`role_name`),
  CONSTRAINT `FK859n2jvi8ivhui0rl0esws6o` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKn6r4465stkbdy93a9p8cw7u24` FOREIGN KEY (`role_name`) REFERENCES `role` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,'USERS'),(2,'USERS'),(3,'USERS'),(4,'USERS'),(5,'USERS'),(6,'USERS'),(7,'USERS'),(8,'USERS'),(9,'USERS'),(10,'USERS'),(11,'USERS'),(12,'USERS'),(13,'USERS'),(14,'USERS'),(15,'USERS'),(16,'USERS'),(17,'USERS'),(18,'USERS'),(19,'USERS');
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video_movie`
--

DROP TABLE IF EXISTS `video_movie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video_movie` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `format` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `resolution` varchar(255) DEFAULT NULL,
  `video_movie` varchar(255) DEFAULT NULL,
  `movie_product_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK3edyvvh4s4ymn4kcuak2c4y4x` (`movie_product_id`),
  CONSTRAINT `FKellit12fkke2jy2unowdkwhb9` FOREIGN KEY (`movie_product_id`) REFERENCES `movie_product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video_movie`
--

LOCK TABLES `video_movie` WRITE;
/*!40000 ALTER TABLE `video_movie` DISABLE KEYS */;
/*!40000 ALTER TABLE `video_movie` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `watch_history`
--

DROP TABLE IF EXISTS `watch_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `watch_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `watched_at` datetime(6) DEFAULT NULL,
  `movie_product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8cqh0vaydajpp5yshmf4bups1` (`movie_product_id`),
  KEY `FKf8ekqw3udry7ve0s63eo2sca7` (`user_id`),
  CONSTRAINT `FK8cqh0vaydajpp5yshmf4bups1` FOREIGN KEY (`movie_product_id`) REFERENCES `movie_product` (`id`),
  CONSTRAINT `FKf8ekqw3udry7ve0s63eo2sca7` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `watch_history`
--

LOCK TABLES `watch_history` WRITE;
/*!40000 ALTER TABLE `watch_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `watch_history` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-03 20:42:11
