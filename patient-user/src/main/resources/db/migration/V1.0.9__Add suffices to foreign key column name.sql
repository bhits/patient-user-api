
ALTER TABLE `user_creation_aud` CHANGE COLUMN `user_type` `user_type_id` bigint(20);

ALTER TABLE `user_scope_assignment_aud` CHANGE COLUMN `scope` `scope_id` bigint(20);
ALTER TABLE `user_scope_assignment_aud` CHANGE COLUMN `user_creation` `user_creation_id` bigint(20);

ALTER TABLE `user_type_scopes_aud` CHANGE COLUMN `user_type` `user_type_id` bigint(20);
ALTER TABLE `user_type_scopes_aud` CHANGE COLUMN `scopes` `scopes_id` bigint(20);

ALTER TABLE `user_creation` CHANGE COLUMN `user_type` `user_type_id` bigint(20);

-- ALTER TABLE `user_type_scopes` CHANGE COLUMN `user_type` `user_type_id` bigint(20);
-- ALTER TABLE `user_type_scopes` CHANGE COLUMN `scopes` `scopes_id` bigint(20);













