-- user_type_scopes
SET FOREIGN_KEY_CHECKS=0;

ALTER TABLE `user_type_scopes`
DROP FOREIGN KEY `FK_1139xwtbp3pljyj5ym4ssb8yn`;
ALTER TABLE `user_type_scopes`
ADD CONSTRAINT `FK_1139xwtbp3pljyj5ym4ssb8yn` FOREIGN KEY (`scopes_id`) REFERENCES `scope` (`id`);

SET FOREIGN_KEY_CHECKS=1;