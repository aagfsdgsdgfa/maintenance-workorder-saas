-- =============================================
-- 配件管理表 zw_part（合并版）
-- =============================================

DROP TABLE IF EXISTS zw_part;
CREATE TABLE zw_part (
  part_id        BIGINT(20)     NOT NULL AUTO_INCREMENT    COMMENT '配件ID',
  part_name      VARCHAR(100)   NOT NULL                   COMMENT '配件名称',
  part_code      VARCHAR(64)    DEFAULT ''                 COMMENT '配件编码',
  part_type      VARCHAR(64)    DEFAULT ''                 COMMENT '配件类别',
  spec_model     VARCHAR(200)   DEFAULT ''                 COMMENT '规格型号',
  unit           VARCHAR(20)    DEFAULT '个'              COMMENT '单位',
  unit_price     DECIMAL(10,2) DEFAULT 0                  COMMENT '参考单价(元)',
  brand          VARCHAR(64)    DEFAULT ''                 COMMENT '品牌',
  remark         VARCHAR(500)   DEFAULT NULL               COMMENT '备注',
  status         CHAR(1)        DEFAULT '0'                COMMENT '状态: 0正常 1停用',
  del_flag       CHAR(1)        DEFAULT '0'                COMMENT '删除标志: 0存在 2删除',
  create_by      VARCHAR(64)    DEFAULT ''                 COMMENT '创建者',
  create_time    DATETIME                                   COMMENT '创建时间',
  update_by      VARCHAR(64)    DEFAULT ''                 COMMENT '更新者',
  update_time    DATETIME                                   COMMENT '更新时间',
  PRIMARY KEY (part_id),
  KEY idx_part_name   (part_name),
  KEY idx_part_type   (part_type),
  KEY idx_part_status (status)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='配件管理表';

INSERT INTO zw_part (part_name, part_code, part_type, spec_model, unit, unit_price, brand, status, del_flag, create_by, create_time) VALUES
  ('电源模块',        'PWR-001',  '电源类',  '标准AC电源',          '个',   150.00, 'Dell',    '0', '0', 'admin', NOW()),
  ('风扇模块',        'FAN-001',  '散热类',  '80mm静音风扇',       '个',    80.00, 'Dell',    '0', '0', 'admin', NOW()),
  ('硬盘',            'HDD-001',  '存储类',  '1TB SAS 7200转',    '块',   600.00, 'Seagate', '0', '0', 'admin', NOW()),
  ('固态硬盘SSD',     'SSD-001',  '存储类',  '512GB SATA SSD',    '块',   450.00, '三星',    '0', '0', 'admin', NOW()),
  ('内存条',          'MEM-001',  '内存类',  '16GB DDR4 3200',    '条',   350.00, '金士顿',  '0', '0', 'admin', NOW()),
  ('网线/光纤跳线',   'NET-001',  '网络类',  'CAT6 3米',          '根',    25.00, '',        '0', '0', 'admin', NOW()),
  ('光纤模块SFP+',    'SFP-001',  '网络类',  '万兆多模 300米',    '个',   180.00, '思科',    '0', '0', 'admin', NOW()),
  ('交换机光模块',     'SWM-001',  '网络类',  '千兆单模 20公里',   '个',   220.00, '华为',    '0', '0', 'admin', NOW()),
  ('UPS电池',         'UPS-001',  '电源类',  '12V 100AH铅酸',     '节',   800.00, '山特',    '0', '0', 'admin', NOW()),
  ('Raid卡/阵列卡',  'RAID-001', '存储类',  'SAS 6Gb/s Raid卡',  '块',  1200.00, 'LSI',     '0', '0', 'admin', NOW()),
  ('显卡',            'GPU-001',  '计算类',  'RTX 4090 24G',     '块', 18000.00, '英伟达', '0', '0', 'admin', NOW()),
  ('服务器主板',      'MB-001',   '核心配件', 'Intel C622双路',    '块',  5000.00, 'Dell',    '0', '0', 'admin', NOW()),
  ('CPU散热器',       'HSF-001',  '散热类',  'LGA2011塔式散热器', '个',   200.00, 'Noctua',  '0', '0', 'admin', NOW()),
  ('防火墙电源线',     'PWR-002',  '电源类',  '16A 3米电源线',     '根',    35.00, '',        '0', '0', 'admin', NOW()),
  ('其他配件',        'OTH-001',  '其他',    '',                   '个',     0.00, '',        '0', '0', 'admin', NOW());

-- =============================================
-- 配件管理菜单（插入 sys_menu）
-- =============================================

SET @parent_id = (
    SELECT COALESCE(MAX(menu_id), 0)
    FROM sys_menu
    WHERE menu_name IN ('智维管理', '维修管理')
    LIMIT 1
);
SET @parent_id = IF(@parent_id = 0, 100, @parent_id);
SET @max_menu_id = (SELECT COALESCE(MAX(menu_id), 0) FROM sys_menu);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
  (@max_menu_id + 1, '配件管理', @parent_id, 8, 'part', 'zw/part/index', 1, 0, 'C', '0', '0', 'zw:part:list', 'tree', 'admin', NOW(), '配件管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
  (@max_menu_id + 2, '配件查询', @max_menu_id + 1, 1, 'index', 'zw/part/index', 1, 0, 'C', '0', '0', 'zw:part:list', '#', 'admin', NOW(), ''),
  (@max_menu_id + 3, '配件新增', @max_menu_id + 1, 2, 'add',   NULL,          1, 0, 'F', '0', '0', 'zw:part:add',   '#', 'admin', NOW(), ''),
  (@max_menu_id + 4, '配件修改', @max_menu_id + 1, 3, 'edit',  NULL,          1, 0, 'F', '0', '0', 'zw:part:edit',  '#', 'admin', NOW(), ''),
  (@max_menu_id + 5, '配件删除', @max_menu_id + 1, 4, 'remove',NULL,          1, 0, 'F', '0', '0', 'zw:part:remove','#', 'admin', NOW(), ''),
  (@max_menu_id + 6, '配件导出', @max_menu_id + 1, 5, 'export',NULL,          1, 0, 'F', '0', '0', 'zw:part:export', '#', 'admin', NOW(), '');
