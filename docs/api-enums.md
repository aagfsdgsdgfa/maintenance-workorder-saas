# API 枚举值映射表

> 智维云服维保系统前后端业务枚举值契约。
> 后端枚举定义：`com.ruoyi.zw.enums.*`，所有枚举均带 `@JsonValue` 注解，JSON 序列化输出 `code` 字符串。
> 前端（PC `ruoyi-ui-customer` + 移动端 `maintenance-app`）展示 `label` 中文文案。

## 工单状态 (WorkOrderStatusEnum)

对应字段：`zw_work_order.status`

| code | label       | 说明                          |
|------|-------------|-----------------------------|
| 0    | 待接单       | 工单创建后未分配             |
| 1    | 处理中       | 工程师已接单/已到达现场       |
| 2    | 待确认       | 工程师申请完工，等待客户确认  |
| 3    | 已完成       | 客户已验收                   |
| 4    | 已取消       | 工单作废                     |

## 巡检任务状态 (InspectionStatusEnum)

对应字段：`zw_inspection_task.status`

| code | label       | 说明                          |
|------|-------------|-----------------------------|
| 0    | 待执行       | 巡检计划已派发未开始          |
| 1    | 执行中       | 工程师已出发/已到达           |
| 2    | 待确认       | 已提交结果，等待客户确认      |
| 3    | 已完成       | 客户已确认                   |
| 4    | 已取消       | 巡检任务作废                 |

## 工单类型 (OrderTypeEnum)

对应字段：`zw_work_order.order_type`

| code | label | 说明           |
|------|-------|--------------|
| 1    | 巡检  | 周期性巡检任务 |
| 2    | 维修  | 故障维修       |
| 3    | 送货  | 备件/设备送货  |
| 4    | 混合  | 巡检+维修组合  |

## 工单优先级 (PriorityEnum)

对应字段：`zw_work_order.priority`

| code | label | 说明             |
|------|-------|----------------|
| 1    | 低    | 不紧急           |
| 2    | 中    | 常规             |
| 3    | 高    | 优先处理         |
| 4    | 紧急  | SLA 紧急响应     |

## 资产状态 (AssetStatusEnum)

对应字段：`zw_asset.status`

| code | label | 说明             |
|------|-------|----------------|
| 0    | 正常  | 设备运行正常     |
| 1    | 故障  | 当前存在故障     |
| 2    | 维修中 | 工程师处理中     |
| 3    | 已报废 | 设备报废         |

## 知识库分类状态 (KbCategoryStatusEnum)

对应字段：`zw_kb_category.status`

| code | label | 说明             |
|------|-------|----------------|
| 0    | 正常  | 分类启用         |
| 1    | 停用  | 分类禁用         |

## 前后端契约要点

1. **后端 → 前端 JSON 序列化**：所有枚举字段直接输出 `code` 字符串（如 `"status": "1"`），不输出 label 文本。
2. **前端展示**：前端需通过本地字典或调用枚举接口自行映射 label；若需要后端返回 label，可调用 `*Service.convertXxxName()` 系列方法（旧风格，向后兼容）。
3. **入参校验**：前端提交枚举字段时，必须传 `code`（字符串），不可传 label 中文。
4. **前端字典缓存**：建议在 Pinia/Vuex store 中初始化枚举字典并缓存，避免每页重复请求。
5. **新增枚举**：在 `ruoyi-system/src/main/java/com/ruoyi/zw/enums/` 新建 enum 文件并补充本表。

## 验证脚本

```bash
# 验证后端枚举编译通过
mvn -pl ruoyi-system -am clean compile -DskipTests

# 验证无遗留魔法字符串
grep -rn '"[0-4]"' ruoyi-system/src/main/java/com/ruoyi/zw/ --include="*.java" \
  | grep -v "/enums/" \
  | grep -E '(switch|case "|return ")' \
  | grep -v "// "
```