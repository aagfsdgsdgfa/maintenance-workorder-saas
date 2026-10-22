<template>
  <scroll-view scroll-y class="execute-page">
    <!-- 加载状态 -->
    <view class="loading-wrap" v-if="loading">
      <view class="skeleton-card">
        <view class="skeleton-line long"></view>
        <view class="skeleton-line medium"></view>
        <view class="skeleton-line short"></view>
      </view>
    </view>

    <template v-else-if="taskInfo">
      <!-- 任务概要卡片 -->
      <view class="section-card summary-card">
        <view class="summary-header">
          <view class="task-name">{{ taskInfo.workOrderTitle || taskInfo.taskName || '巡检任务' }}</view>
          <view class="status-badge status-0">待巡检</view>
        </view>
        <view class="divider"></view>
        <view class="summary-grid">
          <view class="summary-item">
            <text class="slabel">关联客户</text>
            <text class="svalue">{{ taskInfo.customerName || '—' }}</text>
          </view>
          <view class="summary-item">
            <text class="slabel">设备名称</text>
            <text class="svalue">{{ taskInfo.assetName || '—' }}</text>
          </view>
          <view class="summary-item">
            <text class="slabel">计划时间</text>
            <text class="svalue">{{ taskInfo.planTime || '—' }}</text>
          </view>
          <view class="summary-item">
            <text class="slabel">巡检进度</text>
            <text class="svalue highlight">
              {{ taskInfo.inspectedCount || 0 }}/{{ taskInfo.totalPointCount || 0 }} 个点
            </text>
          </view>
        </view>
      </view>

      <!-- GPS打卡区 -->
      <view class="section-card">
        <view class="section-header">
          <text class="section-icon">📍</text>
          <text class="section-title">GPS定位</text>
          <view :class="['verify-badge', gpsVerified ? 'ok' : 'fail']">
            {{ gpsVerified ? '已定位' : '未定位' }}
          </view>
        </view>

        <view class="gps-info" v-if="gpsLocation.lat">
          <view class="gps-row">
            <text class="gps-label">当前位置</text>
            <text class="gps-value">{{ gpsLocation.lat.toFixed(6) }}, {{ gpsLocation.lng.toFixed(6) }}</text>
          </view>
        </view>

        <view class="gps-tip" v-if="!gpsLocation.lat">
          <text>请点击下方按钮获取GPS定位</text>
        </view>

        <view class="action-btn primary" @click="getGps">
          <text>{{ gpsLoading ? '定位中...' : '获取GPS定位' }}</text>
        </view>
      </view>

      <!-- 现场照片区 -->
      <view class="section-card">
        <view class="section-header">
          <text class="section-icon">📷</text>
          <text class="section-title">现场照片</text>
          <text class="photo-count">{{ photoList.length }}张</text>
        </view>

        <view class="photo-tip-box">
          <text class="tip-text">请拍摄至少1张现场照片（必填）</text>
        </view>

        <view class="photo-grid">
          <view
            v-for="(photo, index) in photoList"
            :key="index"
            class="photo-item"
          >
            <image
              class="photo-img"
              :src="photo.url"
              mode="aspectFill"
              @click="previewPhoto(photo.url)" lazy-load />
            <view class="photo-del" @click="removePhoto(index)">
              <text>×</text>
            </view>
          </view>

          <view
            class="photo-add"
            v-if="photoList.length < maxPhotos"
            @click="choosePhoto"
          >
            <text class="add-icon">+</text>
            <text class="add-text">拍摄照片</text>
          </view>
        </view>
      </view>

      <!-- 提交按钮 -->
      <view class="submit-section">
        <view
          :class="['submit-btn', { disabled: !canSubmit }]"
          @click="handleSubmit"
        >
          <text>{{ submitting ? '提交中...' : '确认到达，开始巡检' }}</text>
        </view>
      </view>
    </template>

    <!-- 错误状态 -->
    <view class="error-state" v-if="!loading && !taskInfo">
      <text class="empty-title">加载失败</text>
      <view class="retry-btn" @click="loadTask">重新加载</view>
    </view>
  </scroll-view>
</template>

<script>
import { getInspectionTaskDetail, executeInspectionTask, uploadFile } from '@/api/request.js'

export default {
  data() {
    return {
      loading: false,
      submitting: false,
      taskId: '',
      taskInfo: null,

      // GPS
      gpsLocation: { lat: null, lng: null },
      gpsLoading: false,
      gpsVerified: false,

      // 照片
      photoList: [],
      maxPhotos: 6
    }
  },

  computed: {
    canSubmit() {
      return (
        this.gpsVerified &&
        this.photoList.length > 0 &&
        !this.submitting
      )
    }
  },

  onLoad(options) {
    this.taskId = options.id
    this.loadTask()
  },

  methods: {
    async loadTask() {
      if (!this.taskId) return
      this.loading = true
      try {
        const res = await getInspectionTaskDetail(this.taskId)
        this.taskInfo = res.data || res
      } catch (e) {
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },

    // ========== GPS定位 ==========

    async getGps() {
      if (this.gpsLoading) return
      this.gpsLoading = true
      try {
        await new Promise((resolve, reject) => {
          uni.getLocation({
            type: 'gcj02',
            success: (loc) => {
              this.gpsLocation = { lat: loc.latitude, lng: loc.longitude }
              this.gpsVerified = true
              resolve(loc)
            },
            fail: () => {
              uni.showToast({ title: '获取定位失败，请检查定位权限', icon: 'none' })
              reject(new Error('location failed'))
            }
          })
        })
        uni.showToast({ title: 'GPS定位成功', icon: 'success' })
      } catch (e) {
        this.gpsVerified = false
      } finally {
        this.gpsLoading = false
      }
    },

    // ========== 照片处理 ==========

    choosePhoto() {
      if (this.photoList.length >= this.maxPhotos) {
        uni.showToast({ title: `最多${this.maxPhotos}张照片`, icon: 'none' })
        return
      }

      uni.chooseImage({
        count: this.maxPhotos - this.photoList.length,
        sizeType: ['compressed'],
        sourceType: ['camera'],
        success: async (res) => {
          const tempFiles = res.tempFilePaths || res.tempFiles
          uni.showLoading({ title: '处理中...' })
          try {
            for (const filePath of tempFiles) {
              if (this.photoList.length >= this.maxPhotos) break
              this.photoList.push({ url: filePath, uploaded: false })
            }
          } finally {
            uni.hideLoading()
          }
        },
        fail: () => {}
      })
    },

    previewPhoto(url) {
      uni.previewImage({ urls: [url] })
    },

    removePhoto(index) {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这张照片吗？',
        success: (res) => {
          if (res.confirm) {
            this.photoList.splice(index, 1)
          }
        }
      })
    },

    // ========== 提交 ==========

    async handleSubmit() {
      if (!this.canSubmit) {
        if (!this.gpsVerified) {
          uni.showToast({ title: '请先获取GPS定位', icon: 'none' })
        } else if (this.photoList.length === 0) {
          uni.showToast({ title: '请拍摄至少1张现场照片', icon: 'none' })
        }
        return
      }

      uni.showModal({
        title: '确认到达',
        content: '确认已到达现场并拍摄照片，即将开始巡检？',
        success: async (res) => {
          if (!res.confirm) return
          await this.doSubmit()
        }
      })
    },

    async doSubmit() {
      this.submitting = true

      try {
        // 1. 上传照片
        uni.showLoading({ title: '上传照片...' })
        const photoUrls = []
        for (const photo of this.photoList) {
          if (photo.uploaded) {
            photoUrls.push(photo.url)
            continue
          }
          try {
            const uploadRes = await uploadFile(photo.url)
            const url = uploadRes.url || uploadRes.data?.url || photo.url
            photoUrls.push(url)
          } catch (e) {
            photoUrls.push(photo.url)
          }
        }
        uni.hideLoading()

        // 2. 调用执行接口
        uni.showLoading({ title: '提交中...' })
        const locationStr = this.gpsLocation.lat
          ? `${this.gpsLocation.lat.toFixed(6)},${this.gpsLocation.lng.toFixed(6)}`
          : ''

        await executeInspectionTask({
          taskId: this.taskId,
          checkInLocation: locationStr,
          checkInPhoto: photoUrls.join(','),
          checkInAddress: ''
        })
        uni.hideLoading()

        uni.showToast({ title: '已开始巡检', icon: 'success' })

        // 跳回任务详情页
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: e.msg || '提交失败，请重试', icon: 'none' })
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.execute-page {
  height: 100vh;
  background: #f5f7fa;
  padding-bottom: 40rpx;
}

.loading-wrap { padding: 24rpx; }
.skeleton-card { background: #fff; border-radius: 16rpx; padding: 28rpx; margin-bottom: 20rpx; }
.skeleton-line {
  height: 28rpx; background: linear-gradient(90deg, #f0f2f5 25%, #e6e8eb 50%, #f0f2f5 75%);
  background-size: 200% 100%; border-radius: 8rpx; margin-bottom: 20rpx; animation: shimmer 1.5s infinite;
}
.skeleton-line.long { width: 70%; }
.skeleton-line.medium { width: 50%; }
.skeleton-line.short { width: 35%; }
@keyframes shimmer { 0% { background-position: 200% 0; } 100% { background-position: -200% 0; } }

.section-card {
  background: #fff;
  margin: 24rpx;
  border-radius: 16rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
}

.summary-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20rpx; }
.task-name { font-size: 34rpx; font-weight: 700; color: #303133; flex: 1; margin-right: 16rpx; line-height: 1.4; }
.status-badge { font-size: 22rpx; padding: 6rpx 16rpx; border-radius: 20rpx; white-space: nowrap; flex-shrink: 0; background: #fff7ed; color: #f59e0b; }
.divider { height: 1rpx; background: #ebeef5; margin-bottom: 20rpx; }
.summary-grid { display: flex; flex-wrap: wrap; }
.summary-item { width: 50%; display: flex; flex-direction: column; margin-bottom: 20rpx; }
.slabel { font-size: 24rpx; color: #909399; margin-bottom: 6rpx; }
.svalue { font-size: 28rpx; color: #303133; font-weight: 500; }
.svalue.highlight { color: #2563eb; }

.section-header { display: flex; align-items: center; margin-bottom: 20rpx; gap: 8rpx; }
.section-icon { font-size: 32rpx; }
.section-title { font-size: 32rpx; font-weight: 600; color: #303133; flex: 1; }

.verify-badge { font-size: 22rpx; padding: 6rpx 16rpx; border-radius: 20rpx; }
.verify-badge.ok { background: #f0f9eb; color: #67c23a; }
.verify-badge.fail { background: #fef0e6; color: #e6a23c; }

.gps-info { background: #f5f7fa; border-radius: 12rpx; padding: 20rpx; margin-bottom: 20rpx; }
.gps-row { display: flex; justify-content: space-between; align-items: center; }
.gps-label { font-size: 26rpx; color: #909399; }
.gps-value { font-size: 26rpx; color: #303133; font-family: monospace; }

.gps-tip { font-size: 26rpx; color: #c0c4cc; text-align: center; padding: 20rpx 0; }

.action-btn {
  height: 88rpx;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
  font-weight: 600;
  transition: opacity 0.2s;
}
.action-btn:active { opacity: 0.8; }
.action-btn.primary { background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%); color: #fff; }

.photo-count { font-size: 24rpx; color: #909399; }

.photo-tip-box {
  background: #fff7ed;
  border-radius: 10rpx;
  padding: 16rpx 20rpx;
  margin-bottom: 20rpx;
}
.tip-text { font-size: 26rpx; color: #f59e0b; }

.photo-grid { display: flex; flex-wrap: wrap; gap: 16rpx; }
.photo-item {
  width: calc((100% - 32rpx) / 3);
  aspect-ratio: 1;
  border-radius: 12rpx;
  overflow: hidden;
  position: relative;
}
.photo-img { width: 100%; height: 100%; }
.photo-del {
  position: absolute; top: 4rpx; right: 4rpx;
  width: 40rpx; height: 40rpx;
  background: rgba(0,0,0,0.6);
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 32rpx; font-weight: bold; line-height: 1;
}
.photo-add {
  width: calc((100% - 32rpx) / 3);
  aspect-ratio: 1;
  border-radius: 12rpx;
  border: 2rpx dashed #c0c4cc;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  background: #fafafa;
}
.add-icon { font-size: 56rpx; color: #c0c4cc; line-height: 1; }
.add-text { font-size: 22rpx; color: #909399; margin-top: 8rpx; }

.submit-section { padding: 0 24rpx 40rpx; }
.submit-btn {
  height: 96rpx;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  border-radius: 48rpx;
  display: flex; align-items: center; justify-content: center;
  font-size: 32rpx; font-weight: 600; color: #fff;
  transition: opacity 0.2s;
}
.submit-btn:active { opacity: 0.85; }
.submit-btn.disabled { background: #c0c4cc; opacity: 0.7; }

.error-state { display: flex; flex-direction: column; align-items: center; padding: 160rpx 0; gap: 16rpx; }
.empty-title { font-size: 32rpx; font-weight: 600; color: #303133; }
.retry-btn { margin-top: 24rpx; padding: 16rpx 48rpx; background: #2563eb; color: #fff; border-radius: 40rpx; font-size: 28rpx; }
</style>
