-- Step 1：通知表加 customer_id 字段
ALTER TABLE zw_notification ADD COLUMN customer_id BIGINT NULL COMMENT '关联客户ID（公司隔离用）';

-- Step 2：为已有数据补充 customer_id（关联 sys_user 表）
UPDATE zw_notification n
INNER JOIN sys_user u ON n.user_id = u.user_id
SET n.customer_id = u.customer_id
WHERE u.customer_id IS NOT NULL;
