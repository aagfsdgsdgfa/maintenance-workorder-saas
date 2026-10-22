-- =============================================
-- 智维云服系统 - 完整菜单初始化SQL
-- 4个一级目录，17个业务菜单
-- =============================================

DELETE FROM sys_menu WHERE menu_id >= 2000;

-- =============================================
-- 一级目录1：资产管理
-- =============================================
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2000, '资产管理', 0, 1, 'zwAsset', null, '', '', 1, 0, 'M', '0', '0', '', 'server', 'admin', sysdate(), '资产管理目录');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2010, '资产管理', 2000, 1, 'asset', 'zw/asset/index', '', '', 1, 0, 'C', '0', '0', 'zw:asset:list', 'server', 'admin', sysdate(), '资产管理菜单');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2011, '资产查询', 2010, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:asset:query', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2012, '资产新增', 2010, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:asset:add', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2013, '资产修改', 2010, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:asset:edit', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2014, '资产删除', 2010, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:asset:remove', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2015, '资产导出', 2010, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:asset:export', '#', 'admin', sysdate(), '');

-- =============================================
-- 一级目录2：工单管理
-- =============================================
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2100, '工单管理', 0, 2, 'zwWorkOrder', null, '', '', 1, 0, 'M', '0', '0', '', 'clipboard', 'admin', sysdate(), '工单管理目录');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2110, '工单管理', 2100, 1, 'workOrder', 'zw/workOrder/index', '', '', 1, 0, 'C', '0', '0', 'zw:workorder:list', 'clipboard', 'admin', sysdate(), '工单管理菜单');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2111, '工单查询', 2110, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:workorder:query', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2112, '工单新增', 2110, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:workorder:add', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2113, '工单修改', 2110, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:workorder:edit', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2114, '工单删除', 2110, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:workorder:remove', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2115, '工单导出', 2110, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:workorder:export', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2120, '报修申请', 2100, 2, 'repairApply', 'zw/repairApply/index', '', '', 1, 0, 'C', '0', '0', 'zw:repairapply:list', 'message', 'admin', sysdate(), '报修申请菜单');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2121, '报修查询', 2120, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:repairapply:query', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2122, '报修新增', 2120, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:repairapply:add', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2123, '报修修改', 2120, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:repairapply:edit', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2124, '报修删除', 2120, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:repairapply:remove', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2125, '报修受理', 2120, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:repairapply:accept', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2126, '报修转工单', 2120, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:repairapply:transfer', '#', 'admin', sysdate(), '');

-- =============================================
-- 一级目录3：巡检管理
-- =============================================
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2200, '巡检管理', 0, 3, 'zwInspection', null, '', '', 1, 0, 'M', '0', '0', '', 'check', 'admin', sysdate(), '巡检管理目录');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2210, '巡检任务', 2200, 1, 'inspection', 'zw/inspection/index', '', '', 1, 0, 'C', '0', '0', 'zw:inspection:list', 'check', 'admin', sysdate(), '巡检任务菜单');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2211, '巡检查询', 2210, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:inspection:query', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2212, '巡检新增', 2210, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:inspection:add', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2213, '巡检修改', 2210, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:inspection:edit', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2214, '巡检删除', 2210, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:inspection:remove', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2220, '巡检计划', 2200, 2, 'inspectionPlan', 'zw/inspectionPlan/index', '', '', 1, 0, 'C', '0', '0', 'zw:inspectionplan:list', 'tree', 'admin', sysdate(), '巡检计划菜单');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2221, '计划查询', 2220, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:inspectionplan:query', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2222, '计划新增', 2220, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:inspectionplan:add', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2223, '计划修改', 2220, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:inspectionplan:edit', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2224, '计划删除', 2220, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:inspectionplan:remove', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2230, '巡检点', 2200, 3, 'inspectionPoint', 'zw/inspectionPoint/index', '', '', 1, 0, 'C', '0', '0', 'zw:inspectionpoint:list', 'clipboard', 'admin', sysdate(), '巡检点菜单');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2231, '巡检点查询', 2230, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:inspectionpoint:query', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2232, '巡检点新增', 2230, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:inspectionpoint:add', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2233, '巡检点修改', 2230, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:inspectionpoint:edit', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2234, '巡检点删除', 2230, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:inspectionpoint:remove', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2240, '巡检照片', 2200, 4, 'inspectionPhoto', 'zw/inspectionPhoto/index', '', '', 1, 0, 'C', '0', '0', 'zw:inspectionphoto:list', 'picture', 'admin', sysdate(), '巡检照片菜单');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2241, '照片查询', 2240, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:inspectionphoto:query', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2242, '照片删除', 2240, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:inspectionphoto:remove', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2250, '打卡记录', 2200, 5, 'checkin', 'zw/checkin/index', '', '', 1, 0, 'C', '0', '0', 'zw:checkin:list', 'location', 'admin', sysdate(), '打卡记录菜单');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2251, '打卡查询', 2250, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:checkin:query', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2252, '打卡新增', 2250, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:checkin:add', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2253, '打卡删除', 2250, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:checkin:remove', '#', 'admin', sysdate(), '');

-- =============================================
-- 一级目录4：日常工具
-- =============================================
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2300, '日常工具', 0, 4, 'zwTools', null, '', '', 1, 0, 'M', '0', '0', '', 'tool', 'admin', sysdate(), '日常工具目录');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2310, '故障管理', 2300, 1, 'faultRecord', 'zw/faultRecord/index', '', '', 1, 0, 'C', '0', '0', 'zw:faultrecord:list', 'message', 'admin', sysdate(), '故障记录菜单');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2311, '故障查询', 2310, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:faultrecord:query', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2312, '故障新增', 2310, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:faultrecord:add', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2313, '故障修改', 2310, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:faultrecord:edit', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2314, '故障删除', 2310, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:faultrecord:remove', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2320, '满意度评价', 2300, 2, 'satisfaction', 'zw/satisfaction/index', '', '', 1, 0, 'C', '0', '0', 'zw:satisfaction:list', 'star', 'admin', sysdate(), '满意度评价菜单');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2321, '评价查询', 2320, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:satisfaction:query', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2322, '评价删除', 2320, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:satisfaction:remove', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2330, '日报管理', 2300, 3, 'dailyReport', 'zw/dailyReport/index', '', '', 1, 0, 'C', '0', '0', 'zw:dailyreport:list', 'document', 'admin', sysdate(), '日报管理菜单');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2331, '日报查询', 2330, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:dailyreport:query', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2332, '日报新增', 2330, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:dailyreport:add', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2333, '日报删除', 2330, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:dailyreport:remove', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2334, '日报确认', 2330, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:dailyreport:confirm', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2340, '表单配置', 2300, 4, 'formConfig', 'zw/formConfig/index', '', '', 1, 0, 'C', '0', '0', 'zw:formconfig:list', 'build', 'admin', sysdate(), '表单配置菜单');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2341, '配置查询', 2340, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:formconfig:query', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2342, '配置新增', 2340, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:formconfig:add', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2343, '配置修改', 2340, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:formconfig:edit', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2344, '配置删除', 2340, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:formconfig:remove', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2350, '知识库', 2300, 5, 'knowledgeBase', 'zw/knowledgeBase/index', '', '', 1, 0, 'C', '0', '0', 'zw:knowledge:list', 'book', 'admin', sysdate(), '知识库菜单');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2351, '知识查询', 2350, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:knowledge:query', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2352, '知识新增', 2350, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:knowledge:add', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2353, '知识修改', 2350, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:knowledge:edit', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2354, '知识删除', 2350, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:knowledge:remove', '#', 'admin', sysdate(), '');
-- =============================================
-- 知识库子菜单：分类管理
-- =============================================
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2355, '分类管理', 2350, 1, 'kbCategory', 'zw/kbCategory/index', '', '', 1, 0, 'C', '0', '0', 'zw:kbCategory:list', 'list', 'admin', sysdate(), '知识库分类管理菜单')
on duplicate key update component = 'zw/kbCategory/index';
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2356, '分类查询', 2355, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:kbCategory:query', '#', 'admin', sysdate(), '')
on duplicate key update perms = 'zw:kbCategory:query';
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2357, '分类新增', 2355, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:kbCategory:add', '#', 'admin', sysdate(), '')
on duplicate key update perms = 'zw:kbCategory:add';
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2358, '分类修改', 2355, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:kbCategory:edit', '#', 'admin', sysdate(), '')
on duplicate key update perms = 'zw:kbCategory:edit';
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2359, '分类删除', 2355, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:kbCategory:remove', '#', 'admin', sysdate(), '')
on duplicate key update perms = 'zw:kbCategory:remove';

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2360, '客户联系人', 2300, 6, 'customerUser', 'zw/customerUser/index', '', '', 1, 0, 'C', '0', '0', 'zw:customeruser:list', 'peoples', 'admin', sysdate(), '客户联系人菜单');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2361, '联系人查询', 2360, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:customeruser:query', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2362, '联系人新增', 2360, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:customeruser:add', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2363, '联系人修改', 2360, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:customeruser:edit', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2364, '联系人删除', 2360, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:customeruser:remove', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2370, '通知管理', 2300, 7, 'notification', 'zw/notification/index', '', '', 1, 0, 'C', '0', '0', 'zw:notification:list', 'message', 'admin', sysdate(), '通知管理菜单');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2371, '通知查询', 2370, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:notification:query', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2372, '通知新增', 2370, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:notification:add', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2373, '通知修改', 2370, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:notification:edit', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2374, '通知删除', 2370, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:notification:remove', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2380, '报表导出', 2300, 8, 'reportExport', 'zw/reportExport/index', '', '', 1, 0, 'C', '0', '0', 'zw:report:list', 'excel', 'admin', sysdate(), '报表导出菜单');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2381, '报表查询', 2380, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:report:query', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2382, '报表导出', 2380, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:report:export', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2383, '报表删除', 2380, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:report:remove', '#', 'admin', sysdate(), '');

commit;

-- =============================================
-- 一级目录5：工程师管理
-- =============================================
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2400, '工程师管理', 0, 5, 'zwEngineer', null, '', '', 1, 0, 'M', '0', '0', '', 'user', 'admin', sysdate(), '工程师管理目录')
on duplicate key update component = null;
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values
(2410, '工程师管理', 2400, 1, 'engineer', 'zw/engineer/index', '', '', 1, 0, 'C', '0', '0', 'zw:engineer:list', 'user', 'admin', sysdate(), '工程师管理菜单')
on duplicate key update component = 'zw/engineer/index';
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2411, '工程师查询', 2410, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:engineer:list', '#', 'admin', sysdate(), '')
on duplicate key update perms = 'zw:engineer:list';
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2412, '工程师新增', 2410, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:engineer:add', '#', 'admin', sysdate(), '')
on duplicate key update perms = 'zw:engineer:add';
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2413, '工程师修改', 2410, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:engineer:edit', '#', 'admin', sysdate(), '')
on duplicate key update perms = 'zw:engineer:edit';
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2414, '工程师删除', 2410, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:engineer:remove', '#', 'admin', sysdate(), '')
on duplicate key update perms = 'zw:engineer:remove';
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2415, '工程师导出', 2410, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:engineer:export', '#', 'admin', sysdate(), '')
on duplicate key update perms = 'zw:engineer:export';
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (2416, '工单派发', 2410, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'zw:engineer:dispatch', '#', 'admin', sysdate(), '')
on duplicate key update perms = 'zw:engineer:dispatch';

-- =============================================
-- 工程师扩展表（工程师附加信息，独立于 sys_user）
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
    create_by         VARCHAR(64)    DEFAULT '' COMMENT '创建者',
    create_time       DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by         VARCHAR(64)    DEFAULT '' COMMENT '更新者',
    update_time       DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_employee_no (employee_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工程师扩展信息表';

commit;
