SET FOREIGN_KEY_CHECKS=0;

-- user_scope_assignment
ALTER TABLE `user_scope_assignment`
DROP FOREIGN KEY `FK_b3s60ld6pko88nvtjag54optd`;

ALTER TABLE `user_scope_assignment`
DROP FOREIGN KEY `FK_e9v6r79lemhfpax7g27s17c5r`;

ALTER TABLE `user_scope_assignment`
CHANGE COLUMN `user_creation` `user_creation_id` bigint(20);

ALTER TABLE `user_scope_assignment`
CHANGE COLUMN `scope` `scope_id` bigint(20);

ALTER TABLE `user_scope_assignment`
ADD CONSTRAINT `FK_b3s60ld6pko88nvtjag54optd` FOREIGN KEY (`user_creation_id`) REFERENCES `user_creation` (`id`);

ALTER TABLE `user_scope_assignment`
ADD CONSTRAINT `FK_e9v6r79lemhfpax7g27s17c5r` FOREIGN KEY (`scope_id`) REFERENCES `scope` (`id`);

-- user_type_scopes
ALTER TABLE `user_type_scopes`
DROP FOREIGN KEY `FK_3mr67pecnjd3oe94yoqi49xvl`;

ALTER TABLE `user_type_scopes`
DROP FOREIGN KEY `FK_1139xwtbp3pljyj5ym4ssb8yn`;

ALTER TABLE `user_type_scopes`
CHANGE COLUMN `user_type` `user_type_id` bigint(20);

ALTER TABLE `user_type_scopes`
CHANGE COLUMN `scopes` `scopes_id` bigint(20);

ALTER TABLE `user_type_scopes`
ADD CONSTRAINT `FK_3mr67pecnjd3oe94yoqi49xvl` FOREIGN KEY (`user_type_id`) REFERENCES `user_type` (`id`);

ALTER TABLE `user_type_scopes`
ADD CONSTRAINT `FK_1139xwtbp3pljyj5ym4ssb8yn` FOREIGN KEY (`scopes_id`) REFERENCES `scopes` (`id`);

SET FOREIGN_KEY_CHECKS=1;