-- 知识库分类表
CREATE TABLE zw_kb_category (
    category_id   BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    category_name VARCHAR(50)  NOT NULL COMMENT '分类名称',
    category_code VARCHAR(20)  NOT NULL COMMENT '分类编码（唯一）',
    sort          INT DEFAULT 0 COMMENT '排序（越小越靠前）',
    status        CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    remark        VARCHAR(500) COMMENT '备注',
    create_by     VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    create_time   DATETIME COMMENT '创建时间',
    update_by     VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    update_time   DATETIME COMMENT '更新时间',
    del_flag      CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    UNIQUE KEY uk_category_code (category_code)
) ENGINE=InnoDB COMMENT='知识库分类表';

-- 插入默认数据（迁移原 kb_type 1-4）
INSERT INTO zw_kb_category (category_name, category_code, sort, status, del_flag, create_time) VALUES
('维修手册', 'manual', 1, '0', '0', NOW()),
('错误代码', 'error_code', 2, '0', '0', NOW()),
('FAQ', 'faq', 3, '0', '0', NOW()),
('其他', 'other', 4, '0', '0', NOW());

-- 修改知识库表，加分类字段
ALTER TABLE zw_knowledge_base ADD COLUMN category_id BIGINT COMMENT '分类ID';
UPDATE zw_knowledge_base SET category_id = CAST(kb_type AS SIGNED) WHERE del_flag = '0' AND kb_type IN ('1','2','3','4');
