-- =============================================
-- 智维云服 - 客户门户扩展表
-- 包含：维保合同、合同资产关联、续保商机
-- =============================================

-- ----------------------------
-- 1. 维保合同表
-- ----------------------------
drop table if exists maintenance_contracts;
create table maintenance_contracts (
  id                        bigint(20)      not null auto_increment    comment '合同ID',
  contract_no               varchar(50)     not null                   comment '合同编号',
  customer_id               bigint(20)      not null                   comment '客户ID',
  contract_name             varchar(200)    default ''                 comment '合同名称',
  start_date                date            not null                   comment '合同开始日期',
  end_date                  date            not null                   comment '合同结束日期',
  sla_response_hours        int(4)          default 4                   comment 'SLA响应时间(小时)',
  sla_arrival_hours         int(4)          default 8                   comment 'SLA到场时间(小时)',
  included_inspections      int(4)          default 2                   comment '含巡检次数/年',
  included_repairs          int(4)          default 4                   comment '含维修次数/年',
  free_parts_quota          decimal(10,2)   default 0                   comment '免费配件额度(元)',
  extra_visit_fee           decimal(10,2)   default null               comment '超次上门费(元/次)',
  discount_rate             decimal(5,2)    default null               comment '配件折扣率',
  attachment_url            varchar(500)    default null               comment '合同扫描件URL',
  status                    varchar(20)     default 'active'            comment '状态: active生效 expired过期 expiring即将到期',
  used_inspections          int(4)          default 0                   comment '已使用巡检次数',
  used_repairs              int(4)          default 0                   comment '已使用维修次数',
  used_parts_amount         decimal(10,2)   default 0                   comment '已使用配件金额',
  del_flag                  char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by                 varchar(64)     default ''                 comment '创建者',
  create_time               datetime                                    comment '创建时间',
  update_by                 varchar(64)     default ''                 comment '更新者',
  update_time               datetime                                    comment '更新时间',
  primary key (id),
  unique key uk_contract_no (contract_no),
  key idx_contract_customer (customer_id),
  key idx_contract_end_date (end_date),
  key idx_contract_status (status)
) engine=innodb auto_increment=1 comment = '维保合同表';

-- ----------------------------
-- 2. 合同资产关联表
-- ----------------------------
drop table if exists contract_asset_relations;
create table contract_asset_relations (
  id                bigint(20)  not null auto_increment    comment '主键ID',
  contract_id       bigint(20)  not null                   comment '合同ID',
  asset_id          bigint(20)  not null                   comment '资产ID',
  create_time       datetime                                comment '关联时间',
  primary key (id),
  unique key uk_contract_asset (contract_id, asset_id),
  key idx_relation_contract (contract_id),
  key idx_relation_asset (asset_id)
) engine=innodb auto_increment=1 comment = '合同资产关联表';

-- ----------------------------
-- 3. 续保商机表
-- ----------------------------
drop table if exists renewal_opportunities;
create table renewal_opportunities (
  id                  bigint(20)      not null auto_increment    comment '商机ID',
  customer_id         bigint(20)      not null                   comment '客户ID',
  asset_id            bigint(20)      default null               comment '资产ID(可选)',
  contract_id         bigint(20)      default null               comment '合同ID(可选)',
  opportunity_type    varchar(20)     not null                   comment '商机类型: warranty_expire质保到期 contract_expire合同到期',
  title               varchar(200)    default ''                 comment '商机标题',
  description         varchar(500)    default ''                 comment '商机描述',
  expire_date         date            default null               comment '到期日期',
  sales_user_id       bigint(20)      default null               comment '跟进销售ID',
  status              varchar(20)     default 'open'              comment '状态: open待跟进 following跟进中 converted已成交 lost已流失',
  follow_records      text            default null               comment '跟进记录(JSON数组)',
  quotation_url       varchar(500)    default null               comment '报价单URL',
  deal_amount         decimal(10,2)  default null               comment '成交金额',
  del_flag            char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_time         datetime                                    comment '创建时间',
  update_time         datetime                                    comment '更新时间',
  primary key (id),
  key idx_renewal_customer (customer_id),
  key idx_renewal_asset (asset_id),
  key idx_renewal_expire_date (expire_date),
  key idx_renewal_status (status)
) engine=innodb auto_increment=1 comment = '续保商机表';

-- ----------------------------
-- 4. 资产时间轴日志表(完善写入逻辑)
-- ----------------------------
-- zw_asset_logs 表已在 zw_business.sql 中创建，此处补充注释
-- operation_type 枚举: create创建 move移位 inspect巡检 repair维修 warranty_renew质保续期 contract_renew合同续期 scrap报废

-- ----------------------------
-- 5. 定时任务注册（sys_job 表）
-- ----------------------------
-- SLA超时预警：每10分钟检查一次即将超时的工单
insert into sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, status, create_by, create_time, remark)
values ('SLA超时预警', 'DEFAULT', 'zwSlaCheckTask.checkSlaWarning', '0 */10 * * * ?', '3', '1', 'admin', sysdate(), '检查SLA即将超时的工单并发送预警通知');

-- 续保商机生成：每天凌晨1点检查质保/合同到期资产并生成商机
insert into sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, status, create_by, create_time, remark)
values ('续保商机自动生成', 'DEFAULT', 'zwRenewalOpportunityTask.generateRenewalOpportunities', '0 0 1 * * ?', '3', '1', 'admin', sysdate(), '质保90天内到期、合同60天内到期的资产自动生成商机');
