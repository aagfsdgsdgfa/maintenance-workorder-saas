-- =============================================
-- 智维云服系统 - 新增表SQL
-- zw_inspection_point 表（巡检点）
-- =============================================

drop table if exists zw_inspection_point;
create table zw_inspection_point (
  point_id          bigint(20)      not null auto_increment    comment '巡检点ID',
  asset_id          bigint(20)      not null                   comment '所属资产ID',
  point_name        varchar(100)    not null                   comment '巡检点名称',
  point_code        varchar(64)     not null                   comment '巡检点编码/二维码',
  point_location    varchar(255)   default ''                 comment '巡检点位置描述',
  latitude          decimal(10,7)  default null               comment '纬度',
  longitude         decimal(10,7)  default null               comment '经度',
  scan_radius       int(6)          default 50                 comment '扫码有效半径(米)',
  form_config_id    bigint(20)      default null               comment '关联表单配置ID',
  check_items       json            default null               comment '巡检检查项覆盖配置',
  status            char(1)         default '0'                comment '状态: 0正常 1停用',
  del_flag          char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                 comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                 comment '更新时间',
  primary key (point_id),
  unique key uk_point_code (point_code),
  key idx_ip_asset (asset_id),
  key idx_ip_status (status)
) engine=innodb auto_increment=1 comment = '巡检点表';

-- 初始化示例巡检点
-- insert into zw_inspection_point values(1, 1, '机房门禁巡检点', 'IP-001', '机房门口', 39.9042, 116.4074, 50, null, null, '0', '0', 'admin', now(), '', null);
