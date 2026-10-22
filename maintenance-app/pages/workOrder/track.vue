<template>
  <view class="work-order-track">
    <!-- 顶部信息 -->
    <view class="header-card">
      <text class="order-no">{{ orderNo }}</text>
      <text class="order-title">{{ title }}</text>
    </view>

    <!-- 轨迹时间轴 -->
    <view class="track-timeline">
      <view
        v-for="(item, index) in timeline"
        :key="index"
        class="timeline-item"
        :class="{ completed: item.status === 'completed', current: item.status === 'current' }"
      >
        <!-- 时间点 -->
        <view class="timeline-dot">
          <view class="dot-inner" v-if="item.status === 'completed'">✓</view>
          <view class="dot-pulse" v-else-if="item.status === 'current'"></view>
          <view class="dot-empty" v-else></view>
        </view>

        <!-- 连接线 -->
        <view class="timeline-line" v-if="index < timeline.length - 1"></view>

        <!-- 内容 -->
        <view class="timeline-content">
          <view class="content-header">
            <text class="step-title">{{ item.title }}</text>
            <text class="step-time">{{ item.time }}</text>
          </view>
          <text class="step-desc" v-if="item.desc">{{ item.desc }}</text>
          <view class="step-operator" v-if="item.operator">
            <text class="operator-label">操作人:</text>
            <text class="operator-name">{{ item.operator }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view class="empty" v-if="timeline.length === 0">
      <text class="empty-icon">[</text>
      <text class="empty-text">暂无轨迹记录</text>
    </view>
  </view>
</template>

<script>
import { getWorkOrderDetail, getWorkOrderCheckIns } from '@/api/request.js'

export default {
  data() {
    return {
      id: '',
      orderNo: '',
      title: '',
      timeline: []
    }
  },
  onLoad(options) {
    this.id = options.id
    this.orderNo = options.orderNo || ''
    this.title = options.title || ''
    if (this.id) {
      this.loadData()
    }
  },
  methods: {
    async loadData() {
      uni.showLoading({ title: '加载中...' })
      try {
        // 加载工单详情获取状态变更历史
        const detailRes = await getWorkOrderDetail(this.id)
        this.buildTimeline(detailRes)

        // 加载打卡记录
        const checkInRes = await getWorkOrderCheckIns(this.id)
        this.addCheckInTimeline(checkInRes)
      } catch (e) {
        uni.showToast({ title: '加载轨迹失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    },

    buildTimeline(detail) {
      const s = String(detail.status || 0)
      const steps = [
        { key: 'create', title: '创建工单', status: 'completed' },
        { key: 'dispatch', title: '派发工程师', status: s > '0' ? 'completed' : 'pending' },
        { key: 'accept', title: '接单', status: s >= '1' ? 'completed' : 'pending' },
        { key: 'arrive', title: '到达打卡', status: s >= '1' && detail.arriveTime ? 'completed' : 'pending' },
        { key: 'start', title: '开始维修', status: s >= '2' ? 'completed' : 'pending' },
        { key: 'complete', title: '完工确认', status: s >= '3' ? 'completed' : 'pending' },
        { key: 'verify', title: '客户验收', status: s >= '4' ? 'completed' : 'pending' }
      ]

      this.timeline = steps.map(step => {
        let status = step.status
        if (step.status === 'pending') {
          const currentIndex = steps.findIndex(s => s.status === 'completed')
          const thisIndex = steps.indexOf(step)
          if (thisIndex === currentIndex + 1) {
            status = 'current'
          }
        }

        return {
          title: step.title,
          time: detail[step.key + 'Time'] || '',
          desc: detail[step.key + 'Desc'] || '',
          operator: detail[step.key + 'By'] || '',
          status
        }
      })
    },

    addCheckInTimeline(checkIns) {
      // 将打卡记录插入到对应时间点
      if (!checkIns || checkIns.length === 0) return

      checkIns.forEach(checkIn => {
        const typeText = checkIn.checkType === 'arrive' ? '到达打卡' : '离开打卡'
        this.timeline.push({
          title: typeText,
          time: checkIn.createTime || checkIn.checkTime || '',
          desc: checkIn.address || '',
          operator: '',
          status: 'completed'
        })
      })

      // 按时间排序
      this.timeline.sort((a, b) => {
        if (!a.time) return 1
        if (!b.time) return -1
        return new Date(a.time) - new Date(b.time)
      })
    }
  }
}
</script>

<style scoped>
.work-order-track {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24rpx;
}

.header-card {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.order-no {
  font-family: monospace;
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
  display: block;
}

.order-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #fff;
  margin-top: 8rpx;
  display: block;
}

.track-timeline {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
}

.timeline-item {
  position: relative;
  padding-left: 48rpx;
  padding-bottom: 40rpx;
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-dot {
  position: absolute;
  left: 0;
  top: 0;
  width: 24rpx;
  height: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dot-inner {
  width: 24rpx;
  height: 24rpx;
  background: #67c23a;
  border-radius: 50%;
  color: #fff;
  font-size: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dot-pulse {
  width: 24rpx;
  height: 24rpx;
  background: #2563eb;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(102, 126, 234, 0.4); }
  70% { box-shadow: 0 0 0 12rpx rgba(102, 126, 234, 0); }
  100% { box-shadow: 0 0 0 0 rgba(102, 126, 234, 0); }
}

.dot-empty {
  width: 24rpx;
  height: 24rpx;
  background: #e0e0e0;
  border-radius: 50%;
}

.timeline-line {
  position: absolute;
  left: 10rpx;
  top: 24rpx;
  width: 4rpx;
  height: calc(100% - 24rpx);
  background: #e0e0e0;
}

.timeline-item.completed .timeline-line {
  background: #67c23a;
}

.timeline-content {
  padding-left: 16rpx;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.step-title {
  font-size: 28rpx;
  color: #303133;
  font-weight: bold;
}

.timeline-item.pending .step-title {
  color: #c0c4cc;
}

.step-time {
  font-size: 22rpx;
  color: #909399;
}

.step-desc {
  font-size: 24rpx;
  color: #606266;
  margin-top: 8rpx;
  display: block;
}

.step-operator {
  margin-top: 8rpx;
  font-size: 22rpx;
}

.operator-label {
  color: #909399;
}

.operator-name {
  color: #606266;
  margin-left: 8rpx;
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx;
}

.empty-icon {
  font-size: 80rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #909399;
  margin-top: 16rpx;
}
</style>
