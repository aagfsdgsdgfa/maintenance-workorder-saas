<template>
  <div class="inspection-report">
    <div class="page-header">
      <el-button icon="ArrowLeft" @click="$router.back()">返回</el-button>
      <h2 class="page-title">巡检报告</h2>
      <el-button type="primary" icon="Download" @click="exportPdf">导出PDF</el-button>
    </div>

    <!-- 报告主体 -->
    <div class="report-doc">
      <!-- 报告头部 -->
      <div class="report-header">
        <div class="report-stamp" v-if="report.status === '3'">已完成并验收</div>
        <div class="report-title">设备巡检报告</div>
        <div class="report-meta">
          <span>任务ID：{{ report.taskId }}</span>
          <span>设备ID：{{ report.assetId }}</span>
          <span>计划时间：{{ formatTime(report.planTime) }}</span>
          <span>实际开始：{{ formatTime(report.actualStartTime) }}</span>
          <span>实际结束：{{ formatTime(report.actualEndTime) }}</span>
        </div>
        <div class="report-meta" style="margin-top:4px">
          <span>签到地址：{{ report.checkInAddress }}</span>
          <span>状态：{{ taskStatusName(report.status) }}</span>
        </div>
      </div>

      <!-- 检查项 -->
      <div class="report-section">
        <div class="section-title">巡检结果清单</div>
        <div class="check-grid">
          <div v-for="(r, i) in parsedResults" :key="i" class="check-item">
            <div class="check-name">{{ r.name || r.checkItem || '检查项' + (i+1) }}</div>
            <div class="check-value">{{ r.value || r.checkValue || '-' }}</div>
            <el-tag :type="r.isNormal === 'N' || r.isNormal === false ? 'danger' : 'success'" size="small" style="margin-top:4px">
              {{ r.isNormal === 'N' || r.isNormal === false ? '异常' : '正常' }}
            </el-tag>
          </div>
        </div>
        <div class="check-summary">
          正常 {{ report.normalCount || 0 }} 项 &nbsp;|&nbsp; 异常 {{ report.abnormalCount || 0 }} 项
        </div>
      </div>

      <!-- AI语音记录 -->
      <div class="report-section" v-if="reportText">
        <div class="section-title">
          <el-icon style="margin-right:6px"><Microphone /></el-icon>AI 语音记录
        </div>
        <div class="voice-text">{{ reportText }}</div>
      </div>

      <!-- 现场照片 -->
      <div class="report-section" v-if="report.photoUrls?.length">
        <div class="section-title">现场影像记录</div>
        <div class="photo-grid">
          <el-image v-for="(url, i) in report.photoUrls" :key="i" :src="url" fit="cover" style="width:120px;height:90px;border-radius:6px" :preview-src-list="report.photoUrls" />
        </div>
      </div>

      <!-- 工程师备注 -->
      <div class="report-section" v-if="report.remark">
        <div class="section-title">工程师备注</div>
        <div class="remark-text">{{ report.remark }}</div>
      </div>

      <!-- 客户验收签名 -->
      <div class="report-section signature-section">
        <div class="section-title">客户验收签名</div>
        <div class="signature-area">
          <div class="signature-box" v-if="report.signatureUrl">
            <img :src="report.signatureUrl" style="max-width:200px;max-height:80px" />
            <div style="font-size:12px;color:#909399;margin-top:4px">签名时间：{{ report.actualEndTime }}</div>
          </div>
          <div v-else class="signature-placeholder">待签名</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getInspectionReport } from '@/api/zw/inspection'

const route = useRoute()
const report = ref({})
const loading = ref(false)

const parsedResults = computed(() => {
  if (!report.value.resultList || report.value.resultList.length === 0) return []
  return report.value.resultList.map(r => {
    // 后端已解析 checkItems，直接用 parsedItems
    const items = Array.isArray(r.checkItems) ? r.checkItems : []
    return {
      checkItem: r.remark || (items[0]?.name || '检查项'),
      checkValue: items[0]?.value || '',
      isNormal: r.isNormal,
      name: items[0]?.name || '',
      value: items[0]?.value || ''
    }
  })
})

const reportText = computed(() => {
  if (!report.value.resultList) return ''
  return report.value.resultList.map(r => r.voiceText).filter(Boolean).join('\n')
})

async function loadReport() {
  loading.value = true
  try {
    const res = await getInspectionReport(route.params.taskId)
    if (res.code === 200 && res.data) {
      report.value = res.data
    } else {
      ElMessage.warning(res.msg || '报告加载失败')
    }
  } catch (e) {
    console.error('报告加载失败', e)
    ElMessage.error('报告加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

function exportPdf() { ElMessage.info('PDF导出功能开发中') }

function formatTime(t) {
  if (!t) return '-'
  return t.substring ? t.substring(0, 16) : String(t).substring(0, 16)
}

function taskStatusName(status) {
  return { '0': '待执行', '1': '执行中', '2': '已完成' }[status] || status
}

onMounted(() => { loadReport() })
</script>

<style scoped>
.page-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.page-title { font-size: 20px; font-weight: 700; color: #303133; flex: 1; }
.report-doc { background: #fff; border-radius: 10px; padding: 32px; max-width: 900px; }
.report-header { border-bottom: 2px solid #2155a4; padding-bottom: 16px; margin-bottom: 20px; position: relative; }
.report-stamp { position: absolute; right: 0; top: 0; border: 3px solid #67c23a; color: #67c23a; padding: 4px 12px; border-radius: 4px; font-weight: 700; font-size: 14px; transform: rotate(15deg); opacity: 0.8; }
.report-title { font-size: 22px; font-weight: 800; color: #1a3a6b; text-align: center; margin-bottom: 12px; }
.report-meta { display: flex; gap: 24px; font-size: 13px; color: #606266; }
.report-section { margin-bottom: 20px; }
.section-title { font-size: 15px; font-weight: 700; color: #303133; margin-bottom: 12px; padding-bottom: 6px; border-bottom: 1px solid #f0f0f0; display: flex; align-items: center; }
.check-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.check-item { background: #f5f7fa; border-radius: 8px; padding: 12px; text-align: center; }
.check-name { font-size: 13px; color: #909399; }
.check-value { font-size: 16px; font-weight: 600; color: #303133; margin: 4px 0; }
.check-summary { margin-top: 12px; text-align: center; font-size: 14px; color: #606266; }
.voice-text { background: #f5f7fa; padding: 12px; border-radius: 8px; font-size: 14px; color: #303133; line-height: 1.8; }
.photo-grid { display: flex; gap: 10px; flex-wrap: wrap; }
.remark-text { background: #fffbe6; padding: 12px; border-radius: 8px; font-size: 14px; color: #303133; border-left: 3px solid #e6a23c; }
.signature-area { display: flex; justify-content: flex-end; }
.signature-placeholder { width: 200px; height: 80px; border: 2px dashed #dcdfe6; border-radius: 4px; display: flex; align-items: center; justify-content: center; color: #909399; }
</style>
