create database if not exists gharkharch;
use gharkharch;

CREATE TABLE if not exists `categories` (
  `cat_id` int NOT NULL AUTO_INCREMENT,
  `cat_name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`cat_id`)
)

CREATE TABLE if not exists `sub_categories` (
  `sub_cat_id` int NOT NULL AUTO_INCREMENT,
  `sub_cat_name` varchar(30) DEFAULT NULL,
  `under_cat_id` int NOT NULL,
  PRIMARY KEY (`sub_cat_id`),
  UNIQUE KEY `sub_cat_name` (`sub_cat_name`),
  KEY `fk_subcat` (`under_cat_id`),
  CONSTRAINT `fk_subcat` FOREIGN KEY (`under_cat_id`) REFERENCES `categories` (`cat_id`) ON DELETE RESTRICT
)

