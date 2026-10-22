<template>
  <view class="daily-report-list">
    <!-- 头部 -->
    <view class="header">
      <view class="header-content">
        <text class="header-title">我的日报</text>
        <text class="header-date">{{ todayLabel }}</text>
      </view>
      <view class="generate-btn" @click="goGenerate">
        <c-icon type="plus" :size="16" color="#fff" />
        <text class="btn-text">生成日报</text>
      </view>
    </view>

    <!-- 状态Tab -->
    <view class="tabs-wrap">
      <view class="tabs">
        <view
          v-for="tab in statusTabs"
          :key="tab.value"
          :class="['tab', { active: currentStatus === tab.value }]"
          @click="switchStatus(tab.value)"
        >
          {{ tab.label }}
          <view v-if="currentStatus === tab.value" class="tab-indicator" />
        </view>
      </view>
    </view>

    <!-- 日报列表 -->
    <scroll-view scroll-y class="report-list" @scrolltolower="loadMore">
      <view
        v-for="item in reportList"
        :key="item.id"
        class="report-card"
        @click="goEdit(item)"
      >
        <!-- 左侧日期区域 -->
        <view class="card-date">
          <text class="date-month">{{ formatMonth(item.reportDate) }}</text>
          <text class="date-day">{{ formatDay(item.reportDate) }}</text>
          <text class="date-week">{{ formatWeek(item.reportDate) }}</text>
        </view>

        <!-- 右侧内容区域 -->
        <view class="card-body">
          <view class="card-header">
            <text class="card-title">{{ item.title || '日报' }}</text>
            <view :class="['status-badge', item.status === '1' ? 'submitted' : 'draft']">
              <c-icon :type="item.status === '1' ? 'checkbox' : 'compose'" :size="12" :color="item.status === '1' ? '#52c41a' : '#fa8c16'" />
              <text class="badge-text">{{ item.status === '1' ? '已提交' : '草稿' }}</text>
            </view>
          </view>

          <text class="card-summary" v-if="item.summary">{{ item.summary }}</text>

          <!-- 底部统计 -->
          <view class="card-footer">
            <view class="stat-item">
              <c-icon type="flag" :size="14" color="#909399" />
              <text class="stat-label">工单</text>
              <text class="stat-value">{{ item.workOrderCount || 0 }}</text>
            </view>
            <view class="stat-divider" />
            <view class="stat-item">
              <c-icon type="spinnercycle" :size="14" color="#909399" />
              <text class="stat-label">巡检</text>
              <text class="stat-value">{{ item.inspectionCount || 0 }}</text>
            </view>
            <view class="stat-divider" />
            <view class="stat-item">
              <c-icon type="location" :size="14" color="#909399" />
              <text class="stat-label">打卡</text>
              <text class="stat-value">{{ item.checkInCount || 0 }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-if="reportList.length === 0 && !loading">
        <c-icon type="paperplane" :size="64" color="#c0c4cc" />
        <text class="empty-title">暂无日报</text>
        <text class="empty-desc">点击上方「生成日报」按钮开始记录</text>
      </view>

      <!-- 加载中 -->
      <view class="loading-state" v-if="loading">
        <c-icon type="spinnercycle" :size="24" color="#909399" />
        <text class="loading-text">加载中...</text>
      </view>

      <!-- 没有更多 -->
      <view class="no-more-state" v-if="!hasMore && reportList.length > 0">
        <text class="no-more-text">没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { getDailyReportList } from '@/api/request.js'

export default {
  data() {
    return {
      currentStatus: '',
      statusTabs: [
        { label: '全部', value: '' },
        { label: '草稿', value: '0' },
        { label: '已提交', value: '1' }
      ],
      reportList: [],
      loading: false,
      pageNum: 1,
      pageSize: 20,
      hasMore: true,
      todayLabel: ''
    }
  },
  onLoad() {
    const now = new Date()
    const month = String(now.getMonth() + 1).padStart(2, '0')
    const day = String(now.getDate()).padStart(2, '0')
    const weekDay = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][now.getDay()]
    this.todayLabel = `${now.getFullYear()}年${month}月${day}日 ${weekDay}`
    this.loadData()
  },
  onPullDownRefresh() {
    this.pageNum = 1
    this.reportList = []
    this.hasMore = true
    this.loadData().finally(() => uni.stopPullDownRefresh())
  },
  methods: {
    switchStatus(value) {
      this.currentStatus = value
      this.pageNum = 1
      this.reportList = []
      this.hasMore = true
      this.loadData()
    },
    async loadData() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
        if (this.currentStatus) {
          params.status = this.currentStatus
        }
        const res = await getDailyReportList(params)
        const list = res.rows || res.data?.rows || []
        if (this.pageNum === 1) {
          this.reportList = list
        } else {
          this.reportList = [...this.reportList, ...list]
        }
        this.hasMore = list.length >= this.pageSize
      } catch (e) {
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    loadMore() {
      if (this.hasMore && !this.loading) {
        this.pageNum++
        this.loadData()
      }
    },
    goGenerate() {
      uni.navigateTo({ url: '/pages/dailyReport/generate' })
    },
    goEdit(item) {
      uni.navigateTo({ url: `/pages/dailyReport/generate?id=${item.id}` })
    },
    formatMonth(dateStr) {
      if (!dateStr) return ''
      const d = new Date(dateStr)
      return `${d.getMonth() + 1}月`
    },
    formatDay(dateStr) {
      if (!dateStr) return ''
      const d = new Date(dateStr)
      return String(d.getDate()).padStart(2, '0')
    },
    formatWeek(dateStr) {
      if (!dateStr) return ''
      const d = new Date(dateStr)
      return ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][d.getDay()]
    }
  }
}
</script>

<style scoped>
.daily-report-list {
  min-height: 100vh;
  background: #f5f7fa;
}

/* 头部 */
.header {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  padding: 48rpx 32rpx 60rpx;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.header-title {
  font-size: 40rpx;
  font-weight: 600;
  color: #fff;
  letter-spacing: 1rpx;
}

.header-date {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.75);
  margin-top: 4rpx;
}

.generate-btn {
  display: flex;
  align-items: center;
  gap: 6rpx;
  background: rgba(255, 255, 255, 0.18);
  border: 1rpx solid rgba(255, 255, 255, 0.35);
  border-radius: 40rpx;
  padding: 14rpx 28rpx;
  margin-top: 8rpx;
}

.btn-text {
  font-size: 26rpx;
  color: #fff;
  font-weight: 500;
}

/* 状态Tab */
.tabs-wrap {
  background: #fff;
  border-radius: 20rpx 20rpx 0 0;
  margin-top: -24rpx;
  padding: 0 8rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.tabs {
  display: flex;
  align-items: center;
  padding: 0 24rpx;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 28rpx 0;
  font-size: 28rpx;
  color: #909399;
  position: relative;
  transition: color 0.2s;
}

.tab.active {
  color: #2563eb;
  font-weight: 600;
}

.tab-indicator {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40rpx;
  height: 4rpx;
  background: #2563eb;
  border-radius: 4rpx;
}

/* 日报列表 */
.report-list {
  height: calc(100vh - 260rpx);
  padding: 24rpx;
}

/* 日报卡片 */
.report-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
  margin-bottom: 20rpx;
  display: flex;
  gap: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  border: 1rpx solid rgba(0, 0, 0, 0.04);
}

/* 左侧日期 */
.card-date {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 80rpx;
  padding-right: 24rpx;
  border-right: 2rpx solid #f0f2f5;
}

.date-month {
  font-size: 22rpx;
  color: #909399;
  font-weight: 400;
  margin-bottom: 4rpx;
}

.date-day {
  font-size: 44rpx;
  font-weight: 700;
  color: #1d4ed8;
  line-height: 1;
}

.date-week {
  font-size: 20rpx;
  color: #c0c4cc;
  margin-top: 6rpx;
}

/* 右侧内容 */
.card-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #303133;
  flex: 1;
}

.status-badge {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 6rpx 14rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
  flex-shrink: 0;
}

.status-badge.draft {
  background: #fffbe6;
  color: #fa8c16;
}

.status-badge.submitted {
  background: #f6ffed;
  color: #52c41a;
}

.badge-text {
  font-size: 22rpx;
}

.card-summary {
  font-size: 24rpx;
  color: #909399;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 底部统计 */
.card-footer {
  display: flex;
  align-items: center;
  padding-top: 16rpx;
  border-top: 1rpx solid #f0f2f5;
  gap: 8rpx;
  margin-top: 4rpx;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6rpx;
  flex: 1;
  justify-content: center;
}

.stat-label {
  font-size: 24rpx;
  color: #909399;
}

.stat-value {
  font-size: 26rpx;
  color: #303133;
  font-weight: 600;
}

.stat-divider {
  width: 1rpx;
  height: 28rpx;
  background: #ebeef5;
  flex-shrink: 0;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 140rpx 0;
  gap: 16rpx;
}

.empty-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #909399;
  margin-top: 16rpx;
}

.empty-desc {
  font-size: 26rpx;
  color: #c0c4cc;
  margin-top: 8rpx;
}

/* 加载中 */
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  padding: 32rpx 0;
}

.loading-text {
  font-size: 24rpx;
  color: #909399;
}

/* 没有更多 */
.no-more-state {
  text-align: center;
  padding: 32rpx 0;
}

.no-more-text {
  font-size: 24rpx;
  color: #c0c4cc;
}
</style>