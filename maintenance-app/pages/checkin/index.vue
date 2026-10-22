<template>
  <view class="checkin-page">
    <!-- GPS状态 -->
    <view class="gps-section">
      <view class="gps-status" :class="{ active: gpsReady }">
        <view class="gps-icon-wrap" :class="{ active: gpsReady }">
          <c-icon :type="gpsReady ? 'location' : 'spinnercycle'" :size="20" :color="gpsReady ? '#fff' : '#909399'" />
        </view>
        <text class="gps-text">{{ gpsReady ? '定位成功' : '定位中...' }}</text>
      </view>
      <view class="gps-coords" v-if="gpsReady">
        <view class="coord-item">
          <text class="coord-label">经度</text>
          <text class="coord-value">{{ location.longitude }}</text>
        </view>
        <view class="coord-item">
          <text class="coord-label">纬度</text>
          <text class="coord-value">{{ location.latitude }}</text>
        </view>
      </view>
      <view class="gps-address" v-if="address">
        <c-icon type="location" :size="16" color="#909399" />
        <text class="address-text">{{ address }}</text>
      </view>
      <button class="refresh-btn" size="mini" @click="getLocation" v-if="gpsReady">
        <c-icon type="refresh" :size="14" color="#fff" />
        <text>刷新定位</text>
      </button>
    </view>

    <!-- 打卡类型 -->
    <view class="type-section">
      <view class="section-title">打卡类型</view>
      <view class="type-options">
        <view
          class="type-option"
          :class="{ selected: checkinType === 'arrival' }"
          @click="checkinType = 'arrival'"
        >
          <c-icon type="location" :size="32" :color="checkinType === 'arrival' ? '#2563eb' : '#909399'" />
          <text class="type-label">到达</text>
        </view>
        <view
          class="type-option"
          :class="{ selected: checkinType === 'departure' }"
          @click="checkinType = 'departure'"
        >
          <c-icon type="undo" :size="32" :color="checkinType === 'departure' ? '#2563eb' : '#909399'" />
          <text class="type-label">离开</text>
        </view>
      </view>
    </view>

    <!-- 关联工单 -->
    <view class="order-section" v-if="relatedOrder">
      <view class="section-title">关联工单</view>
      <view class="order-card">
        <text class="order-no">{{ relatedOrder.workOrderNo }}</text>
        <text class="order-title">{{ relatedOrder.title }}</text>
        <text class="order-customer">
          <c-icon type="person" :size="12" color="#909399" />
          {{ relatedOrder.customerName }}
        </text>
      </view>
    </view>

    <!-- 拍照区域 -->
    <view class="photo-section">
      <view class="section-title">现场拍照</view>
      <view class="photo-area" @click="takePhoto">
        <image
          v-if="photoPath"
          class="photo-preview"
          :src="photoPath"
          mode="aspectFill"
          @click.stop="previewPhoto"
         lazy-load></image>
        <view class="photo-placeholder" v-else>
          <c-icon type="camera" :size="48" color="#d1d5db" />
          <text class="camera-text">点击拍照</text>
        </view>
      </view>
      <view class="watermark-hint" v-if="photoPath">
        <c-icon type="info" :size="14" color="green" />
        <text class="hint-text">照片已添加水印：时间 + 地址</text>
      </view>
    </view>

    <!-- 提交按钮 -->
    <view class="submit-section">
      <button
        class="submit-btn"
        :disabled="!canSubmit || submitting"
        :loading="submitting"
        @click="handleSubmit"
      >
        <c-icon type="checkmark" :size="20" color="#fff" />
        <text>{{ submitting ? '提交中...' : '提交打卡' }}</text>
      </button>
    </view>
  </view>
</template>

<script>
import { createCheckIn } from '@/api/request.js'

export default {
  data() {
    return {
      gpsReady: false,
      location: { longitude: '', latitude: '' },
      address: '',
      checkinType: 'arrival',
      photoPath: '',
      photoPathWithWatermark: '',
      submitting: false,
      relatedOrder: null
    }
  },
  computed: {
    canSubmit() {
      return this.gpsReady
    }
  },
  onLoad(options) {
    if (options.orderId) {
      this.relatedOrder = { id: options.orderId, workOrderNo: options.orderNo, title: options.title, customerName: options.customerName }
    }
    this.getLocation()
  },
  methods: {
    getLocation() {
      uni.showLoading({ title: '正在定位...' })
      uni.getLocation({
        type: 'gcj02',
        success: (res) => {
          this.location = { longitude: res.longitude.toFixed(6), latitude: res.latitude.toFixed(6) }
          this.gpsReady = true
          uni.hideLoading()
          this.getAddress(res.longitude, res.latitude)
        },
        fail: (err) => {
          uni.hideLoading()
          uni.showToast({ title: '定位失败，请开启GPS权限', icon: 'none' })
        }
      })
    },

    getAddress(longitude, latitude) {
      const QQ_MAP_KEY = ''
      if (!QQ_MAP_KEY) {
        this.address = `${longitude.toFixed(4)}, ${latitude.toFixed(4)}`
        return
      }
      uni.request({
        url: `https://apis.map.qq.com/ws/geocoder/v1/?location=${latitude},${longitude}&key=${QQ_MAP_KEY}`,
        success: (res) => {
          if (res.data && res.data.status === 0) {
            this.address = res.data.result.address || res.data.result.formatted_addresses?.recommend || ''
          } else {
            this.address = `${longitude.toFixed(4)}, ${latitude.toFixed(4)}`
          }
        },
        fail: () => { this.address = `${longitude.toFixed(4)}, ${latitude.toFixed(4)}` }
      })
    },

    takePhoto() {
      uni.chooseImage({
        count: 1,
        sourceType: ['camera'],
        success: async (res) => {
          this.photoPath = res.tempFilePaths[0]
          this.photoPathWithWatermark = await this.drawWatermark(this.photoPath)
        }
      })
    },

    previewPhoto() {
      uni.previewImage({ urls: [this.photoPath], current: this.photoPath })
    },

    drawWatermark(filePath) {
      return new Promise((resolve) => {
        uni.getImageInfo({
          src: filePath,
          success: (imgInfo) => {
            const ctx = uni.createCanvasContext('watermarkCanvas')
            const W = imgInfo.width, H = imgInfo.height
            this._watermarkCanvasSize = { width: W, height: H }
            ctx.drawImage(filePath, 0, 0, W, H)
            const now = new Date()
            const timeStr = `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}-${String(now.getDate()).padStart(2,'0')} ${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}:${String(now.getSeconds()).padStart(2,'0')}`
            const addrLine1 = this.address ? this.address.substring(0, 20) : ''
            const addrLine2 = this.address ? this.address.substring(20, 40) : ''
            ctx.setFillStyle('rgba(0,0,0,0.5)')
            const padding = 20, lineH = 36
            const bgH = addrLine2 ? lineH * 3 + padding * 2 : lineH * 2 + padding * 2
            ctx.fillRect(0, H - bgH - 60, W, bgH)
            ctx.setFillStyle('#ffffff')
            ctx.font = `bold ${Math.max(24, Math.floor(W/20))}px sans-serif`
            ctx.fillText(timeStr, padding, H - bgH - 60 + lineH + padding)
            ctx.font = `${Math.max(18, Math.floor(W/28))}px sans-serif`
            ctx.fillText(addrLine1, padding, H - bgH - 60 + lineH * 2 + padding)
            if (addrLine2) ctx.fillText(addrLine2, padding, H - bgH - 60 + lineH * 3 + padding)
            ctx.draw(false, () => {
              uni.canvasToTempFilePath({
                canvasId: 'watermarkCanvas', x: 0, y: 0, width: W, height: H,
                destWidth: W, destHeight: H, fileType: 'jpg', quality: 0.9,
                success: (r) => resolve(r.tempFilePath),
                fail: () => resolve(filePath)
              })
            })
          },
          fail: () => resolve(filePath)
        })
      })
    },

    async handleSubmit() {
      if (!this.gpsReady) { uni.showToast({ title: '请等待定位完成', icon: 'none' }); return }
      this.submitting = true
      uni.showLoading({ title: '提交中...' })
      try {
        const engineerId = uni.getStorageSync('userInfo')?.userId
        const data = {
          engineerId,
          longitude: this.location.longitude,
          latitude: this.location.latitude,
          address: this.address,
          checkType: this.checkinType,
          workOrderId: this.relatedOrder?.id || null
        }
        if (this.photoPath) {
          const { uploadFile } = await import('@/api/request.js')
          const uploadRes = await uploadFile(this.photoPathWithWatermark || this.photoPath)
          data.photoUrl = uploadRes.url || uploadRes.data?.url
        }
        await createCheckIn(data)
        uni.hideLoading()
        uni.showToast({ title: '打卡成功', icon: 'success' })
        setTimeout(() => { uni.navigateBack() }, 1500)
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: e.msg || '打卡失败', icon: 'none' })
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.checkin-page { min-height: 100vh; background: #f5f7fa; padding: 24rpx; padding-bottom: 120rpx; }

.gps-section { background: #fff; border-radius: 16rpx; padding: 32rpx; margin-bottom: 24rpx; }
.gps-status { display: flex; align-items: center; gap: 16rpx; margin-bottom: 24rpx; }
.gps-icon-wrap {
  width: 48rpx; height: 48rpx; border-radius: 50%;
  background: #f0f2f5; display: flex; align-items: center; justify-content: center;
}
.gps-icon-wrap.active { background: #10b981; }
.gps-text { font-size: 28rpx; color: #909399; }
.gps-status.active .gps-text { color: #10b981; }

.gps-coords { display: flex; gap: 32rpx; margin-bottom: 16rpx; }
.coord-item { display: flex; align-items: center; gap: 12rpx; }
.coord-label { font-size: 24rpx; color: #909399; }
.coord-value { font-size: 28rpx; color: #303133; font-family: monospace; }

.gps-address {
  display: flex; align-items: flex-start; gap: 10rpx;
  background: #f5f7fa; border-radius: 12rpx; padding: 16rpx; margin-bottom: 16rpx;
}
.address-text { font-size: 26rpx; color: #606266; line-height: 1.5; flex: 1; }

.refresh-btn {
  background: #2563eb; color: #fff; font-size: 24rpx; border-radius: 24rpx;
  display: flex; align-items: center; gap: 8rpx; padding: 8rpx 20rpx;
}

.section-title { font-size: 30rpx; font-weight: bold; color: #303133; margin-bottom: 20rpx; }
.type-section { background: #fff; border-radius: 16rpx; padding: 32rpx; margin-bottom: 24rpx; }
.type-options { display: flex; gap: 24rpx; }
.type-option {
  flex: 1; display: flex; flex-direction: column; align-items: center; gap: 12rpx;
  padding: 32rpx; border-radius: 16rpx; border: 2rpx solid #ebeef5; transition: all 0.3s;
}
.type-option.selected { border-color: #2563eb; background: #f0f1ff; }
.type-label { font-size: 28rpx; color: #303133; }
.type-option.selected .type-label { color: #2563eb; font-weight: bold; }

.order-section { background: #fff; border-radius: 16rpx; padding: 32rpx; margin-bottom: 24rpx; }
.order-card { background: #f5f7fa; border-radius: 12rpx; padding: 24rpx; }
.order-no { font-size: 26rpx; color: #2563eb; font-weight: bold; display: block; margin-bottom: 8rpx; }
.order-title { font-size: 28rpx; color: #303133; display: block; margin-bottom: 8rpx; }
.order-customer { font-size: 24rpx; color: #909399; display: flex; align-items: center; gap: 6rpx; }

.photo-section { background: #fff; border-radius: 16rpx; padding: 32rpx; margin-bottom: 24rpx; }
.photo-area {
  border: 2rpx dashed #dcdfe6; border-radius: 16rpx; height: 400rpx;
  display: flex; align-items: center; justify-content: center; overflow: hidden; position: relative;
}
.photo-placeholder { display: flex; flex-direction: column; align-items: center; gap: 12rpx; }
.camera-text { font-size: 28rpx; color: #909399; }
.photo-preview { width: 100%; height: 100%; }

.watermark-hint { display: flex; align-items: center; gap: 8rpx; margin-top: 16rpx; }
.hint-text { font-size: 24rpx; color: #67c23a; }

.submit-section {
  position: fixed; bottom: 0; left: 0; right: 0; background: #fff;
  padding: 24rpx 32rpx; padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -4rpx 16rpx rgba(0,0,0,0.08);
}
.submit-btn {
  width: 100%; height: 96rpx;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  border-radius: 48rpx; color: #fff; font-size: 32rpx; font-weight: bold; border: none;
  display: flex; align-items: center; justify-content: center; gap: 12rpx;
}
.submit-btn::after { border: none; }
.submit-btn[disabled] { opacity: 0.5; }
</style>