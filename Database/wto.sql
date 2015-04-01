-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema wto
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema wto
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `wto` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `wto` ;

-- -----------------------------------------------------
-- Table `wto`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `wto`.`user` (
  `iduser` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(16) NOT NULL,
  `email` VARCHAR(150) NOT NULL,
  `password` MEDIUMTEXT NOT NULL,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`iduser`))
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `wto`.`image`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `wto`.`image` (
  `idimage` INT NOT NULL AUTO_INCREMENT,
  `iduser` INT NOT NULL,
  `title` VARCHAR(150) NOT NULL,
  `content` LONGTEXT NOT NULL,
  `points` INT NOT NULL DEFAULT 0,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idimage`),
  INDEX `fk_image_1_idx` (`iduser` ASC),
  CONSTRAINT `fk_image_1`
    FOREIGN KEY (`iduser`)
    REFERENCES `wto`.`user` (`iduser`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `wto`.`image_vote`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `wto`.`image_vote` (
  `idvote` INT NOT NULL AUTO_INCREMENT,
  `idimage` INT NOT NULL COMMENT 'this attribute is the id either of the image or the comment',
  `votetype` TINYINT(1) NOT NULL COMMENT 'true is upvote\nfalse is downvote',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idvote`),
  CONSTRAINT `fk_vote_2`
    FOREIGN KEY (`idimage`)
    REFERENCES `wto`.`image` (`idimage`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci
COMMENT = 'Trigger explanation: \nafter create - if votetype is true increase points by one in image else decrease points by one\nafter update - if votetype is true increase points by 2 (one to neutral and one to upvote) else decrease by 2\nbefore delete - if votetype is true decrease points by 1 else increase by 1\nsame goes for comment_vote';


-- -----------------------------------------------------
-- Table `wto`.`comment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `wto`.`comment` (
  `idcomment` INT NOT NULL AUTO_INCREMENT,
  `idimage` INT NOT NULL,
  `iduser` INT NOT NULL,
  `content` VARCHAR(140) NOT NULL,
  `points` INT NOT NULL DEFAULT 0,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idcomment`),
  INDEX `fk_comment_1_idx` (`iduser` ASC),
  INDEX `fk_comment_2_idx` (`idimage` ASC),
  CONSTRAINT `fk_comment_1`
    FOREIGN KEY (`iduser`)
    REFERENCES `wto`.`user` (`iduser`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comment_2`
    FOREIGN KEY (`idimage`)
    REFERENCES `wto`.`image` (`idimage`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `wto`.`tag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `wto`.`tag` (
  `idtag` INT NOT NULL AUTO_INCREMENT,
  `idimage` INT NOT NULL,
  `content` VARCHAR(140) NOT NULL DEFAULT 'GENERAL',
  PRIMARY KEY (`idtag`),
  INDEX `fk_tag_1_idx` (`idimage` ASC),
  CONSTRAINT `fk_tag_1`
    FOREIGN KEY (`idimage`)
    REFERENCES `wto`.`image` (`idimage`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `wto`.`comment_vote`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `wto`.`comment_vote` (
  `idvote` INT NOT NULL AUTO_INCREMENT,
  `idcomment` INT NOT NULL COMMENT 'this attribute is the id either of the image or the comment',
  `votetype` TINYINT(1) NOT NULL COMMENT 'true is upvote\nfalse is downvote',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idvote`),
  INDEX `fk_comment_vote_comment1_idx` (`idcomment` ASC),
  CONSTRAINT `fk_comment_vote_comment1`
    FOREIGN KEY (`idcomment`)
    REFERENCES `wto`.`comment` (`idcomment`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
USE `wto`;

DELIMITER $$
USE `wto`$$
CREATE DEFINER = CURRENT_USER TRIGGER `wto`.`image_vote_AFTER_INSERT` AFTER INSERT ON `image_vote` FOR EACH ROW
BEGIN
	IF `votetype` = TRUE THEN
		UPDATE `comment` SET `points` = `points` + 1 ;
	ELSE
		UPDATE `comment` SET `points` = `points` - 1;
	END IF;
END
$$

USE `wto`$$
CREATE DEFINER = CURRENT_USER TRIGGER `wto`.`image_vote_AFTER_UPDATE` AFTER UPDATE ON `image_vote` FOR EACH ROW
BEGIN
	IF `votetype` = TRUE THEN
		UPDATE `comment` SET `points` = `points` + 2 ;
	ELSE
		UPDATE `comment` SET `points` = `points` - 2;
	END IF;
END
$$

USE `wto`$$
CREATE DEFINER = CURRENT_USER TRIGGER `wto`.`image_vote_BEFORE_DELETE` BEFORE DELETE ON `image_vote` FOR EACH ROW
BEGIN
	IF `votetype` = TRUE THEN
		UPDATE `comment` SET `points` = `points` - 1 ;
	ELSE
		UPDATE `comment` SET `points` = `points` + 1;
	END IF;
END
$$

USE `wto`$$
CREATE DEFINER = CURRENT_USER TRIGGER `wto`.`comment_vote_AFTER_INSERT` AFTER INSERT ON `comment_vote` FOR EACH ROW
BEGIN
	IF `votetype` = TRUE THEN
		UPDATE `comment` SET `points` = `points` + 1 ;
	ELSE
		UPDATE `comment` SET `points` = `points` - 1;
	END IF;
END
$$

USE `wto`$$
CREATE DEFINER = CURRENT_USER TRIGGER `wto`.`comment_vote_AFTER_UPDATE` AFTER UPDATE ON `comment_vote` FOR EACH ROW
BEGIN
	IF `votetype` = TRUE THEN
		UPDATE `comment` SET `points` = `points` + 2 ;
	ELSE
		UPDATE `comment` SET `points` = `points` - 2;
	END IF;
END
$$

USE `wto`$$
CREATE DEFINER = CURRENT_USER TRIGGER `wto`.`comment_vote_BEFORE_DELETE` BEFORE DELETE ON `comment_vote` FOR EACH ROW
BEGIN
	IF `votetype` = TRUE THEN
		UPDATE `comment` SET `points` = `points` - 1 ;
	ELSE
		UPDATE `comment` SET `points` = `points` + 1;
	END IF;
END
$$


DELIMITER ;
