<template>
  <div class="app-container">
    <!-- 查询条件 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="工单标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入工单标题"
          clearable
          style="width: 180px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="工单编号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入工单编号"
          clearable
          style="width: 180px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="工单状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 140px">
          <el-option label="待接单" value="0" />
          <el-option label="处理中" value="1" />
          <el-option label="待确认" value="2" />
          <el-option label="已完成" value="3" />
          <el-option label="已取消" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="优先级" prop="priority">
        <el-select v-model="queryParams.priority" placeholder="请选择优先级" clearable style="width: 120px">
          <el-option label="低" value="1" />
          <el-option label="中" value="2" />
          <el-option label="高" value="3" />
          <el-option label="紧急" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="客户名称" prop="customerId">
        <el-input
          v-model="queryParams.customerName"
          placeholder="请输入客户名称"
          clearable
          style="width: 160px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="工程师" prop="assignedTo">
        <el-input
          v-model="queryParams.assignedToName"
          placeholder="请输入工程师"
          clearable
          style="width: 140px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd HH:mm:ss"
          style="width: 320px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['zw:workorder:add']"
        >新增工单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zw:workorder:export']"
        >导出Excel</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 工单列表 -->
    <el-table v-loading="loading" :data="workOrderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="工单编号" align="center" prop="orderNo" :show-overflow-tooltip="true" width="180" />
      <el-table-column label="工单标题" align="center" prop="title" :show-overflow-tooltip="true" min-width="200" />
      <el-table-column label="工单类型" align="center" prop="orderTypeName" width="90">
        <template slot-scope="scope">
          <span>{{ orderTypeNameMap[scope.row.orderType] || scope.row.orderTypeName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="优先级" align="center" prop="priority" width="90">
        <template slot-scope="scope">
          <el-tag :type="priorityTagType(scope.row.priority)">{{ priorityNameMap[scope.row.priority] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="客户" align="center" prop="customerName" :show-overflow-tooltip="true" width="150" />
      <el-table-column label="工程师" align="center" prop="assignedToName" :show-overflow-tooltip="true" width="120" />
      <el-table-column label="SLA截止时间" align="center" prop="slaDeadline" width="170">
        <template slot-scope="scope">
          <span v-if="isSlaOverdue(scope.row)" style="color: #F56C6C; font-weight: 600;">
            {{ parseTime(scope.row.slaDeadline) }}
          </span>
          <span v-else>{{ parseTime(scope.row.slaDeadline) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ statusNameMap[scope.row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >查看详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zw:workorder:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zw:workorder:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增 / 修改 弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="750px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="工单编号">
              <el-input v-model="form.orderNo" disabled placeholder="系统自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工单标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入工单标题" maxlength="200" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="工单类型" prop="orderType">
              <el-select v-model="form.orderType" placeholder="请选择工单类型" style="width: 100%">
                <el-option label="巡检" value="1" />
                <el-option label="维修" value="2" />
                <el-option label="送货" value="3" />
                <el-option label="混合" value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联资产" prop="assetId">
              <el-select v-model="form.assetId" placeholder="请选择资产（可选）" clearable style="width: 100%">
                <el-option v-for="item in assetOptions" :key="item.assetId" :label="item.assetName" :value="item.assetId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="请选择客户" clearable style="width: 100%" @change="onCustomerChange">
                <el-option v-for="item in customerOptions" :key="item.deptId" :label="item.deptName" :value="item.deptId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="form.priority" placeholder="请选择优先级" style="width: 100%">
                <el-option label="低" value="1" />
                <el-option label="中" value="2" />
                <el-option label="高" value="3" />
                <el-option label="紧急" value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="分配工程师" prop="assignedTo">
              <el-select v-model="form.assignedTo" placeholder="请选择工程师（可选）" clearable style="width: 100%">
                <el-option v-for="item in engineerOptions" :key="item.userId" :label="item.realName" :value="item.userId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="SLA截止时间" prop="slaDeadline">
              <el-date-picker
                v-model="form.slaDeadline"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择SLA截止时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="工单描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入工单描述" maxlength="1000" show-word-limit />
        </el-form-item>
        <el-divider content-position="left">客户联系人信息</el-divider>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系人姓名" prop="contactName">
              <el-input v-model="form.contactName" placeholder="请输入联系人姓名" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系人电话" maxlength="20" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="上门地址" prop="contactAddress">
          <el-input v-model="form.contactAddress" placeholder="请输入上门地址" maxlength="255" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listWorkOrder, getWorkOrder, addWorkOrder, updateWorkOrder, delWorkOrder, exportWorkOrder } from "@/api/zw/workOrder"
import { listDept } from "@/api/system/dept"
import { listAsset } from "@/api/zw/asset"
import { listDispatchableEngineer } from "@/api/zw/engineer"

export default {
  name: "ZwWorkOrder",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 工单表格数据
      workOrderList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: undefined,
        orderNo: undefined,
        customerName: undefined,
        assignedToName: undefined,
        status: undefined,
        priority: undefined,
        orderType: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        title: [
          { required: true, message: "工单标题不能为空", trigger: "blur" },
          { max: 200, message: "工单标题最大长度为200个字符", trigger: "blur" }
        ],
        orderType: [
          { required: true, message: "请选择工单类型", trigger: "change" }
        ],
        customerId: [
          { required: true, message: "请选择客户", trigger: "change" }
        ],
        priority: [
          { required: true, message: "请选择优先级", trigger: "change" }
        ],
        contactPhone: [
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
        ]
      },
      // 下拉选项数据
      customerOptions: [],
      assetOptions: [],
      engineerOptions: [],
      // 标记是否正在加载表单（避免 watch 误触发）
      formLoading: false,
      // 工单状态映射
      statusOptions: [
        { label: "待接单", value: "0", tagType: "info" },
        { label: "处理中", value: "1", tagType: "warning" },
        { label: "待确认", value: "2", tagType: "primary" },
        { label: "已完成", value: "3", tagType: "success" },
        { label: "已取消", value: "4", tagType: "info" }
      ],
      // 工单类型映射
      orderTypeNameMap: {
        '1': '巡检',
        '2': '维修',
        '3': '送货',
        '4': '混合'
      },
      // 状态名称映射
      statusNameMap: {
        '0': '待接单',
        '1': '处理中',
        '2': '待确认',
        '3': '已完成',
        '4': '已取消'
      },
      // 优先级名称映射
      priorityNameMap: {
        '1': '低',
        '2': '中',
        '3': '高',
        '4': '紧急'
      }
    }
  },
  created() {
    this.getList()
  },
  watch: {
    'form.customerId'(val) {
      if (!this.formLoading) {
        this.loadAssets(val)
      }
    }
  },
  methods: {
    /** 查询工单列表 */
    getList() {
      this.loading = true
      const params = { ...this.queryParams }
      if (this.dateRange && this.dateRange.length === 2) {
        params.dateRange = this.dateRange
      }
      listWorkOrder(params).then(response => {
        this.workOrderList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    /** 加载客户列表（sys_dept） */
    loadCustomers() {
      listDept({ pageNum: 1, pageSize: 1000 }).then(res => {
        this.customerOptions = (res.rows || []).filter(d => d.parentId !== 0)
      })
    },
    /** 加载资产列表（按客户过滤） */
    loadAssets(customerId) {
      if (customerId) {
        listAsset({ customerId, pageNum: 1, pageSize: 500 }).then(res => {
          this.assetOptions = res.rows || []
        })
      } else {
        this.assetOptions = []
      }
    },
    /** 加载可派发工程师列表 */
    loadEngineers() {
      listDispatchableEngineer().then(res => {
        this.engineerOptions = res.data || []
      })
    },
    /** 客户变更时重置资产 */
    onCustomerChange(val) {
      this.form.assetId = undefined
      this.loadAssets(val)
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        workOrderId: undefined,
        orderNo: undefined,
        customerId: undefined,
        assetId: undefined,
        orderType: undefined,
        title: undefined,
        description: undefined,
        priority: '2',
        assignedTo: undefined,
        contactName: undefined,
        contactPhone: undefined,
        contactAddress: undefined,
        slaDeadline: undefined
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = []
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.workOrderId)
      this.single = selection.length !== 1
      this.multiple = selection.length === 0
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.formLoading = true
      this.loadCustomers()
      this.loadEngineers()
      this.assetOptions = []
      this.open = true
      this.title = "新增工单"
      this.$nextTick(() => { this.formLoading = false })
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      this.formLoading = true
      this.loadCustomers()
      this.loadEngineers()
      const workOrderId = row.workOrderId || this.ids[0]
      getWorkOrder(workOrderId).then(response => {
        this.form = response.data
        this.loadAssets(this.form.customerId)
        this.open = true
        this.title = "修改工单"
        this.$nextTick(() => { this.formLoading = false })
      })
    },
    /** 查看详情 */
    handleDetail(row) {
      const workOrderId = row.workOrderId || this.ids[0]
      this.$router.push({ path: "/zw/workOrder/detail", query: { workOrderId } })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.workOrderId != undefined) {
            updateWorkOrder(this.form).then(() => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addWorkOrder(this.form).then(() => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const workOrderIds = row.workOrderId || this.ids
      this.$modal.confirm('是否确认删除工单编号为"' + (row.orderNo || workOrderIds) + '"的数据项？').then(() => {
        return delWorkOrder(workOrderIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮 */
    handleExport() {
      const queryParams = this.queryParams
      this.$modal.confirm("是否确认导出所有工单数据项？").then(() => {
        exportWorkOrder(queryParams).then(response => {
          const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
          const url = window.URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = '工单数据_' + this.parseTime(new Date(), '{y}{m}{d}{h}{i}{s}') + '.xlsx'
          link.click()
          window.URL.revokeObjectURL(url)
          this.$modal.msgSuccess("导出成功")
        })
      }).catch(() => {})
    },
    /** 状态标签颜色 */
    statusTagType(status) {
      const map = {
        '0': 'info',
        '1': 'warning',
        '2': 'primary',
        '3': 'success',
        '4': 'info'
      }
      return map[status] || ''
    },
    /** 优先级标签颜色 */
    priorityTagType(priority) {
      const map = {
        '1': 'info',
        '2': 'primary',
        '3': 'warning',
        '4': 'danger'
      }
      return map[priority] || ''
    },
    /** SLA是否超时 */
    isSlaOverdue(row) {
      if (!row.slaDeadline || row.status === '3' || row.status === '4') {
        return false
      }
      return new Date(row.slaDeadline) < new Date()
    }
  }
}
</script>
