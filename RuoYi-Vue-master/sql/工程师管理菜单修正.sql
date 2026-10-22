-- =============================================
-- 工程师管理菜单修正 SQL
-- 场景：menu_id=2400 已是工程师管理目录，但组件路径错误导致页面不显示
-- =============================================

-- 修正 menu_id=2400 的组件（原来是目录M，组件为NULL是对的，但子菜单需要对齐）
-- 将 2410 的组件从错误的路径改为正确的工程师页面
UPDATE sys_menu SET component = 'zw/engineer/index' WHERE menu_id = 2410 AND menu_name = '工程师管理';

-- 如果 2410 不存在（之前没插入成功），则插入
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2410, '工程师管理', 2400, 1, 'engineer', 'zw/engineer/index', '', '', 1, 0, 'C', '0', '0', 'zw:engineer:list', 'user', 'admin', sysdate(), '工程师管理菜单'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2410);

-- 插入按钮权限（如果不存在）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2411, '工程师查询', 2410, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:engineer:list', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2411);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2412, '工程师新增', 2410, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:engineer:add', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2412);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2413, '工程师修改', 2410, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:engineer:edit', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2413);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2414, '工程师删除', 2410, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:engineer:remove', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2414);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2415, '工程师导出', 2410, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:engineer:export', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2415);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2416, '工单派发', 2410, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:engineer:dispatch', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2416);

COMMIT;