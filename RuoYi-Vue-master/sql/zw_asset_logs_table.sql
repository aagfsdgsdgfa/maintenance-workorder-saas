-- =============================================
-- zw_asset_logs 资产时间轴日志表
-- 如果该表已在 zw_business.sql 中创建，则本文件可跳过
-- =============================================
drop table if exists zw_asset_logs;
create table zw_asset_logs (
  log_id          bigint(20)      not null auto_increment    comment '日志ID',
  asset_id        bigint(20)      not null                   comment '资产ID',
  work_order_id   bigint(20)      default null               comment '关联工单ID',
  operation_type  varchar(32)     default ''                 comment '操作类型',
  description     varchar(500)    default ''                 comment '操作描述',
  operator_id     bigint(20)      default null               comment '操作人ID',
  operator_name   varchar(64)     default ''                 comment '操作人名称',
  operate_time    datetime        default null               comment '操作时间',
  extra_data      json            default null               comment '附加数据',
  create_time     datetime        default null               comment '创建时间',
  primary key (log_id),
  key idx_log_asset (asset_id),
  key idx_log_wo (work_order_id)
) engine=innodb auto_increment=1 comment = '资产时间轴日志表';
