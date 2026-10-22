<template>
  <view class="asset-detail-page">
    <!-- 加载状态 -->
    <view class="loading-state" v-if="loading">
      <c-icon type="spinnercycle" :size="48" color="#d1d5db" />
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 错误状态 -->
    <view class="error-state" v-else-if="error">
      <c-icon type="info" :size="64" color="red" />
      <text class="error-title">未找到资产</text>
      <text class="error-msg">{{ error }}</text>
      <button class="retry-btn" @click="loadData">重新加载</button>
    </view>

    <!-- 资产详情 -->
    <view class="detail-content" v-else-if="asset">
      <!-- 设备照片 -->
      <view class="asset-photo" v-if="asset.photoUrl">
        <image class="photo" :src="asset.photoUrl" mode="aspectFill" @click="previewPhoto" lazy-load />
      </view>
      <view class="asset-photo placeholder" v-else>
        <c-icon type="image" :size="64" color="#d1d5db" />
        <text class="placeholder-text">暂无设备照片</text>
      </view>

      <!-- 基本信息 -->
      <view class="info-section">
        <view class="section-header">
          <text class="section-title">基本信息</text>
        </view>
        <view class="info-grid">
          <view class="info-item">
            <text class="info-label">设备名称</text>
            <text class="info-value">{{ asset.assetName || '-' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">设备编码</text>
            <text class="info-value code">{{ asset.assetCode || '-' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">设备型号</text>
            <text class="info-value">{{ asset.model || asset.spec || '-' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">设备品牌</text>
            <text class="info-value">{{ asset.brand || '-' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">设备分类</text>
            <text class="info-value">{{ asset.categoryName || asset.category || '-' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">设备状态</text>
            <text class="info-value" :class="getStatusClass(asset.status)">
              {{ getStatusText(asset.status) }}
            </text>
          </view>
        </view>
      </view>

      <!-- 位置信息 -->
      <view class="info-section" v-if="asset.locationName || asset.location || asset.address">
        <view class="section-header">
          <text class="section-title">位置信息</text>
        </view>
        <view class="info-grid">
          <view class="info-item full">
            <text class="info-label">安装位置</text>
            <text class="info-value location">{{ asset.locationName || asset.location || asset.address || '-' }}</text>
          </view>
          <view class="info-item" v-if="asset.area">
            <text class="info-label">所属区域</text>
            <text class="info-value">{{ asset.area }}</text>
          </view>
          <view class="info-item" v-if="asset.floor">
            <text class="info-label">楼层</text>
            <text class="info-value">{{ asset.floor }}</text>
          </view>
        </view>
        <view class="map-preview" v-if="asset.longitude && asset.latitude" @click="openMap">
          <map
            class="mini-map"
            :latitude="Number(asset.latitude)"
            :longitude="Number(asset.longitude)"
            :markers="[{ id: 1, latitude: Number(asset.latitude), longitude: Number(asset.longitude), iconPath: '/static/location.png' }]"
            :scale="15"
            :show-location="true"
          ></map>
          <view class="map-overlay">
            <c-icon type="location" :size="16" color="#fff" />
            <text class="map-text">点击查看地图</text>
          </view>
        </view>
      </view>

      <!-- 客户信息 -->
      <view class="info-section" v-if="asset.customerName">
        <view class="section-header">
          <text class="section-title">关联客户</text>
        </view>
        <view class="info-grid">
          <view class="info-item full">
            <text class="info-label">客户名称</text>
            <text class="info-value">{{ asset.customerName }}</text>
          </view>
          <view class="info-item" v-if="asset.customerContact">
            <text class="info-label">联系人</text>
            <text class="info-value">{{ asset.customerContact }}</text>
          </view>
          <view class="info-item" v-if="asset.customerPhone">
            <text class="info-label">联系电话</text>
            <text class="info-value phone" @click="callPhone(asset.customerPhone)">
              {{ asset.customerPhone }}
              <c-icon type="phone" :size="14" color="blue" style="margin-left:4rpx" />
            </text>
          </view>
        </view>
      </view>

      <!-- 维保信息 -->
      <view class="info-section" v-if="asset.maintenanceDate || asset.warrantyDate">
        <view class="section-header">
          <text class="section-title">维保信息</text>
        </view>
        <view class="info-grid">
          <view class="info-item" v-if="asset.maintenanceDate">
            <text class="info-label">上次维保</text>
            <text class="info-value">{{ asset.maintenanceDate }}</text>
          </view>
          <view class="info-item" v-if="asset.warrantyDate">
            <text class="info-label">保修到期</text>
            <text class="info-value" :class="{ warning: isWarrantyExpiring }">
              {{ asset.warrantyDate }}
              <text v-if="isWarrantyExpiring" class="warn-tag">即将到期</text>
            </text>
          </view>
        </view>
      </view>

      <!-- 快捷操作 -->
      <view class="action-section">
        <button class="action-btn primary" @click="goFaultReport">
          <c-icon type="flag" :size="20" color="#fff" />
          <text>故障上报</text>
        </button>
        <button class="action-btn" @click="goInspection">
          <c-icon type="search" :size="20" color="gray" />
          <text>巡检执行</text>
        </button>
        <button class="action-btn" @click="goCheckIn">
          <c-icon type="location" :size="20" color="gray" />
          <text>GPS打卡</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import { scanAsset } from '@/api/request.js'

export default {
  data() {
    return {
      qrCode: '',
      asset: null,
      loading: false,
      error: ''
    }
  },
  computed: {
    isWarrantyExpiring() {
      if (!this.asset?.warrantyDate) return false
      const expDate = new Date(this.asset.warrantyDate)
      const now = new Date()
      const diff = expDate - now
      return diff > 0 && diff < 30 * 86400000
    }
  },
  onLoad(options) {
    if (options.qrCode) {
      this.qrCode = options.qrCode
      this.loadData()
    } else if (options.assetCode) {
      this.qrCode = options.assetCode
      this.loadData()
    } else {
      this.error = '缺少设备编码参数'
    }
  },
  methods: {
    async loadData() {
      this.loading = true
      this.error = ''
      try {
        const res = await scanAsset(this.qrCode)
        this.asset = res.data || res
      } catch (e) {
        this.error = e.msg || '未找到该设备'
      } finally {
        this.loading = false
      }
    },

    getStatusText(status) {
      const map = { '0': '正常', '1': '运行中', '2': '故障', '3': '维修中', '4': '停机', '5': '报废' }
      return map[status] || status || '-'
    },

    getStatusClass(status) {
      const map = {
        '0': 'status-normal', '1': 'status-running', '2': 'status-fault',
        '3': 'status-repair', '4': 'status-stop', '5': 'status-scrap'
      }
      return map[status] || ''
    },

    previewPhoto() {
      if (this.asset?.photoUrl) uni.previewImage({ urls: [this.asset.photoUrl] })
    },

    openMap() {
      if (!this.asset?.latitude || !this.asset?.longitude) return
      uni.openLocation({
        latitude: Number(this.asset.latitude),
        longitude: Number(this.asset.longitude),
        name: this.asset.locationName || this.asset.assetName,
        address: this.asset.address || '',
        fail: () => { uni.showToast({ title: '无法打开地图', icon: 'none' }) }
      })
    },

    callPhone(phone) {
      uni.makePhoneCall({ phoneNumber: phone, fail: () => {} })
    },

    goFaultReport() {
      const code = this.asset?.assetCode || this.qrCode
      uni.navigateTo({ url: `/pages/fault/report?assetCode=${encodeURIComponent(code)}` })
    },

    goInspection() {
      uni.navigateTo({
        url: `/pages/inspection/pointExecute?assetId=${this.asset?.assetId || ''}&assetCode=${encodeURIComponent(this.qrCode)}`
      })
    },

    goCheckIn() {
      uni.navigateTo({ url: `/pages/checkin/index?assetCode=${encodeURIComponent(this.qrCode)}` })
    }
  }
}
</script>

<style scoped>
.asset-detail-page { min-height: 100vh; background: #f5f7fa; padding-bottom: 120rpx; }

.loading-state, .error-state {
  display: flex; flex-direction: column; align-items: center; padding: 120rpx 0; gap: 16rpx;
}

.loading-text { font-size: 28rpx; color: #909399; }
.error-title { font-size: 32rpx; color: #303133; font-weight: bold; }
.error-msg { font-size: 26rpx; color: #909399; }

.retry-btn {
  background: #2563eb; color: #fff; border: none; border-radius: 24rpx;
  padding: 16rpx 48rpx; font-size: 28rpx;
}

.asset-photo { height: 400rpx; background: #fff; }
.asset-photo .photo { width: 100%; height: 100%; }
.asset-photo.placeholder { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8rpx; }
.placeholder-text { font-size: 28rpx; color: #c0c4cc; }

.info-section { background: #fff; margin: 24rpx; border-radius: 16rpx; padding: 32rpx; }
.section-header { margin-bottom: 24rpx; }
.section-title { font-size: 32rpx; font-weight: bold; color: #303133; padding-left: 16rpx; border-left: 6rpx solid #2563eb; }

.info-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 24rpx; }
.info-item { display: flex; flex-direction: column; gap: 6rpx; }
.info-item.full { grid-column: 1 / -1; }
.info-label { font-size: 24rpx; color: #909399; }
.info-value { font-size: 28rpx; color: #303133; }
.info-value.code { font-family: monospace; color: #2563eb; }
.info-value.location { font-size: 26rpx; line-height: 1.5; }
.info-value.phone { color: #409eff; display: flex; align-items: center; }
.status-normal { color: #67c23a; }
.status-running { color: #409eff; }
.status-fault { color: #f56c6c; }
.status-repair { color: #e6a23c; }
.status-stop { color: #909399; }
.status-scrap { color: #c0c4cc; }
.warning { color: #e6a23c !important; }
.warn-tag { font-size: 20rpx; background: #fef0f0; color: #f56c6c; padding: 2rpx 8rpx; border-radius: 6rpx; margin-left: 8rpx; }

.map-preview { position: relative; height: 300rpx; border-radius: 12rpx; overflow: hidden; margin-top: 20rpx; }
.mini-map { width: 100%; height: 100%; }
.map-overlay {
  position: absolute; bottom: 0; left: 0; right: 0;
  background: rgba(0,0,0,0.5); padding: 16rpx;
  display: flex; align-items: center; justify-content: center; gap: 8rpx;
}
.map-text { font-size: 24rpx; color: #fff; }

.action-section { padding: 24rpx; display: flex; flex-direction: column; gap: 16rpx; }
.action-btn {
  width: 100%; height: 96rpx; background: #fff; border-radius: 48rpx;
  color: #606266; font-size: 30rpx; border: none;
  display: flex; align-items: center; justify-content: center; gap: 8rpx;
}
.action-btn::after { border: none; }
.action-btn:active { opacity: 0.8; }
.action-btn.primary { background: linear-gradient(135deg, #f56c6c 0%, #e6a23c 100%); color: #fff; }
</style>