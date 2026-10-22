<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="报告日期" prop="reportDate">
        <el-date-picker v-model="queryParams.reportDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" style="width:150px"/>
      </el-form-item>
      <el-form-item label="工程师ID" prop="engineerId">
        <el-input v-model="queryParams.engineerId" placeholder="请输入工程师ID" clearable style="width:150px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px">
          <el-option label="草稿" value="0" />
          <el-option label="已生成" value="1" />
          <el-option label="已确认" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['zw:dailyReport:add']">新增日报</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-refresh" size="mini" @click="handleAutoGenerate" v-hasPermi="['zw:dailyReport:add']">自动汇总</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="reportList">
      <el-table-column label="日报ID" align="center" prop="reportId" width="80" />
      <el-table-column label="报告日期" align="center" prop="reportDate" width="120" />
      <el-table-column label="工程师" align="center" prop="engineerName" width="120" />
      <el-table-column label="报告标题" align="center" prop="reportTitle" :show-overflow-tooltip="true" min-width="160" />
      <el-table-column label="工单数" align="center" prop="workOrderCount" width="80" />
      <el-table-column label="巡检数" align="center" prop="inspectionCount" width="80" />
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <el-tag :type="statusTag(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['zw:dailyReport:query']">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)" v-hasPermi="['zw:dailyReport:edit']">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['zw:dailyReport:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="报告日期" prop="reportDate">
              <el-date-picker v-model="form.reportDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工程师ID" prop="engineerId">
              <el-input-number v-model="form.engineerId" :min="1" style="width:100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="报告标题" prop="reportTitle">
          <el-input v-model="form.reportTitle" placeholder="请输入报告标题"/>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="工单数">
              <el-input-number v-model="form.workOrderCount" :min="0" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="巡检数">
              <el-input-number v-model="form.inspectionCount" :min="0" style="width:100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="草稿" value="0" />
            <el-option label="已生成" value="1" />
            <el-option label="已确认" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="报告内容">
          <el-input v-model="form.reportContent" type="textarea" :rows="4" placeholder="请输入报告内容"/>
        </el-form-item>
        <el-form-item label="语音汇总">
          <el-input v-model="form.voiceSummary" type="textarea" :rows="3" placeholder="请输入语音记录汇总"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="日报详情" :visible.sync="detailOpen" width="650px" append-to-body>
      <el-descriptions :column="2" border v-if="detailForm.reportId">
        <el-descriptions-item label="日报ID">{{ detailForm.reportId }}</el-descriptions-item>
        <el-descriptions-item label="报告日期">{{ detailForm.reportDate }}</el-descriptions-item>
        <el-descriptions-item label="工程师">{{ detailForm.engineerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTag(detailForm.status)">{{ statusText(detailForm.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="工单数">{{ detailForm.workOrderCount }}</el-descriptions-item>
        <el-descriptions-item label="巡检数">{{ detailForm.inspectionCount }}</el-descriptions-item>
        <el-descriptions-item label="报告标题" :span="2">{{ detailForm.reportTitle }}</el-descriptions-item>
        <el-descriptions-item label="报告内容" :span="2">{{ detailForm.reportContent }}</el-descriptions-item>
        <el-descriptions-item label="语音汇总" :span="2">{{ detailForm.voiceSummary }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer"><el-button @click="detailOpen = false">关 闭</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { listDailyReport, getDailyReport, addDailyReport, updateDailyReport, delDailyReport, autoGenerateDailyReport } from "@/api/zw/dailyReport"

export default {
  name: "ZwDailyReport",
  data() {
    return {
      loading: true,
      total: 0,
      reportList: [],
      open: false,
      detailOpen: false,
      title: "",
      showSearch: true,
      queryParams: { pageNum: 1, pageSize: 10, reportDate: undefined, engineerId: undefined, status: undefined },
      form: {},
      detailForm: {},
      rules: {
        reportDate: [{ required: true, message: "报告日期不能为空", trigger: "change" }],
        engineerId: [{ required: true, message: "工程师ID不能为空", trigger: "blur" }],
        reportTitle: [{ required: true, message: "报告标题不能为空", trigger: "blur" }]
      }
    }
  },
  created() { this.getList() },
  methods: {
    getList() {
      this.loading = true
      listDailyReport(this.queryParams).then(res => { this.reportList = res.rows; this.total = res.total; this.loading = false })
    },
    cancel() { this.open = false; this.reset() },
    reset() { this.form = { reportDate: undefined, engineerId: undefined, reportTitle: undefined, reportContent: undefined, workOrderCount: 0, inspectionCount: 0, voiceSummary: undefined, status: '0' }; this.resetForm("form") },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery() },
    handleAdd() { this.reset(); this.open = true; this.title = "新增日报" },
    handleEdit(row) { getDailyReport(row.reportId).then(res => { this.form = res.data; this.open = true; this.title = "编辑日报" }) },
    handleView(row) { getDailyReport(row.reportId).then(res => { this.detailForm = res.data; this.detailOpen = true }) },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const action = this.form.reportId ? updateDailyReport(this.form) : addDailyReport(this.form)
          action.then(() => { this.$modal.msgSuccess(this.form.reportId ? "修改成功" : "新增成功"); this.open = false; this.getList() })
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除日报ID为"' + row.reportId + '"的记录？').then(() => delDailyReport(row.reportId)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功") }).catch(() => {})
    },
    handleAutoGenerate() {
      autoGenerateDailyReport().then(res => {
        this.$modal.msgSuccess("自动汇总成功，日报ID：" + res.data.reportId)
        this.getList()
      })
    },
    statusText(v) { return { '0': '草稿', '1': '已生成', '2': '已确认' }[v] || '-' },
    statusTag(v) { return { '0': 'info', '1': 'warning', '2': 'success' }[v] || '' }
  }
}
</script>
