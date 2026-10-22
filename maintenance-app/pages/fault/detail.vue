<template>
  <view class="fault-detail">
    <!-- 状态卡片 -->
    <view class="status-card">
      <text :class="['status-tag', faultDetail.status]">
        {{ getStatusText(faultDetail.status) }}
      </text>
      <text class="fault-no">故障编号: {{ faultDetail.faultNo }}</text>
    </view>

    <!-- 基础信息 -->
    <view class="info-section">
      <view class="section-title">故障信息</view>
      <view class="info-grid">
        <view class="info-item">
          <text class="info-label">设备名称</text>
          <text class="info-value">{{ faultDetail.assetName }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">设备编号</text>
          <text class="info-value">{{ faultDetail.assetSn }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">故障类型</text>
          <text class="info-value">{{ faultDetail.faultType }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">紧急程度</text>
          <text :class="['info-value', 'priority', faultDetail.severity]">
            {{ faultDetail.severity === 'urgent' ? '紧急' : '一般' }}
          </text>
        </view>
      </view>

      <view class="info-row">
        <text class="info-label">故障描述</text>
        <text class="info-content">{{ faultDetail.faultDesc }}</text>
      </view>

      <view class="info-row" v-if="faultDetail.faultPhotos && faultDetail.faultPhotos.length">
        <text class="info-label">故障照片</text>
        <view class="photo-list">
          <image
            v-for="(photo, idx) in faultDetail.faultPhotos"
            :key="idx"
            :src="photo"
            class="photo-item"
            mode="aspectFill"
            lazy-load
            @click="previewImage(photo)"
          />
        </view>
      </view>

      <view class="info-row">
        <text class="info-label">上报时间</text>
        <text class="info-value">{{ faultDetail.createTime }}</text>
      </view>
    </view>

    <!-- 处理记录 -->
    <view class="info-section" v-if="handleRecords.length">
      <view class="section-title">处理记录</view>
      <view class="record-list">
        <view v-for="(record, index) in handleRecords" :key="index" class="record-item">
          <view class="record-dot"></view>
          <view class="record-content">
            <text class="record-title">{{ record.title }}</text>
            <text class="record-desc">{{ record.desc }}</text>
            <text class="record-time">{{ record.time }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 操作按钮 -->
    <view class="action-bar" v-if="faultDetail.status === 'pending'">
      <button class="action-btn" @click="goToWorkOrder">创建工单</button>
      <button class="action-btn primary" @click="markResolved">标记已解决</button>
    </view>
  </view>
</template>

<script>
// @deprecated 当前无入口页面跳转至此——故障列表页面尚未开发。
// 仅保留以备未来工单/故障列表打通后启用，不在业务导航中暴露。
import { getFaultDetail, resolveFaultRecord } from '@/api/request.js'

export default {
  data() {
    return {
      id: '',
      faultDetail: {
        status: 'pending',
        faultNo: '',
        assetName: '',
        assetSn: '',
        faultType: '',
        severity: 'normal',
        faultDesc: '',
        faultPhotos: [],
        createTime: ''
      },
      handleRecords: []
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
        const res = await getFaultDetail(this.id)
        this.faultDetail = { ...this.faultDetail, ...res }
        this.handleRecords = res.records || []
      } catch (e) {
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    },

    getStatusText(status) {
      const map = {
        pending: '待处理',
        processing: '处理中',
        resolved: '已解决',
        closed: '已关闭'
      }
      return map[status] || status
    },

    previewImage(url) {
      uni.previewImage({
        urls: this.faultDetail.faultPhotos,
        current: url
      })
    },

    goToWorkOrder() {
      // 跳转到创建工单页面，携带故障信息
      uni.navigateTo({
        url: `/pages/workOrder/create?faultId=${this.id}&assetId=${this.faultDetail.assetId}`
      })
    },

    async markResolved() {
      try {
        await resolveFaultRecord(this.id)
        uni.showToast({ title: '已标记解决', icon: 'success' })
        this.loadDetail()
      } catch (e) {
        uni.showToast({ title: e.msg || '操作失败', icon: 'none' })
      }
    }
  }
}
</script>

<style scoped>
.fault-detail {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 120rpx;
}

.status-card {
  background: #fff;
  padding: 24rpx 32rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-tag {
  font-size: 26rpx;
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
}

.status-tag.pending {
  background: #fdf6ec;
  color: #e6a23c;
}

.status-tag.processing {
  background: #ecf5ff;
  color: #409eff;
}

.status-tag.resolved {
  background: #f0f9eb;
  color: #67c23a;
}

.fault-no {
  font-size: 24rpx;
  color: #909399;
}

.info-section {
  background: #fff;
  margin: 24rpx;
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

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24rpx;
  margin-bottom: 24rpx;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.info-label {
  font-size: 24rpx;
  color: #909399;
  margin-bottom: 8rpx;
}

.info-value {
  font-size: 28rpx;
  color: #303133;
}

.info-value.priority.urgent {
  color: #f56c6c;
  font-weight: bold;
}

.info-row {
  margin-bottom: 24rpx;
}

.info-content {
  font-size: 28rpx;
  color: #303133;
  line-height: 1.6;
  display: block;
  margin-top: 8rpx;
}

.photo-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 12rpx;
}

.photo-item {
  width: 160rpx;
  height: 160rpx;
  border-radius: 12rpx;
}

.record-list {
  position: relative;
  padding-left: 32rpx;
}

.record-item {
  position: relative;
  padding-bottom: 32rpx;
}

.record-item:last-child {
  padding-bottom: 0;
}

.record-dot {
  position: absolute;
  left: -32rpx;
  top: 8rpx;
  width: 16rpx;
  height: 16rpx;
  background: #2563eb;
  border-radius: 50%;
}

.record-content {
  background: #f5f7fa;
  padding: 16rpx;
  border-radius: 12rpx;
}

.record-title {
  font-size: 28rpx;
  color: #303133;
  font-weight: 500;
  display: block;
}

.record-desc {
  font-size: 26rpx;
  color: #606266;
  margin-top: 8rpx;
  display: block;
}

.record-time {
  font-size: 22rpx;
  color: #909399;
  margin-top: 8rpx;
  display: block;
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
