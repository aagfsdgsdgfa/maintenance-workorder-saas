<template>
  <div class="asset-list">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-title">设备管理</div>
      <div class="header-actions">
        <el-button type="primary" @click="$router.push('/repair/create')">
          <el-icon><Plus /></el-icon>新建报修
        </el-button>
      </div>
    </div>

    <!-- 搜索区 -->
    <div class="search-bar">
      <el-input
        v-model="query.assetName"
        placeholder="搜索设备名称、SN码"
        clearable
        style="width: 200px"
        @keyup.enter="loadData"
      >
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-select v-model="query.assetType" placeholder="设备类型" clearable style="width: 130px">
        <el-option label="设备" value="device" />
        <el-option label="软件" value="software" />
        <el-option label="耗材" value="consumable" />
      </el-select>
      <el-select v-model="query.status" placeholder="运行状态" clearable style="width: 120px">
        <el-option label="正常" value="0" />
        <el-option label="故障" value="1" />
        <el-option label="维修中" value="2" />
        <el-option label="已报废" value="3" />
      </el-select>
      <el-button type="primary" @click="loadData">
        <el-icon><Search /></el-icon>搜索
      </el-button>
      <el-button @click="resetQuery">
        <el-icon><Refresh /></el-icon>重置
      </el-button>
    </div>

    <!-- 表格 -->
    <div class="table-card">
      <el-table
        :data="list"
        v-loading="loading"
        @row-click="openDrawer"
        highlight-current-row
        stripe
      >
        <el-table-column label="设备名称" prop="assetName" min-width="160">
          <template #default="{ row }">
            <div style="display:flex;align-items:center;gap:8px;">
              <div class="asset-icon">
                <el-icon><Box /></el-icon>
              </div>
              <span style="font-weight:500">{{ row.assetName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="SN码" prop="assetCode" width="160">
          <template #default="{ row }">
            <span style="font-family:monospace;font-size:12px;color:var(--text-secondary)">{{ row.assetCode }}</span>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="90" align="center">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ assetTypeName(row.assetType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="品牌/型号" min-width="160">
          <template #default="{ row }">{{ row.brand }} {{ row.model }}</template>
        </el-table-column>
        <el-table-column label="位置" prop="location" min-width="140" show-overflow-tooltip />
        <el-table-column label="健康分" width="90" align="center">
          <template #default="{ row }">
            <span :class="healthClass(row.healthScore)" style="font-weight:700">{{ row.healthScore ?? '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">
              {{ statusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="质保到期" width="110" align="center">
          <template #default="{ row }">
            <span :class="isWarrantyExpiring(row.warrantyEndDate) ? 'status-fault' : ''" style="font-size:13px">
              {{ formatDate(row.warrantyEndDate) }}
            </span>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>

    <!-- 设备详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="设备详情" size="520px" direction="rtl">
      <div v-if="currentAsset" class="asset-detail">
        <!-- 设备状态头 -->
        <div class="detail-hero">
          <div class="detail-hero-icon">
            <el-icon size="28"><Box /></el-icon>
          </div>
          <div class="detail-hero-info">
            <div class="detail-name">{{ currentAsset.assetName }}</div>
            <el-tag :type="statusTag(currentAsset.status)" size="small" style="margin-top:4px">
              {{ statusName(currentAsset.status) }}
            </el-tag>
          </div>
        </div>

        <!-- 健康分 -->
        <div class="health-score-card">
          <div class="health-label">健康评分</div>
          <div class="health-value" :class="healthClass(currentAsset.healthScore)">
            {{ currentAsset.healthScore ?? 0 }}
            <span class="health-unit">/100</span>
          </div>
          <el-progress
            :percentage="currentAsset.healthScore ?? 0"
            :color="healthColor(currentAsset.healthScore)"
            :show-text="false"
            :stroke-width="8"
            style="margin-top:8px"
          />
        </div>

        <!-- 基本信息 -->
        <div class="detail-section">
          <div class="detail-section-title">
            <el-icon><InfoFilled /></el-icon>基本信息
          </div>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="SN码">{{ currentAsset.assetCode }}</el-descriptions-item>
            <el-descriptions-item label="品牌">{{ currentAsset.brand }}</el-descriptions-item>
            <el-descriptions-item label="型号">{{ currentAsset.model }}</el-descriptions-item>
            <el-descriptions-item label="安装位置">{{ currentAsset.location }}</el-descriptions-item>
            <el-descriptions-item label="质保到期">{{ formatDate(currentAsset.warrantyEndDate) }}</el-descriptions-item>
            <el-descriptions-item label="购入日期">{{ formatDate(currentAsset.purchaseDate) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 一物一码 -->
        <div class="detail-section">
          <div class="detail-section-title">
            <el-icon><FullScreen /></el-icon>一物一码
          </div>
          <div class="qr-box">
            <div class="qr-placeholder">
              <span>{{ currentAsset.qrCode || '暂无二维码' }}</span>
            </div>
            <el-button type="primary" plain size="small">下载打印</el-button>
          </div>
        </div>

        <!-- 维保历史 -->
        <div class="detail-section">
          <div class="detail-section-title">
            <el-icon><Clock /></el-icon>维保历史
          </div>
          <div class="timeline" v-if="assetLogs.length">
            <div v-for="log in assetLogs" :key="log.logId" class="timeline-item">
              <div class="timeline-dot"><el-icon size="10"><Check /></el-icon></div>
              <div class="timeline-content">
                <div class="timeline-title">{{ log.operationType || log.logType || '记录' }}</div>
                <div class="timeline-desc">{{ log.remark || log.content }}</div>
                <div class="timeline-time">{{ log.operationTime || log.createTime }}</div>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无维保记录" :image-size="60" />
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAssetList, getAssetDetail, getAssetLogs } from '@/api/zw/asset'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = ref({ pageNum: 1, pageSize: 10, assetName: '', assetType: '', status: '', warrantyStatus: '' })
const drawerVisible = ref(false)
const currentAsset = ref(null)
const assetLogs = ref([])

async function loadData() {
  loading.value = true
  try {
    const res = await getAssetList(query.value)
    if (res.code === 200 || res.code === 0) {
      list.value = res.rows || []
      total.value = res.total || 0
    }
  } catch (e) {
    console.error('资产列表加载失败', e)
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.value = { pageNum: 1, pageSize: 10, assetName: '', assetType: '', status: '', warrantyStatus: '' }
  loadData()
}

async function openDrawer(row) {
  currentAsset.value = row
  drawerVisible.value = true
  try {
    const res = await getAssetDetail(row.assetId)
    if (res.code === 200 && res.data) currentAsset.value = res.data
  } catch (e) { console.error('资产详情加载失败', e) }
  try {
    const logRes = await getAssetLogs(row.assetId)
    if (logRes.code === 200) assetLogs.value = logRes.data || []
  } catch (e) { assetLogs.value = [] }
}

function statusTag(status) {
  return { '0': 'success', '1': 'danger', '2': 'warning', '3': 'info' }[status] || 'info'
}
function statusName(status) {
  return { '0': '正常', '1': '故障', '2': '维修中', '3': '已报废' }[status] || status
}
function assetTypeName(type) {
  return { 'device': '设备', 'software': '软件', 'consumable': '耗材', 'other': '其他' }[type] || type
}
function healthClass(score) {
  if (!score) return 'status-muted'
  if (score >= 80) return 'status-normal'
  if (score >= 60) return 'status-repair'
  return 'status-fault'
}
function healthColor(score) {
  if (!score) return '#94a3b8'
  if (score >= 80) return '#10b981'
  if (score >= 60) return '#f59e0b'
  return '#ef4444'
}
function isWarrantyExpiring(dateStr) {
  if (!dateStr) return false
  const diff = (new Date(dateStr) - new Date()) / (1000 * 60 * 60 * 24)
  return diff > 0 && diff <= 30
}
function formatDate(dateStr) {
  if (!dateStr) return '-'
  return dateStr.substring ? dateStr.substring(0, 10) : String(dateStr).substring(0, 10)
}

onMounted(() => { loadData() })
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-title::before {
  content: '';
  display: block;
  width: 4px;
  height: 20px;
  background: var(--primary-gradient);
  border-radius: 2px;
}

.search-bar {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 16px 20px;
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
  box-shadow: var(--shadow-sm);
  margin-bottom: 16px;
}

.table-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 0;
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.asset-icon {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  background: var(--bg-page);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-light);
  flex-shrink: 0;
}

.status-muted { color: var(--text-muted); }

.asset-detail {
  padding: 0 4px;
}

.detail-hero {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 16px;
}

.detail-hero-icon {
  width: 52px;
  height: 52px;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-light);
  flex-shrink: 0;
}

.detail-hero-info {
  flex: 1;
}

.detail-name {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

.health-score-card {
  background: linear-gradient(135deg, #f0fdf4, #ecfdf5);
  border-radius: var(--radius-lg);
  padding: 20px;
  text-align: center;
  margin-bottom: 16px;
}

.health-label {
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 4px;
}

.health-value {
  font-size: 52px;
  font-weight: 800;
  line-height: 1.2;
}

.health-unit {
  font-size: 18px;
  color: var(--text-muted);
}

.detail-section {
  margin-top: 16px;
}

.detail-section-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.qr-box {
  display: flex;
  align-items: center;
  gap: 16px;
}

.qr-placeholder {
  min-width: 80px;
  height: 80px;
  background: var(--bg-page);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  color: var(--text-muted);
  padding: 4px;
  word-break: break-all;
}
</style>
