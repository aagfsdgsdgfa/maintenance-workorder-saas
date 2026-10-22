<template>
  <div class="workbench">
    <!-- 顶部欢迎横幅 -->
    <div class="hero-banner fade-in-up">
      <div class="hero-decoration">
        <div class="hero-deco-circle circle-1"></div>
        <div class="hero-deco-circle circle-2"></div>
      </div>
      <div class="hero-left">
        <div class="hero-greeting">
          {{ greeting }}，<span class="hero-name">{{ userInfo.nickName || '客户' }}</span>
        </div>
        <div class="hero-sub">
          <span class="hero-date">{{ currentDate }}</span>
          <span class="hero-divider-dot">·</span>
          <span class="hero-status">
            <span class="status-dot" :class="hasUrgent ? 'dot-danger' : 'dot-success'"></span>
            {{ hasUrgent ? '有紧急待办' : '设备运行稳定' }}
          </span>
        </div>
      </div>
      <div class="hero-right">
        <div class="hero-stat">
          <span class="stat-num">{{ dash.assetSummary?.total || 0 }}</span>
          <span class="stat-label">台设备</span>
        </div>
        <div class="hero-divider"></div>
        <div class="hero-stat">
          <span class="stat-num">{{ dash.serviceSummary?.monthWorkOrderCount || 0 }}</span>
          <span class="stat-label">本月工单</span>
        </div>
      </div>
    </div>

    <!-- 指标卡片区 -->
    <el-row :gutter="16" class="metric-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="metric-card fade-in-up fade-in-up-delay-1">
          <div class="metric-icon-wrap" style="background: #eff6ff;">
            <el-icon style="color:#2563EB;font-size:22px"><Box /></el-icon>
          </div>
          <div class="metric-title">设备总数</div>
          <div class="metric-value">
            {{ dash.assetSummary?.total || 0 }}
            <span class="metric-unit">台</span>
          </div>
          <div class="metric-sub">
            <span class="sub-stat normal">
              <span class="sub-dot" style="background:#10b981"></span>
              正常 {{ dash.assetSummary?.normal || 0 }}
            </span>
            <span class="sub-stat fault">
              <span class="sub-dot" style="background:#ef4444"></span>
              故障 {{ dash.assetSummary?.fault || 0 }}
            </span>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="metric-card success fade-in-up fade-in-up-delay-2">
          <div class="metric-icon-wrap" style="background: #f0fdf4;">
            <el-icon style="color:#10b981;font-size:22px"><Tickets /></el-icon>
          </div>
          <div class="metric-title">本月工单</div>
          <div class="metric-value">
            {{ dash.serviceSummary?.monthWorkOrderCount || 0 }}
            <span class="metric-unit">单</span>
          </div>
          <div class="metric-sub">
            <span class="sub-stat">
              已完成 {{ dash.serviceSummary?.completedCount || 0 }}
            </span>
            <span class="sub-stat" style="margin-left:12px">
              处理中 {{ dash.serviceSummary?.processingCount || 0 }}
            </span>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="metric-card accent fade-in-up fade-in-up-delay-3">
          <div class="metric-icon-wrap" style="background: #f0fdfa;">
            <el-icon style="color:#00C9A7;font-size:22px"><Document /></el-icon>
          </div>
          <div class="metric-title">本月巡检</div>
          <div class="metric-value">
            {{ dash.serviceSummary?.monthInspectionCount || 0 }}
            <span class="metric-unit">次</span>
          </div>
          <div class="metric-sub">设备健康巡检任务</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="metric-card fade-in-up fade-in-up-delay-4" :class="hasUrgent ? 'danger' : 'warning'">
          <div class="metric-icon-wrap" :style="{ background: hasUrgent ? '#fef2f2' : '#fffbeb' }">
            <el-icon :style="{ color: hasUrgent ? '#ef4444' : '#f59e0b', fontSize: '22px' }"><Bell /></el-icon>
          </div>
          <div class="metric-title">待办事项</div>
          <div class="metric-value">
            {{ dash.serviceSummary?.processingCount || 0 }}
            <span class="metric-unit">条</span>
          </div>
          <div class="metric-sub">
            <span :class="hasUrgent ? 'status-fault' : 'status-repair'">
              {{ hasUrgent ? '⚠ 有紧急工单' : '✓ 暂无紧急事项' }}
            </span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 中部图表 + 提醒 -->
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :lg="14">
        <div class="panel-card fade-in-up" style="animation-delay: 0.25s; opacity: 0;">
          <div class="panel-title">
            <el-icon style="color: var(--primary-light)"><DataAnalysis /></el-icon>
            设备故障分析 (Top 5)
          </div>
          <div ref="faultChartRef" style="height: 260px"></div>
        </div>
      </el-col>
      <el-col :xs="24" :lg="10">
        <div class="reminder-card fade-in-up" style="animation-delay: 0.3s; opacity: 0; min-height: 300px;" v-if="dash.expiringContracts?.length">
          <div class="reminder-header">
            <el-icon><WarningFilled /></el-icon>
            <span>重要提醒</span>
            <el-tag type="danger" size="small" style="margin-left: auto;">{{ dash.expiringContracts.length }} 条</el-tag>
          </div>
          <div class="reminder-list">
            <div class="reminder-item" v-for="c in dash.expiringContracts" :key="c.contractId">
              <div class="reminder-item-header">
                <span class="reminder-contract-no">{{ c.contractNo }}</span>
                <el-tag size="small" type="warning">即将到期</el-tag>
              </div>
              <div class="reminder-item-body">
                到期时间：{{ c.endDate }} · 覆盖 {{ c.assetCount }} 台设备
              </div>
              <el-button type="primary" size="small" plain style="margin-top: 8px;" @click="$router.push('/contract/detail/' + c.contractId)">
                查看合同
              </el-button>
            </div>
          </div>
        </div>
        <div class="panel-card fade-in-up" style="animation-delay: 0.3s; opacity: 0; display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 300px;" v-else>
          <EmptyState
            icon="success"
            title="暂无重要提醒"
            description="所有合同正常运行，设备状态良好"
          />
        </div>
      </el-col>
    </el-row>

    <!-- 底部最近报修 -->
    <div class="bottom-section fade-in-up" style="animation-delay: 0.35s; opacity: 0; margin-top: 16px;">
      <div class="bottom-header">
        <span class="section-title">最近报修记录</span>
        <div style="display: flex; gap: 8px;">
          <el-button type="primary" size="small" @click="$router.push('/repair/create')">
            <el-icon><Plus /></el-icon>快速报修
          </el-button>
          <el-button size="small" @click="$router.push('/repair/list')">查看全部</el-button>
        </div>
      </div>
      <el-table
        :data="recentOrders"
        style="margin-top: 14px;"
        v-loading="orderLoading"
        :header-cell-style="{ background: '#f8fafc' }"
      >
        <el-table-column prop="orderNo" label="工单号" width="170">
          <template #default="{ row }">
            <span class="order-no">{{ row.orderNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="故障描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="assetId" label="设备ID" width="100" align="center" />
        <el-table-column label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small" style="min-width: 68px; text-align: center;">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="165" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button
              v-if="row.status === '3'"
              type="success"
              size="small"
              @click="$router.push('/repair/list?pendingEval=' + row.workOrderId)"
            >
              确认完成
            </el-button>
            <el-button
              v-else-if="row.status === '4'"
              type="info"
              size="small"
              disabled
            >
              已完成
            </el-button>
            <el-button
              v-else
              type="primary"
              link
              size="small"
              @click="$router.push('/repair/detail/' + row.workOrderId)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import * as echarts from 'echarts/core'
import { BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { getCustomerDashboard } from '@/api/zw/customer'
import { getWorkOrderList } from '@/api/zw/workOrder'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import EmptyState from '@/components/EmptyState.vue'

echarts.use([BarChart, GridComponent, TooltipComponent, CanvasRenderer])

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const dash = ref({
  assetSummary: {},
  serviceSummary: {},
  expiringContracts: []
})
const recentOrders = ref([])
const orderLoading = ref(false)
const faultChartRef = ref()

const hasUrgent = computed(() => (dash.value.serviceSummary?.processingCount || 0) > 3)

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 12) return '早上好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const currentDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
})

async function loadDashboard() {
  try {
    const res = await getCustomerDashboard()
    if (res.code === 200 && res.data) {
      dash.value = res.data
      setTimeout(initChart, 50)
    }
  } catch (e) {
    ElMessage.error('工作台数据加载失败')
  }
}

async function loadRecentOrders() {
  orderLoading.value = true
  try {
    const res = await getWorkOrderList({ pageNum: 1, pageSize: 5 })
    if (res.code === 200) {
      recentOrders.value = (res.rows || []).map(w => ({
        workOrderId: w.workOrderId,
        orderNo: w.orderNo,
        title: w.title || w.description,
        assetId: w.assetId,
        status: w.status,
        createTime: w.createTime
      }))
    }
  } catch (e) {
    ElMessage.error('工单列表加载失败')
  } finally {
    orderLoading.value = false
  }
}

function statusTagType(status) {
  const map = { '0': 'warning', '1': 'primary', '2': 'warning', '3': 'success', '4': 'info' }
  return map[status] || 'info'
}

function statusLabel(status) {
  const map = { '0': '待接单', '1': '处理中', '2': '待确认', '3': '已完成', '4': '已取消' }
  return map[status] || status
}

function initChart() {
  if (!faultChartRef.value) return
  const chart = echarts.init(faultChartRef.value)
  const data = dash.value.topFaultAssets || []

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: '#fff',
      borderColor: '#e2e8f0',
      borderWidth: 1,
      textStyle: { color: '#1e293b', fontSize: 13 },
      formatter: (params) => {
        const p = params[0]
        return `<b>${p.name}</b><br/>故障次数：<b style="color:#ef4444">${p.value}</b> 次`
      }
    },
    grid: { left: 12, right: 20, top: 10, bottom: 30, containLabel: true },
    xAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#f1f5f9', type: 'dashed' } },
      axisLabel: { color: '#94a3b8', fontSize: 12 }
    },
    yAxis: {
      type: 'category',
      data: data.map(d => d.assetName),
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#64748b', fontSize: 13, fontWeight: 500 }
    },
    series: [{
      type: 'bar',
      data: data.map(d => d.faultCount),
      itemStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 1, y2: 0,
          colorStops: [
            { offset: 0, color: '#ef4444' },
            { offset: 1, color: '#f87171' }
          ]
        },
        borderRadius: [0, 6, 6, 0]
      },
      barMaxWidth: 28,
      label: {
        show: true,
        position: 'right',
        color: '#64748b',
        fontSize: 12,
        formatter: '{c} 次'
      }
    }]
  }

  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

onMounted(() => {
  loadDashboard()
  loadRecentOrders()
})
</script>

<style scoped>
.workbench {
  max-width: 1400px;
  margin: 0 auto;
}

/* ============ 欢迎横幅 ============ */
.hero-banner {
  background: linear-gradient(135deg, #1B4B8E 0%, #2563EB 100%);
  border-radius: 16px;
  padding: 28px 32px;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  position: relative;
  overflow: hidden;
}

.hero-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.hero-deco-circle {
  position: absolute;
  border-radius: 50%;
}

.circle-1 {
  width: 280px;
  height: 280px;
  background: rgba(255, 255, 255, 0.05);
  top: -100px;
  right: -60px;
}

.circle-2 {
  width: 160px;
  height: 160px;
  background: rgba(255, 255, 255, 0.03);
  bottom: -60px;
  right: 120px;
}

.hero-left {
  position: relative;
  z-index: 1;
}

.hero-greeting {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 6px;
}

.hero-name {
  color: #00C9A7;
}

.hero-sub {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  opacity: 0.75;
}

.hero-divider-dot {
  opacity: 0.5;
}

.hero-status {
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  flex-shrink: 0;
}

.dot-success {
  background: #00C9A7;
  box-shadow: 0 0 6px rgba(0, 201, 167, 0.6);
}

.dot-danger {
  background: #ef4444;
  box-shadow: 0 0 6px rgba(239, 68, 68, 0.6);
  animation: pulse 2s infinite;
}

.hero-right {
  display: flex;
  align-items: center;
  gap: 28px;
  position: relative;
  z-index: 1;
}

.hero-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-num {
  font-size: 30px;
  font-weight: 800;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  opacity: 0.7;
  margin-top: 3px;
}

.hero-divider {
  width: 1px;
  height: 44px;
  background: rgba(255, 255, 255, 0.2);
}

/* ============ 指标卡片 ============ */
.metric-row {
  margin-top: 0 !important;
}

.metric-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(27, 75, 142, 0.08);
  border-top: 4px solid #2563EB;
  transition: all 0.2s;
  position: relative;
  overflow: hidden;
}

.metric-card::after {
  content: '';
  position: absolute;
  top: -20px;
  right: -20px;
  width: 80px;
  height: 80px;
  background: rgba(37, 99, 235, 0.03);
  border-radius: 50%;
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8rpx 32rpx rgba(27, 75, 142, 0.14);
}

.metric-card.success { border-top-color: #10b981; }
.metric-card.success::after { background: rgba(16, 185, 129, 0.03); }
.metric-card.warning { border-top-color: #f59e0b; }
.metric-card.warning::after { background: rgba(245, 158, 11, 0.03); }
.metric-card.danger { border-top-color: #ef4444; }
.metric-card.danger::after { background: rgba(239, 68, 68, 0.03); }
.metric-card.accent { border-top-color: #00C9A7; }
.metric-card.accent::after { background: rgba(0, 201, 167, 0.03); }

.metric-icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 14px;
}

.metric-title {
  font-size: 13px;
  color: #94a3b8;
  font-weight: 500;
  margin-bottom: 6px;
}

.metric-value {
  font-size: 30px;
  font-weight: 800;
  color: #0f172a;
  line-height: 1.1;
  margin-bottom: 8px;
}

.metric-unit {
  font-size: 14px;
  font-weight: 400;
  color: #94a3b8;
}

.metric-sub {
  font-size: 12px;
  color: #94a3b8;
}

.sub-stat {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.sub-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
}

.status-fault { color: #ef4444; font-weight: 600; }
.status-repair { color: #10b981; font-weight: 600; }

/* ============ 面板 ============ */
.panel-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(27, 75, 142, 0.08);
}

.panel-title {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.panel-title::before {
  content: '';
  display: block;
  width: 4px;
  height: 16px;
  background: linear-gradient(135deg, #1B4B8E, #2563EB);
  border-radius: 2px;
}

/* ============ 提醒卡片 ============ */
.reminder-card {
  background: linear-gradient(135deg, #fff7f6, #fff0ee);
  border: 1px solid #ffc0b4;
  border-radius: 16px;
  padding: 20px;
  min-height: 300px;
}

.reminder-header {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #c0392b;
  font-weight: 700;
  font-size: 15px;
  margin-bottom: 14px;
}

.reminder-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.reminder-item {
  background: #fff;
  border-radius: 10px;
  padding: 12px 14px;
  border: 1px solid #ffdcd6;
}

.reminder-item-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 4px;
}

.reminder-contract-no {
  font-weight: 600;
  color: #0f172a;
  font-size: 14px;
  font-family: monospace;
}

.reminder-item-body {
  font-size: 12px;
  color: #475569;
}

/* ============ 全好状态（已迁移至 EmptyState 组件） ============ */

/* ============ 底部区域 ============ */
.bottom-section {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(27, 75, 142, 0.08);
  margin-top: 16px;
}

.bottom-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.section-title {
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
  display: flex;
  align-items: center;
  gap: 6px;
}

.section-title::before {
  content: '';
  display: block;
  width: 4px;
  height: 16px;
  background: linear-gradient(135deg, #1B4B8E, #2563EB);
  border-radius: 2px;
}

.order-no {
  font-family: 'Courier New', monospace;
  font-size: 13px;
  color: #2563EB;
  font-weight: 600;
}

/* ============ 动画 ============ */
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}
</style>
