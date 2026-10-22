<template>
  <view class="workbench">
    <!-- 新任务通知卡片 -->
    <view class="notif-banner" v-if="hasNewTasks">
      <view class="notif-header">
        <view class="notif-dot"></view>
        <text class="notif-title">您有新的任务</text>
        <text class="notif-count">{{ newTaskCount }} 个待处理</text>
      </view>
      <view class="notif-list">
        <view class="notif-item" v-if="newWorkOrders.length" @click="goToWorkOrders('')">
          <view class="notif-icon wo">
            <c-icon type="document" :size="18" color="#fff" />
          </view>
          <view class="notif-body">
            <text class="notif-item-title">新工单</text>
            <text class="notif-item-sub">{{ newWorkOrders.length }} 个新工单待接单</text>
          </view>
          <c-icon type="arrowright" :size="16" color="#fff" />
        </view>
        <view class="notif-item" v-if="newInspections.length" @click="goInspections">
          <view class="notif-icon insp">
            <c-icon type="search" :size="18" color="#fff" />
          </view>
          <view class="notif-body">
            <text class="notif-item-title">新巡检任务</text>
            <text class="notif-item-sub">{{ newInspections.length }} 个新巡检待执行</text>
          </view>
          <c-icon type="arrowright" :size="16" color="#fff" />
        </view>
      </view>
    </view>

    <!-- 顶部个人信息 -->
    <view class="header-card">
      <view class="user-row">
        <view class="user-info">
          <text class="user-name">{{ userInfo?.nickName || userInfo?.userName || '工程师' }}</text>
          <text class="user-role">维保工程师</text>
        </view>
        <view class="date-block">
          <text class="date-day">{{ dayStr }}</text>
          <text class="date-week">{{ weekStr }}</text>
        </view>
      </view>
    </view>

    <!-- 今日概览 -->
    <view class="overview-section">
      <text class="section-label">今日概览</text>
      <view class="overview-grid">
        <view class="overview-item" @click="goToWorkOrders('0')">
          <view class="ov-icon" style="background:#fef2f2">
            <c-icon type="alert" :size="22" color="#ef4444" />
          </view>
          <text class="ov-num" style="color:#ef4444">{{ stats.pendingOrders }}</text>
          <text class="ov-label">待接单</text>
        </view>
        <view class="overview-divider"></view>
        <view class="overview-item" @click="goInspections">
          <view class="ov-icon" style="background:#fffbeb">
            <c-icon type="search" :size="22" color="#f59e0b" />
          </view>
          <text class="ov-num" style="color:#f59e0b">{{ stats.pendingInspections }}</text>
          <text class="ov-label">待巡检</text>
        </view>
        <view class="overview-divider"></view>
        <view class="overview-item" @click="goToCheckInHistory">
          <view class="ov-icon" style="background:#eff6ff">
            <c-icon type="location" :size="22" color="#2563eb" />
          </view>
          <text class="ov-num" style="color:#2563eb">{{ stats.todayCheckIns }}</text>
          <text class="ov-label">今日签到</text>
        </view>
      </view>
    </view>

    <!-- 快捷入口 -->
    <view class="entry-section">
      <text class="section-label">快捷操作</text>
      <view class="entry-grid">
        <view class="entry-item" @click="goToWorkOrders('')">
          <view class="entry-icon" style="background:#eff6ff">
            <c-icon type="list" :size="32" color="#2563eb" />
          </view>
          <text class="entry-label">全部工单</text>
        </view>
        <view class="entry-item" @click="goToCheckIn">
          <view class="entry-icon" style="background:#ecfdf5">
            <c-icon type="plus" :size="32" color="#10b981" />
          </view>
          <text class="entry-label">GPS打卡</text>
        </view>
        <view class="entry-item" @click="goToFaultReport">
          <view class="entry-icon" style="background:#fef2f2">
            <c-icon type="flag" :size="32" color="#ef4444" />
          </view>
          <text class="entry-label">故障上报</text>
        </view>
        <view class="entry-item" @click="handleScan">
          <view class="entry-icon" style="background:#f5f3ff">
            <c-icon type="scan" :size="32" color="#8b5cf6" />
          </view>
          <text class="entry-label">设备扫码</text>
        </view>
        <view class="entry-item" @click="goNotifications">
          <view class="entry-icon" style="background:#fef2f2;position:relative">
            <c-icon type="bell" :size="32" color="#ef4444" />
            <view class="notif-badge" v-if="unreadCount > 0">{{ unreadCount > 99 ? '99+' : unreadCount }}</view>
          </view>
          <text class="entry-label">消息通知</text>
        </view>
      </view>
    </view>

    <!-- 待办任务 -->
    <view class="todo-section">
      <view class="todo-header">
        <text class="section-label">待办任务</text>
        <view class="todo-badge" v-if="todoList.length">
          <text>{{ todoList.length }}</text>
        </view>
      </view>

      <view class="todo-list" v-if="todoList.length">
        <view
          class="todo-item"
          v-for="item in todoList"
          :key="item.id"
          @click="goToDetail(item)"
        >
          <view class="todo-type-bar" :class="item.type"></view>
          <view class="todo-body">
            <view class="todo-top">
              <text class="todo-title">{{ item.title }}</text>
              <view class="todo-tag" :class="item.type">
                {{ item.type === 'workOrder' ? '工单' : '巡检' }}
              </view>
            </view>
            <text class="todo-desc" v-if="item.desc">{{ item.desc }}</text>
            <view class="todo-bottom">
              <text class="todo-status">{{ getStatusText(item.status, item.type) }}</text>
              <c-icon type="arrowright" :size="16" color="#d1d5db" />
            </view>
          </view>
        </view>
      </view>

      <view class="todo-empty" v-else>
        <c-icon type="checkmarkempty" :size="48" color="#d1d5db" />
        <text class="empty-text">暂无待办任务</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getMyTasks, getInspectionTaskList, getCheckInList, getUnreadCount } from '@/api/request.js'

export default {
  data() {
    return {
      userInfo: null,
      stats: { pendingOrders: 0, pendingInspections: 0, todayCheckIns: 0 },
      todoList: [],
      newWorkOrders: [],
      newInspections: [],
      dayStr: '',
      weekStr: '',
      unreadCount: 0,
      lastWorkOrderIds: [],
      lastInspectionTaskIds: []
    }
  },
  computed: {
    hasNewTasks() {
      return this.newWorkOrders.length > 0 || this.newInspections.length > 0
    },
    newTaskCount() {
      return this.newWorkOrders.length + this.newInspections.length
    }
  },
  onShow() {
    const token = uni.getStorageSync('token')
    if (!token) {
      uni.reLaunch({ url: '/pages/login/login' })
      return
    }
    this.userInfo = uni.getStorageSync('userInfo') || null
    this.initDate()
    this.loadData()
    this.loadUnreadCount()
  },
  onPullDownRefresh() {
    this.loadData().finally(() => uni.stopPullDownRefresh())
  },
  methods: {
    initDate() {
      const now = new Date()
      this.dayStr = String(now.getDate()).padStart(2, '0')
      const weeks = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      this.weekStr = weeks[now.getDay()]
    },

    async loadData() {
      uni.showLoading({ title: '加载中...' })
      try {
        this.todoList = []
        await Promise.all([this.loadWorkOrders(), this.loadInspections(), this.loadCheckIns()])
      } finally {
        uni.hideLoading()
      }
    },

    async loadWorkOrders() {
      try {
        const engineerId = this.userInfo?.userId
        const res = await getMyTasks({ assignedTo: engineerId, status: '0,1,2', pageNum: 1, pageSize: 50 })
        const allList = res.rows || res.data?.rows || []
        this.stats.pendingOrders = allList.filter(item => item.status === '0').length

        // 新任务：有 assignedTo 但不在 lastWorkOrderIds 里
        const currentIds = allList.map(item => item.workOrderId)
        this.newWorkOrders = allList.filter(item => !this.lastWorkOrderIds.includes(item.workOrderId))
        this.lastWorkOrderIds = currentIds

        const tasks = allList.map(item => ({
          id: item.workOrderId,
          type: 'workOrder',
          title: item.title || item.orderNo || '工单',
          desc: [item.customerName, item.assetName].filter(Boolean).join(' · ') || item.contactAddress || '',
          status: item.status,
          raw: item
        }))
        this.todoList = [...this.todoList, ...tasks]
      } catch (e) {
        uni.showToast({ title: '工单加载失败', icon: 'none' })
      }
    },

    async loadInspections() {
      try {
        const engineerId = this.userInfo?.userId
        const res = await getInspectionTaskList({ assignedTo: engineerId, pageNum: 1, pageSize: 50 })
        const list = (res.rows || res.data?.rows || []).filter(item => item.status !== '2' && item.status !== '3')
        this.stats.pendingInspections = list.length

        // 新任务：有 assignedTo 但不在 lastInspectionTaskIds 里
        const currentIds = list.map(item => item.taskId)
        this.newInspections = list.filter(item => !this.lastInspectionTaskIds.includes(item.taskId))
        this.lastInspectionTaskIds = currentIds

        const tasks = list.map(item => ({
          id: item.taskId,
          type: 'inspection',
          title: item.workOrderTitle || item.taskName || '巡检任务',
          desc: [item.customerName, item.assetName].filter(Boolean).join(' · '),
          status: item.status,
          raw: item
        }))
        this.todoList = [...this.todoList, ...tasks]
      } catch (e) {
        uni.showToast({ title: '巡检加载失败', icon: 'none' })
      }
    },

    async loadCheckIns() {
      try {
        const engineerId = this.userInfo?.userId
        const res = await getCheckInList({ engineerId })
        const list = Array.isArray(res) ? res : (res.rows || res.data?.rows || [])
        const today = this.getTodayStr()
        this.stats.todayCheckIns = list.filter(item => {
          const t = (item.checkTime || item.createTime || '').substring(0, 10)
          return t === today
        }).length
      } catch (e) {
        this.stats.todayCheckIns = 0
      }
    },

    getTodayStr() {
      const now = new Date()
      return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
    },

    getStatusText(status, type) {
      if (type === 'workOrder') {
        const m = { '0': '待接单', '1': '处理中', '2': '待确认', '3': '已完成' }
        return m[status] || status
      }
      const m = { '0': '待执行', '1': '执行中', '2': '已完成' }
      return m[status] || status
    },

    async loadUnreadCount() {
      try {
        const res = await getUnreadCount()
        this.unreadCount = res?.data || res?.count || res || 0
      } catch (e) {
        this.unreadCount = 0
      }
    },
    goNotifications() { uni.navigateTo({ url: '/pages/notification/list' }) },
    goToWorkOrders(status) {
      uni.navigateTo({ url: `/pages/workOrder/list?status=${status || ''}` })
    },
    goInspections() { uni.switchTab({ url: '/pages/inspection/taskList' }) },
    goToCheckIn() { uni.navigateTo({ url: '/pages/checkin/index' }) },
    goToCheckInHistory() { uni.navigateTo({ url: '/pages/checkin/history' }) },
    goToFaultReport() { uni.navigateTo({ url: '/pages/fault/report' }) },
    goToDetail(item) {
      if (item.type === 'workOrder') {
        uni.navigateTo({ url: `/pages/workOrder/detail?id=${item.id}` })
      } else {
        uni.navigateTo({ url: `/pages/inspection/taskDetail?id=${item.id}` })
      }
    },
    handleScan() {
      uni.scanCode({
        success: (res) => {
          uni.navigateTo({ url: `/pages/common/assetDetail?qrCode=${res.result}` })
        },
        fail: () => { uni.showToast({ title: '扫码失败', icon: 'none' }) }
      })
    }
  }
}
</script>

<style scoped>
.workbench {
  min-height: 100vh;
  background: #f5f7fa;
}

/* ===== 新任务通知卡片 ===== */
.notif-banner {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  margin: 24rpx;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 20rpx rgba(37, 99, 235, 0.3);
}
.notif-header {
  display: flex;
  align-items: center;
  padding: 28rpx 28rpx 0;
  gap: 12rpx;
}
.notif-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 6rpx;
  background: #fbbf24;
  animation: blink 1.5s infinite;
  flex-shrink: 0;
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}
.notif-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #fff;
  flex: 1;
}
.notif-count {
  font-size: 22rpx;
  color: rgba(255,255,255,0.8);
  background: rgba(255,255,255,0.2);
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
}
.notif-list {
  padding: 16rpx 20rpx 24rpx;
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}
.notif-item {
  display: flex;
  align-items: center;
  background: rgba(255,255,255,0.15);
  border-radius: 14rpx;
  padding: 20rpx 20rpx;
  gap: 16rpx;
}
.notif-icon {
  width: 56rpx;
  height: 56rpx;
  border-radius: 14rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.notif-icon.wo { background: #2563eb; }
.notif-icon.insp { background: #10b981; }
.notif-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}
.notif-item-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #fff;
}
.notif-item-sub {
  font-size: 22rpx;
  color: rgba(255,255,255,0.75);
}

/* ===== 顶部 ===== */
.header-card {
  background: #fff;
  padding: 36rpx 32rpx;
}
.user-row {
  display: flex;
  align-items: center;
  gap: 20rpx;
}
.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}
.user-name {
  font-size: 34rpx;
  font-weight: 700;
  color: #1a1a1a;
}
.user-role {
  font-size: 24rpx;
  color: #909399;
}
.date-block {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #eff6ff;
  border-radius: 14rpx;
  padding: 12rpx 22rpx;
  min-width: 80rpx;
}
.date-day {
  font-size: 38rpx;
  font-weight: 800;
  color: #2563eb;
  line-height: 1;
}
.date-week {
  font-size: 20rpx;
  color: #2563eb;
  margin-top: 4rpx;
}

/* ===== 通用 ===== */
.section-label {
  font-size: 28rpx;
  font-weight: 600;
  color: #303133;
  display: block;
  margin-bottom: 24rpx;
}

/* ===== 今日概览 ===== */
.overview-section {
  background: #fff;
  padding: 32rpx;
  margin-bottom: 20rpx;
}
.overview-grid {
  display: flex;
  align-items: center;
}
.overview-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
}
.ov-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 4rpx;
}
.ov-num {
  font-size: 48rpx;
  font-weight: 800;
  line-height: 1;
}
.ov-label {
  font-size: 24rpx;
  color: #909399;
  margin-top: 4rpx;
}
.overview-divider {
  width: 1rpx;
  height: 64rpx;
  background: #ebeef5;
}

/* ===== 快捷入口 ===== */
.entry-section {
  background: #fff;
  padding: 32rpx;
  margin-bottom: 20rpx;
}
.entry-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16rpx;
}
.entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}
.entry-icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 22rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
.entry-label {
  font-size: 24rpx;
  color: #4b5563;
}
.notif-badge {
  position: absolute;
  top: -6rpx;
  right: -6rpx;
  background: #ef4444;
  color: #fff;
  font-size: 18rpx;
  font-weight: 700;
  min-width: 32rpx;
  height: 32rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 6rpx;
  line-height: 1;
  border: 2rpx solid #fff;
  box-sizing: border-box;
}

/* ===== 待办任务 ===== */
.todo-section {
  background: #fff;
  padding: 32rpx;
}
.todo-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 24rpx;
}
.todo-badge {
  background: #2563eb;
  color: #fff;
  font-size: 20rpx;
  font-weight: 600;
  padding: 2rpx 10rpx;
  border-radius: 20rpx;
  line-height: 1.6;
}
.todo-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}
.todo-item {
  display: flex;
  border-radius: 16rpx;
  overflow: hidden;
  background: #fafafa;
  border: 1rpx solid #f0f0f0;
}
.todo-type-bar {
  width: 6rpx;
  flex-shrink: 0;
}
.todo-type-bar.workOrder { background: #2563eb; }
.todo-type-bar.inspection { background: #10b981; }
.todo-body {
  flex: 1;
  padding: 22rpx 24rpx;
}
.todo-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8rpx;
}
.todo-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #1a1a1a;
  flex: 1;
  margin-right: 12rpx;
  line-height: 1.4;
}
.todo-tag {
  font-size: 20rpx;
  font-weight: 500;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  flex-shrink: 0;
}
.todo-tag.workOrder { background: #eff6ff; color: #2563eb; }
.todo-tag.inspection { background: #ecfdf5; color: #10b981; }
.todo-desc {
  font-size: 24rpx;
  color: #909399;
  display: block;
  margin-bottom: 14rpx;
  line-height: 1.4;
}
.todo-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 14rpx;
  border-top: 1rpx solid #f0f0f0;
}
.todo-status {
  font-size: 22rpx;
  color: #6b7280;
}

/* ===== 空状态 ===== */
.todo-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 0;
  gap: 16rpx;
}
.empty-text {
  font-size: 26rpx;
  color: #909399;
}
</style>
