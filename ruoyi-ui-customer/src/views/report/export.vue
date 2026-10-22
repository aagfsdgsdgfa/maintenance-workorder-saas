<template>
  <div class="export-page">
    <div class="page-header">
      <el-button icon="ArrowLeft" @click="$router.back()">返回</el-button>
      <h2 class="page-title">报告导出</h2>
    </div>

    <div class="export-form-card">
      <div class="card-title">
        <el-icon><Download /></el-icon>
        选择导出条件
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" size="default">
        <el-form-item label="报告类型" prop="reportType">
          <el-radio-group v-model="form.reportType">
            <el-radio value="1">
              <span class="radio-label">巡检报告</span>
              <span class="radio-desc">汇总一段时间内的所有巡检任务数据</span>
            </el-radio>
            <el-radio value="2">
              <span class="radio-label">维保报告</span>
              <span class="radio-desc">汇总工单维保记录及完成情况</span>
            </el-radio>
            <el-radio value="3">
              <span class="radio-label">资产报告</span>
              <span class="radio-desc">资产台账及运行状态汇总</span>
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="日期范围" prop="dateRange">
          <el-date-picker
            v-model="form.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
            style="width:100%"
          />
        </el-form-item>

        <el-form-item label="文件格式">
          <el-select v-model="form.fileFormat" style="width:200px">
            <el-option value="PDF" label="PDF 文档" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="exporting" icon="Download" @click="handleExport">
            {{ exporting ? '正在生成...' : '生成并下载报告' }}
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 历史导出记录 -->
    <div class="history-card" v-if="history.length > 0">
      <div class="card-title">
        <el-icon><Clock /></el-icon>
        最近导出记录
      </div>
      <el-table :data="history" stripe style="width:100%">
        <el-table-column label="文件名" prop="fileName" :show-overflow-tooltip="true" min-width="200" />
        <el-table-column label="报告类型" width="100">
          <template #default="{ row }">{{ typeName(row.reportType) }}</template>
        </el-table-column>
        <el-table-column label="日期范围" width="200">
          <template #default="{ row }">{{ row.startDate }} ~ {{ row.endDate }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.exportStatus === '1' ? 'success' : row.exportStatus === '2' ? 'danger' : 'info'" size="small">
              {{ statusName(row.exportStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时间" prop="createTime" width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.exportStatus === '1'"
              type="primary"
              size="small"
              link
              icon="Download"
              @click="downloadFile(row.exportId, row.fileName)"
            >
              下载
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { exportReport, downloadReport, listReportExport } from '@/api/zw/reportExport'

const formRef = ref()
const exporting = ref(false)
const history = ref([])

const form = reactive({
  reportType: '1',
  dateRange: [],
  fileFormat: 'PDF'
})

const rules = {
  reportType: [{ required: true, message: '请选择报告类型', trigger: 'change' }],
  dateRange: [{ required: true, message: '请选择日期范围', trigger: 'change' }]
}

function disabledDate(date) {
  return date > new Date()
}

async function handleExport() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  if (form.dateRange.length !== 2) {
    ElMessage.warning('请选择完整的日期范围')
    return
  }

  exporting.value = true
  try {
    const res = await exportReport({
      reportType: form.reportType,
      startDate: form.dateRange[0],
      endDate: form.dateRange[1],
      fileFormat: form.fileFormat
    })
    if (res.code === 200 && res.data.downloadUrl) {
      ElMessage.success('报告生成成功，开始下载')
      await downloadFile(res.data.exportId, res.data.fileName)
      loadHistory()
    } else {
      ElMessage.error(res.msg || '生成失败')
    }
  } catch (e) {
    ElMessage.error('导出失败：' + (e.message || '未知错误'))
  } finally {
    exporting.value = false
  }
}

async function downloadFile(exportId, fileName) {
  try {
    const token = localStorage.getItem('customer_token')
    const baseUrl = import.meta.env.VITE_APP_BASE_API || 'http://localhost:8080'
    const url = baseUrl + '/zw/report/download/' + exportId

    const response = await fetch(url, {
      headers: { 'Authorization': 'Bearer ' + token }
    })
    if (!response.ok) {
      const err = await response.text()
      ElMessage.error('下载失败：' + err)
      return
    }
    const blob = await response.blob()
    const objectUrl = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = objectUrl
    a.download = fileName || 'report.pdf'
    a.click()
    URL.revokeObjectURL(objectUrl)
  } catch (e) {
    ElMessage.error('下载失败')
  }
}

async function loadHistory() {
  try {
    const res = await listReportExport({ pageNum: 1, pageSize: 10 })
    if (res.code === 200) {
      history.value = res.rows || []
    }
  } catch (e) {
    // ignore
  }
}

function typeName(type) {
  return { '1': '巡检报告', '2': '维保报告', '3': '资产报告' }[type] || '-'
}

function statusName(s) {
  return { '0': '生成中', '1': '成功', '2': '失败' }[s] || '-'
}

function formatTime(t) {
  if (!t) return '-'
  return t.substring ? t.substring(0, 16) : String(t).substring(0, 16)
}

function resetForm() {
  form.reportType = '1'
  form.dateRange = []
  form.fileFormat = 'PDF'
  formRef.value?.resetFields()
}

onMounted(() => { loadHistory() })
</script>

<style scoped>
.export-page { max-width: 800px; }
.page-header { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; }
.page-title { font-size: 20px; font-weight: 700; color: #303133; }
.export-form-card, .history-card {
  background: #fff; border-radius: 10px; padding: 24px; margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}
.card-title {
  font-size: 15px; font-weight: 700; color: #1B4B8E; margin-bottom: 20px;
  display: flex; align-items: center; gap: 6px;
}
.radio-label { font-weight: 600; margin-right: 8px; }
.radio-desc { color: #909399; font-size: 12px; }
:deep(.el-radio) { display: flex; align-items: flex-start; margin-bottom: 12px; }
:deep(.el-radio__label) { display: flex; flex-direction: column; line-height: 1.6; }
</style>
