-- =============================================
-- 客户门户测试账号 SQL
-- 执行前请确保已执行 ALTER TABLE 添加 customer_id 字段
-- =============================================

-- Step 1：给 sys_user 加字段（如果还未执行）
ALTER TABLE sys_user ADD COLUMN customer_id BIGINT NULL COMMENT '关联客户ID（客户门户用）';

-- Step 2：创建两个公司的测试账号
-- A公司（云智科技，customer_id=100）
INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, email, phonenumber, sex, avatar, password, status, del_flag, customer_id, create_by, create_time)
VALUES (200, 100, 'yunzhi_user', '云智科技管理员', 'yunzhi@test.com', '13800001111', '0', '', '$2a$10$7JB720yubVSOfUI26rcq9uk4LIT7GVEGbB4A3ZG6zQLqDEIG/mfAO', '0', '0', 100, 'admin', NOW())
ON DUPLICATE KEY UPDATE customer_id = 100;

-- B公司（东方集团，customer_id=101）
INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, email, phonenumber, sex, avatar, password, status, del_flag, customer_id, create_by, create_time)
VALUES (201, 101, 'dongfang_user', '东方集团管理员', 'dongfang@test.com', '13800002222', '0', '', '$2a$10$7JB720yubVSOfUI26rcq9uk4LIT7GVEGbB4A3ZG6zQLqDEIG/mfAO', '0', '0', 101, 'admin', NOW())
ON DUPLICATE KEY UPDATE customer_id = 101;

-- 给账号分配角色（2=普通角色，使能登录）
INSERT INTO sys_user_role (user_id, role_id) VALUES (200, 2) ON DUPLICATE KEY UPDATE role_id = role_id;
INSERT INTO sys_user_role (user_id, role_id) VALUES (201, 2) ON DUPLICATE KEY UPDATE role_id = role_id;

-- =============================================
-- 验证查询（执行后查看结果）
-- =============================================
SELECT user_id, user_name, nick_name, customer_id FROM sys_user WHERE user_name IN ('yunzhi_user', 'dongfang_user');
