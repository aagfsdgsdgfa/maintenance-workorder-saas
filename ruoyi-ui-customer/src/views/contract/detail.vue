<template>
  <div class="contract-detail-page">
    <!-- 顶部导航 -->
    <div class="back-bar">
      <el-button text @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
      <span class="back-title">合同详情</span>
    </div>

    <div v-if="c" class="detail-layout">
      <!-- 合同状态卡片 -->
      <div class="contract-hero-card">
        <div class="hero-left">
          <div class="contract-name">{{ c.contractName || c.contractNo }}</div>
          <div class="contract-no">合同编号：{{ c.contractNo }}</div>
          <div class="contract-period-display">
            <el-icon><Calendar /></el-icon>
            {{ formatDate(c.startDate) }} 至 {{ formatDate(c.endDate) }}
          </div>
        </div>
        <div class="hero-right">
          <el-tag :type="contractStatusTag(c)" size="large" effect="dark" style="font-size:14px;padding:6px 16px">
            {{ contractStatusLabel(c) }}
          </el-tag>
        </div>
      </div>

      <!-- SLA + 配额卡片 -->
      <el-row :gutter="16" style="margin-top: 16px">
        <el-col :span="12" v-if="c.slaResponseHours">
          <div class="sla-card">
            <div class="sla-header">
              <el-icon style="color:var(--primary-light)"><Timer /></el-icon>
              SLA 服务承诺
            </div>
            <el-descriptions :column="1" border size="small">
              <el-descriptions-item label="响应时效">接单后 <strong>{{ c.slaResponseHours }} 小时</strong>内响应</el-descriptions-item>
              <el-descriptions-item label="到场时效">{{ c.slaArrivalHours }} 小时内到场</el-descriptions-item>
              <el-descriptions-item label="免费配件额度">¥{{ c.freePartsQuota?.toLocaleString() }}</el-descriptions-item>
              <el-descriptions-item label="已用配件额度">¥{{ c.usedPartsAmount?.toLocaleString() }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-col>

        <el-col :span="12" v-if="c.includedInspectionsPerYear || c.includedRepairsPerYear">
          <div class="quota-cards">
            <div class="quota-card" v-if="c.includedInspectionsPerYear">
              <div class="quota-label">例行巡检剩余</div>
              <div class="quota-big" style="color:#3b82f6">
                {{ Math.max(0, (c.includedInspectionsPerYear || 0) - (c.usedInspections || 0)) }}
                <span class="quota-unit">次</span>
              </div>
              <el-progress
                :percentage="inspectionPct"
                :stroke-width="8"
                :color="inspectionPct > 80 ? '#ef4444' : '#3b82f6'"
              />
              <div class="quota-sub">已用 {{ c.usedInspections || 0 }} / 含 {{ c.includedInspectionsPerYear }} 次</div>
            </div>
            <div class="quota-card" v-if="c.includedRepairsPerYear">
              <div class="quota-label">紧急报修剩余</div>
              <div class="quota-big" style="color:#f59e0b">
                {{ Math.max(0, (c.includedRepairsPerYear || 0) - (c.usedRepairs || 0)) }}
                <span class="quota-unit">次</span>
              </div>
              <el-progress
                :percentage="repairPct"
                :stroke-width="8"
                :color="repairPct > 80 ? '#ef4444' : '#f59e0b'"
              />
              <div class="quota-sub">已用 {{ c.usedRepairs || 0 }} / 含 {{ c.includedRepairsPerYear }} 次</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 覆盖设备 -->
      <div class="panel-section" v-if="c.assets?.length">
        <div class="panel-title">
          <el-icon><Box /></el-icon>合同覆盖设备（共 {{ c.assets.length }} 台）
        </div>
        <el-table :data="c.assets" stripe style="margin-top: 12px" :header-cell-style="{ background: '#f8fafc' }">
          <el-table-column prop="assetName" label="设备名称" />
          <el-table-column prop="location" label="安装位置" />
          <el-table-column label="状态" width="90" align="center">
            <template #default="{ row }">
              <el-tag :type="{ '0': 'success', '1': 'danger', '2': 'warning' }[row.status] || 'info'" size="small">
                {{ { '0': '正常', '1': '故障', '2': '维修' }[row.status] || '未知' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <div v-else-if="!loading" style="text-align:center;padding:60px;color:var(--text-muted)">
      <el-empty description="合同不存在" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getContractDetail } from '@/api/zw/contract'

const route = useRoute()
const c = ref(null)
const loading = ref(false)

async function loadDetail() {
  loading.value = true
  try {
    const res = await getContractDetail(route.params.contractId)
    if (res.code === 200 && res.data) c.value = res.data
  } catch (e) {
    console.error('合同详情加载失败', e)
  } finally {
    loading.value = false
  }
}

const inspectionPct = computed(() => {
  if (!c.value?.includedInspectionsPerYear) return 0
  return Math.min(100, Math.round((c.value.usedInspections / c.value.includedInspectionsPerYear) * 100))
})

const repairPct = computed(() => {
  if (!c.value?.includedRepairsPerYear) return 0
  return Math.min(100, Math.round((c.value.usedRepairs / c.value.includedRepairsPerYear) * 100))
})

function contractStatusTag(c) {
  if (!c.endDate) return 'info'
  const diff = (new Date(c.endDate) - new Date()) / (1000 * 60 * 60 * 24)
  if (diff < 0) return 'info'
  if (diff <= 30) return 'danger'
  return 'success'
}

function contractStatusLabel(c) {
  if (!c.endDate) return '状态未知'
  const diff = (new Date(c.endDate) - new Date()) / (1000 * 60 * 60 * 24)
  if (diff < 0) return '已到期'
  if (diff <= 30) return '即将到期'
  return '生效中'
}

function formatDate(d) {
  if (!d) return '-'
  return d.substring ? d.substring(0, 10) : String(d).substring(0, 10)
}

onMounted(() => { loadDetail() })
</script>

<style scoped>
.back-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.back-title {
  font-size: 15px;
  color: var(--text-secondary);
}

.contract-hero-card {
  background: var(--primary-gradient);
  border-radius: var(--radius-lg);
  padding: 24px 28px;
  color: #fff;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  position: relative;
  overflow: hidden;
}

.contract-hero-card::before {
  content: '';
  position: absolute;
  right: -30px;
  top: -30px;
  width: 160px;
  height: 160px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 50%;
}

.contract-name {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 6px;
}

.contract-no {
  font-size: 13px;
  opacity: 0.7;
  margin-bottom: 10px;
}

.contract-period-display {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  opacity: 0.8;
}

.sla-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--shadow-sm);
}

.sla-header {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.quota-cards {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--shadow-sm);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.quota-card {
  /* 每个配额 */
}

.quota-label {
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 4px;
}

.quota-big {
  font-size: 36px;
  font-weight: 800;
  line-height: 1.2;
}

.quota-unit {
  font-size: 14px;
  color: var(--text-muted);
}

.quota-sub {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 4px;
}

.panel-section {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--shadow-sm);
  margin-top: 16px;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 6px;
}
</style>
