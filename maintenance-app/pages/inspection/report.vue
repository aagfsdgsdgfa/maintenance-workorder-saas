<template>
  <view class="inspection-report">
    <!-- 报告头部 -->
    <view class="report-header">
      <text class="report-title">巡检报告</text>
      <text class="task-name">{{ taskName }}</text>
      <text class="task-time">{{ inspectionDate }}</text>
    </view>

    <!-- 统计概览 -->
    <view class="stats-overview">
      <view class="stat-item">
        <text class="stat-num">{{ stats.totalPoints }}</text>
        <text class="stat-label">巡检点</text>
      </view>
      <view class="stat-item">
        <text class="stat-num success">{{ stats.normalPoints }}</text>
        <text class="stat-label">正常</text>
      </view>
      <view class="stat-item">
        <text class="stat-num warning">{{ stats.abnormalPoints }}</text>
        <text class="stat-label">异常</text>
      </view>
      <view class="stat-item">
        <text class="stat-num">{{ stats.totalDuration }}</text>
        <text class="stat-label">巡检时长</text>
      </view>
    </view>

    <!-- 资产列表 -->
    <view class="section" v-for="asset in assets" :key="asset.id">
      <view class="section-header">
        <text class="asset-name">{{ asset.name }}</text>
        <text class="asset-sn">SN: {{ asset.sn }}</text>
      </view>

      <view
        v-for="point in asset.points"
        :key="point.id"
        class="point-item"
        :class="{ abnormal: point.hasAbnormal }"
      >
        <view class="point-header">
          <text class="point-name">{{ point.name }}</text>
          <text :class="['point-status', point.hasAbnormal ? 'abnormal' : 'normal']">
            {{ point.hasAbnormal ? '异常' : '正常' }}
          </text>
        </view>

        <view class="point-items">
          <view v-for="item in point.checkItems" :key="item.id" class="check-item">
            <text class="item-name">{{ item.name }}</text>
            <text :class="['item-value', item.isNormal ? 'normal' : 'abnormal']">
              {{ item.isNormal ? '✓' : '✗' }} {{ item.value || (item.isNormal ? '正常' : '异常') }}
            </text>
          </view>
        </view>

        <!-- 异常项高亮 -->
        <view class="abnormal-list" v-if="point.abnormalItems.length">
          <text class="abnormal-title">异常项:</text>
          <text class="abnormal-content" v-for="item in point.abnormalItems" :key="item.id">
            {{ item.name }}: {{ item.value }}
          </text>
        </view>

        <!-- 备注 -->
        <text class="point-remark" v-if="point.remark">
          备注: {{ point.remark }}
        </text>

        <!-- 照片 -->
        <view class="point-photos" v-if="point.photos && point.photos.length">
          <image
            v-for="(photo, idx) in point.photos"
            :key="idx"
            :src="photo"
            class="photo-thumb"
            mode="aspectFill"
            lazy-load
            @click="previewImage(photo)"
          />
        </view>
      </view>
    </view>

    <!-- 总结 -->
    <view class="summary-section" v-if="summary">
      <view class="summary-title">巡检总结</view>
      <text class="summary-content">{{ summary }}</text>
    </view>

    <!-- 操作按钮 -->
    <view class="action-bar">
      <button class="action-btn" @click="shareReport">分享报告</button>
      <button class="action-btn primary" @click="exportReport">导出PDF</button>
    </view>
  </view>
</template>

<script>
import { getInspectionTaskDetail, submitInspectionResult } from '@/api/request.js'

export default {
  data() {
    return {
      taskId: '',
      taskName: '',
      inspectionDate: '',
      assets: [],
      stats: {
        totalPoints: 0,
        normalPoints: 0,
        abnormalPoints: 0,
        totalDuration: ''
      },
      summary: ''
    }
  },
  onLoad(options) {
    this.taskId = options.id
    this.taskName = options.name || '巡检任务'
    this.inspectionDate = options.date || new Date().toLocaleDateString()
    if (this.taskId) {
      this.loadReport()
    }
  },
  methods: {
    async loadReport() {
      uni.showLoading({ title: '加载中...' })
      try {
        const res = await getInspectionTaskDetail(this.taskId)
        this.parseReportData(res)
      } catch (e) {
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    },

    parseReportData(data) {
      // 解析巡检任务数据，生成报告
      const assets = data.assets || []
      let normalCount = 0
      let abnormalCount = 0

      assets.forEach(asset => {
        const points = asset.points || []
        points.forEach(point => {
          if (point.hasAbnormal) {
            abnormalCount++
          } else {
            normalCount++
          }
        })
      })

      this.assets = assets
      this.stats = {
        totalPoints: normalCount + abnormalCount,
        normalPoints: normalCount,
        abnormalPoints: abnormalCount,
        totalDuration: data.duration || '1小时30分'
      }
      this.summary = data.summary || ''
    },

    previewImage(url) {
      uni.previewImage({
        urls: [url],
        current: url
      })
    },

    shareReport() {
      uni.showShareMenu({
        withShareTicket: true,
        menus: ['shareAppMessage', 'shareTimeline']
      })
    },

    exportReport() {
      uni.showToast({ title: '导出功能开发中', icon: 'none' })
    }
  }
}
</script>

<style scoped>
.inspection-report {
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

.task-name {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.9);
  margin-top: 12rpx;
  display: block;
}

.task-time {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.7);
  margin-top: 8rpx;
  display: block;
}

.stats-overview {
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
  color: #303133;
  display: block;
}

.stat-num.success {
  color: #67c23a;
}

.stat-num.warning {
  color: #f56c6c;
}

.stat-label {
  font-size: 24rpx;
  color: #909399;
  margin-top: 8rpx;
  display: block;
}

.section {
  background: #fff;
  margin: 0 24rpx 24rpx;
  border-radius: 16rpx;
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  background: #f5f7fa;
  border-bottom: 1rpx solid #ebeef5;
}

.asset-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #303133;
}

.asset-sn {
  font-size: 24rpx;
  color: #909399;
}

.point-item {
  padding: 24rpx;
  border-bottom: 1rpx solid #ebeef5;
}

.point-item.abnormal {
  background: #fef0f0;
}

.point-item:last-child {
  border-bottom: none;
}

.point-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.point-name {
  font-size: 28rpx;
  color: #303133;
  font-weight: 500;
}

.point-status {
  font-size: 22rpx;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
}

.point-status.normal {
  background: #f0f9eb;
  color: #67c23a;
}

.point-status.abnormal {
  background: #fef0f0;
  color: #f56c6c;
}

.point-items {
  margin-bottom: 12rpx;
}

.check-item {
  display: flex;
  justify-content: space-between;
  padding: 8rpx 0;
}

.item-name {
  font-size: 26rpx;
  color: #606266;
}

.item-value {
  font-size: 26rpx;
}

.item-value.normal {
  color: #67c23a;
}

.item-value.abnormal {
  color: #f56c6c;
}

.abnormal-list {
  background: #fff;
  padding: 12rpx;
  border-radius: 8rpx;
  margin-bottom: 12rpx;
}

.abnormal-title {
  font-size: 24rpx;
  color: #f56c6c;
  font-weight: bold;
}

.abnormal-content {
  font-size: 24rpx;
  color: #f56c6c;
  display: block;
  margin-top: 4rpx;
}

.point-remark {
  font-size: 24rpx;
  color: #909399;
  font-style: italic;
  display: block;
  margin-bottom: 12rpx;
}

.point-photos {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.photo-thumb {
  width: 120rpx;
  height: 120rpx;
  border-radius: 8rpx;
}

.summary-section {
  background: #fff;
  margin: 0 24rpx 24rpx;
  border-radius: 16rpx;
  padding: 24rpx;
}

.summary-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #303133;
  margin-bottom: 12rpx;
}

.summary-content {
  font-size: 26rpx;
  color: #606266;
  line-height: 1.6;
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
