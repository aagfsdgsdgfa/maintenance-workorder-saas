<template>
  <view class="history-page">
    <!-- 日期选择器 -->
    <view class="date-filter">
      <view class="date-nav">
        <text class="nav-arrow" @click="changeMonth(-1)">‹</text>
        <text class="date-label">{{ currentYear }}/{{ String(currentMonth).padStart(2, '0') }}</text>
        <text class="nav-arrow" @click="changeMonth(1)">›</text>
      </view>
    </view>

    <!-- 加载中 -->
    <view class="loading-state" v-if="loading">
      <text class="loading-icon">-</text>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 空状态 -->
    <view class="empty-state" v-else-if="groupedList.length === 0">
      <text class="empty-icon">[</text>
      <text class="empty-text">本月暂无打卡记录</text>
    </view>

    <!-- 分组列表 -->
    <view class="list-container" v-else>
      <view
        class="day-group"
        v-for="group in groupedList"
        :key="group.date"
      >
        <view class="day-header">
          <text class="day-date">{{ formatDateLabel(group.date) }}</text>
          <text class="day-week">{{ getWeekDay(group.date) }}</text>
          <text class="day-count">{{ group.items.length }}次</text>
        </view>

        <view
          class="record-item"
          v-for="item in group.items"
          :key="item.checkId || item.id"
          @click="goToDetail(item)"
        >
          <view class="record-type" :class="item.checkType">
            <text class="type-icon">{{ item.checkType === 'arrival' ? 'A' : 'D' }}</text>
            <text class="type-text">{{ item.checkType === 'arrival' ? '到达' : '离开' }}</text>
          </view>

          <view class="record-info">
            <view class="info-row">
              <text class="info-label">打卡时间</text>
              <text class="info-value">{{ item.checkTime || item.createTime }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">打卡地点</text>
              <text class="info-value location">{{ item.address || '未知位置' }}</text>
            </view>
            <view class="info-row" v-if="item.workOrderNo">
              <text class="info-label">关联工单</text>
              <text class="info-value order">{{ item.workOrderNo }}</text>
            </view>
          </view>

          <view class="record-photo" v-if="item.photoUrl" @click.stop="previewImage(item.photoUrl)">
            <image class="photo-thumb" :src="item.photoUrl" mode="aspectFill" lazy-load></image>
          </view>
        </view>
      </view>
    </view>

    <!-- 加载更多 -->
    <view class="load-more" v-if="hasMore && !loading" @click="loadMore">
      <text class="load-more-text">加载更多</text>
    </view>
  </view>
</template>

<script>
import { getCheckInList } from '@/api/request.js'

export default {
  data() {
    return {
      currentYear: new Date().getFullYear(),
      currentMonth: new Date().getMonth() + 1,
      pageNum: 1,
      pageSize: 20,
      list: [],
      loading: false,
      hasMore: false
    }
  },
  computed: {
    groupedList() {
      const grouped = {}
      this.list.forEach(item => {
        const dateStr = (item.checkTime || item.createTime || '').substring(0, 10)
        if (!grouped[dateStr]) {
          grouped[dateStr] = { date: dateStr, items: [] }
        }
        grouped[dateStr].items.push(item)
      })
      return Object.values(grouped).sort((a, b) => b.date.localeCompare(a.date))
    }
  },
  onLoad() {
    this.loadData()
  },
  onPullDownRefresh() {
    this.pageNum = 1
    this.list = []
    this.loadData().finally(() => uni.stopPullDownRefresh())
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const engineerId = this.userInfo?.userId || uni.getStorageSync('userInfo')?.userId
        const monthStart = `${this.currentYear}-${String(this.currentMonth).padStart(2, '0')}-01`
        const nextMonth = this.currentMonth === 12 ? 1 : this.currentMonth + 1
        const nextYear = this.currentMonth === 12 ? this.currentYear + 1 : this.currentYear
        const monthEnd = `${nextYear}-${String(nextMonth).padStart(2, '0')}-01`

        const res = await getCheckInList({
          engineerId,
          startDate: monthStart,
          endDate: monthEnd,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        })

        const rows = res.rows || res.data?.rows || []
        if (this.pageNum === 1) {
          this.list = rows
        } else {
          this.list = [...this.list, ...rows]
        }
        this.hasMore = rows.length >= this.pageSize
      } catch (e) {
        uni.showToast({ title: '加载打卡历史失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },

    loadMore() {
      if (!this.hasMore) return
      this.pageNum++
      this.loadData()
    },

    changeMonth(delta) {
      let month = this.currentMonth + delta
      let year = this.currentYear
      if (month > 12) {
        month = 1
        year++
      } else if (month < 1) {
        month = 12
        year--
      }
      this.currentYear = year
      this.currentMonth = month
      this.pageNum = 1
      this.list = []
      this.loadData()
    },

    formatDateLabel(dateStr) {
      const [y, m, d] = dateStr.split('-')
      const today = new Date()
      const yesterday = new Date(today)
      yesterday.setDate(today.getDate() - 1)

      const tStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
      const yStr = `${yesterday.getFullYear()}-${String(yesterday.getMonth() + 1).padStart(2, '0')}-${String(yesterday.getDate()).padStart(2, '0')}`

      if (dateStr === tStr) return '今天'
      if (dateStr === yStr) return '昨天'
      return `${parseInt(m)}月${parseInt(d)}日`
    },

    getWeekDay(dateStr) {
      const day = new Date(dateStr).getDay()
      const weeks = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      return weeks[day]
    },

    previewImage(url) {
      uni.previewImage({ urls: [url], current: url })
    },

    goToDetail(item) {
      // 可扩展：跳转打卡详情
    }
  }
}
</script>

<style scoped>
.history-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 40rpx;
}

.date-filter {
  background: #fff;
  padding: 24rpx 32rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  border-bottom: 1rpx solid #ebeef5;
  position: sticky;
  top: 0;
  z-index: 10;
}

.date-nav {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.nav-arrow {
  font-size: 48rpx;
  color: #2563eb;
  padding: 8rpx 16rpx;
}

.date-label {
  font-size: 32rpx;
  font-weight: bold;
  color: #303133;
  min-width: 160rpx;
  text-align: center;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 0;
}

.loading-icon {
  font-size: 64rpx;
}

.loading-text {
  font-size: 28rpx;
  color: #909399;
  margin-top: 16rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}

.empty-icon {
  font-size: 100rpx;
  margin-bottom: 24rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #909399;
}

.list-container {
  padding: 24rpx;
}

.day-group {
  margin-bottom: 32rpx;
}

.day-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.day-date {
  font-size: 30rpx;
  font-weight: bold;
  color: #303133;
}

.day-week {
  font-size: 24rpx;
  color: #909399;
}

.day-count {
  font-size: 24rpx;
  color: #2563eb;
  background: #f0f1ff;
  padding: 4rpx 12rpx;
  border-radius: 12rpx;
}

.record-item {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  display: flex;
  gap: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.record-type {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 100rpx;
}

.type-icon {
  font-size: 40rpx;
  margin-bottom: 8rpx;
}

.type-text {
  font-size: 24rpx;
  color: #606266;
}

.record-type.arrival .type-text {
  color: #67c23a;
}

.record-type.departure .type-text {
  color: #e6a23c;
}

.record-info {
  flex: 1;
}

.info-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 8rpx;
}

.info-label {
  font-size: 24rpx;
  color: #909399;
  min-width: 120rpx;
}

.info-value {
  font-size: 26rpx;
  color: #303133;
  flex: 1;
}

.info-value.location {
  font-size: 24rpx;
  color: #606266;
}

.info-value.order {
  font-size: 24rpx;
  color: #2563eb;
}

.record-photo {
  width: 120rpx;
  height: 120rpx;
  border-radius: 12rpx;
  overflow: hidden;
  flex-shrink: 0;
}

.photo-thumb {
  width: 100%;
  height: 100%;
}

.load-more {
  text-align: center;
  padding: 32rpx;
}

.load-more-text {
  font-size: 28rpx;
  color: #2563eb;
}
</style>
