<template>
  <view class="inspection-list">
    <!-- 筛选Tab -->
    <view class="filter-bar">
      <view
        v-for="tab in statusTabs"
        :key="tab.value"
        :class="['filter-tab', { active: queryParams.status === tab.value }]"
        @click="changeStatus(tab.value)"
      >
        <text>{{ tab.label }}</text>
      </view>
    </view>

    <!-- 列表 -->
    <scroll-view scroll-y class="task-scroll" @scrolltolower="loadMore">
      <view class="task-list" v-if="taskList.length">
        <view
          class="task-card"
          v-for="item in taskList"
          :key="item.taskId || item.id"
          @click="goToDetail(item)"
        >
          <!-- 卡片头部 -->
          <view class="card-header">
            <view class="task-name">{{ item.workOrderTitle || item.taskName }}</view>
            <view class="status-badge" :class="'status-' + item.status">
              {{ statusMap[item.status] || '未知' }}
            </view>
          </view>

          <!-- 关联信息 -->
          <view class="card-info" v-if="item.customerName || item.assetName">
            <c-icon type="person" :size="14" color="#909399" />
            <text class="info-text">{{ item.customerName || '未关联客户' }}</text>
            <c-icon type="calendar" :size="14" color="#909399" style="margin-left:24rpx" />
            <text class="info-text">{{ item.planTime || '未安排' }}</text>
          </view>

          <!-- 进度 -->
          <view class="card-progress" v-if="item.totalPointCount > 0">
            <view class="progress-bar">
              <view class="progress-fill" :style="{ width: getProgress(item) + '%' }"></view>
            </view>
            <text class="progress-text">{{ item.inspectedCount || 0 }}/{{ item.totalPointCount }} 个点</text>
          </view>
        </view>

        <view class="load-more" v-if="hasMore" @click="loadMore">
          <text class="load-text">加载更多</text>
        </view>
        <view class="no-more" v-else>
          <text class="no-more-text">没有更多了</text>
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-else-if="!loading">
        <c-icon type="search" :size="64" color="#d1d5db" />
        <text class="empty-title">暂无巡检任务</text>
        <text class="empty-desc">暂时没有需要执行的巡检任务</text>
      </view>

      <!-- 骨架屏 -->
      <view class="skeleton-list" v-if="loading && !taskList.length">
        <view v-for="i in 4" :key="i" class="skeleton-card">
          <view class="skeleton-line long"></view>
          <view class="skeleton-line medium"></view>
          <view class="skeleton-line short"></view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { getInspectionTaskList } from '@/api/request.js'

export default {
  data() {
    return {
      loading: false,
      taskList: [],
      hasMore: false,
      queryParams: { status: '', pageNum: 1, pageSize: 10 },
      statusTabs: [
        { label: '全部', value: '' },
        { label: '待巡检', value: '0' },
        { label: '巡检中', value: '1' },
        { label: '已完成', value: '2' }
      ],
      statusMap: { '0': '待巡检', '1': '巡检中', '2': '已完成' }
    }
  },
  onShow() {
    if (!uni.getStorageSync('token')) {
      uni.reLaunch({ url: '/pages/login/login' })
      return
    }
    this.refreshList()
  },
  onPullDownRefresh() {
    this.refreshList().finally(() => uni.stopPullDownRefresh())
  },
  methods: {
    async refreshList() {
      this.queryParams.pageNum = 1
      this.hasMore = false
      await this.fetchList()
    },

    async fetchList(append = false) {
      if (this.loading) return
      this.loading = true
      try {
        const userInfo = uni.getStorageSync('userInfo')
        const engineerId = userInfo?.userId
        const res = await getInspectionTaskList({ ...this.queryParams, assignedTo: engineerId })
        const list = res.rows || res.data?.rows || []
        if (append) {
          this.taskList = [...this.taskList, ...list]
        } else {
          this.taskList = list
        }
        this.hasMore = list.length >= this.queryParams.pageSize
      } catch (e) {
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },

    async loadMore() {
      if (!this.hasMore || this.loading) return
      this.queryParams.pageNum++
      await this.fetchList(true)
    },

    changeStatus(status) {
      this.queryParams.status = status
      this.refreshList()
    },

    goToDetail(item) {
      const taskId = item.taskId || item.id
      uni.navigateTo({ url: `/pages/inspection/taskDetail?id=${taskId}` })
    },

    getProgress(item) {
      const total = item.totalPointCount || 0
      if (total === 0) return 0
      return Math.round(((item.inspectedCount || 0) / total) * 100)
    }
  }
}
</script>

<style scoped>
.inspection-list {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}

.filter-bar {
  background: #fff;
  display: flex;
  border-bottom: 1rpx solid #ebeef5;
}

.filter-tab {
  flex: 1;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  color: #606266;
  position: relative;
}

.filter-tab.active {
  color: #2563eb;
  font-weight: 600;
}

.filter-tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 48rpx;
  height: 4rpx;
  background: #2563eb;
  border-radius: 2rpx;
}

.task-scroll {
  flex: 1;
  height: calc(100vh - 88rpx);
}

.task-list {
  padding: 24rpx;
}

.task-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
  margin-bottom: 16rpx;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16rpx;
}

.task-name {
  font-size: 30rpx;
  font-weight: 600;
  color: #1a1a1a;
  flex: 1;
  margin-right: 16rpx;
  line-height: 1.4;
}

.status-badge {
  font-size: 22rpx;
  padding: 4rpx 14rpx;
  border-radius: 8rpx;
  flex-shrink: 0;
}

.status-badge.status-0 { background: #fff7ed; color: #f59e0b; }
.status-badge.status-1 { background: #eff6ff; color: #2563eb; }
.status-badge.status-2 { background: #f0fdf4; color: #10b981; }

.card-info {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
  gap: 8rpx;
}

.info-text {
  font-size: 24rpx;
  color: #909399;
}

.card-progress {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.progress-bar {
  flex: 1;
  height: 6rpx;
  background: #f0f2f5;
  border-radius: 3rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #2563eb;
  border-radius: 3rpx;
}

.progress-text {
  font-size: 22rpx;
  color: #909399;
  flex-shrink: 0;
}

.load-more, .no-more {
  text-align: center;
  padding: 32rpx;
}

.load-text { font-size: 26rpx; color: #2563eb; }
.no-more-text { font-size: 24rpx; color: #c0c4cc; }

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 160rpx 0;
  gap: 16rpx;
}

.empty-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #303133;
  margin-top: 8rpx;
}

.empty-desc {
  font-size: 26rpx;
  color: #909399;
}

.skeleton-list {
  padding: 24rpx;
}

.skeleton-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
  margin-bottom: 16rpx;
}

.skeleton-line {
  height: 28rpx;
  background: linear-gradient(90deg, #f0f2f5 25%, #e6e8eb 50%, #f0f2f5 75%);
  background-size: 200% 100%;
  border-radius: 8rpx;
  margin-bottom: 20rpx;
  animation: shimmer 1.5s infinite;
}

.skeleton-line.long { width: 65%; }
.skeleton-line.medium { width: 45%; }
.skeleton-line.short { width: 30%; }

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>
