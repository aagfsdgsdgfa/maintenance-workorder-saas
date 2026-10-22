-- =============================================
-- 工程师管理菜单增量 SQL（修正版）
-- 注意：数据库中 menu_id=2400 已被合同管理占用，此处使用 2500 起的新 ID
-- =============================================

-- -----------------------------------------------
-- 1. 工程师管理目录
-- -----------------------------------------------
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2500, '工程师管理', 0, 6, 'zwEngineer', null, '', '', 1, 0, 'M', '0', '0', '', 'user', 'admin', sysdate(), '工程师管理目录');

-- -----------------------------------------------
-- 2. 工程师管理菜单
-- -----------------------------------------------
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2510, '工程师管理', 2500, 1, 'engineer', 'zw/engineer/index', '', '', 1, 0, 'C', '0', '0', 'zw:engineer:list', 'user', 'admin', sysdate(), '工程师管理菜单');

-- -----------------------------------------------
-- 3. 按钮权限
-- -----------------------------------------------
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2511, '工程师查询', 2510, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:engineer:list', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2512, '工程师新增', 2510, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:engineer:add', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2513, '工程师修改', 2510, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:engineer:edit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2514, '工程师删除', 2510, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:engineer:remove', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2515, '工程师导出', 2510, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:engineer:export', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2516, '工单派发', 2510, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:engineer:dispatch', '#', 'admin', sysdate(), '');

COMMIT;
