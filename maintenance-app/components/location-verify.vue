<template>
  <view class="location-verify-wrapper">
    <!-- 目标位置信息 -->
    <view class="target-info" v-if="targetAddress || targetLat">
      <view class="info-row">
        <text class="info-icon">📍</text>
        <view class="info-content">
          <text class="info-label">目标位置</text>
          <text class="info-value" v-if="targetAddress">{{ targetAddress }}</text>
          <text class="info-value coords" v-if="targetLat && targetLng">
            {{ parseFloat(targetLat).toFixed(6) }}, {{ parseFloat(targetLng).toFixed(6) }}
          </text>
        </view>
      </view>
      <view class="info-row" v-if="scanRadius">
        <text class="info-icon">🎯</text>
        <view class="info-content">
          <text class="info-label">允许范围</text>
          <text class="info-value">{{ scanRadius }} 米</text>
        </view>
      </view>
    </view>

    <!-- 当前GPS状态 -->
    <view class="gps-section">
      <view class="gps-header">
        <view class="gps-status" :class="{ success: locationReady, fail: locationFailed }">
          <text class="status-icon">{{ locationReady ? '✅' : locationFailed ? '❌' : '📡' }}</text>
          <text class="status-text">{{ locationStatusText }}</text>
        </view>
        <button
          class="refresh-btn"
          size="mini"
          :loading="locating"
          @click="startLocation"
          :disabled="locating"
        >
          {{ locating ? '定位中...' : '刷新定位' }}
        </button>
      </view>

      <!-- 当前坐标 -->
      <view class="current-coords" v-if="currentLocation">
        <view class="coord-item">
          <text class="coord-label">经度</text>
          <text class="coord-value">{{ currentLocation.longitude.toFixed(6) }}</text>
        </view>
        <view class="coord-divider"></view>
        <view class="coord-item">
          <text class="coord-label">纬度</text>
          <text class="coord-value">{{ currentLocation.latitude.toFixed(6) }}</text>
        </view>
      </view>

      <!-- 距离计算 -->
      <view class="distance-row" v-if="currentLocation && targetLat && targetLng">
        <text class="distance-label">距离目标</text>
        <text class="distance-value" :class="{ near: isWithinRange, far: !isWithinRange }">
          {{ formatDistance(distance) }}
        </text>
      </view>
    </view>

    <!-- 验证结果 -->
    <view class="verify-result" v-if="showResult">
      <view class="result-badge" :class="{ pass: verifyPassed, fail: !verifyPassed }">
        <text class="result-icon">{{ verifyPassed ? '✅' : '❌' }}</text>
        <text class="result-text">{{ verifyPassed ? '验证通过' : '验证失败' }}</text>
      </view>
      <text class="result-reason" v-if="verifyMessage">{{ verifyMessage }}</text>
    </view>

    <!-- 操作按钮 -->
    <view class="action-bar" v-if="showActions">
      <button
        class="action-btn action-btn-primary"
        :disabled="!canVerify"
        @click="handleVerify"
      >
        提交验证
      </button>
      <button
        class="action-btn action-btn-scan"
        v-if="showScanButton"
        @click="handleScan"
      >
        扫码验证
      </button>
    </view>
  </view>
</template>

<script>
/**
 * location-verify.vue - 位置验证组件
 * 功能：显示当前GPS坐标，计算与目标坐标的距离，支持扫码验证
 * props: targetLat, targetLng, targetAddress, scanRadius
 * 显示绿色"验证通过"或红色"验证失败"状态
 * methods: startLocation() 开始定位, verifyScan(qrCode) 扫码验证
 */
export default {
  name: 'location-verify',
  props: {
    // 目标纬度
    targetLat: {
      type: [Number, String],
      default: null
    },
    // 目标经度
    targetLng: {
      type: [Number, String],
      default: null
    },
    // 目标地址名称
    targetAddress: {
      type: String,
      default: ''
    },
    // 允许扫描的范围（米）
    scanRadius: {
      type: Number,
      default: 100
    },
    // 是否显示操作按钮
    showActions: {
      type: Boolean,
      default: true
    },
    // 是否显示扫码按钮
    showScanButton: {
      type: Boolean,
      default: true
    },
    // 定位类型
    locationType: {
      type: String,
      default: 'gcj02' // gcj02 或 wgs84
    }
  },
  data() {
    return {
      currentLocation: null,
      locating: false,
      locationFailed: false,
      showResult: false,
      verifyPassed: false,
      verifyMessage: '',
      _watchId: null
    }
  },
  computed: {
    locationReady() {
      return !!this.currentLocation
    },
    locationStatusText() {
      if (this.locating) return '定位中...'
      if (this.locationFailed) return '定位失败'
      if (this.currentLocation) return '定位成功'
      return '等待定位'
    },
    canVerify() {
      return this.locationReady && this.targetLat && this.targetLng
    },
    distance() {
      if (!this.currentLocation || !this.targetLat || !this.targetLng) return null
      return this.calcDistance(
        parseFloat(this.currentLocation.latitude),
        parseFloat(this.currentLocation.longitude),
        parseFloat(this.targetLat),
        parseFloat(this.targetLng)
      )
    },
    isWithinRange() {
      return this.distance !== null && this.distance <= this.scanRadius
    }
  },
  mounted() {
    this.startLocation()
  },
  beforeDestroy() {
    this.stopLocationWatch()
  },
  methods: {
    /**
     * 开始定位
     */
    startLocation() {
      this.locating = true
      this.locationFailed = false
      this.showResult = false

      uni.getLocation({
        type: this.locationType,
        success: (res) => {
          this.currentLocation = {
            latitude: res.latitude,
            longitude: res.longitude,
            accuracy: res.accuracy,
            address: res.address || ''
          }
          this.locating = false
          this.locationFailed = false
          this.$emit('location-update', this.currentLocation)
        },
        fail: (err) => {
          this.locating = false
          this.locationFailed = true
          this.$emit('location-fail', err)
          uni.showToast({
            title: '定位失败，请检查GPS权限',
            icon: 'none'
          })
        }
      })
    },

    /**
     * 停止监听位置变化
     */
    stopLocationWatch() {
      if (this._watchId !== null) {
        uni.stopLocation({})
        this._watchId = null
      }
    },

    /**
     * 计算两点之间的距离（米）- Haversine公式
     */
    calcDistance(lat1, lon1, lat2, lon2) {
      const R = 6371000 // 地球半径（米）
      const dLat = this.toRad(lat2 - lat1)
      const dLon = this.toRad(lon2 - lon1)
      const a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(this.toRad(lat1)) * Math.cos(this.toRad(lat2)) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2)
      const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
      return R * c
    },

    toRad(deg) {
      return deg * (Math.PI / 180)
    },

    /**
     * 格式化距离
     */
    formatDistance(meters) {
      if (meters === null || meters === undefined) return '-'
      if (meters < 1000) {
        return `${Math.round(meters)} 米`
      }
      return `${(meters / 1000).toFixed(2)} 公里`
    },

    /**
     * 提交验证
     */
    handleVerify() {
      if (!this.canVerify) {
        uni.showToast({ title: '请等待定位完成', icon: 'none' })
        return
      }

      const passed = this.isWithinRange
      this.verifyPassed = passed
      this.showResult = true

      if (passed) {
        this.verifyMessage = `当前位置距目标${this.formatDistance(this.distance)}，在允许范围内`
      } else {
        this.verifyMessage = `当前位置距目标${this.formatDistance(this.distance)}，超出允许范围${this.scanRadius}米`
      }

      this.$emit('verify', {
        passed,
        distance: this.distance,
        currentLocation: this.currentLocation
      })

      if (passed) {
        uni.showToast({ title: '验证通过', icon: 'success' })
      } else {
        uni.showToast({ title: '距离超出允许范围', icon: 'none' })
      }
    },

    /**
     * 扫码验证
     */
    handleScan() {
      // #ifdef MP-WEIXIN
      uni.scanCode({
        onlyFromCamera: true,
        success: (res) => {
          this.verifyScan(res.result || res.scanStr)
        },
        fail: () => {
          uni.showToast({ title: '扫码失败', icon: 'none' })
        }
      })
      // #endif
      // #ifndef MP-WEIXIN
      uni.scanCode({
        onlyFromCamera: true,
        success: (res) => {
          this.verifyScan(res.result || res.scanStr)
        },
        fail: () => {
          uni.showToast({ title: '扫码失败', icon: 'none' })
        }
      })
      // #endif
    },

    /**
     * 处理二维码扫描结果进行验证
     * @param {string} qrCode - 扫码结果
     */
    verifyScan(qrCode) {
      if (!qrCode) {
        uni.showToast({ title: '扫码内容为空', icon: 'none' })
        return
      }

      let targetCode = `${this.targetLat},${this.targetLng}`

      // 如果二维码包含目标坐标信息，解析后与当前定位比较
      // 这里根据实际二维码格式进行解析
      // 示例：二维码内容为 "lat,lng,address" 格式
      let scanPassed = false
      let scanMessage = ''

      try {
        const parts = qrCode.split(',')
        if (parts.length >= 2) {
          const scanLat = parseFloat(parts[0])
          const scanLng = parseFloat(parts[1])

          if (!isNaN(scanLat) && !isNaN(scanLng)) {
            const distFromQR = this.calcDistance(
              parseFloat(this.currentLocation.latitude),
              parseFloat(this.currentLocation.longitude),
              scanLat,
              scanLng
            )

            scanPassed = distFromQR <= this.scanRadius
            scanMessage = scanPassed
              ? `扫码目标距离${this.formatDistance(distFromQR)}，验证通过`
              : `扫码目标距离${this.formatDistance(distFromQR)}，超出允许范围`
          }
        }

        // 与预期目标坐标比较
        const distFromTarget = this.calcDistance(
          parseFloat(this.currentLocation.latitude),
          parseFloat(this.currentLocation.longitude),
          parseFloat(this.targetLat),
          parseFloat(this.targetLng)
        )

        if (distFromTarget <= this.scanRadius) {
          scanPassed = true
          scanMessage = `扫码验证通过，当前位置在允许范围内`
        } else {
          scanPassed = false
          scanMessage = `扫码验证失败，距离目标${this.formatDistance(distFromTarget)}，超出允许范围`
        }
      } catch (e) {
        scanPassed = false
        scanMessage = '二维码格式解析失败'
      }

      this.verifyPassed = scanPassed
      this.showResult = true
      this.verifyMessage = scanMessage

      this.$emit('scan-verify', {
        passed: scanPassed,
        message: scanMessage,
        qrCode
      })

      if (scanPassed) {
        uni.showToast({ title: '扫码验证通过', icon: 'success' })
      } else {
        uni.showToast({ title: '扫码验证失败', icon: 'none' })
      }
    },

    /**
     * 重置验证状态
     */
    reset() {
      this.showResult = false
      this.verifyPassed = false
      this.verifyMessage = ''
    }
  }
}
</script>

<style scoped>
.location-verify-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0;
}

/* 目标位置信息 */
.target-info {
  background: #f5f7fa;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  margin-bottom: 24rpx;
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.info-row {
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
}

.info-icon {
  font-size: 28rpx;
  flex-shrink: 0;
  margin-top: 2rpx;
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
  flex: 1;
}

.info-label {
  font-size: 22rpx;
  color: #909399;
}

.info-value {
  font-size: 26rpx;
  color: #303133;
  font-weight: bold;
}

.info-value.coords {
  font-family: monospace;
  font-size: 24rpx;
  color: #667eea;
  font-weight: normal;
}

/* GPS 区块 */
.gps-section {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  border: 1rpx solid #ebeef5;
  margin-bottom: 24rpx;
}

.gps-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.gps-status {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.status-icon {
  font-size: 32rpx;
}

.status-text {
  font-size: 28rpx;
  color: #909399;
}

.gps-status.success .status-text {
  color: #67c23a;
}

.gps-status.fail .status-text {
  color: #f56c6c;
}

.refresh-btn {
  background: #667eea;
  color: #fff;
  font-size: 24rpx;
  border: none;
}

.current-coords {
  display: flex;
  align-items: center;
  background: #f5f7fa;
  border-radius: 12rpx;
  padding: 16rpx 20rpx;
  margin-bottom: 16rpx;
}

.coord-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.coord-label {
  font-size: 22rpx;
  color: #909399;
  margin-bottom: 6rpx;
}

.coord-value {
  font-size: 28rpx;
  color: #303133;
  font-family: monospace;
  font-weight: bold;
}

.coord-divider {
  width: 1rpx;
  height: 50rpx;
  background: #dcdfe6;
  margin: 0 16rpx;
}

.distance-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16rpx;
  border-top: 1rpx solid #ebeef5;
}

.distance-label {
  font-size: 26rpx;
  color: #909399;
}

.distance-value {
  font-size: 30rpx;
  font-weight: bold;
}

.distance-value.near {
  color: #67c23a;
}

.distance-value.far {
  color: #f56c6c;
}

/* 验证结果 */
.verify-result {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 24rpx;
  border: 1rpx solid #ebeef5;
}

.result-badge {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 12rpx 32rpx;
  border-radius: 100rpx;
  font-size: 30rpx;
  font-weight: bold;
}

.result-badge.pass {
  background: #f0f9eb;
  color: #67c23a;
}

.result-badge.fail {
  background: #fef0f0;
  color: #f56c6c;
}

.result-icon {
  font-size: 36rpx;
}

.result-reason {
  font-size: 24rpx;
  color: #909399;
  text-align: center;
}

/* 操作按钮 */
.action-bar {
  display: flex;
  gap: 16rpx;
}

.action-btn {
  flex: 1;
  height: 88rpx;
  border-radius: 44rpx;
  font-size: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}

.action-btn-primary {
  background: #667eea;
  color: #fff;
}

.action-btn-primary[disabled] {
  background: #c0c4cc;
  color: #fff;
}

.action-btn-scan {
  background: #ecf5ff;
  color: #409eff;
  flex: 0 0 auto;
  padding: 0 32rpx;
}
</style>
