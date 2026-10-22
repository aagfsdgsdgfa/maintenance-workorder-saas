<template>
  <div class="app-container dashboard-container">
    <!-- 顶部数字卡片 -->
    <el-row :gutter="20" class="summary-cards">
      <el-col :span="6">
        <div class="card card-blue">
          <div class="card-value">{{ summary.assetTotal || 0 }}</div>
          <div class="card-label">总资产数</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="card card-green">
          <div class="card-value">{{ summary.monthInspectionCount || 0 }}</div>
          <div class="card-label">本月巡检</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="card card-purple">
          <div class="card-value">{{ summary.workOrderCompleteRate || 0 }}%</div>
          <div class="card-label">工单完成率</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="card card-red">
          <div class="card-value">{{ summary.faultRate || 0 }}%</div>
          <div class="card-label">故障率</div>
        </div>
      </el-col>
    </el-row>

    <!-- 次要指标行 -->
    <el-row :gutter="20" class="summary-cards sub-cards">
      <el-col :span="6">
        <div class="card card-sub">
          <div class="card-value">{{ summary.assetNormal || 0 }}</div>
          <div class="card-label">正常资产</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="card card-sub">
          <div class="card-value">{{ summary.assetFault || 0 }}</div>
          <div class="card-label">故障资产</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="card card-sub">
          <div class="card-value">{{ summary.monthWorkOrderCount || 0 }}</div>
          <div class="card-label">本月工单</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="card card-sub">
          <div class="card-value">{{ summary.monthAssetNew || 0 }}</div>
          <div class="card-label">本月新增资产</div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 健康度饼图 -->
      <el-col :span="10">
        <div class="chart-card">
          <div class="chart-title">资产健康度分布</div>
          <div v-loading="healthLoading" class="chart-body">
            <div v-if="healthData.length === 0 && !healthLoading" class="chart-empty">暂无数据</div>
            <div ref="healthChartRef" class="chart-canvas" :style="{ height: '300px' }"></div>
          </div>
        </div>
      </el-col>
      <!-- 趋势折线图 -->
      <el-col :span="14">
        <div class="chart-card">
          <div class="chart-title">巡检与工单趋势（近30天）</div>
          <div v-loading="trendLoading" class="chart-body">
            <div v-if="trendData.length === 0 && !trendLoading" class="chart-empty">暂无数据</div>
            <div ref="trendChartRef" class="chart-canvas" :style="{ height: '300px' }"></div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 最新工单列表 -->
    <el-row :gutter="20" class="table-row">
      <el-col :span="24">
        <div class="chart-card">
          <div class="chart-title">数据概览</div>
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

    <!-- 刷新按钮 -->
    <el-row class="refresh-row">
      <el-button type="primary" icon="el-icon-refresh" size="small" @click="loadAllData" :loading="summaryLoading">
        刷新数据
      </el-button>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getDashboardSummary, getDashboardHealth, getDashboardTrend } from "@/api/zw/dashboard"

export default {
  name: "ZwDashboard",
  data() {
    return {
      summaryLoading: false,
      healthLoading: false,
      trendLoading: false,
      summary: {},
      healthData: [],
      trendData: [],
      healthChart: null,
      trendChart: null,
      healthChartRef: null,
      trendChartRef: null
    }
  },
  mounted() {
    this.healthChartRef = this.$refs.healthChartRef
    this.trendChartRef = this.$refs.trendChartRef
    this.loadAllData()
    // 监听窗口变化，重绘图表
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts)
    if (this.healthChart) {
      this.healthChart.dispose()
    }
    if (this.trendChart) {
      this.trendChart.dispose()
    }
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
      }).catch(() => {
        this.summaryLoading = false
      })
    },
    loadHealth() {
      this.healthLoading = true
      getDashboardHealth().then(response => {
        this.healthData = response.data || []
        this.healthLoading = false
        this.$nextTick(() => {
          this.renderHealthChart()
        })
      }).catch(() => {
        this.healthLoading = false
      })
    },
    loadTrend() {
      this.trendLoading = true
      getDashboardTrend().then(response => {
        this.trendData = response.data || []
        this.trendLoading = false
        this.$nextTick(() => {
          this.renderTrendChart()
        })
      }).catch(() => {
        this.trendLoading = false
      })
    },
    renderHealthChart() {
      if (!this.healthChartRef) return
      if (this.healthChart) {
        this.healthChart.dispose()
      }
      this.healthChart = echarts.init(this.healthChartRef)

      // 颜色映射
      const colorMap = {
        '优': '#67C23A',
        '良': '#409EFF',
        '中': '#E6A23C',
        '差': '#F56C6C',
        '优秀': '#67C23A',
        '良好': '#409EFF',
        '一般': '#E6A23C',
        '较差': '#F56C6C'
      }

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: '5%',
          top: 'center',
          textStyle: { color: '#666' }
        },
        series: [{
          name: '健康度',
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['35%', '50%'],
          avoidLabelOverlap: false,
          label: {
            show: false,
            position: 'outside'
          },
          labelLine: {
            show: false
          },
          data: this.healthData.map(item => ({
            name: item.name || '未知',
            value: item.value || 0,
            itemStyle: {
              color: colorMap[item.name] || '#909399'
            }
          }))
        }]
      }
      this.healthChart.setOption(option)
    },
    renderTrendChart() {
      if (!this.trendChartRef) return
      if (this.trendChart) {
        this.trendChart.dispose()
      }
      this.trendChart = echarts.init(this.trendChartRef)

      const dates = this.trendData.map(item => item.date || '')
      const inspectionCounts = this.trendData.map(item => item.inspectionCount || 0)
      const workOrderCounts = this.trendData.map(item => item.workOrderCount || 0)

      const option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['巡检数量', '工单数量'],
          textStyle: { color: '#666' }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates,
          axisLabel: {
            color: '#666',
            interval: Math.floor(dates.length / 6)
          },
          axisLine: { lineStyle: { color: '#ddd' } }
        },
        yAxis: {
          type: 'value',
          axisLabel: { color: '#666' },
          axisLine: { lineStyle: { color: '#ddd' } },
          splitLine: { lineStyle: { color: '#f0f0f0' } }
        },
        series: [
          {
            name: '巡检数量',
            type: 'line',
            smooth: true,
            data: inspectionCounts,
            itemStyle: { color: '#409EFF' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(64,158,255,0.3)' },
                { offset: 1, color: 'rgba(64,158,255,0.05)' }
              ])
            }
          },
          {
            name: '工单数量',
            type: 'line',
            smooth: true,
            data: workOrderCounts,
            itemStyle: { color: '#67C23A' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(103,194,58,0.3)' },
                { offset: 1, color: 'rgba(103,194,58,0.05)' }
              ])
            }
          }
        ]
      }
      this.trendChart.setOption(option)
    },
    resizeCharts() {
      if (this.healthChart) {
        this.healthChart.resize()
      }
      if (this.trendChart) {
        this.trendChart.resize()
      }
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  background: #f5f7fa;
  min-height: calc(100vh - 140px);
}

.summary-cards {
  margin-bottom: 16px;
}

.sub-cards {
  margin-bottom: 20px;
}

.card {
  border-radius: 8px;
  padding: 24px 20px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s;
}

.card:hover {
  transform: translateY(-2px);
}

.card-blue { background: linear-gradient(135deg, #409EFF, #66b1ff); color: #fff; }
.card-green { background: linear-gradient(135deg, #67C23A, #85ce61); color: #fff; }
.card-purple { background: linear-gradient(135deg, #845ef7, #a779f7); color: #fff; }
.card-red { background: linear-gradient(135deg, #F56C6C, #f78989); color: #fff; }
.card-sub { background: #fff; border: 1px solid #ebeef5; }

.card-sub .card-value { color: #303133; }
.card-sub .card-label { color: #909399; }

.card-value {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
  font-family: 'Helvetica Neue', Arial, sans-serif;
}

.card-label {
  font-size: 14px;
  opacity: 0.9;
}

.card-sub .card-label {
  opacity: 1;
}

.chart-row {
  margin-bottom: 16px;
}

.chart-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.chart-body {
  position: relative;
}

.chart-empty {
  text-align: center;
  color: #909399;
  padding: 80px 0;
  font-size: 14px;
}

.chart-canvas {
  width: 100%;
}

.table-row {
  margin-bottom: 16px;
}

.overview-descriptions {
  margin-top: 8px;
}

.refresh-row {
  text-align: center;
  margin-bottom: 20px;
}
</style>
