# 工单Word导出 测试用例

## 功能概述

- **测试模块**: 工单管理 - Word导出
- **后端技术栈**: Java Spring Boot + Apache POI (XWPF)
- **前端技术栈**: Vue.js Element UI
- **测试范围**: 导出接口调用、Word模板填充、文件生成、字段格式校验
- **测试依据**: `ZwWorkOrderExportController.java`, `ZwWorkOrderExportServiceImpl.java`

---

## 测试数据前提条件

### 必要的数据库数据

| 数据项 | 说明 |
|--------|------|
| `zw_work_order` 表记录 | 存在已完成的工单（status='3'），ID 可为 1/101 等测试ID |
| `sys_user` 表记录 | 存在工程师用户，userId=1，nickName='张工程师' |
| 模板文件 | `resources/static/template/维保工单模板.docx` 存在于 classpath |

### 模板占位符要求

模板中应包含以下占位符（`${fieldName}` 格式）：

```
${orderNo}          - 工单编号
${createTime}       - 创建时间（yyyy年MM月dd日）
${customerName}     - 客户名称
${contactName}      - 联系人
${contactPhone}     - 联系电话
${address}          - 地址
${completeTime}     - 完成时间
${title}            - 工单标题
${description}      - 工单描述
${assetName}        - 资产名称
${serialNumber}     - 资产序列号
${orderType}        - 工单类型（1=巡检 2=维修 3=送货 4=混合）
${priority}         - 优先级（1=低 2=中 3=高 4=紧急）
${status}          - 工单状态（0=待接单 1=处理中 2=待确认 3=已完成 4=已取消）
${engineerName}     - 工程师姓名（通过 assignedTo 查询 sys_user）
${slaDeadline}     - SLA截止时间
```

---

## 接口测试

### 接口: GET /zw/workOrder/exportWord/{id}

| 用例ID | 用例名称 | 优先级 | 前提条件 | 请求 | 预期结果 |
|--------|---------|--------|---------|------|---------|
| WO-EXP-001 | 正常导出工单Word | P0 | 工单ID=1存在，模板存在 | GET `/zw/workOrder/exportWord/1` | HTTP 200，Content-Type=`application/vnd.openxmlformats-officedocument.wordprocessingml.document`，响应体为 `.docx` 二进制流，Content-Disposition 包含 `filename=工单_*.docx` |
| WO-EXP-002 | 工单不存在返回错误 | P0 | 工单ID=999不存在 | GET `/zw/workOrder/exportWord/999` | Service层返回 null（文件不存在），Controller返回 JSON `{"code":500,"msg":"Word模板文件未找到..."}` |
| WO-EXP-003 | 模板文件不存在时返回错误 | P0 | 模板文件从 classpath 和文件系统均不存在 | GET `/zw/workOrder/exportWord/1` | 返回 JSON `{"code":500,"msg":"Word模板文件未找到..."}` |
| WO-EXP-004 | 导出文件名包含工单编号 | P1 | 工单orderNo='WO-2024-001'，模板存在 | GET `/zw/workOrder/exportWord/1` | Content-Disposition filename 包含 `工单_WO-2024-001_` |
| WO-EXP-005 | 导出文件格式为docx | P1 | 正常工单 | GET `/zw/workOrder/exportWord/1` | Content-Type 为 `application/vnd.openxmlformats-officedocument.wordprocessingml.document` |
| WO-EXP-006 | 文件生成失败（磁盘满/权限） | P2 | uploadPath指向不可写目录 | GET `/zw/workOrder/exportWord/1` | 返回 JSON `{"code":404,"msg":"文件生成失败"}` |
| WO-EXP-007 | 未授权用户禁止导出 | P1 | 当前用户无 `zw:workOrder:export` 权限 | GET `/zw/workOrder/exportWord/1` | HTTP 403 Forbidden |

---

## 字段填充测试

> 前提：工单数据完整，模板占位符存在

| 用例ID | 用例名称 | 优先级 | 输入数据 | 预期结果 |
|--------|---------|--------|---------|---------|
| WO-FLD-001 | 客户信息字段正确填充 | P1 | customerName='华润电力'，contactName='李经理'，contactPhone='13800138000'，contactAddress='北京市朝阳区' | 模板中 `${customerName}` → '华润电力'，`${contactName}` → '李经理'，`${contactPhone}` → '13800138000'，`${address}` → '北京市朝阳区' |
| WO-FLD-002 | 工程师姓名通过assignedTo查询 | P1 | assignedTo=1（对应 sys_user nickName='张工程师'） | 模板中 `${engineerName}` → '张工程师' |
| WO-FLD-003 | 日期格式正确（yyyy年MM月dd日） | P1 | createTime=2024-03-15 | 模板中 `${createTime}` → '2024年03月15日' |
| WO-FLD-004 | 工单类型数字转中文 | P1 | orderType='1'（巡检） | 模板中 `${orderType}` → '巡检'；orderType='2' → '维修'；orderType='3' → '送货'；orderType='4' → '混合' |
| WO-FLD-005 | 优先级数字转中文 | P1 | priority='3'（高） | 模板中 `${priority}` → '高' |
| WO-FLD-006 | 工单状态数字转中文 | P1 | status='3'（已完成） | 模板中 `${status}` → '已完成' |
| WO-FLD-007 | 资产名称填充 | P1 | assetName='Dell服务器R740' | 模板中 `${assetName}` → 'Dell服务器R740' |
| WO-FLD-008 | 工单编号填充 | P1 | orderNo='WO-20240315-001' | 模板中 `${orderNo}` → 'WO-20240315-001' |
| WO-FLD-009 | 模板段落中占位符替换 | P1 | 模板段落中含 `客户名称：${customerName}` | 段落文本替换为 `客户名称：华润电力`，保留原格式（加粗/颜色等） |
| WO-FLD-010 | 模板表格中占位符替换 | P1 | 表格单元格中含 `${contactPhone}` | 单元格文本替换为 `13800138000` |

---

## 边界测试

| 用例ID | 用例名称 | 优先级 | 场景 | 预期结果 |
|--------|---------|--------|------|---------|
| WO-BND-001 | 工单无关联资产（assetName为空） | P1 | 工单 assetName=null | `buildFieldMap` 中 `nullToStr(wo.getAssetName(), "-")` 返回 '-'，模板显示 '-' |
| WO-BND-002 | 工单无工程师分配（assignedTo为空） | P1 | 工单 assignedTo=null | `lookupEngineerName(null)` → 返回 '-'，模板显示 '-' |
| WO-BND-003 | 工单无客户名称 | P2 | 工单 customerName=null | `nullToStr(wo.getCustomerName(), "客户")` 返回 '客户' |
| WO-BND-004 | 日期字段为null | P2 | completeTime=null | `formatDate(null)` → 返回 '-' |
| WO-BND-005 | 工单无标题 | P2 | 工单 title=null | `nullToStr(wo.getTitle())` → 返回 ''（空字符串） |
| WO-BND-006 | 工程师用户不存在于sys_user | P1 | assignedTo=999（对应sys_user无记录） | `lookupEngineerName(999)` 捕获异常，返回 '-' |
| WO-BND-007 | 工单编号特殊字符（中文/空格） | P2 | orderNo='维保 2024/03 工单-001' | 文件名中特殊字符正常处理，不出现路径注入 |
| WO-BND-008 | 工单无描述内容 | P2 | description=null | `nullToStr(wo.getDescription())` → 返回 '' |
| WO-BND-009 | 表格中多个占位符同一单元格 | P1 | 单元格文本=`${orderNo} - ${customerName}` | 正确替换两个占位符 |
| WO-BND-010 | 并发导出同一工单 | P2 | 两个请求同时导出ID=1 | 均生成独立文件（文件名含时间戳），不相互覆盖 |

---

## 补充说明

### 缺失字段现状

根据 `ZwWorkOrderExportServiceImpl.buildFieldMap()` 分析，以下字段目前硬编码为空字符串，**暂不测试填充逻辑**（后续扩展时启用）：

| 字段 | 当前值 | 说明 |
|------|--------|------|
| `visitFee`（上门费） | ""（空） | SQL已有字段，待实现 |
| `serviceFee`（工时费） | ""（空） | SQL已有字段，待实现 |
| `totalAmount`（合计金额） | ""（空） | SQL已有字段，待实现 |
| `chargeStatus`（是否收费） | 未填充 | 若为"是"应显示☑，待实现 |
| `customerSignUrl` 签字图片 | ""（空） | 待实现图片插入逻辑 |
| `engineerSignUrl` 签字图片 | ""（空） | 待实现图片插入逻辑 |
| `faultReason`（故障原因） | ""（空） | SQL已有字段，待实现 |

### 签字图片说明

当前 `buildFieldMap` 中 `signImage` 字段硬编码为空字符串。后续扩展图片插入功能后，需新增测试用例：

- `customerSignUrl` 有值时：图片正确插入Word
- `customerSignUrl` 为空时：占位符位置留空
- `engineerSignUrl` 有值时：图片正确插入Word
- `engineerSignUrl` 为空时：占位符位置留空

### 测试方法建议

1. **单元测试**（Service层）：Mock `ZwWorkOrderService.selectZwWorkOrderById()` 和 `ISysUserService.selectUserById()`，验证字段映射逻辑
2. **集成测试**（Controller层）：使用 MockMvc，验证 HTTP 响应头和状态码
3. **端到端测试**：结合真实数据库和模板文件，验证生成的 `.docx` 文件内容

---

## 后端单元测试示例（Service层）

```java
@ExtendWith(MockitoExtension.class)
class ZwWorkOrderExportServiceTest {

    @Mock
    private IZwWorkOrderService workOrderService;

    @Mock
    private ISysUserService sysUserService;

    @InjectMocks
    private ZwWorkOrderExportServiceImpl service;

    // WO-FLD-002: 工程师姓名通过 assignedTo 查询
    @Test
    void lookupEngineerName_existingUser_returnsNickName() {
        SysUser user = new SysUser();
        user.setNickName("张工程师");
        when(sysUserService.selectUserById(1L)).thenReturn(user);

        String name = service.lookupEngineerName(1L);
        assertEquals("张工程师", name);
    }

    // WO-BND-002: 无工程师分配返回 -
    @Test
    void lookupEngineerName_nullAssignedTo_returnsDash() {
        String name = service.lookupEngineerName(null);
        assertEquals("-", name);
    }

    // WO-FLD-003: 日期格式正确
    @Test
    void formatDate_correctFormat() throws Exception {
        java.util.Date date = new java.text.SimpleDateFormat("yyyy-MM-dd").parse("2024-03-15");
        String result = service.formatDate(date);
        assertEquals("2024年03月15日", result);
    }

    // WO-FLD-004: 工单类型转换
    @Test
    void formatOrderType_allTypes() {
        assertEquals("巡检", service.formatOrderType("1"));
        assertEquals("维修", service.formatOrderType("2"));
        assertEquals("送货", service.formatOrderType("3"));
        assertEquals("混合", service.formatOrderType("4"));
    }
}
```

> 提示：Service层的 `lookupEngineerName`、`formatDate`、`formatOrderType` 等方法需改为 `protected` 或包级别可见，以便单元测试直接调用。