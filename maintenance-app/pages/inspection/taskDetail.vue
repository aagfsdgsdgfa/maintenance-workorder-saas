<template>
  <scroll-view scroll-y class="detail-page">
    <!-- 加载状态 -->
    <view class="loading-wrap" v-if="loading">
      <view class="skeleton-card">
        <view class="skeleton-line long"></view>
        <view class="skeleton-line medium"></view>
        <view class="skeleton-line short"></view>
      </view>
    </view>

    <template v-else-if="taskDetail">
      <!-- 任务概要卡片 -->
      <view class="section-card summary-card">
        <view class="summary-header">
          <view class="task-name">{{ taskDetail.workOrderTitle || taskDetail.taskName || '巡检任务' }}</view>
          <view :class="['status-badge', `status-${taskDetail.status}`]">
            {{ statusMap[taskDetail.status] }}
          </view>
        </view>

        <view class="divider"></view>

        <view class="summary-grid">
          <view class="summary-item">
            <text class="slabel">关联客户</text>
            <text class="svalue">{{ taskDetail.customerName || '—' }}</text>
          </view>
          <view class="summary-item">
            <text class="slabel">设备名称</text>
            <text class="svalue">{{ taskDetail.assetName || '—' }}</text>
          </view>
          <view class="summary-item">
            <text class="slabel">计划时间</text>
            <text class="svalue">{{ taskDetail.planTime || '—' }}</text>
          </view>
          <view class="summary-item">
            <text class="slabel">执行人</text>
            <text class="svalue">{{ taskDetail.engineerName || '—' }}</text>
          </view>
          <view class="summary-item">
            <text class="slabel">资产数量</text>
            <text class="svalue">{{ taskDetail.assetCount || 0 }} 台</text>
          </view>
          <view class="summary-item">
            <text class="slabel">巡检进度</text>
            <text class="svalue highlight">
              {{ taskDetail.inspectedCount || 0 }}/{{ taskDetail.totalPointCount || 0 }} 个点
            </text>
          </view>
        </view>

        <!-- 进度条 -->
        <view class="progress-section" v-if="taskDetail.totalPointCount > 0">
          <view class="progress-bar-wrap">
            <view class="progress-bar">
              <view class="progress-fill" :style="{ width: getProgress() + '%' }"></view>
            </view>
            <text class="progress-pct">{{ getProgress() }}%</text>
          </view>
        </view>
      </view>

      <!-- 待巡检执行入口 -->
      <view class="section-card execute-entry" v-if="taskDetail && taskDetail.status === '0'">
        <view class="execute-tip">
          <text class="tip-icon">📍</text>
          <text class="tip-text">到达现场后，请先获取GPS定位并拍摄现场照片</text>
        </view>
        <view class="execute-btn" @click="goToExecute">
          <text>开始巡检</text>
        </view>
      </view>

      <!-- 已完成：查看报告入口 -->
      <view class="section-card report-entry" v-if="taskDetail && taskDetail.status === '2'">
        <view class="report-tip">
          <text class="tip-icon">📄</text>
          <text class="tip-text">巡检已完成，可查看巡检报告</text>
        </view>
        <view class="report-btn" @click="goToReport">
          <text>查看巡检报告</text>
        </view>
      </view>

      <!-- 关联资产列表 -->
      <view class="section" v-if="assetList.length">
        <view class="section-title">
          <c-icon type="shop" :size="20" color="active" />
          <text>关联资产</text>
          <text class="title-count">({{ assetList.length }})</text>
        </view>

        <view class="asset-list">
          <view
            v-for="asset in assetList"
            :key="asset.assetId || asset.id"
            class="asset-item"
          >
            <view class="asset-info">
              <view class="asset-name">{{ asset.assetName || '未命名设备' }}</view>
              <view class="asset-meta">
                <text class="meta-tag">编码: {{ asset.assetCode || asset.serialNumber || '—' }}</text>
                <text class="meta-tag" v-if="asset.location">位置: {{ asset.location }}</text>
              </view>
            </view>
            <c-icon type="arrowright" :size="16" color="#d1d5db" />
          </view>
        </view>
      </view>

      <!-- 巡检点列表 -->
      <view class="section" v-if="pointList.length">
        <view class="section-title">
          <c-icon type="search" :size="20" color="active" />
          <text>巡检点</text>
          <text class="title-count">({{ pointList.length }})</text>
        </view>

        <view class="point-list">
          <view
            v-for="point in pointList"
            :key="point.pointId || point.id"
            :class="['point-item', `point-status-${point.status}`]"
            @click="executePoint(point)"
          >
            <view class="point-icon-wrap" :class="getPointIconClass(point.status)">
              <c-icon :type="getPointIcon(point.status)" :size="20" :color="getPointIconColor(point.status)" />
            </view>
            <view class="point-content">
              <view class="point-name">{{ point.pointName }}</view>
              <view class="point-meta">
                <text class="meta-tag">编码: {{ point.pointCode || '—' }}</text>
                <text class="meta-tag" v-if="point.location">位置: {{ point.location }}</text>
              </view>
            </view>
            <view class="point-right">
              <view :class="['point-status-tag', `status-${point.status}`]">
                {{ pointStatusMap[point.status] || '未知' }}
              </view>
              <c-icon type="arrowright" :size="16" color="#d1d5db" />
            </view>
          </view>
        </view>
      </view>

      <!-- 无数据状态 -->
      <view class="empty-state" v-if="!loading && !pointList.length">
        <c-icon type="folder" :size="64" color="#d1d5db" />
        <text class="empty-title">暂无巡检点</text>
        <text class="empty-desc">该任务下没有配置巡检点</text>
      </view>
    </template>

    <!-- 错误状态 -->
    <view class="error-state" v-if="!loading && !taskDetail">
      <c-icon type="info" :size="64" color="red" />
      <text class="empty-title">加载失败</text>
      <text class="empty-desc">无法获取任务详情</text>
      <view class="retry-btn" @click="loadDetail">重新加载</view>
    </view>
  </scroll-view>
</template>

<script>
import { getInspectionTaskDetail, getInspectionPointsByAsset } from '@/api/request.js'

export default {
  data() {
    return {
      loading: false,
      taskId: '',
      taskDetail: null,
      assetList: [],
      pointList: [],
      statusMap: { '0': '待巡检', '1': '巡检中', '2': '已完成' },
      pointStatusMap: { '0': '未巡检', '1': '已完成', '2': '异常' }
    }
  },
  onLoad(options) {
    this.taskId = options.id
    this.loadDetail()
  },
  onPullDownRefresh() {
    this.loadDetail().finally(() => uni.stopPullDownRefresh())
  },
  methods: {
    async loadDetail() {
      if (!this.taskId) return
      this.loading = true
      try {
        const res = await getInspectionTaskDetail(this.taskId)
        this.taskDetail = res.data || res
        const assets = this.taskDetail.assetList || this.taskDetail.assets || []
        this.assetList = assets
        if (assets.length > 0) {
          await this.loadPointsForAssets(assets)
        } else if (this.taskDetail.assetId) {
          await this.loadPointsForAssets([{ assetId: this.taskDetail.assetId }])
        }
      } catch (e) {
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },

    async loadPointsForAssets(assets) {
      try {
        const results = await Promise.all(
          assets.map(asset => {
            const assetId = asset.assetId || asset.id || this.taskDetail?.assetId
            if (!assetId) return Promise.resolve([])
            return getInspectionPointsByAsset(assetId).catch(() => [])
          })
        )
        this.pointList = results.flat().filter(Boolean)
      } catch (e) {}
    },

    getProgress() {
      if (!this.taskDetail) return 0
      const total = this.taskDetail.totalPointCount || 0
      if (total === 0) return 0
      return Math.round(((this.taskDetail.inspectedCount || 0) / total) * 100)
    },

    getPointIcon(status) {
      if (status === '1' || status === 1) return 'checkmarkempty'
      if (status === '2' || status === 2) return 'close'
      return 'circle'
    },

    getPointIconColor(status) {
      if (status === '1' || status === 1) return '#fff'
      if (status === '2' || status === 2) return '#fff'
      return '#c0c4cc'
    },

    getPointIconClass(status) {
      if (status === '1' || status === 1) return 'icon-done'
      if (status === '2' || status === 2) return 'icon-error'
      return 'icon-pending'
    },

    executePoint(point) {
      const pointId = point.pointId || point.id
      uni.navigateTo({ url: `/pages/inspection/pointExecute?pointId=${pointId}&taskId=${this.taskId}` })
    },

    goToExecute() {
      uni.navigateTo({ url: `/pages/inspection/taskExecute?id=${this.taskId}` })
    },

    goToReport() {
      const title = this.taskDetail.workOrderTitle || this.taskDetail.taskName || '巡检报告'
      const date = this.taskDetail.planTime || this.taskDetail.completeTime || ''
      uni.navigateTo({
        url: `/pages/inspection/report?id=${this.taskId}&name=${encodeURIComponent(title)}&date=${encodeURIComponent(date)}`
      })
    }
  }
}
</script>

<style scoped>
.detail-page { height: 100vh; background: #f5f7fa; }

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

.section-card { background: #fff; border-radius: 16rpx; padding: 28rpx; margin: 24rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); }
.summary-card { margin-bottom: 0; }

.summary-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20rpx; }
.task-name { font-size: 34rpx; font-weight: 700; color: #303133; flex: 1; margin-right: 16rpx; line-height: 1.4; }

.status-badge {
  font-size: 22rpx; padding: 6rpx 16rpx; border-radius: 20rpx; white-space: nowrap; flex-shrink: 0;
}
.status-badge.status-0 { background: #fff7ed; color: #f59e0b; }
.status-badge.status-1 { background: #eff6ff; color: #2563eb; }
.status-badge.status-2 { background: #ecfdf5; color: #10b981; }

.execute-entry { margin-bottom: 0; }
.execute-tip { display: flex; align-items: center; gap: 8rpx; background: #fff7ed; border-radius: 10rpx; padding: 16rpx 20rpx; margin-bottom: 20rpx; }
.tip-icon { font-size: 28rpx; }
.tip-text { font-size: 26rpx; color: #f59e0b; }
.execute-btn {
  height: 88rpx;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  border-radius: 44rpx;
  display: flex; align-items: center; justify-content: center;
  font-size: 32rpx; font-weight: 600; color: #fff;
}

.report-entry { margin-bottom: 24rpx; }
.report-tip { display: flex; align-items: center; gap: 8rpx; background: #ecfdf5; border-radius: 10rpx; padding: 16rpx 20rpx; margin-bottom: 20rpx; }
.report-tip .tip-text { color: #10b981; }
.report-btn {
  height: 88rpx;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border-radius: 44rpx;
  display: flex; align-items: center; justify-content: center;
  font-size: 32rpx; font-weight: 600; color: #fff;
}

.divider { height: 1rpx; background: #ebeef5; margin-bottom: 20rpx; }

.summary-grid { display: flex; flex-wrap: wrap; }
.summary-item { width: 50%; display: flex; flex-direction: column; margin-bottom: 20rpx; }
.slabel { font-size: 24rpx; color: #909399; margin-bottom: 6rpx; }
.svalue { font-size: 28rpx; color: #303133; font-weight: 500; }
.svalue.highlight { color: #2563eb; }

.progress-section { margin-top: 8rpx; }
.progress-bar-wrap { display: flex; align-items: center; gap: 16rpx; }
.progress-bar { flex: 1; height: 8rpx; background: #ebeef5; border-radius: 4rpx; overflow: hidden; }
.progress-fill { height: 100%; background: linear-gradient(90deg, #2563eb, #1d4ed8); border-radius: 4rpx; transition: width 0.3s; }
.progress-pct { font-size: 24rpx; color: #2563eb; font-weight: 600; min-width: 72rpx; text-align: right; }

.section { padding: 0 24rpx 24rpx; }
.section-title { display: flex; align-items: center; gap: 10rpx; font-size: 32rpx; font-weight: 600; color: #303133; padding: 32rpx 0 20rpx; }
.title-count { font-size: 26rpx; color: #909399; font-weight: 400; margin-left: 4rpx; }

.asset-list { background: #fff; border-radius: 16rpx; overflow: hidden; }
.asset-item { display: flex; align-items: center; padding: 24rpx; border-bottom: 1rpx solid #ebeef5; }
.asset-item:last-child { border-bottom: none; }
.asset-name { font-size: 30rpx; color: #303133; font-weight: 500; margin-bottom: 10rpx; }
.asset-meta { display: flex; flex-wrap: wrap; gap: 12rpx; }

.point-list { background: #fff; border-radius: 16rpx; overflow: hidden; }
.point-item { display: flex; align-items: center; padding: 28rpx 24rpx; border-bottom: 1rpx solid #ebeef5; transition: background 0.2s; }
.point-item:last-child { border-bottom: none; }
.point-item:active { background: #f5f7fa; }
.point-item.point-status-1 { background: #f8fff8; }

.point-icon-wrap { width: 48rpx; height: 48rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; flex-shrink: 0; margin-right: 16rpx; }
.icon-pending { background: #f0f2f5; }
.icon-done { background: #10b981; }
.icon-error { background: #ef4444; }

.point-content { flex: 1; }
.point-name { font-size: 30rpx; color: #303133; font-weight: 500; margin-bottom: 10rpx; }
.point-meta { display: flex; flex-wrap: wrap; gap: 12rpx; }

.meta-tag { font-size: 22rpx; color: #909399; background: #f5f7fa; padding: 4rpx 12rpx; border-radius: 6rpx; }

.point-right { display: flex; align-items: center; gap: 12rpx; }
.point-status-tag { font-size: 22rpx; padding: 4rpx 12rpx; border-radius: 8rpx; }
.point-status-tag.status-0, .point-status-tag.status-\30 { background: #fff7ed; color: #f59e0b; }
.point-status-tag.status-1, .point-status-tag.status-\31 { background: #ecfdf5; color: #10b981; }
.point-status-tag.status-2, .point-status-tag.status-\32 { background: #fef2f2; color: #ef4444; }

.empty-state, .error-state { display: flex; flex-direction: column; align-items: center; padding: 160rpx 0; gap: 12rpx; }
.empty-title { font-size: 32rpx; font-weight: 600; color: #303133; }
.empty-desc { font-size: 26rpx; color: #909399; }
.retry-btn { margin-top: 24rpx; padding: 16rpx 48rpx; background: #2563eb; color: #fff; border-radius: 40rpx; font-size: 28rpx; }
</style>