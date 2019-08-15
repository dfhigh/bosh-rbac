CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` VARCHAR(31) NOT NULL,
  `username` VARCHAR(255) NOT NULL,
  `admin` TINYINT(1) NOT NULL DEFAULT 0,
  `created_at` DATETIME(3) NOT NULL
);

CREATE TABLE `roles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL
);

CREATE TABLE `policies` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255) DEFAULT null,
  `resource_type` TINYINT(1) NOT NULL,
  `resource_value` VARCHAR(255) NOT NULL,
  `action` TINYINT(1) NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `updated_at` DATETIME(3) NOT NULL
);

CREATE TABLE `users_roles` (
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL
);

CREATE TABLE `entities_policies` (
  `entity_type` TINYINT(1) NOT NULL,
  `entity_id` BIGINT NOT NULL,
  `policy_id` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL
);
