-- ============================================================
-- 智维云服小程序（工程师端）后端接口补充 DDL
-- 生成时间: 2026-04-22
-- 说明: 补充 zw_inspection_result 表字段，支持小程序巡检点执行页 GPS 定位和工程师追溯
-- ============================================================

-- -----------------------------------------------
-- 1. zw_inspection_result 表新增字段
-- -----------------------------------------------
ALTER TABLE zw_inspection_result
  ADD COLUMN point_id BIGINT DEFAULT NULL COMMENT '巡检点ID',
  ADD COLUMN engineer_id BIGINT DEFAULT NULL COMMENT '工程师ID（执行人）',
  ADD COLUMN latitude DECIMAL(10,6) DEFAULT NULL COMMENT '签到纬度',
  ADD COLUMN longitude DECIMAL(10,6) DEFAULT NULL COMMENT '签到经度',
  ADD COLUMN address VARCHAR(500) DEFAULT NULL COMMENT '签到地址';

-- -----------------------------------------------
-- 2. 巡检点扫码验证表（可选，如需独立记录扫码验证日志）
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS zw_inspection_verify_log (
    log_id       BIGINT         AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    point_id     BIGINT         NOT NULL         COMMENT '巡检点ID',
    engineer_id  BIGINT         DEFAULT NULL    COMMENT '工程师ID',
    scan_code    VARCHAR(255)   DEFAULT NULL    COMMENT '扫码内容',
    latitude     DECIMAL(10,6)  DEFAULT NULL    COMMENT '扫码时纬度',
    longitude    DECIMAL(10,6)  DEFAULT NULL    COMMENT '扫码时经度',
    verify_result TINYINT(1)   DEFAULT 0        COMMENT '验证结果：0失败 1成功',
    verify_time   DATETIME      DEFAULT NULL    COMMENT '验证时间',
    create_by     VARCHAR(64)   DEFAULT ''      COMMENT '创建者',
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by     VARCHAR(64)   DEFAULT ''      COMMENT '更新者',
    update_time   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag      CHAR(1)       DEFAULT '0'     COMMENT '删除标志（0正常 2删除）',
    INDEX idx_point_id (point_id),
    INDEX idx_engineer_id (engineer_id),
    INDEX idx_verify_time (verify_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检点扫码验证记录表';

-- -----------------------------------------------
-- 3. AI 日报内容表（可选，如需存储 AI 生成的原始内容）
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS zw_ai_report_cache (
    cache_id       BIGINT         AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    engineer_id    BIGINT         NOT NULL        COMMENT '工程师ID',
    report_date    VARCHAR(20)    NOT NULL        COMMENT '日报日期 yyyy-MM-dd',
    ai_title       VARCHAR(255)  DEFAULT NULL     COMMENT 'AI生成标题',
    ai_summary     TEXT           DEFAULT NULL    COMMENT 'AI生成摘要',
    ai_sections    TEXT           DEFAULT NULL    COMMENT 'AI生成各节内容（JSON格式）',
    raw_data       TEXT           DEFAULT NULL    COMMENT '原始聚合数据（JSON格式）',
    status         VARCHAR(10)    DEFAULT '0'     COMMENT '状态：0草稿 1已提交',
    create_by      VARCHAR(64)   DEFAULT ''       COMMENT '创建者',
    create_time    DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by      VARCHAR(64)   DEFAULT ''       COMMENT '更新者',
    update_time    DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag       CHAR(1)       DEFAULT '0'      COMMENT '删除标志',
    UNIQUE KEY uk_engineer_date (engineer_id, report_date),
    INDEX idx_report_date (report_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI日报缓存表';

-- -----------------------------------------------
-- 4. 故障拒单记录表（可选，如需记录拒单原因）
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS zw_work_order_reject_log (
    log_id         BIGINT         AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    work_order_id   BIGINT         NOT NULL        COMMENT '工单ID',
    engineer_id    BIGINT          DEFAULT NULL    COMMENT '拒单工程师ID',
    reject_reason  VARCHAR(500)   DEFAULT NULL     COMMENT '拒单原因',
    reject_time    DATETIME        DEFAULT NULL    COMMENT '拒单时间',
    create_by      VARCHAR(64)   DEFAULT ''        COMMENT '创建者',
    create_time    DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_work_order_id (work_order_id),
    INDEX idx_engineer_id (engineer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单拒单记录表';

-- -----------------------------------------------
-- 5. 完工确认记录表（可选，如需记录完工配件和签名）
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS zw_work_order_complete_log (
    log_id          BIGINT         AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    work_order_id   BIGINT         NOT NULL        COMMENT '工单ID',
    result_desc     TEXT           DEFAULT NULL    COMMENT '维修结果描述',
    sign_image      VARCHAR(500)   DEFAULT NULL    COMMENT '甲方签名图片URL',
    photos          TEXT           DEFAULT NULL    COMMENT '完工照片URLs（逗号分隔）',
    parts           TEXT           DEFAULT NULL    COMMENT '更换配件（JSON格式）',
    next_visit_time VARCHAR(50)    DEFAULT NULL    COMMENT '建议下次巡检时间',
    complete_time   DATETIME       DEFAULT NULL    COMMENT '完工时间',
    create_by       VARCHAR(64)   DEFAULT ''       COMMENT '创建者',
    create_time     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_work_order_id (work_order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单完工确认记录表';
