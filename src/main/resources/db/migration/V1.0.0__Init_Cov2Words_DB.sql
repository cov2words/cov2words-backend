-- Initiale functions
DROP FUNCTION IF EXISTS uuidToString;
DROP FUNCTION IF EXISTS uuidToBinary;

CREATE FUNCTION `uuidToBinary`(uuid VARCHAR(36)) RETURNS BINARY(16)
RETURN UNHEX(REPLACE(uuid, '-', ''));

CREATE FUNCTION `uuidToString`(uuid BINARY(16)) RETURNS VARCHAR(36)
RETURN LOWER(CONCAT(
    SUBSTR(HEX(uuid), 1, 8), '-',
    SUBSTR(HEX(uuid), 9, 4), '-',
    SUBSTR(HEX(uuid), 13, 4), '-',
    SUBSTR(HEX(uuid), 17, 4), '-',
    SUBSTR(HEX(uuid), 21)
  ));

-- ====================================================
-- === Account
-- ====================================================

-- Table structure for table `accounts`
CREATE TABLE IF NOT EXISTS accounts
(
  uuid          BINARY(16)   NOT NULL,
  bad_logins    BIGINT(20)  DEFAULT NULL,
  date_created  DATETIME(3) DEFAULT NULL,
  date_modified DATETIME(3) DEFAULT NULL,
  email         VARCHAR(255) NOT NULL,
  last_login    DATETIME(3) DEFAULT NULL,
  last_logout   DATETIME(3) DEFAULT NULL,
  password      VARCHAR(255) NOT NULL,
  platform      INT(11)      NOT NULL,
  status        INT(11)      NOT NULL,
  account_type  INT(11)      NOT NULL,
  PRIMARY KEY (uuid)
);

-- ====================================================
-- === API Keys
-- ====================================================

-- Table structure for table `api_keys`
CREATE TABLE IF NOT EXISTS `api_keys`
(
  `uuid`             BINARY(16)  NOT NULL,
  `date_created`     DATETIME             DEFAULT NULL,
  `date_modified`    DATETIME             DEFAULT NULL,
  `api_key_priority` SMALLINT(5) NOT NULL DEFAULT '0',
  `free_usage`       INT         NULL,
  `date_invalidated` DATETIME(3) NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uk_api_keys` (`uuid`)
);

-- create account api key mapping
CREATE TABLE `accounts_api_key_mapping`
(
  `uuid`          BINARY(16)  NOT NULL,
  `date_created`  DATETIME(3) NULL DEFAULT NULL,
  `date_modified` DATETIME(3) NULL DEFAULT NULL,
  `api_key_uuid`  BINARY(16)  NULL,
  `account_uuid`  BINARY(16)  NULL,
  PRIMARY KEY (`uuid`),
  INDEX `fk_account_api_key_idx` (`api_key_uuid` ASC),
  INDEX `fk_accounts_accounts_idx` (`account_uuid` ASC)
);

-- ====================================================
-- === Account Information
-- ====================================================
CREATE TABLE accounts_information
(
  uuid              BINARY(16)   NOT NULL,
  company_name      VARCHAR(128) NULL,
  date_created      DATETIME(3)  NULL,
  date_modified     DATETIME(3)  NULL,
  referral_source   varchar(64)  NULL,
  referral_campaign varchar(256) NULL,
  referral_code     varchar(256) NULL,
  account_type      tinyint(1)   NULL,
  first_name        varchar(64)  NULL,
  last_name         varchar(64)  NULL,
  mail              varchar(64)  NULL,
  PRIMARY KEY (`uuid`),
  CONSTRAINT `fk_accounts_accounts_information`
    FOREIGN KEY (`uuid`)
      REFERENCES `accounts` (`uuid`)
      ON DELETE NO ACTION
      ON UPDATE NO ACTION
);

CREATE TABLE account_contacts
(
  uuid              BINARY(16)  NOT NULL,
  account_info_uuid BINARY(16)  NULL,
  contact_type      TINYINT(1)  NOT NULL,
  first_name        VARCHAR(64),
  last_name         VARCHAR(64),
  mail              VARCHAR(64),
  phone             VARCHAR(32),
  date_modified     DATETIME(3) NOT NULL,
  date_created      DATETIME(3) NOT NULL,
  date_invalidated  DATETIME(3),
  PRIMARY KEY (uuid),
  CONSTRAINT fk_contact_account_info FOREIGN KEY (account_info_uuid) REFERENCES accounts_information (uuid) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE account_addresses
(
  uuid              BINARY(16)  NOT NULL,
  account_info_uuid BINARY(16)  NULL,
  address_type      TINYINT(1)  NOT NULL,
  address_line_1    VARCHAR(128),
  address_line_2    VARCHAR(128),
  city              VARCHAR(32),
  state             VARCHAR(32),
  zip_code          VARCHAR(8),
  country           VARCHAR(32),
  date_created      DATETIME(3) NOT NULL,
  date_modified     DATETIME(3) NOT NULL,
  date_invalidated  DATETIME(3),
  PRIMARY KEY (uuid),
  CONSTRAINT fk_address_account_info FOREIGN KEY (account_info_uuid) REFERENCES accounts_information (uuid) ON DELETE NO ACTION ON UPDATE NO ACTION
);

-- ====================================================
-- === Account Permissions
-- ====================================================
CREATE TABLE permissions
(
  uuid             BINARY(16)  NOT NULL,
  date_created     DATETIME(3) NOT NULL,
  date_modified    DATETIME(3) NOT NULL,
  date_invalidated DATETIME(3) NULL,
  permission_label VARCHAR(32) NOT NULL,
  PRIMARY KEY (uuid)
);

CREATE TABLE account_permissions
(
  uuid             BINARY(16)    NOT NULL,
  date_created     DATETIME(3)   NOT NULL,
  date_modified    DATETIME(3)   NOT NULL,
  date_invalidated DATETIME(3)   NULL,
  account_uuid     BINARY(16)    NOT NULL,
  permission_uuid  BINARY(16)    NOT NULL,
  value_string     VARCHAR(64)   NULL,
  value_boolean    TINYINT(1)    NULL,
  value_number     DECIMAL(9, 3) NULL,
  PRIMARY KEY (`uuid`),
  INDEX fk_account_permission_account_idx (account_uuid ASC),
  INDEX fk_account_permission_permission_idx (permission_uuid ASC),
  CONSTRAINT fk_account_permission_permissions
    FOREIGN KEY (permission_uuid)
      REFERENCES permissions (uuid)
      ON DELETE NO ACTION
      ON UPDATE NO ACTION
);

-- Add initial permissions
INSERT INTO `permissions` (`uuid`, `date_created`, `date_modified`, `date_invalidated`, `permission_label`)
VALUES (uuidToBinary(uuid()), now(), now(), null, 'GitHub');

INSERT INTO `permissions` (`uuid`, `date_created`, `date_modified`, `date_invalidated`, `permission_label`)
VALUES (uuidToBinary(uuid()), now(), now(), null, 'accounts');

INSERT INTO `permissions` (`uuid`, `date_created`, `date_modified`, `date_invalidated`, `permission_label`)
VALUES (uuidToBinary(uuid()), now(), now(), null, 'storage_size');

INSERT INTO `permissions` (`uuid`, `date_created`, `date_modified`, `date_invalidated`, `permission_label`)
VALUES (uuidToBinary(uuid()), now(), now(), null, 'storage_downloads');

INSERT INTO `permissions` (`uuid`, `date_created`, `date_modified`, `date_invalidated`, `permission_label`)
VALUES (uuidToBinary(uuid()), now(), now(), null, 'storage_period');

-- Assign initial permissions to account
INSERT INTO account_permissions
SELECT uuidToBinary(uuid())                                                           as uuid,
       now()                                                                          as date_created,
       now()                                                                          as date_modified,
       null                                                                           as date_invalidated,
       a.uuid,
       (SELECT p.uuid FROM permissions p WHERE p.permission_label = 'GitHub' LIMIT 1) as permission_uuid,
       null                                                                           as value_string,
       1                                                                              as value_boolean,
       null                                                                           as value_number
FROM accounts a;

INSERT INTO account_permissions
SELECT uuidToBinary(uuid())                                                             as uuid,
       now()                                                                            as date_created,
       now()                                                                            as date_modified,
       null                                                                             as date_invalidated,
       a.uuid,
       (SELECT p.uuid FROM permissions p WHERE p.permission_label = 'accounts' LIMIT 1) as permission_uuid,
       null                                                                             as value_string,
       1                                                                                as value_boolean,
       null                                                                             as value_number
FROM accounts a;

-- ====================================================
-- === Account Settings
-- ====================================================
CREATE TABLE account_settings
(
  uuid             BINARY(16) NOT NULL,
  date_created     DATETIME(3)  DEFAULT NULL,
  date_modified    DATETIME(3)  DEFAULT NULL,
  date_invalidated DATETIME(3)  DEFAULT NULL,
  setting_type     INT(11)      DEFAULT NULL,
  setting_category INT(11)      DEFAULT NULL,
  setting_order    INT(11)      DEFAULT NULL,
  setting_default  VARCHAR(255) DEFAULT NULL,
  setting_label    VARCHAR(64)  DEFAULT NULL,
  PRIMARY KEY (uuid)
);

-- Create table for account - settings - mapping
CREATE TABLE account_settings_mapping
(
  uuid                  BINARY(16) NOT NULL,
  date_created          DATETIME(3) DEFAULT NULL,
  date_modified         DATETIME(3) DEFAULT NULL,
  account_uuid          BINARY(16) NOT NULL,
  account_settings_uuid BINARY(16) NOT NULL,
  date_invalidated      DATETIME(3) DEFAULT NULL,
  setting_value         TEXT        DEFAULT NULL,
  PRIMARY KEY (uuid),
  KEY fk_account_account_settings_mapping_index (account_uuid),
  KEY fk_settings_account_settings_mapping_index (account_settings_uuid),
  CONSTRAINT fk_account_account_settings_mapping FOREIGN KEY (account_uuid)
    REFERENCES accounts (uuid)
    ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_settings_account_settings_mapping FOREIGN KEY (account_settings_uuid)
    REFERENCES account_settings (uuid)
    ON DELETE NO ACTION ON UPDATE NO ACTION
);


-- ====================================================
-- === Account Events
-- ====================================================
CREATE TABLE account_events
(
  uuid         BINARY(16)  NOT NULL,
  account_uuid BINARY(16)  NOT NULL,
  event_type   TINYINT(4)  NOT NULL,
  date_created DATETIME(3) NOT NULL,
  PRIMARY KEY (uuid)
);

CREATE TABLE account_event_variables
(
  event_uuid BINARY(16)   NOT NULL,
  name       VARCHAR(50)  NOT NULL,
  value      VARCHAR(100) NOT NULL,
  ordinal    MEDIUMINT    NOT NULL,
  INDEX idx_account_event_variables (event_uuid),
  CONSTRAINT fk_account_event_variables
    FOREIGN KEY (event_uuid)
      REFERENCES account_events (uuid)
      ON DELETE CASCADE
);
