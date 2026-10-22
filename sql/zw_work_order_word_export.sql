-- 维保工单Word导出 - 工单表字段扩展
-- 执行前请备份数据

-- 受理时间
ALTER TABLE zw_work_order ADD COLUMN accept_time DATETIME COMMENT '受理时间' AFTER contact_address;

-- 服务时间
ALTER TABLE zw_work_order ADD COLUMN service_time DATETIME COMMENT '服务时间' AFTER accept_time;

-- 维保类型（定期检查 / 故障维修 / 其他）
ALTER TABLE zw_work_order ADD COLUMN work_order_type VARCHAR(20) COMMENT '维保类型' AFTER service_time;

-- 故障描述（甲方填写）
ALTER TABLE zw_work_order ADD COLUMN fault_description TEXT COMMENT '故障描述（甲方）' AFTER work_order_type;

-- 故障原因（乙方填写）
ALTER TABLE zw_work_order ADD COLUMN fault_cause TEXT COMMENT '故障原因（乙方）' AFTER fault_description;

-- 上门费
ALTER TABLE zw_work_order ADD COLUMN visit_fee DECIMAL(10,2) DEFAULT 0 COMMENT '上门费' AFTER fault_cause;

-- 工时费
ALTER TABLE zw_work_order ADD COLUMN service_fee DECIMAL(10,2) DEFAULT 0 COMMENT '工时费' AFTER visit_fee;

-- 合计金额
ALTER TABLE zw_work_order ADD COLUMN total_amount DECIMAL(10,2) DEFAULT 0 COMMENT '合计金额' AFTER service_fee;

-- 是否收费（是 / 否）
ALTER TABLE zw_work_order ADD COLUMN charge_status VARCHAR(10) COMMENT '是否收费' AFTER total_amount;

-- 测试情况与运行状态
ALTER TABLE zw_work_order ADD COLUMN test_status TEXT COMMENT '测试情况与运行状态' AFTER charge_status;

-- 客户签字图片URL
ALTER TABLE zw_work_order ADD COLUMN customer_sign_url VARCHAR(500) COMMENT '客户签字图片URL' AFTER test_status;

-- 工程师签字图片URL
ALTER TABLE zw_work_order ADD COLUMN engineer_sign_url VARCHAR(500) COMMENT '工程师签字图片URL' AFTER customer_sign_url;

-- 备注说明
ALTER TABLE zw_work_order ADD COLUMN remark TEXT COMMENT '备注说明' AFTER engineer_sign_url;
