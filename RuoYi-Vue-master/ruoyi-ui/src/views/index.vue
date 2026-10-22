<template>
  <div class="app-container home">
    <!-- 页面标题区 -->
    <div class="page-header">
      <div class="header-left">
        <div class="header-title">工作台</div>
        <div class="header-sub">{{ greeting }}，{{ nickName }}，祝您工作顺利！</div>
      </div>
      <div class="header-right">
        <span class="header-date">{{ currentDate }}</span>
      </div>
    </div>

    <!-- 快捷操作栏 -->
    <div class="quick-actions">
      <div class="quick-actions-inner">
        <span class="quick-label"><i class="el-icon-menu"></i> 快捷操作</span>
        <el-button size="small" icon="el-icon-plus" type="primary" plain @click="$router.push('/zw/asset')">新增资产</el-button>
        <el-button size="small" icon="el-icon-document" type="primary" plain @click="$router.push('/zw/workOrder')">新建工单</el-button>
        <el-button size="small" icon="el-icon-search" type="primary" plain @click="$router.push('/zw/inspection')">巡检任务</el-button>
        <el-button size="small" icon="el-icon-s-order" type="primary" plain @click="$router.push('/zw/repairApply')">报修申请</el-button>
        <el-button size="small" icon="el-icon-bell" type="primary" plain @click="$router.push('/zw/notification')">发送通知</el-button>
      </div>
    </div>

    <!-- 顶部主卡片 -->
    <el-row :gutter="16" class="summary-cards">
      <el-col :xs="12" :sm="6">
        <div class="card card-blue" @mouseenter="hoverCard($event)" @mouseleave="leaveCard($event)">
          <div class="card-stripe stripe-blue"></div>
          <div class="card-icon"><i class="el-icon-s-data"></i></div>
          <div class="card-body">
            <div class="card-value">{{ summary.assetTotal || 0 }}</div>
            <div class="card-label">总资产数</div>
            <div class="card-trend up"><i class="el-icon-top"></i> {{ summary.monthAssetNew || 0 }} 本月新增</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="card card-green" @mouseenter="hoverCard($event)" @mouseleave="leaveCard($event)">
          <div class="card-stripe stripe-green"></div>
          <div class="card-icon"><i class="el-icon-s-check"></i></div>
          <div class="card-body">
            <div class="card-value">{{ summary.monthInspectionCount || 0 }}</div>
            <div class="card-label">本月巡检</div>
            <div class="card-trend neutral"><i class="el-icon-bottom"></i> 计划完成</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="card card-purple" @mouseenter="hoverCard($event)" @mouseleave="leaveCard($event)">
          <div class="card-stripe stripe-purple"></div>
          <div class="card-icon"><i class="el-icon-circle-check"></i></div>
          <div class="card-body">
            <div class="card-value">{{ summary.workOrderCompleteRate || 0 }}<span class="card-unit">%</span></div>
            <div class="card-label">工单完成率</div>
            <div class="card-trend up"><i class="el-icon-top"></i> {{ summary.monthWorkOrderCount || 0 }} 本月工单</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="card card-red" @mouseenter="hoverCard($event)" @mouseleave="leaveCard($event)">
          <div class="card-stripe stripe-red"></div>
          <div class="card-icon"><i class="el-icon-warning"></i></div>
          <div class="card-body">
            <div class="card-value">{{ summary.faultRate || 0 }}<span class="card-unit">%</span></div>
            <div class="card-label">故障率</div>
            <div class="card-trend down"><i class="el-icon-bottom"></i> {{ summary.assetFault || 0 }} 故障资产</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 次要指标行 -->
    <el-row :gutter="16" class="summary-cards sub-cards">
      <el-col :xs="12" :sm="6">
        <div class="card card-sub" @mouseenter="hoverCard($event)" @mouseleave="leaveCard($event)">
          <div class="card-stripe stripe-success"></div>
          <div class="card-icon sub-icon"><i class="el-icon-circle-check-outline"></i></div>
          <div class="card-body">
            <div class="card-value sub-value">{{ summary.assetNormal || 0 }}</div>
            <div class="card-label sub-label">正常资产</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="card card-sub" @mouseenter="hoverCard($event)" @mouseleave="leaveCard($event)">
          <div class="card-stripe stripe-danger"></div>
          <div class="card-icon sub-icon"><i class="el-icon-error-outline"></i></div>
          <div class="card-body">
            <div class="card-value sub-value">{{ summary.assetFault || 0 }}</div>
            <div class="card-label sub-label">故障资产</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="card card-sub" @mouseenter="hoverCard($event)" @mouseleave="leaveCard($event)">
          <div class="card-stripe stripe-accent"></div>
          <div class="card-icon sub-icon"><i class="el-icon-document-copy"></i></div>
          <div class="card-body">
            <div class="card-value sub-value">{{ summary.monthWorkOrderCount || 0 }}</div>
            <div class="card-label sub-label">本月工单</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="card card-sub" @mouseenter="hoverCard($event)" @mouseleave="leaveCard($event)">
          <div class="card-stripe stripe-blue"></div>
          <div class="card-icon sub-icon"><i class="el-icon-circle-plus-outline"></i></div>
          <div class="card-body">
            <div class="card-value sub-value">{{ summary.monthAssetNew || 0 }}</div>
            <div class="card-label sub-label">本月新增资产</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :sm="10">
        <div class="chart-card">
          <div class="chart-title">
            <span class="title-dot dot-teal"></span>
            资产健康度分布
          </div>
          <div v-loading="healthLoading" class="chart-body">
            <div v-if="healthData.length === 0 && !healthLoading" class="chart-empty">暂无数据</div>
            <div ref="healthChartRef" class="chart-canvas" :style="{ height: '280px' }"></div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="14">
        <div class="chart-card">
          <div class="chart-title">
            <span class="title-dot dot-blue"></span>
            巡检与工单趋势（近30天）
          </div>
          <div v-loading="trendLoading" class="chart-body">
            <div v-if="trendData.length === 0 && !trendLoading" class="chart-empty">暂无数据</div>
            <div ref="trendChartRef" class="chart-canvas" :style="{ height: '280px' }"></div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 数据概览 -->
    <el-row :gutter="16" class="table-row">
      <el-col :span="24">
        <div class="chart-card">
          <div class="chart-title">
            <span class="title-dot dot-accent"></span>
            数据概览
            <el-button type="primary" size="mini" icon="el-icon-refresh"
              style="float:right; background:#1B3A6B; border-color:#1B3A6B"
              @click="loadAllData" :loading="summaryLoading">
              刷新数据
            </el-button>
          </div>
          <el-descriptions :column="4" border class="overview-descriptions">
            <el-descriptions-item label="总资产数">{{ summary.assetTotal || 0 }}</el-descriptions-item>
            <el-descriptions-item label="正常资产">{{ summary.assetNormal || 0 }}</el-descriptions-item>
            <el-descriptions-item label="故障资产">{{ summary.assetFault || 0 }}</el-descriptions-item>
            <el-descriptions-item label="本月新增">{{ summary.monthAssetNew || 0 }}</el-descriptions-item>
            <el-descriptions-item label="本月巡检">{{ summary.monthInspectionCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="本月工单">{{ summary.monthWorkOrderCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="工单完成率">{{ summary.workOrderCompleteRate || 0 }}%</el-descriptions-item>
            <el-descriptions-item label="故障率">{{ summary.faultRate || 0 }}%</el-descriptions-item>
          </el-descriptions>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getDashboardSummary, getDashboardHealth, getDashboardTrend } from "@/api/zw/mock"

export default {
  name: "Index",
  data() {
    return {
      summaryLoading: false,
      healthLoading: false,
      trendLoading: false,
      summary: {},
      healthData: [],
      trendData: [],
      healthChart: null,
      trendChart: null
    }
  },
  computed: {
    nickName() {
      return this.$store.getters.nickName || '管理员'
    },
    greeting() {
      const h = new Date().getHours()
      if (h < 12) return '上午好'
      if (h < 14) return '中午好'
      if (h < 18) return '下午好'
      return '晚上好'
    },
    currentDate() {
      return new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
    }
  },
  mounted() {
    this.loadAllData()
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts)
    if (this.healthChart) this.healthChart.dispose()
    if (this.trendChart) this.trendChart.dispose()
  },
  methods: {
    loadAllData() {
      this.loadSummary()
      this.loadHealth()
      this.loadTrend()
    },
    loadSummary() {
      this.summaryLoading = true
      getDashboardSummary().then(response => {
        this.summary = response.data || {}
        this.summaryLoading = false
      }).catch(() => { this.summaryLoading = false })
    },
    loadHealth() {
      this.healthLoading = true
      getDashboardHealth().then(response => {
        this.healthData = response.data || []
        this.healthLoading = false
        this.$nextTick(() => { this.renderHealthChart() })
      }).catch(() => { this.healthLoading = false })
    },
    loadTrend() {
      this.trendLoading = true
      getDashboardTrend().then(response => {
        this.trendData = response.data || []
        this.trendLoading = false
        this.$nextTick(() => { this.renderTrendChart() })
      }).catch(() => { this.trendLoading = false })
    },
    hoverCard(e) {
      e.currentTarget.style.transform = 'translateY(-3px)'
      e.currentTarget.style.boxShadow = '0 8px 32px rgba(27,58,107,0.15)'
    },
    leaveCard(e) {
      e.currentTarget.style.transform = 'translateY(0)'
      e.currentTarget.style.boxShadow = '0 2px 12px rgba(27,58,107,0.08)'
    },
    renderHealthChart() {
      const el = this.$refs.healthChartRef
      if (!el) return
      if (this.healthChart) this.healthChart.dispose()
      this.healthChart = echarts.init(el)
      const colorMap = {
        '优': '#0EA5E9', '优秀': '#0EA5E9',
        '良': '#3B82F6', '良好': '#3B82F6',
        '中': '#F59E0B', '一般': '#F59E0B',
        '差': '#EF4444', '较差': '#EF4444'
      }
      this.healthChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)', backgroundColor: '#fff', borderColor: '#E2E8F0', borderWidth: 1, textStyle: { color: '#1A1F36' } },
        legend: { orient: 'vertical', right: '5%', top: 'center', textStyle: { color: '#6B7A99' } },
        series: [{
          name: '健康度', type: 'pie', radius: ['42%', '72%'],
          center: ['35%', '50%'], avoidLabelOverlap: false,
          label: { show: false },
          labelLine: { show: false },
          data: this.healthData.map(item => ({
            name: item.name || '未知',
            value: item.value || 0,
            itemStyle: { color: colorMap[item.name] || '#6B7A99' }
          }))
        }]
      })
    },
    renderTrendChart() {
      const el = this.$refs.trendChartRef
      if (!el) return
      if (this.trendChart) this.trendChart.dispose()
      this.trendChart = echarts.init(el)
      const dates = this.trendData.map(item => item.date || '')
      const inspectionCounts = this.trendData.map(item => item.inspectionCount || 0)
      const workOrderCounts = this.trendData.map(item => item.workOrderCount || 0)
      this.trendChart.setOption({
        tooltip: { trigger: 'axis', backgroundColor: '#fff', borderColor: '#E2E8F0', borderWidth: 1, textStyle: { color: '#1A1F36' } },
        legend: { data: ['巡检数量', '工单数量'], textStyle: { color: '#6B7A99' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category', boundaryGap: false, data: dates,
          axisLabel: { color: '#6B7A99', interval: Math.floor(dates.length / 6) },
          axisLine: { lineStyle: { color: '#E2E8F0' } },
          splitLine: { show: false }
        },
        yAxis: {
          type: 'value', axisLabel: { color: '#6B7A99' },
          axisLine: { lineStyle: { color: '#E2E8F0' } },
          splitLine: { lineStyle: { color: '#F0F2F8' } }
        },
        series: [
          {
            name: '巡检数量', type: 'line', smooth: true, data: inspectionCounts,
            itemStyle: { color: '#3B82F6' },
            areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(59,130,246,0.25)' },
              { offset: 1, color: 'rgba(59,130,246,0.02)' }
            ])}
          },
          {
            name: '工单数量', type: 'line', smooth: true, data: workOrderCounts,
            itemStyle: { color: '#0EA5E9' },
            areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(14,165,233,0.25)' },
              { offset: 1, color: 'rgba(14,165,233,0.02)' }
            ])}
          }
        ]
      })
    },
    resizeCharts() {
      if (this.healthChart) this.healthChart.resize()
      if (this.trendChart) this.trendChart.resize()
    }
  }
}
</script>

<style scoped lang="scss">
.home {
  background: #F4F7FE;
  min-height: calc(100vh - 140px);
  padding: 20px;
  font-family: "PingFang SC", "Microsoft YaHei", sans-serif;
  font-size: 13px;
  color: #1A1F36;
}

/* 页面标题区 */
.page-header {
  background: linear-gradient(135deg, #1B3A6B 0%, #2155A4 60%, #3B82F6 100%);
  border-radius: 12px;
  padding: 20px 24px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 4px 20px rgba(27,58,107,0.2);
}
.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.header-title {
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 1px;
}
.header-sub {
  font-size: 13px;
  color: rgba(255,255,255,0.75);
}
.header-right {
  display: flex;
  align-items: center;
}
.header-date {
  font-size: 12px;
  color: rgba(255,255,255,0.65);
  background: rgba(255,255,255,0.1);
  padding: 4px 12px;
  border-radius: 20px;
  border: 1px solid rgba(255,255,255,0.15);
}

/* 快捷操作栏 */
.quick-actions {
  background: #fff;
  border-radius: 12px;
  padding: 12px 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(27,58,107,0.06);
  display: flex;
  align-items: center;
}
.quick-actions-inner {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  width: 100%;
}
.quick-label {
  font-size: 13px;
  color: #6B7A99;
  margin-right: 4px;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 卡片通用 */
.card {
  border-radius: 12px;
  background: #ffffff;
  overflow: hidden;
  position: relative;
  cursor: default;
  box-shadow: 0 2px 12px rgba(27,58,107,0.08);
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

/* 顶部色条 */
.card-stripe {
  height: 4px;
  width: 100%;
}
.stripe-blue   { background: linear-gradient(90deg, #1B3A6B, #3B82F6); }
.stripe-green  { background: linear-gradient(90deg, #3B82F6, #0EA5E9); }
.stripe-purple { background: linear-gradient(90deg, #7C3AED, #A78BFA); }
.stripe-red    { background: linear-gradient(90deg, #EF4444, #F97316); }
.stripe-success { background: linear-gradient(90deg, #3B82F6, #60A5FA); }
.stripe-danger  { background: linear-gradient(90deg, #EF4444, #F87171); }
.stripe-accent  { background: linear-gradient(90deg, #1B3A6B, #0EA5E9); }

/* 主卡片内容 */
.card-body {
  padding: 18px 18px 16px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.card-icon {
  position: absolute;
  top: 16px;
  right: 16px;
  font-size: 28px;
  opacity: 0.15;
}
.card-blue   .card-icon { color: #3B82F6; }
.card-green  .card-icon { color: #0EA5E9; }
.card-purple .card-icon { color: #7C3AED; }
.card-red    .card-icon { color: #EF4444; }
.sub-icon { color: #1B3A6B; opacity: 0.12; }

.card-value {
  font-size: 32px;
  font-weight: 700;
  color: #1A1F36;
  line-height: 1.2;
  margin-bottom: 0;
  font-family: "DIN Alternate", "Helvetica Neue", Arial, sans-serif;
}
.card-unit { font-size: 18px; font-weight: 600; }
.card-label {
  font-size: 13px;
  color: #6B7A99;
  letter-spacing: 1px;
}
.card-trend {
  font-size: 11px;
  font-weight: 500;
  margin-top: 6px;
  display: flex;
  align-items: center;
  gap: 2px;
}
.card-trend.up   { color: #10B981; }
.card-trend.down { color: #EF4444; }
.card-trend.neutral { color: #6B7A99; }

/* 次要卡片 */
.sub-value { font-size: 26px; color: #1B3A6B; }
.sub-label { font-size: 12px; color: #6B7A99; }

/* 布局 */
.summary-cards { margin-bottom: 16px; }
.sub-cards { margin-bottom: 20px; }

/* 图表卡片 */
.chart-row { margin-bottom: 16px; }
.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(27,58,107,0.08);
}
.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: #1B3A6B;
  margin-bottom: 14px;
  padding-bottom: 10px;
  border-bottom: 1px solid #F0F2F8;
  display: flex;
  align-items: center;
  gap: 8px;
}
.title-dot {
  display: inline-block;
  width: 8px; height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}
.dot-teal   { background: #0EA5E9; }
.dot-blue   { background: #3B82F6; }
.dot-accent { background: #0EA5E9; }
.chart-body { position: relative; }
.chart-empty {
  text-align: center;
  color: #B0B8C8;
  padding: 80px 0;
  font-size: 14px;
}
.chart-canvas { width: 100%; }

/* 数据概览 */
.table-row { margin-bottom: 20px; }
.overview-descriptions {
  margin-top: 8px;
  ::v-deep .el-descriptions-item__label {
    color: #6B7A99;
    font-size: 13px;
  }
  ::v-deep .el-descriptions-item__content {
    color: #1A1F36;
    font-size: 14px;
    font-weight: 600;
  }
}
</style>
