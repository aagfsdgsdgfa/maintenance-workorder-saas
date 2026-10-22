-- =============================================
-- 智维云服系统 - 业务数据表
-- 基于 RuoYi-Vue 框架扩展
-- 表前缀: zw_ (智维拼音首字母)
-- =============================================

-- ----------------------------
-- 1. 资产设备表
-- ----------------------------
drop table if exists zw_asset;
create table zw_asset (
  asset_id           bigint(20)      not null auto_increment    comment '资产ID',
  asset_code         varchar(64)     not null                   comment '资产编码/SN',
  asset_name         varchar(200)    not null                   comment '资产名称',
  asset_type         varchar(32)     default ''                 comment '类型: device软件系统 consumable耗材 other其它',
  brand              varchar(64)     default ''                 comment '品牌',
  model              varchar(128)    default ''                 comment '型号',
  customer_id        bigint(20)     default null               comment '归属客户ID',
  is_third_party     char(1)         default '0'                comment '是否第三方代保: 0自售 1代保',
  purchase_date      date            default null               comment '购买日期',
  warranty_end_date  date            default null               comment '保修截止日期',
  location           varchar(255)    default ''                 comment '安装位置',
  qr_code            varchar(255)    default ''                 comment '二维码/NFC标签编号',
  status             char(1)         default '0'                comment '状态: 0正常 1故障 2维修中 3已报废',
  health_score       int(3)          default 100                comment '健康度评分(0-100)',
  inspection_config  json            default null               comment '动态巡检指标配置JSON',
  remark             varchar(500)   default null               comment '备注',
  del_flag           char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  primary key (asset_id),
  unique key uk_asset_code (asset_code),
  key idx_asset_customer (customer_id),
  key idx_asset_status (status),
  key idx_asset_qrcode (qr_code),
  key idx_asset_type (asset_type)
) engine=innodb auto_increment=1 comment = '资产设备表';

-- ----------------------------
-- 2. 工单主表
-- ----------------------------
drop table if exists zw_work_order;
create table zw_work_order (
  work_order_id      bigint(20)      not null auto_increment    comment '工单ID',
  order_no           varchar(32)     not null                   comment '工单编号',
  customer_id        bigint(20)      not null                   comment '客户ID',
  asset_id           bigint(20)      default null               comment '关联资产ID',
  order_type         char(1)         default '1'                comment '工单类型: 1巡检 2维修 3送货 4混合',
  title              varchar(200)    not null                   comment '工单标题',
  description        text                                       comment '工单描述',
  priority           char(1)         default '2'                comment '优先级: 1低 2中 3高 4紧急',
  assigned_to        bigint(20)      default null               comment '分配给工程师',
  assigned_by        bigint(20)      default null               comment '派单人',
  status             char(1)         default '0'                comment '状态: 0待接单 1处理中 2待确认 3已完成 4已取消',
  sla_deadline       datetime        default null               comment 'SLA截止时间',
  sla_warning_sent   char(1)         default 'N'               comment '是否已发超时预警: Y N',
  contact_name       varchar(64)     default ''                 comment '客户联系人',
  contact_phone      varchar(20)     default ''                 comment '联系人电话',
  contact_address    varchar(255)    default ''                 comment '上门地址',
  complete_time      datetime        default null               comment '完成时间',
  sign_image         varchar(500)    default ''                 comment '甲方签名图片URL',
  del_flag           char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  primary key (work_order_id),
  unique key uk_order_no (order_no),
  key idx_wo_customer (customer_id),
  key idx_wo_asset (asset_id),
  key idx_wo_assignee (assigned_to),
  key idx_wo_status (status),
  key idx_wo_sla (sla_deadline),
  key idx_wo_type (order_type),
  key idx_wo_priority (priority)
) engine=innodb auto_increment=1 comment = '工单主表';

-- ----------------------------
-- 3. 巡检任务表
-- ----------------------------
drop table if exists zw_inspection_task;
create table zw_inspection_task (
  task_id            bigint(20)      not null auto_increment    comment '任务ID',
  work_order_id      bigint(20)      not null                   comment '关联工单ID',
  asset_id           bigint(20)      not null                   comment '资产ID',
  task_type          varchar(32)     default ''                 comment '任务类型',
  plan_time          datetime        default null               comment '计划执行时间',
  actual_start_time  datetime        default null               comment '实际开始时间',
  actual_end_time    datetime        default null               comment '实际结束时间',
  check_in_location  varchar(255)    default ''                 comment '打卡位置JSON: {lat,lng}',
  check_in_photo     varchar(500)    default ''                 comment '打卡照片URL',
  check_in_address   varchar(255)    default ''                 comment '打卡地址文字',
  status             char(1)         default '0'                comment '状态: 0待执行 1已到达 2进行中 3已完成',
  del_flag           char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  primary key (task_id),
  key idx_task_wo (work_order_id),
  key idx_task_asset (asset_id),
  key idx_task_status (status),
  key idx_task_plan (plan_time)
) engine=innodb auto_increment=1 comment = '巡检任务表';

-- ----------------------------
-- 4. 设备检查项结果表
-- ----------------------------
drop table if exists zw_inspection_result;
create table zw_inspection_result (
  result_id          bigint(20)      not null auto_increment    comment '结果ID',
  task_id            bigint(20)      not null                   comment '关联巡检任务ID',
  asset_id           bigint(20)      not null                   comment '资产ID',
  check_items        json            default null               comment '检查项结果JSON数组',
  photo_urls         json            default null               comment '现场照片URL数组',
  voice_text         text                                       comment 'AI语音转写文本',
  remark             varchar(500)    default null               comment '备注',
  del_flag           char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  primary key (result_id),
  key idx_ir_task (task_id),
  key idx_ir_asset (asset_id)
) engine=innodb auto_increment=1 comment = '设备检查项结果表';

-- ----------------------------
-- 5. 打卡记录表
-- ----------------------------
drop table if exists zw_check_in;
create table zw_check_in (
  check_id           bigint(20)      not null auto_increment    comment '打卡ID',
  work_order_id      bigint(20)      not null                   comment '工单ID',
  engineer_id        bigint(20)      not null                   comment '工程师ID',
  check_type         char(1)         not null                   comment '打卡类型: 1到达打卡 2离开打卡',
  latitude           decimal(10,7)   default null               comment '纬度',
  longitude          decimal(10,7)   default null               comment '经度',
  address            varchar(255)    default ''                 comment '地址',
  photo_url          varchar(500)    default ''                 comment '照片URL含水印',
  check_time         datetime        not null                   comment '打卡时间',
  del_flag           char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  primary key (check_id),
  key idx_ci_wo (work_order_id),
  key idx_ci_engineer (engineer_id),
  key idx_ci_time (check_time)
) engine=innodb auto_increment=1 comment = '打卡记录表';

-- ----------------------------
-- 6. 满意度评价表
-- ----------------------------
drop table if exists zw_satisfaction;
create table zw_satisfaction (
  eval_id            bigint(20)      not null auto_increment    comment '评价ID',
  work_order_id      bigint(20)      not null                   comment '工单ID',
  customer_id        bigint(20)      not null                   comment '客户ID',
  response_score     int(1)          default 5                  comment '响应速度评分1-5',
  tech_score         int(1)          default 5                  comment '技术水平评分1-5',
  overall_score      int(1)          default 5                  comment '综合评分1-5',
  comment            varchar(500)    default null               comment '评价内容',
  eval_time          datetime        not null                   comment '评价时间',
  del_flag           char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  primary key (eval_id),
  unique key uk_wo_eval (work_order_id),
  key idx_sat_customer (customer_id)
) engine=innodb auto_increment=1 comment = '满意度评价表';

-- ----------------------------
-- 7. AI日报表
-- ----------------------------
drop table if exists zw_daily_report;
create table zw_daily_report (
  report_id          bigint(20)      not null auto_increment    comment '日报ID',
  report_date        date            not null                   comment '报告日期',
  engineer_id        bigint(20)      not null                   comment '工程师ID',
  report_title       varchar(200)    default ''                 comment '报告标题',
  report_content     text                                       comment '报告内容AI生成',
  work_order_count   int(4)          default 0                  comment '完成工单数',
  inspection_count   int(4)          default 0                  comment '巡检设备数',
  voice_summary      text                                       comment '语音记录汇总',
  status             char(1)         default '0'                comment '状态: 0草稿 1已生成 2已确认',
  del_flag           char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  primary key (report_id),
  unique key uk_report_date_engineer (report_date, engineer_id),
  key idx_dr_engineer (engineer_id),
  key idx_dr_status (status)
) engine=innodb auto_increment=1 comment = 'AI日报表';

-- ----------------------------
-- 8. 动态表单配置表
-- ----------------------------
drop table if exists zw_form_config;
create table zw_form_config (
  config_id          bigint(20)      not null auto_increment    comment '配置ID',
  config_name        varchar(100)    not null                   comment '配置名称',
  asset_type         varchar(32)     default ''                 comment '资产类型',
  brand              varchar(64)     default ''                 comment '品牌',
  form_schema        json            default null               comment '表单Schema JSON',
  status             char(1)         default '0'                comment '状态: 0正常 1停用',
  del_flag           char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (config_id),
  key idx_fc_type (asset_type),
  key idx_fc_brand (brand)
) engine=innodb auto_increment=1 comment = '动态表单配置表';

-- ----------------------------
-- 9. 知识库表
-- ----------------------------
drop table if exists zw_knowledge_base;
create table zw_knowledge_base (
  kb_id              bigint(20)      not null auto_increment    comment '知识ID',
  kb_title           varchar(200)    not null                   comment '标题',
  kb_type            char(1)         default '1'                comment '类型: 1维修手册 2错误代码 3FAQ 4其他',
  brand              varchar(64)     default ''                 comment '适用品牌',
  asset_type         varchar(32)     default ''                 comment '适用资产类型',
  keywords           varchar(500)    default ''                 comment '关键词逗号分隔',
  content            text                                       comment '知识内容',
  attachments        json            default null               comment '附件URL数组',
  status             char(1)         default '0'                comment '状态: 0正常 1停用',
  del_flag           char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  primary key (kb_id),
  key idx_kb_type (kb_type),
  key idx_kb_brand (brand),
  key idx_kb_asset_type (asset_type)
) engine=innodb auto_increment=1 comment = '知识库表';

-- ----------------------------
-- 10. 客户资产关系表
-- ----------------------------
drop table if exists zw_customer_asset;
create table zw_customer_asset (
  id                 bigint(20)      not null auto_increment    comment '主键',
  customer_id        bigint(20)      not null                   comment '客户ID',
  asset_id           bigint(20)      not null                   comment '资产ID',
  relation_type      varchar(32)     default 'owned'            comment '关系类型: owned/maintained/warranty',
  start_date         date            default null               comment '开始日期',
  end_date           date            default null               comment '结束日期',
  del_flag           char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  primary key (id),
  unique key uk_customer_asset (customer_id, asset_id, relation_type),
  key idx_ca_asset (asset_id)
) engine=innodb auto_increment=1 comment = '客户资产关系表';

-- ----------------------------
-- 11. 工单消息通知表
-- ----------------------------
drop table if exists zw_notification;
create table zw_notification (
  notif_id           bigint(20)      not null auto_increment    comment '通知ID',
  user_id            bigint(20)      not null                   comment '接收用户',
  title              varchar(200)   not null                   comment '标题',
  content            text                                       comment '内容',
  type               char(1)         default '4'                comment '类型: 1工单下发 2超时预警 3报修通知 4系统消息',
  related_id         bigint(20)      default null               comment '关联ID工单ID等',
  is_read            char(1)         default '0'                comment '是否已读: 0未读 1已读',
  read_time          datetime        default null               comment '阅读时间',
  del_flag           char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  primary key (notif_id),
  key idx_notif_user (user_id),
  key idx_notif_read (is_read),
  key idx_notif_type (type),
  key idx_notif_time (create_time)
) engine=innodb auto_increment=1 comment = '工单消息通知表';

-- ----------------------------
-- 12. 巡检计划表
-- ----------------------------
drop table if exists zw_inspection_plan;
create table zw_inspection_plan (
  plan_id            bigint(20)      not null auto_increment    comment '计划ID',
  plan_name          varchar(200)    not null                   comment '计划名称',
  customer_id        bigint(20)      not null                   comment '客户ID',
  asset_ids          json            default null               comment '资产ID数组',
  frequency_type     char(1)         default '1'                comment '周期类型: 1每日 2每周 3每月 4自定义',
  frequency_cron     varchar(64)     default ''                 comment 'Cron表达式',
  assigned_to        bigint(20)      default null               comment '负责工程师',
  status             char(1)         default '0'                comment '状态: 0启用 1停用',
  del_flag           char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (plan_id),
  key idx_ip_customer (customer_id),
  key idx_ip_assignee (assigned_to),
  key idx_ip_status (status)
) engine=innodb auto_increment=1 comment = '巡检计划表';

-- ----------------------------
-- 13. 客户用户表（甲方联系人）
-- ----------------------------
drop table if exists zw_customer_user;
create table zw_customer_user (
  cust_user_id       bigint(20)      not null auto_increment    comment '客户用户ID',
  customer_id        bigint(20)      not null                   comment '所属客户',
  user_name          varchar(64)     not null                   comment '姓名',
  phone              varchar(20)     default ''                 comment '手机号',
  email              varchar(100)    default ''                 comment '邮箱',
  position           varchar(64)     default ''                 comment '职位',
  is_primary         char(1)         default 'N'                comment '是否主联系人: Y N',
  del_flag           char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  primary key (cust_user_id),
  key idx_cu_customer (customer_id),
  key idx_cu_phone (phone)
) engine=innodb auto_increment=1 comment = '客户用户表';

-- ----------------------------
-- 14. 设备故障记录表
-- ----------------------------
drop table if exists zw_fault_record;
create table zw_fault_record (
  fault_id           bigint(20)      not null auto_increment    comment '故障ID',
  asset_id           bigint(20)      not null                   comment '资产ID',
  work_order_id      bigint(20)      default null               comment '关联工单ID',
  fault_desc         text                                       comment '故障描述',
  fault_type         varchar(64)     default ''                 comment '故障类型',
  fault_level        char(1)         default '2'                comment '级别: 1轻微 2一般 3严重 4紧急',
  fault_status       char(1)         default '0'                comment '状态: 0已上报 1处理中 2已解决 3已验收',
  fault_time         datetime        not null                   comment '故障发生时间',
  resolve_time       datetime        default null               comment '解决时间',
  resolution         text                                       comment '解决方案',
  del_flag           char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  primary key (fault_id),
  key idx_fr_asset (asset_id),
  key idx_fr_wo (work_order_id),
  key idx_fr_status (fault_status),
  key idx_fr_level (fault_level)
) engine=innodb auto_increment=1 comment = '设备故障记录表';

-- ----------------------------
-- 15. 巡检照片表
-- ----------------------------
drop table if exists zw_inspection_photo;
create table zw_inspection_photo (
  photo_id           bigint(20)      not null auto_increment    comment '照片ID',
  task_id            bigint(20)      default null               comment '巡检任务ID',
  work_order_id      bigint(20)      default null               comment '工单ID',
  photo_type         char(1)         default '1'                comment '类型: 1现场 2设备 3故障 4签收',
  photo_url          varchar(500)    not null                   comment '照片URL',
  latitude           decimal(10,7)   default null               comment '纬度',
  longitude          decimal(10,7)   default null               comment '经度',
  shoot_time         datetime        not null                   comment '拍摄时间',
  watermark_info     varchar(255)    default ''                 comment '水印信息',
  del_flag           char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  primary key (photo_id),
  key idx_ip_task (task_id),
  key idx_ip_wo (work_order_id),
  key idx_ip_type (photo_type),
  key idx_ip_shoot (shoot_time)
) engine=innodb auto_increment=1 comment = '巡检照片表';

-- ----------------------------
-- 16. 报表导出记录表
-- ----------------------------
drop table if exists zw_report_export;
create table zw_report_export (
  export_id          bigint(20)      not null auto_increment    comment '导出ID',
  customer_id        bigint(20)      default null               comment '客户ID',
  report_type        char(1)         default '1'                comment '类型: 1巡检报告 2维保报告 3资产报告',
  start_date         date            default null               comment '开始日期',
  end_date           date            default null               comment '结束日期',
  file_name          varchar(200)    default ''                 comment '文件名',
  file_path          varchar(500)    default ''                 comment '文件路径',
  file_format        varchar(16)     default 'PDF'              comment '格式: PDF Word Excel',
  export_status      char(1)         default '0'                comment '状态: 0生成中 1成功 2失败',
  download_count     int(4)          default 0                  comment '下载次数',
  del_flag           char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  primary key (export_id),
  key idx_re_customer (customer_id),
  key idx_re_type (report_type),
  key idx_re_status (export_status)
) engine=innodb auto_increment=1 comment = '报表导出记录表';

-- ----------------------------
-- 17. 客户报修申请表（客户门户端）
-- ----------------------------
drop table if exists zw_repair_apply;
create table zw_repair_apply (
  apply_id           bigint(20)      not null auto_increment    comment '报修ID',
  customer_id        bigint(20)      not null                   comment '客户ID',
  asset_id           bigint(20)      default null               comment '资产ID',
  apply_title        varchar(200)    not null                   comment '报修标题',
  fault_desc         text                                       comment '故障描述',
  fault_photos       json            default null               comment '故障图片数组',
  fault_videos       json            default null               comment '故障视频数组',
  contact_name       varchar(64)     default ''                 comment '联系人',
  contact_phone      varchar(20)     default ''                 comment '联系电话',
  expected_time      datetime        default null               comment '期望上门时间',
  status             char(1)         default '0'                comment '状态: 0待受理 1已受理 2处理中 3已完成 4已评价 5已取消',
  work_order_id      bigint(20)      default null               comment '关联工单ID',
  del_flag           char(1)         default '0'                comment '删除标志: 0存在 2删除',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  primary key (apply_id),
  key idx_ra_customer (customer_id),
  key idx_ra_asset (asset_id),
  key idx_ra_status (status),
  key idx_ra_wo (work_order_id)
) engine=innodb auto_increment=1 comment = '客户报修申请表';

-- ----------------------------
-- 字典数据初始化 - 智维云服业务字典
-- ----------------------------
-- 注意: 需要在 sys_dict_type 中先添加字典类型,然后再插入数据
-- 建议添加以下字典类型:
-- zw_asset_type    - 资产类型
-- zw_order_type    - 工单类型
-- zw_order_status  - 工单状态
-- zw_priority      - 优先级
-- zw_fault_level   - 故障级别
-- zw_kb_type       - 知识库类型
-- zw_report_type   - 报表类型

-- ----------------------------
-- 初始化示例数据
-- ----------------------------

-- 插入示例客户（作为部门插入到 sys_dept）
-- 假设在 sys_dept 中客户公司的 dept_id 从 1000 开始
-- insert into sys_dept values(1000, 0, '0', '示例客户公司', 1, '张经理', '13800138000', 'customer@example.com', '0', '0', 'admin', sysdate(), '', null);

-- 插入示例资产
-- insert into zw_asset values(1, 'SN-DELL-001', 'Dell PowerEdge R740 服务器', 'device', 'Dell', 'PowerEdge R740', 1000, '0', '2023-01-15', '2026-01-15', '机房A-机柜12', 'QR-DELL-001', '0', 95, null, '核心生产服务器', '0', 'admin', sysdate(), '', null);

-- 插入示例工单
-- insert into zw_work_order values(1, 'WO-20260415-0001', 1000, 1, '1', '月度定期巡检', '对Dell服务器进行例行巡检', '2', 2, 1, '0', date_add(now(), interval 24 hour), 'N', '张经理', '13800138000', '北京市朝阳区XX大厦', null, '', '0', 'admin', sysdate(), '', null);

commit;
