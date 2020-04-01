ALTER TABLE `timestamps`
CHANGE COLUMN `root_hash` `root_hash` VARCHAR(64) NULL ,
CHANGE COLUMN `transaction` `transaction` VARCHAR(64) NULL ;
