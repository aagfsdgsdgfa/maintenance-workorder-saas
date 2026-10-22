<template>
  <view class="notification-page">
    <!-- 顶部统计条 -->
    <view class="stat-bar">
      <view class="stat-item">
        <text class="stat-num">{{ total }}</text>
        <text class="stat-label">全部</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-num" style="color:#ef4444">{{ unreadCount }}</text>
        <text class="stat-label">未读</text>
      </view>
      <view class="stat-btn" v-if="unreadCount > 0" @click="markAllRead">
        <text class="stat-btn-text">全部已读</text>
      </view>
    </view>

    <!-- 空状态 -->
    <view class="empty-state" v-if="!loading && list.length === 0">
      <c-icon type="bell" :size="64" color="#d1d5db" />
      <text class="empty-title">暂无通知</text>
    </view>

    <!-- 通知列表 -->
    <view class="notification-list" v-else>
      <view
        class="notif-item"
        :class="{ unread: !isRead(item) }"
        v-for="item in list"
        :key="item.notifId || item.id"
        @click="handleClick(item)"
      >
        <!-- 类型图标 -->
        <view class="notif-icon" :class="getIconClass(item.notificationType)">
          <c-icon :type="getIconType(item.notificationType)" :size="22" :color="getIconColor(item.notificationType)" />
        </view>

        <!-- 内容 -->
        <view class="notif-body">
          <view class="notif-header">
            <text class="notif-title">{{ item.title }}</text>
            <view class="unread-dot" v-if="!isRead(item)"></view>
          </view>
          <text class="notif-content">{{ item.content || item.message || item.description }}</text>
          <text class="notif-time">{{ formatTime(item.createTime) }}</text>
        </view>

        <!-- 箭头 -->
        <c-icon type="arrowright" :size="16" color="#d1d5db" />
      </view>
    </view>

    <!-- 加载更多 -->
    <view class="load-more" v-if="hasMore" @click="loadMore">
      <text class="load-text">加载更多</text>
    </view>
  </view>
</template>

<script>
import { getNotificationList, getUnreadCount, markNotificationRead, markAllNotificationRead } from '@/api/request.js'

export default {
  data() {
    return {
      pageNum: 1,
      pageSize: 20,
      list: [],
      unreadCount: 0,
      total: 0,
      loading: false,
      hasMore: false
    }
  },
  onLoad() {
    if (!uni.getStorageSync('token')) {
      uni.reLaunch({ url: '/pages/login/login' })
      return
    }
    this.loadData()
    this.loadUnreadCount()
  },
  onShow() {
    this.loadUnreadCount()
  },
  onPullDownRefresh() {
    this.pageNum = 1
    this.list = []
    Promise.all([this.loadData(), this.loadUnreadCount()])
      .finally(() => uni.stopPullDownRefresh())
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getNotificationList({ pageNum: this.pageNum, pageSize: this.pageSize })
        const rows = res.rows || res.data?.rows || []
        this.total = res.total || res.data?.total || 0
        if (this.pageNum === 1) {
          this.list = rows
        } else {
          this.list = [...this.list, ...rows]
        }
        this.hasMore = rows.length >= this.pageSize
      } catch (e) {} finally {
        this.loading = false
      }
    },

    async loadUnreadCount() {
      try {
        const res = await getUnreadCount()
        this.unreadCount = res.data || res.count || res.unreadCount || 0
      } catch (e) {}
    },

    loadMore() {
      if (!this.hasMore) return
      this.pageNum++
      this.loadData()
    },

    isRead(item) {
      return item.isRead === '1' || item.isRead === 1
    },

    getIconType(type) {
      const map = {
        workOrder: 'list',
        inspection: 'search',
        fault: 'flag',
        sla: 'calendar',
        dailyReport: 'paperclip',
        announcement: 'star',
        system: 'info'
      }
      return map[type] || 'bell'
    },

    getIconColor(type) {
      const map = {
        workOrder: '#2563eb',
        inspection: '#10b981',
        fault: '#ef4444',
        sla: '#f59e0b',
        dailyReport: '#8b5cf6',
        announcement: '#f59e0b',
        system: '#909399'
      }
      return map[type] || '#909399'
    },

    getIconClass(type) {
      const map = {
        workOrder: 'bg-blue',
        inspection: 'bg-green',
        fault: 'bg-red',
        sla: 'bg-yellow',
        dailyReport: 'bg-purple',
        announcement: 'bg-yellow',
        system: 'bg-gray'
      }
      return map[type] || 'bg-gray'
    },

    formatTime(timeStr) {
      if (!timeStr) return ''
      const now = new Date()
      const date = new Date(timeStr)
      const diff = now - date
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(diff / 3600000)
      const days = Math.floor(diff / 86400000)
      if (minutes < 1) return '刚刚'
      if (minutes < 60) return `${minutes}分钟前`
      if (hours < 24) return `${hours}小时前`
      if (days < 7) return `${days}天前`
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const d = String(date.getDate()).padStart(2, '0')
      return `${y}-${m}-${d}`
    },

    markAllRead() {
      if (this.unreadCount === 0) return
      markAllNotificationRead().then(() => {
        this.list.forEach(item => { item.isRead = '1' })
        this.unreadCount = 0
      }).catch(() => {})
    },

    async handleClick(item) {
      const notifId = item.notifId || item.id
      if (!this.isRead(item)) {
        try {
          await markNotificationRead(notifId)
          item.isRead = '1'
          this.unreadCount = Math.max(0, this.unreadCount - 1)
        } catch (e) {}
      }
      if (item.businessType === 'workOrder' && item.businessId) {
        uni.navigateTo({ url: `/pages/workOrder/detail?id=${item.businessId}` })
      } else if (item.businessType === 'inspection' && item.businessId) {
        uni.navigateTo({ url: `/pages/inspection/taskDetail?id=${item.businessId}` })
      } else if (item.businessType === 'fault' && item.businessId) {
        uni.navigateTo({ url: `/pages/fault/report?faultId=${item.businessId}` })
      }
    }
  }
}
</script>

<style scoped>
.notification-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.stat-bar {
  background: #fff;
  padding: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 80rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rpx;
}

.stat-num {
  font-size: 48rpx;
  font-weight: 800;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 24rpx;
  color: #909399;
}

.stat-divider {
  width: 1rpx;
  height: 64rpx;
  background: #ebeef5;
}

.stat-btn {
  padding: 8rpx 20rpx;
  border: 1rpx solid #dcdfe6;
  border-radius: 8rpx;
  margin-left: 24rpx;
}

.stat-btn-text {
  font-size: 24rpx;
  color: #606266;
}

.notification-list {
  padding: 16rpx 24rpx;
}

.notif-item {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 12rpx;
  display: flex;
  align-items: flex-start;
  gap: 20rpx;
  transition: opacity 0.2s;
}

.notif-item:active { opacity: 0.8; }

.notif-item.unread {
  background: #f8faff;
}

.notif-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.bg-blue { background: #eff6ff; }
.bg-green { background: #ecfdf5; }
.bg-red { background: #fef2f2; }
.bg-yellow { background: #fffbeb; }
.bg-purple { background: #f5f3ff; }
.bg-gray { background: #f5f7fa; }

.notif-body {
  flex: 1;
  overflow: hidden;
}

.notif-header {
  display: flex;
  align-items: center;
  gap: 10rpx;
  margin-bottom: 8rpx;
}

.notif-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #1a1a1a;
  flex: 1;
}

.unread-dot {
  width: 12rpx;
  height: 12rpx;
  background: #ef4444;
  border-radius: 50%;
  flex-shrink: 0;
}

.notif-content {
  font-size: 26rpx;
  color: #909399;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 8rpx;
}

.notif-time {
  font-size: 22rpx;
  color: #c0c4cc;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 160rpx 0;
  gap: 16rpx;
}

.empty-title {
  font-size: 28rpx;
  color: #909399;
  margin-top: 8rpx;
}

.load-more {
  text-align: center;
  padding: 32rpx;
}

.load-text {
  font-size: 26rpx;
  color: #2563eb;
}
</style>
