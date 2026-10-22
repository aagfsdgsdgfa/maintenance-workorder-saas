<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="计划名称" prop="planName">
        <el-input v-model="queryParams.planName" placeholder="请输入计划名称" clearable style="width:150px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="客户" prop="customerId">
        <el-select v-model="queryParams.customerId" placeholder="全部" clearable filterable style="width:160px">
          <el-option v-for="c in customerOptions" :key="c.id" :label="c.label" :value="c.id"/>
        </el-select>
      </el-form-item>
      <el-form-item label="周期类型" prop="frequencyType">
        <el-select v-model="queryParams.frequencyType" placeholder="全部" clearable style="width:120px">
          <el-option label="每日" value="1" />
          <el-option label="每周" value="2" />
          <el-option label="每月" value="3" />
          <el-option label="自定义" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:100px">
          <el-option label="启用" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['zw:inspectionPlan:add']">新增计划</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="planList">
      <el-table-column label="计划ID" align="center" prop="planId" width="80" />
      <el-table-column label="计划名称" align="center" prop="planName" :show-overflow-tooltip="true" min-width="140" />
      <el-table-column label="客户" align="center" prop="customerName" :show-overflow-tooltip="true" min-width="140">
        <template slot-scope="scope"><span>{{ scope.row.customerName || ('客户#' + scope.row.customerId) }}</span></template>
      </el-table-column>
      <el-table-column label="周期类型" align="center" prop="frequencyType" width="100">
        <template slot-scope="scope"><el-tag>{{ freqTypeText(scope.row.frequencyType) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="执行计划" align="center" prop="frequencyCron" :show-overflow-tooltip="true" min-width="160">
        <template slot-scope="scope"><span>{{ cronToText(scope.row.frequencyCron) }}</span></template>
      </el-table-column>
      <el-table-column label="负责工程师" align="center" prop="engineerName" :show-overflow-tooltip="true" width="110">
        <template slot-scope="scope"><span>{{ scope.row.engineerName || ('工程师#' + scope.row.assignedTo) }}</span></template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.status" active-value="0" inactive-value="1" @change="handleStatusChange(scope.row)" v-hasPermi="['zw:inspectionPlan:edit']"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['zw:inspectionPlan:query']">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)" v-hasPermi="['zw:inspectionPlan:edit']">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['zw:inspectionPlan:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="计划名称" prop="planName">
          <el-input v-model="form.planName" placeholder="请输入计划名称"/>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="请选择客户" filterable style="width:100%">
                <el-option v-for="c in customerOptions" :key="c.id" :label="c.label" :value="c.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责工程师" prop="assignedTo">
              <el-select v-model="form.assignedTo" placeholder="请选择工程师" clearable filterable style="width:100%">
                <el-option v-for="e in engineerOptions" :key="e.engineerId" :label="e.realName + ' (' + e.employeeNo + ')'" :value="e.engineerId"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="周期类型" prop="frequencyType">
              <el-select v-model="form.frequencyType" style="width:100%">
                <el-option label="每日" value="1" />
                <el-option label="每周" value="2" />
                <el-option label="每月" value="3" />
                <el-option label="自定义" value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="执行计划">
              <el-input v-model="form.frequencyCron" placeholder="Cron表达式，如: 0 0 9 * * ?" style="width:70%"/>
              <span style="color:#999;font-size:12px;margin-left:8px">例: 每天9点 / 每月1号3点</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="资产ID列表">
          <el-input v-model="form.assetIds" type="textarea" :rows="2" placeholder='JSON数组，如：[1,2,3]'/>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="计划详情" :visible.sync="detailOpen" width="550px" append-to-body>
      <el-descriptions :column="2" border v-if="detailForm.planId">
        <el-descriptions-item label="计划ID">{{ detailForm.planId }}</el-descriptions-item>
        <el-descriptions-item label="计划名称">{{ detailForm.planName }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ detailForm.customerName || ('客户#' + detailForm.customerId) }}</el-descriptions-item>
        <el-descriptions-item label="负责工程师">{{ detailForm.engineerName || ('工程师#' + detailForm.assignedTo) }}</el-descriptions-item>
        <el-descriptions-item label="周期类型">{{ freqTypeText(detailForm.frequencyType) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detailForm.status === '0' ? '启用' : '停用' }}</el-descriptions-item>
        <el-descriptions-item label="执行计划">{{ cronToText(detailForm.frequencyCron) }}</el-descriptions-item>
        <el-descriptions-item label="资产ID">{{ detailForm.assetIds }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer"><el-button @click="detailOpen = false">关 闭</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { listInspectionPlan, getInspectionPlan, addInspectionPlan, updateInspectionPlan, delInspectionPlan } from "@/api/zw/inspectionPlan"
import { companyTreeSelect } from "@/api/system/user"
import { listDispatchableEngineer } from "@/api/zw/engineer"

export default {
  name: "ZwInspectionPlan",
  data() {
    return {
      loading: true, total: 0, planList: [], open: false, detailOpen: false, title: "", showSearch: true,
      customerOptions: [], engineerOptions: [],
      queryParams: { pageNum: 1, pageSize: 10, planName: undefined, customerId: undefined, frequencyType: undefined, assignedTo: undefined, status: undefined },
      form: {}, detailForm: {},
      rules: { planName: [{ required: true, message: "计划名称不能为空", trigger: "blur" }], customerId: [{ required: true, message: "请选择客户", trigger: "change" }], frequencyType: [{ required: true, message: "请选择周期类型", trigger: "change" }] }
    }
  },
  created() { this.getList(); this.getCustomerOptions(); this.getEngineerOptions() },
  methods: {
    getList() { this.loading = true; listInspectionPlan(this.queryParams).then(res => { this.planList = res.rows; this.total = res.total; this.loading = false }) },
    getCustomerOptions() { companyTreeSelect().then(res => { this.customerOptions = res.data || [] }) },
    getEngineerOptions() { listDispatchableEngineer().then(res => { this.engineerOptions = res.data || [] }) },
    cancel() { this.open = false; this.reset() },
    reset() { this.form = { planName: undefined, customerId: undefined, assetIds: undefined, frequencyType: undefined, frequencyCron: '0 0 9 * * ?', assignedTo: undefined, status: '0' }; this.resetForm("form") },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery() },
    handleAdd() { this.reset(); this.open = true; this.title = "新增计划" },
    handleEdit(row) { getInspectionPlan(row.planId).then(res => { this.form = res.data; this.open = true; this.title = "编辑计划" }) },
    handleView(row) { getInspectionPlan(row.planId).then(res => { this.detailForm = res.data; this.detailOpen = true }) },
    handleStatusChange(row) { updateInspectionPlan({ planId: row.planId, status: row.status }).then(() => this.$modal.msgSuccess("状态修改成功")) },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const action = this.form.planId ? updateInspectionPlan(this.form) : addInspectionPlan(this.form)
          action.then(() => { this.$modal.msgSuccess(this.form.planId ? "修改成功" : "新增成功"); this.open = false; this.getList() })
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除计划ID为"' + row.planId + '"的记录？').then(() => delInspectionPlan(row.planId)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功") }).catch(() => {})
    },
    freqTypeText(v) { return { '1': '每日', '2': '每周', '3': '每月', '4': '自定义' }[v] || '-' },
    cronToText(cron) {
      if (!cron) return '-'
      const parts = cron.trim().split(/\s+/)
      if (parts.length < 5) return cron
      const [sec, min, hour, dayOfMonth, month, dayOfWeek] = parts
      const time = hour === '0' && min === '0' ? '零点'
        : `${hour}点${min === '0' ? '' : min + '分'}`
      let desc = time
      if (dayOfMonth !== '*' && dayOfMonth !== '?' && dayOfMonth !== 'L') {
        const days = dayOfMonth.split(',').map(d => d + '号').join('、')
        desc = `每月${days} ${time}`
      } else if (month !== '*') {
        const monthNames = { '1': '1月', '2': '2月', '3': '3月', '4': '4月', '5': '5月', '6': '6月', '7': '7月', '8': '8月', '9': '9月', '10': '10月', '11': '11月', '12': '12月' }
        const ms = month.split(',').map(m => monthNames[m] || m + '月').join('、')
        desc = `每季${ms} ${time}`
      } else if (dayOfWeek !== '*' && dayOfWeek !== '?') {
        const weekNames = { '1': '周一', '2': '周二', '3': '周三', '4': '周四', '5': '周五', '6': '周六', '7': '周日', 'SUN': '周日', 'MON': '周一', 'TUE': '周二', 'WED': '周三', 'THU': '周四', 'FRI': '周五', 'SAT': '周六' }
        const ws = dayOfWeek.split(',').map(d => weekNames[d] || d).join('、')
        desc = `每${ws} ${time}`
      } else {
        desc = `每天 ${time}`
      }
      return desc
    }
  }
}
</script>
