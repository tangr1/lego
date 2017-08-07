CREATE TABLE `acl_class` (
  `id`    BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `class` VARCHAR(100)        NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `acl_sid` (
  `id`        BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `principal` TINYINT(1)          NOT NULL,
  `sid`       VARCHAR(100)        NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `acl_object_identity` (
  `id`                 BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `object_id_class`    BIGINT(20) UNSIGNED NOT NULL,
  `object_id_identity` BIGINT(20)          NOT NULL,
  `parent_object`      BIGINT(20) UNSIGNED          DEFAULT NULL,
  `owner_sid`          BIGINT(20) UNSIGNED          DEFAULT NULL,
  `entries_inheriting` TINYINT(1)          NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `acl_entry` (
  `id`                  BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `acl_object_identity` BIGINT(20) UNSIGNED NOT NULL,
  `ace_order`           INT(11)             NOT NULL,
  `sid`                 BIGINT(20) UNSIGNED NOT NULL,
  `mask`                INT(10) UNSIGNED    NOT NULL,
  `granting`            TINYINT(1)          NOT NULL,
  `audit_success`       TINYINT(1)          NOT NULL,
  `audit_failure`       TINYINT(1)          NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `account` (
  `id`       BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `phone`    VARCHAR(255)    NOT NULL,
  `name`     VARCHAR(255)             DEFAULT '',
  `password` VARCHAR(255)    NOT NULL,
  `roles`    SET('ADMIN', 'OPERATOR', 'USER'),
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `client` (
  `id`                             MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `client_id`                      VARCHAR(255)       NOT NULL,
  `client_secret`                  VARCHAR(255)       NOT NULL,
  `scope`                          VARCHAR(255)       NOT NULL,
  `access_token_validity_seconds`  INT UNSIGNED                DEFAULT 604800,
  `refresh_token_validity_seconds` INT UNSIGNED                DEFAULT 604800,
  `resource`                       VARCHAR(255)       NOT NULL,
  `authority`                      VARCHAR(255)       NOT NULL,
  `grant_type`                     VARCHAR(255)       NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
