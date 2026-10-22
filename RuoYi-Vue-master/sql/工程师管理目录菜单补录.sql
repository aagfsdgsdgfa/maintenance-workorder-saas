-- =============================================
-- 工程师管理目录和菜单补录（修正列名转义）
-- =============================================

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2500, '工程师管理', 0, 6, 'zwEngineer', null, '', '', 1, 0, 'M', '0', '0', '', 'user', 'admin', sysdate(), '工程师管理目录');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2510, '工程师管理', 2500, 1, 'engineer', 'zw/engineer/index', '', '', 1, 0, 'C', '0', '0', 'zw:engineer:list', 'user', 'admin', sysdate(), '工程师管理菜单');

COMMIT;
