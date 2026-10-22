-- 修复：zw_inspection_task 表缺少 report_url 字段
ALTER TABLE zw_inspection_task ADD COLUMN report_url VARCHAR(500) DEFAULT '' COMMENT '巡检报告URL';
