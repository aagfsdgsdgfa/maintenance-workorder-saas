# xd-tactical-platform 项目文档

> 个人维护项目文档 - 智慧运维平台基于 RuoYi-Vue

---

## 1. 项目概述

### 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 4.0.3 |
| Java 版本 | Java | 17+ |
| ORM | MyBatis | 4.0.1 |
| 数据库连接池 | Druid | 1.2.28 |
| 前端框架 | Vue 2 | 2.6.12 |
| UI 组件库 | Element UI | 2.15.14 |
| 状态管理 | Vuex | 3.6.0 |
| 路由 | Vue Router | 3.4.9 |
| HTTP 客户端 | Axios | 0.30.3 |
| 分页 | pagehelper | 2.1.1 |
| JSON处理 | fastjson2 | 2.0.61 |
| API文档 | Springdoc | 3.0.2 |

### 项目模块

```
RuoYi-Vue-master/
├── ruoyi-admin/        # Web入口 (WAR, Spring Boot启动类)
├── ruoyi-framework/    # 核心框架功能
├── ruoyi-system/       # 系统/业务逻辑模块 (主要开发目录)
├── ruoyi-quartz/       # 定时任务调度
├── ruoyi-generator/    # 代码生成器
├── ruoyi-common/       # 公共工具类
├── ruoyi-ui/          # Vue前端
└── sql/               # 数据库脚本
```

---

## 2. 后端架构 (com.ruoyi.zw)

### 2.1 目录结构

```
com.ruoyi.zw/
├── controller/         # REST API 控制器 (15个文件)
├── service/
│   ├── impl/          # 服务实现 (13个文件)
│   └── IZwXxxService.java  # 服务接口 (13个文件)
├── mapper/             # MyBatis Mapper接口 (15个文件)
├── domain/             # 实体类 (16个文件)
└── mock/              # Mock数据服务 (开发测试用)
```

### 2.2 领域实体

| 实体 | 表名 | 说明 |
|------|------|------|
| `ZwAsset` | `zw_asset` | 设备/资产 (服务器、网络设备等) |
| `ZwWorkOrder` | `zw_work_order` | 工单 (巡检、维修、交付、混合) |
| `ZwWorkOrderDetail` | (继承ZwWorkOrder) | 工单详情，含嵌套子记录 |
| `ZwInspectionPlan` | `zw_inspection_plan` | 巡检计划 (CRON表达式) |
| `ZwInspectionTask` | `zw_inspection_task` | 巡检任务实例 |
| `ZwInspectionPoint` | `zw_inspection_point` | 资产上的二维码巡检点 |
| `ZwInspectionResult` | `zw_inspection_result` | 巡检结果/检查项 |
| `ZwInspectionPhoto` | `zw_inspection_photo` | 巡检照片 (含GPS) |
| `ZwCheckIn` | `zw_check_in` | 工程师签到记录 |
| `ZwFaultRecord` | `zw_fault_record` | 故障记录 |
| `ZwRepairApply` | `zw_repair_apply` | 维修申请 (可转为工单) |
| `ZwCustomerUser` | `zw_customer_user` | 客户联系人 |
| `ZwNotification` | `zw_notification` | 系统通知 |
| `ZwSatisfaction` | `zw_satisfaction` | 客户满意度调查 |
| `ZwDailyReport` | `zw_daily_report` | 每日报告 (AI汇总) |
| `ZwReportExport` | `zw_report_export` | 导出任务跟踪 |
| `ZwKnowledgeBase` | `zw_knowledge_base` | 知识库 (品牌/类型技术文档) |

### 2.3 控制器列表

| 控制器 | 基础路径 | 关键端点 |
|--------|----------|----------|
| `ZwWorkOrderController` | `/zw/workOrder` | CRUD + `changeStatus`, `assign` |
| `ZwAssetController` | `/zw/asset` | CRUD + `scan/{qrCode}` |
| `ZwInspectionController` | `/zw/inspection` | 任务CRUD + `execute`, `complete` |
| `ZwInspectionPlanController` | `/zw/inspectionPlan` | 巡检计划CRUD |
| `ZwInspectionPointController` | `/zw/inspectionPoint` | 点位CRUD + `asset/{assetId}`, `toggle` |
| `ZwCheckInController` | `/zw/checkin` | 签到CRUD + `listByWorkOrder` |
| `ZwFaultRecordController` | `/zw/fault` | 故障CRUD + `toWorkOrder` |
| `ZwRepairApplyController` | `/zw/repairApply` | 维修申请CRUD + `accept`, `transfer` |
| `ZwCustomerUserController` | `/zw/customerUser` | 联系人CRUD |
| `ZwNotificationController` | `/zw/notification` | 通知CRUD + `markRead` |
| `ZwSatisfactionController` | `/zw/satisfaction` | 满意度CRUD |
| `ZwDailyReportController` | `/zw/dailyReport` | 日报CRUD + `autoGenerate` |
| `ZwReportExportController` | `/zw/reportExport` | 导出任务CRUD |
| `ZwKnowledgeBaseController` | `/zw/knowledgeBase` | 知识库CRUD + `suggest` |
| `ZwDashboardController` | `/zw/dashboard` | `summary`, `health`, `trend` |

### 2.4 标准CRUD签名

**Controller:**
```java
@GetMapping("/list")              TableDataInfo list(Domain obj)
@GetMapping("/{id}")             AjaxResult getInfo(@PathVariable Long id)
@PostMapping                    AjaxResult add(@RequestBody Domain obj)
@PutMapping                     AjaxResult edit(@RequestBody Domain obj)
@DeleteMapping("/{ids}")         AjaxResult remove(@PathVariable Long[] ids)
```

**Service Interface:**
```java
List<Domain> selectXxxList(Domain obj)
Domain selectXxxById(Long id)
int insertXxx(Domain obj)
int updateXxx(Domain obj)
int deleteXxxByIds(Long[] ids)
```

---

## 3. 前端架构 (ruoyi-ui)

### 3.1 目录结构

```
ruoyi-ui/src/
├── api/                    # API请求模块
│   ├── login.js            # 认证API
│   ├── menu.js             # 菜单API
│   ├── monitor/            # 系统监控 (cache, job, log, online, operlog, server)
│   ├── system/             # 系统管理 (config, dept, dict, menu, notice, post, role, user)
│   ├── tool/               # 开发工具 (gen)
│   └── zw/                 # 业务模块API
├── assets/                 # 静态资源
├── components/             # 公共组件
├── directive/             # 自定义指令
├── layout/                # 布局组件
├── plugins/               # 插件
├── router/                # 路由配置
├── store/                 # Vuex状态管理
│   └── modules/           # store模块 (app, dict, lock, permission, settings, tagsView, user)
├── utils/                 # 工具函数 (request.js是axios封装)
└── views/                 # 页面组件
    ├── error/             # 错误页 (404, 401)
    ├── login/             # 登录页
    └── zw/                # 业务模块页面
```

### 3.2 业务模块页面 (views/zw/)

| 模块 | 路径 | 说明 |
|------|------|------|
| asset | `zw/asset/index.vue` | 资产管理 |
| checkin | `zw/checkin/index.vue` | 签到记录 |
| customerUser | `zw/customerUser/index.vue` | 客户联系人 |
| dailyReport | `zw/dailyReport/index.vue` | 工程师日报 |
| dashboard | `zw/dashboard/index.vue` | 统计仪表盘 |
| faultRecord | `zw/faultRecord/index.vue` | 故障记录 |
| formConfig | `zw/formConfig/index.vue` | 表单配置 |
| inspection | `zw/inspection/index.vue` | 巡检任务 |
| inspectionPhoto | `zw/inspectionPhoto/index.vue` | 巡检照片 |
| inspectionPlan | `zw/inspectionPlan/index.vue` | 巡检计划 |
| inspectionPoint | `zw/inspectionPoint/index.vue` | 巡检点位 |
| knowledgeBase | `zw/knowledgeBase/index.vue` | 知识库 |
| notification | `zw/notification/index.vue` | 通知消息 |
| repairApply | `zw/repairApply/index.vue` | 维修申请 |
| reportExport | `zw/reportExport/index.vue` | 报告导出 |
| satisfaction | `zw/satisfaction/index.vue` | 满意度调查 |
| workOrder | `zw/workOrder/index.vue` + `detail.vue` | 工单管理 |

### 3.3 API层模式 (api/zw/)

```javascript
import request from '@/utils/request'

export function listXxx(query) {
  return request({
    url: '/zw/xxx/list',
    method: 'get',
    params: query
  })
}

export function getXxx(id) {
  return request({
    url: '/zw/xxx/' + id,
    method: 'get'
  })
}

export function addXxx(data) {
  return request({
    url: '/zw/xxx',
    method: 'post',
    data: data
  })
}

export function updateXxx(data) {
  return request({
    url: '/zw/xxx',
    method: 'put',
    data: data
  })
}

export function delXxx(ids) {
  return request({
    url: '/zw/xxx/' + ids,
    method: 'delete'
  })
}
```

---

## 4. 通用模式

### 4.1 分页模式

**后端:**
```java
@GetMapping("/list")
public TableDataInfo list(ZwWorkOrder workOrder) {
    startPage();                              // 设置分页线程变量
    List<ZwWorkOrder> list = service.select...();
    return getDataTable(list);               // 包装 list + total
}
```

**前端:**
```javascript
getList() {
  this.loading = true
  listXxx(this.queryParams).then(response => {
    this.dataList = response.rows
    this.total = response.total
    this.loading = false
  })
}
```

### 4.2 权限控制模式

**后端:**
```java
@PreAuthorize("@ss.hasPermi('zw:xxx:action')")
```

**前端 (指令):**
```vue
<el-button v-hasPermi="['zw:xxx:edit']">修改</el-button>
```

**前端 (路由):**
```javascript
meta: { permissions: ['zw:workorder:edit'] }
```

### 4.3 状态映射模式

```javascript
// data()
statusNameMap: {
  '0': '待接单',
  '1': '处理中',
  '2': '待确认',
  '3': '已完成',
  '4': '已取消'
},
statusTagType(status) {
  const map = {
    '0': 'info',
    '1': 'warning',
    '2': 'primary',
    '3': 'success',
    '4': 'info'
  }
  return map[status] || ''
}

// template
<el-tag :type="statusTagType(scope.row.status)">
  {{ statusNameMap[scope.row.status] }}
</el-tag>
```

### 4.4 审计字段

```java
// 插入时
workOrder.setCreateBy(SecurityUtils.getUsername());

// 更新时
workOrder.setUpdateBy(SecurityUtils.getUsername());
```

### 4.5 逻辑删除

- `delFlag = "0"` - 活跃
- `delFlag = "2"` - 已删除

删除操作使用 `update ... set del_flag = '2'` 而非物理删除。

---

## 5. 实体关系

```
Customer (客户)
├── ZwCustomerUser (1:N) -- 联系人
├── ZwAsset (1:N) -- 设备
│     ├── ZwInspectionPoint (1:N) -- 二维码点位
│     │     ├── ZwInspectionTask (1:N) -- 巡检任务
│     │     │     ├── ZwInspectionResult (1:1) -- 任务结果
│     │     │     └── ZwInspectionPhoto (1:N) -- 巡检照片
│     ├── ZwWorkOrder (1:N) -- 工单
│     │     ├── ZwInspectionTask (1:N)
│     │     ├── ZwCheckIn (1:N) -- 签到记录
│     │     ├── ZwFaultRecord (1:N) -- 故障记录
│     │     └── ZwSatisfaction (1:1) -- 满意度
│     ├── ZwRepairApply (1:N) -- 可转工单
│     └── ZwNotification (1:N) -- 通知
```

---

## 6. 状态机

### 工单状态
| 值 | 状态 | 说明 |
|----|------|------|
| 0 | 待接单 | 等待工程师接单 |
| 1 | 处理中 | 工程师已接单处理中 |
| 2 | 待确认 | 待客户确认完成 |
| 3 | 已完成 | 已完成 |
| 4 | 已取消 | 已取消 |

### 维修申请状态
| 值 | 状态 | 说明 |
|----|------|------|
| 0 | 待受理 | 等待受理 |
| 1 | 已受理 | 已受理 |
| 2 | 处理中 | 转工单后状态 |
| 3 | 已完成 | 已完成 |
| 4 | 已评价 | 已评价 |
| 5 | 已取消 | 已取消 |

### 巡检任务状态
| 值 | 状态 | 说明 |
|----|------|------|
| 0 | 待执行 | 等待执行 |
| 1 | 已到达 | 工程师已到达 |
| 2 | 进行中 | 巡检进行中 |
| 3 | 已完成 | 已完成 |

---

## 7. 命名规范

### 后端

| 元素 | 规范 | 示例 |
|------|------|------|
| 包名 | 小写 | `com.ruoyi.zw.service.impl` |
| 类名 | PascalCase, 前缀`Zw` | `ZwWorkOrder`, `ZwAssetController` |
| 接口名 | `I`前缀 + PascalCase | `IZwWorkOrderService` |
| Mapper名 | PascalCase + `Mapper` | `ZwWorkOrderMapper` |
| 表名 | snake_case, 前缀`zw_` | `zw_work_order` |
| 字段名 | camelCase | `workOrderId`, `assetCode` |
| 方法名 | `select`/`insert`/`update`/`delete` + 实体名 | `selectZwWorkOrderList` |
| 权限 | `zw:entity:action` | `zw:workorder:list`, `zw:asset:add` |

### 前端

| 元素 | 规范 | 示例 |
|------|------|------|
| 文件名 | kebab-case | `workOrder.js`, `inspectionPlan.vue` |
| 组件名 | PascalCase | `name: "ZwWorkOrder"` |
| API函数 | camelCase + 操作前缀 | `listWorkOrder`, `getWorkOrder`, `addWorkOrder` |
| 查询参数 | `queryParams` | `this.queryParams.pageNum` |
| 表单数据 | `form` | `this.form` |
| 列表数据 | `xxxList` 或 `dataList` | `this.dataList` |

---

## 8. 开发流程

### 新增功能步骤

1. **数据库** - 创建/修改 SQL 表
2. **后端 Domain** - 在 `domain/` 创建实体类
3. **后端 Mapper** - 在 `mapper/` 创建 Mapper 接口 + XML
4. **后端 Service** - 创建 Service 接口和实现
5. **后端 Controller** - 创建 REST Controller
6. **前端 API** - 在 `api/zw/` 创建 API 模块
7. **前端页面** - 在 `views/zw/` 创建页面组件

### 关键文件位置

```
后端:
- 实体类:     ruoyi-system/src/main/java/com/ruoyi/zw/domain/
- Mapper:     ruoyi-system/src/main/java/com/ruoyi/zw/mapper/
- Mapper XML: ruoyi-system/src/main/resources/mapper/zw/
- Service:    ruoyi-system/src/main/java/com/ruoyi/zw/service/
- Controller: ruoyi-system/src/main/java/com/ruoyi/zw/controller/

前端:
- API:        ruoyi-ui/src/api/zw/
- 页面:        ruoyi-ui/src/views/zw/
```

---

## 9. 配置信息

| 配置项 | 值 |
|--------|-----|
| 后端端口 | 8080 |
| Context Path | / |
| Redis | localhost:6379 |
| 文件上传路径 | D:/ruoyi/uploadPath |
| Token过期时间 | 30分钟 |
| 数据库 | MySQL |

---

## 10. 特殊功能

### Mock数据系统
- `MockDataService` 生成内存假数据
- `ZwMockController` 暴露 `/zw/mock/*` 端点
- 用于前端独立开发无需后端

### SLA跟踪
- `slaDeadline` 工单截止时间
- `slaWarningSent` 预警标志
- `isOverdue` 动态计算

### 地理位置
- 签到记录含经纬度
- 巡检点位含GPS坐标和扫描半径(默认50m)
- 巡检照片含GPS和水印

### 工单编号格式
`WOyyyyMMddNNNNNN` (日期 + 6位序号)

---

*文档版本: 2026-04-17*
