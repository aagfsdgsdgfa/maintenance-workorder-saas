<template>
  <view class="daily-report-detail">
    <!-- 报告头部 -->
    <view class="report-header">
      <text class="report-title">{{ report.title }}</text>
      <view class="report-meta">
        <text class="report-date">{{ report.reportDate }}</text>
        <text :class="['status-tag', report.status]">
          {{ report.status === 'submitted' ? '已提交' : '草稿' }}
        </text>
      </view>
    </view>

    <!-- 统计卡片 -->
    <view class="stats-card">
      <view class="stat-item">
        <text class="stat-num">{{ report.checkInCount || 0 }}</text>
        <text class="stat-label">签到次数</text>
      </view>
      <view class="stat-item">
        <text class="stat-num">{{ report.workOrderCount || 0 }}</text>
        <text class="stat-label">完工工单</text>
      </view>
      <view class="stat-item">
        <text class="stat-num">{{ report.inspectionCount || 0 }}</text>
        <text class="stat-label">巡检设备</text>
      </view>
      <view class="stat-item">
        <text class="stat-num">{{ report.faultCount || 0 }}</text>
        <text class="stat-label">故障上报</text>
      </view>
    </view>

    <!-- 日报内容 -->
    <view class="content-section">
      <view class="section-title">日报详情</view>

      <!-- AI生成的摘要 -->
      <view class="summary-block" v-if="report.summary">
        <text class="summary-label">摘要</text>
        <text class="summary-text">{{ report.summary }}</text>
      </view>

      <!-- 各部分内容 -->
      <view v-for="section in report.sections" :key="section.title" class="section-block">
        <view class="block-title">{{ section.title }}</view>
        <text class="block-content">{{ section.content }}</text>
      </view>
    </view>

    <!-- 签到记录详情 -->
    <view class="detail-section" v-if="report.checkIns && report.checkIns.length">
      <view class="section-title">签到详情</view>
      <view v-for="(checkIn, index) in report.checkIns" :key="index" class="check-in-item">
        <view class="check-in-type">
          <text :class="['type-tag', checkIn.checkType]">
            {{ checkIn.checkType === 'arrive' ? '到达' : '离开' }}
          </text>
        </view>
        <view class="check-in-info">
          <text class="check-in-address">{{ checkIn.address }}</text>
          <text class="check-in-time">{{ checkIn.checkTime }}</text>
        </view>
      </view>
    </view>

    <!-- 工单记录详情 -->
    <view class="detail-section" v-if="report.workOrders && report.workOrders.length">
      <view class="section-title">工单详情</view>
      <view v-for="wo in report.workOrders" :key="wo.id" class="work-order-item">
        <text class="wo-no">{{ wo.workOrderNo }}</text>
        <text class="wo-title">{{ wo.title }}</text>
        <text class="wo-result">{{ wo.resultDesc }}</text>
      </view>
    </view>

    <!-- 操作按钮 -->
    <view class="action-bar" v-if="report.status === 'draft'">
      <button class="action-btn" @click="editReport">编辑</button>
      <button class="action-btn primary" @click="submitReport">提交日报</button>
    </view>
  </view>
</template>

<script>
import { getDailyReportDetail, submitDailyReport } from '@/api/request.js'

export default {
  data() {
    return {
      id: '',
      report: {
        title: '',
        reportDate: '',
        status: 'draft',
        summary: '',
        sections: []
      }
    }
  },
  onLoad(options) {
    this.id = options.id
    if (this.id) {
      this.loadDetail()
    }
  },
  methods: {
    async loadDetail() {
      uni.showLoading({ title: '加载中...' })
      try {
        const res = await getDailyReportDetail(this.id)
        this.report = { ...this.report, ...res }
      } catch (e) {
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    },

    editReport() {
      uni.navigateTo({
        url: `/pages/dailyReport/generate?editId=${this.id}`
      })
    },

    async submitReport() {
      try {
        await submitDailyReport({
          id: this.id,
          status: 'submitted'
        })
        uni.showToast({ title: '提交成功', icon: 'success' })
        this.loadDetail()
      } catch (e) {
        uni.showToast({ title: e.msg || '提交失败', icon: 'none' })
      }
    }
  }
}
</script>

<style scoped>
.daily-report-detail {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 120rpx;
}

.report-header {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  padding: 40rpx 32rpx;
}

.report-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
  display: block;
}

.report-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16rpx;
}

.report-date {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
}

.status-tag {
  font-size: 24rpx;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.status-tag.submitted {
  background: rgba(103, 194, 58, 0.3);
}

.stats-card {
  display: flex;
  background: #fff;
  margin: 24rpx;
  border-radius: 16rpx;
  padding: 32rpx 16rpx;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-num {
  font-size: 40rpx;
  font-weight: bold;
  color: #2563eb;
  display: block;
}

.stat-label {
  font-size: 24rpx;
  color: #909399;
  margin-top: 8rpx;
  display: block;
}

.content-section,
.detail-section {
  background: #fff;
  margin: 0 24rpx 24rpx;
  border-radius: 16rpx;
  padding: 24rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #303133;
  margin-bottom: 24rpx;
  padding-bottom: 16rpx;
  border-bottom: 1rpx solid #ebeef5;
}

.summary-block {
  background: #f5f7fa;
  padding: 20rpx;
  border-radius: 12rpx;
  margin-bottom: 24rpx;
}

.summary-label {
  font-size: 24rpx;
  color: #2563eb;
  font-weight: bold;
}

.summary-text {
  font-size: 28rpx;
  color: #303133;
  line-height: 1.6;
  display: block;
  margin-top: 8rpx;
}

.section-block {
  margin-bottom: 24rpx;
}

.block-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #303133;
  margin-bottom: 12rpx;
}

.block-content {
  font-size: 26rpx;
  color: #606266;
  line-height: 1.6;
}

.check-in-item {
  display: flex;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #ebeef5;
}

.check-in-item:last-child {
  border-bottom: none;
}

.type-tag {
  font-size: 22rpx;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
}

.type-tag.arrive {
  background: #ecf5ff;
  color: #409eff;
}

.type-tag.leave {
  background: #f0f9eb;
  color: #67c23a;
}

.check-in-info {
  flex: 1;
  margin-left: 16rpx;
}

.check-in-address {
  font-size: 26rpx;
  color: #303133;
  display: block;
}

.check-in-time {
  font-size: 22rpx;
  color: #909399;
  display: block;
  margin-top: 4rpx;
}

.work-order-item {
  padding: 16rpx 0;
  border-bottom: 1rpx solid #ebeef5;
}

.work-order-item:last-child {
  border-bottom: none;
}

.wo-no {
  font-family: monospace;
  font-size: 24rpx;
  color: #909399;
  display: block;
}

.wo-title {
  font-size: 28rpx;
  color: #303133;
  font-weight: 500;
  display: block;
  margin-top: 4rpx;
}

.wo-result {
  font-size: 24rpx;
  color: #606266;
  display: block;
  margin-top: 4rpx;
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  padding: 24rpx;
  background: #fff;
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.action-btn {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  font-size: 30rpx;
  margin: 0 8rpx;
  background: #fff;
  color: #2563eb;
  border: 2rpx solid #2563eb;
}

.action-btn.primary {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  color: #fff;
  border: none;
}

.action-btn::after {
  border: none;
}
</style>
