<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="客户ID" prop="customerId">
        <el-input v-model="queryParams.customerId" placeholder="请输入客户ID" clearable style="width:140px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="报告类型" prop="reportType">
        <el-select v-model="queryParams.reportType" placeholder="全部" clearable style="width:130px">
          <el-option label="巡检报告" value="1" />
          <el-option label="维保报告" value="2" />
          <el-option label="资产报告" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="exportStatus">
        <el-select v-model="queryParams.exportStatus" placeholder="全部" clearable style="width:110px">
          <el-option label="生成中" value="0" />
          <el-option label="成功" value="1" />
          <el-option label="失败" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['zw:reportExport:add']">新增导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="reportList">
      <el-table-column label="导出ID" align="center" prop="exportId" width="80" />
      <el-table-column label="客户名称" align="center" prop="customerName" width="130" :show-overflow-tooltip="true" />
      <el-table-column label="报告类型" align="center" prop="reportType" width="100">
        <template slot-scope="scope"><el-tag>{{ reportTypeText(scope.row.reportType) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="日期范围" align="center" width="200">
        <template slot-scope="scope">{{ scope.row.startDate }} ~ {{ scope.row.endDate }}</template>
      </el-table-column>
      <el-table-column label="文件名" align="center" prop="fileName" :show-overflow-tooltip="true" min-width="160" />
      <el-table-column label="格式" align="center" prop="fileFormat" width="80" />
      <el-table-column label="状态" align="center" prop="exportStatus" width="90">
        <template slot-scope="scope">
          <el-tag :type="statusTag(scope.row.exportStatus)">{{ statusText(scope.row.exportStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="下载次数" align="center" prop="downloadCount" width="90" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="140" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['zw:reportExport:query']">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['zw:reportExport:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户ID" prop="customerId">
              <el-input-number v-model="form.customerId" :min="1" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报告类型" prop="reportType">
              <el-select v-model="form.reportType" style="width:100%">
                <el-option label="巡检报告" value="1" />
                <el-option label="维保报告" value="2" />
                <el-option label="资产报告" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="开始日期">
              <el-date-picker v-model="form.startDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期">
              <el-date-picker v-model="form.endDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" style="width:100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="文件格式">
              <el-select v-model="form.fileFormat" style="width:100%">
                <el-option label="PDF" value="PDF" />
                <el-option label="Word" value="Word" />
                <el-option label="Excel" value="Excel" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.exportStatus" style="width:100%">
                <el-option label="生成中" value="0" />
                <el-option label="成功" value="1" />
                <el-option label="失败" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="文件名">
          <el-input v-model="form.fileName" placeholder="请输入文件名"/>
        </el-form-item>
        <el-form-item label="文件路径">
          <el-input v-model="form.filePath" placeholder="请输入文件路径"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="导出详情" :visible.sync="detailOpen" width="550px" append-to-body>
      <el-descriptions :column="2" border v-if="detailForm.exportId">
        <el-descriptions-item label="导出ID">{{ detailForm.exportId }}</el-descriptions-item>
        <el-descriptions-item label="客户名称">{{ detailForm.customerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="报告类型">{{ reportTypeText(detailForm.reportType) }}</el-descriptions-item>
        <el-descriptions-item label="文件格式">{{ detailForm.fileFormat }}</el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ detailForm.startDate }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ detailForm.endDate }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTag(detailForm.exportStatus)">{{ statusText(detailForm.exportStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="下载次数">{{ detailForm.downloadCount }}</el-descriptions-item>
        <el-descriptions-item label="文件名" :span="2">{{ detailForm.fileName }}</el-descriptions-item>
        <el-descriptions-item label="文件路径" :span="2">{{ detailForm.filePath }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer"><el-button @click="detailOpen = false">关 闭</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { listReportExport, getReportExport, addReportExport, delReportExport } from "@/api/zw/reportExport"

export default {
  name: "ZwReportExport",
  data() {
    return {
      loading: true, total: 0, reportList: [], open: false, detailOpen: false, title: "", showSearch: true,
      queryParams: { pageNum: 1, pageSize: 10, customerId: undefined, reportType: undefined, exportStatus: undefined },
      form: {}, detailForm: {},
      rules: { customerId: [{ required: true, message: "客户ID不能为空", trigger: "blur" }], reportType: [{ required: true, message: "请选择报告类型", trigger: "change" }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listReportExport(this.queryParams).then(res => { this.reportList = res.rows; this.total = res.total; this.loading = false }) },
    cancel() { this.open = false; this.reset() },
    reset() { this.form = { customerId: undefined, reportType: undefined, startDate: undefined, endDate: undefined, fileName: undefined, filePath: undefined, fileFormat: 'Excel', exportStatus: '0' }; this.resetForm("form") },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery() },
    handleAdd() { this.reset(); this.open = true; this.title = "新增导出" },
    handleView(row) { getReportExport(row.exportId).then(res => { this.detailForm = res.data; this.detailOpen = true }) },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) { addReportExport(this.form).then(() => { this.$modal.msgSuccess("新增成功"); this.open = false; this.getList() }) }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除导出ID为"' + row.exportId + '"的记录？').then(() => delReportExport(row.exportId)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功") }).catch(() => {})
    },
    reportTypeText(v) { return { '1': '巡检报告', '2': '维保报告', '3': '资产报告' }[v] || '-' },
    statusText(v) { return { '0': '生成中', '1': '成功', '2': '失败' }[v] || '-' },
    statusTag(v) { return { '0': 'info', '1': 'success', '2': 'danger' }[v] || '' }
  }
}
</script>
