-- =============================================
-- 维修配件字典（zw_part）
-- =============================================

-- 1. 字典类型
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES (100, '维修配件', 'zw_part', '0', 'admin', NOW(), '', NULL, '维修更换配件列表')
ON DUPLICATE KEY UPDATE dict_name = '维修配件', remark = '维修更换配件列表';

-- 2. 字典数据
SET @max_code = (SELECT COALESCE(MAX(dict_code), 99) FROM sys_dict_data);
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES
  (@max_code + 1,  1, '电源模块',       'power_module',       'zw_part', '', '', 'N', '0', 'admin', NOW(), '', NULL, '电源模块'),
  (@max_code + 2,  2, '风扇模块',       'fan_module',         'zw_part', '', '', 'N', '0', 'admin', NOW(), '', NULL, '风扇模块'),
  (@max_code + 3,  3, '硬盘',           'hard_disk',          'zw_part', '', '', 'N', '0', 'admin', NOW(), '', NULL, '硬盘'),
  (@max_code + 4,  4, '内存条',         'memory',             'zw_part', '', '', 'N', '0', 'admin', NOW(), '', NULL, '内存条'),
  (@max_code + 5,  5, '网线/光纤',     'network_cable',      'zw_part', '', '', 'N', '0', 'admin', NOW(), '', NULL, '网线或光纤跳线'),
  (@max_code + 6,  6, '交换机端口模块', 'switch_module',      'zw_part', '', '', 'N', '0', 'admin', NOW(), '', NULL, '交换机光模块/电模块'),
  (@max_code + 7,  7, '防火墙电源线',   'firewall_cable',    'zw_part', '', '', 'N', '0', 'admin', NOW(), '', NULL, '防火墙电源线'),
  (@max_code + 8,  8, 'UPS电池',       'ups_battery',        'zw_part', '', '', 'N', '0', 'admin', NOW(), '', NULL, 'UPS不间断电源电池'),
  (@max_code + 9,  9, '服务器主板',     'server_motherboard', 'zw_part', '', '', 'N', '0', 'admin', NOW(), '', NULL, '服务器主板'),
  (@max_code + 10, 10, '显卡',          'graphics_card',      'zw_part', '', '', 'N', '0', 'admin', NOW(), '', NULL, '显卡'),
  (@max_code + 11, 11, '散热器/CPU风扇', 'heatsink',          'zw_part', '', '', 'N', '0', 'admin', NOW(), '', NULL, '散热器或CPU风扇'),
  (@max_code + 12, 12, 'Raid卡/阵列卡', 'raid_card',         'zw_part', '', '', 'N', '0', 'admin', NOW(), '', NULL, 'Raid卡或阵列卡'),
  (@max_code + 13, 13, '光纤模块(SFP+)', 'sfp_module',       'zw_part', '', '', 'N', '0', 'admin', NOW(), '', NULL, '万兆光纤模块'),
  (@max_code + 14, 14, '网线头/水晶头', 'rj45_connector',   'zw_part', '', '', 'N', '0', 'admin', NOW(), '', NULL, 'RJ45网线水晶头'),
  (@max_code + 15, 15, '其他配件',       'other',             'zw_part', '', '', 'N', '0', 'admin', NOW(), '', NULL, '其他未分类配件');
