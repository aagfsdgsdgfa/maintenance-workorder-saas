-- =============================================
-- 工程师扩展信息表
-- 菜单已通过 sys_menu_zw.sql 或增量 SQL 导入，此文件仅建表
-- =============================================

CREATE TABLE IF NOT EXISTS zw_engineer_ext (
    user_id           BIGINT          NOT NULL PRIMARY KEY COMMENT '用户ID（关联sys_user.user_id）',
    employee_no       VARCHAR(32)     DEFAULT NULL COMMENT '工号',
    skill_level       VARCHAR(20)     DEFAULT NULL COMMENT '资质等级：初级/中级/高级/专家',
    specialties       VARCHAR(255)    DEFAULT NULL COMMENT '擅长领域',
    join_date         DATE            DEFAULT NULL COMMENT '入职日期',
    total_orders      INT(11)         DEFAULT 0 COMMENT '累计接单数',
    completed_orders  INT(11)         DEFAULT 0 COMMENT '累计完工数',
    rating_avg        DECIMAL(3,2)    DEFAULT 0 COMMENT '评分均分',
    pending_today     INT(11)         DEFAULT 0 COMMENT '今日待处理数',
    last_login_time   DATETIME        DEFAULT NULL COMMENT '最后登录时间',
    create_by         VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time       DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by         VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time       DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_employee_no (employee_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工程师扩展信息表';

COMMIT;
