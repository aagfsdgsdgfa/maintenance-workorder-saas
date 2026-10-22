<template>
  <view class="work-order-detail">
    <!-- 加载状态 -->
    <view class="loading-state" v-if="loading">
      <text>加载中...</text>
    </view>

    <template v-else-if="detail">
      <!-- 状态进度条 -->
      <view class="status-progress">
        <view
          v-for="(step, index) in statusSteps"
          :key="index"
          :class="['step', {
            active: detail.status >= step.value,
            current: detail.status === step.value
          }]"
        >
          <view class="step-dot"></view>
          <text class="step-label">{{ step.label }}</text>
        </view>
      </view>

      <!-- 基础信息卡片 -->
      <view class="info-card">
        <view class="card-header">
          <text class="work-order-no">{{ detail.workOrderNo }}</text>
          <text :class="['status-tag', getStatusClass(detail.status)]">
            {{ getStatusText(detail.status) }}
          </text>
        </view>
        <view class="card-title">{{ detail.title }}</view>

        <view class="info-grid">
          <view class="info-row">
            <text class="info-label">客户名称</text>
            <text class="info-value">{{ detail.customerName || '-' }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">设备名称</text>
            <text class="info-value">{{ detail.assetName || '-' }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">设备编号</text>
            <text class="info-value">{{ detail.assetNo || '-' }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">设备型号</text>
            <text class="info-value">{{ detail.modelNo || '-' }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">上门地址</text>
            <text class="info-value">{{ detail.contactAddress || '-' }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">故障类型</text>
            <text class="info-value">{{ detail.faultType || '-' }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">优先级</text>
            <text :class="['priority-value', detail.priority]">
              {{ getPriorityText(detail.priority) }}
            </text>
          </view>
          <view class="info-row" v-if="detail.slaDeadline">
            <text class="info-label">SLA截止时间</text>
            <text class="info-value deadline">{{ detail.slaDeadline }}</text>
          </view>
        </view>
      </view>

      <!-- 故障描述 -->
      <view class="info-card" v-if="detail.faultDesc">
        <view class="card-title-sm">故障描述</view>
        <text class="desc-text">{{ detail.faultDesc }}</text>
      </view>

      <!-- 打卡记录 -->
      <view class="info-card" v-if="checkIns.length > 0">
        <view class="card-title-sm">打卡记录</view>
        <view
          v-for="(checkIn, index) in checkIns"
          :key="index"
          class="checkin-item"
        >
          <view class="checkin-dot"></view>
          <view class="checkin-content">
            <text class="checkin-type">{{ checkIn.typeLabel || '打卡' }}</text>
            <text class="checkin-time">{{ checkIn.createTime }}</text>
            <text class="checkin-location" v-if="checkIn.location">
              {{ checkIn.location }}
            </text>
          </view>
        </view>
      </view>

      <!-- 操作按钮区 -->
      <view class="action-bar">
        <!-- 待接单: 接单 / 拒单 -->
        <template v-if="detail.status === '0'">
          <button class="btn btn-reject" @click="handleReject">拒单</button>
          <button class="btn btn-primary" @click="handleAccept">接单</button>
        </template>

        <!-- 处理中: 到达打卡 / 开始维修 -->
        <template v-else-if="detail.status === '1'">
          <button class="btn btn-default" @click="handleCheckIn">到达打卡</button>
          <button class="btn btn-primary" @click="handleStartRepair">开始维修</button>
        </template>

        <!-- 维修中: 完工确认 -->
        <template v-else-if="detail.status === '2'">
          <button class="btn btn-primary btn-block" @click="goToComplete">
            完工确认
          </button>
        </template>

        <!-- 待验收: 提醒验收 -->
        <template v-else-if="detail.status === '3'">
          <button class="btn btn-default btn-block" @click="handleRemind">提醒甲方验收</button>
        </template>
      </view>

      <!-- 轨迹入口（任何状态可见） -->
      <view class="track-entry">
        <button class="btn btn-default btn-block" @click="goToTrack">
          <text class="track-icon">🛤</text>
          查看工单轨迹
        </button>
      </view>

      <!-- 拒单弹窗 -->
      <view class="modal-mask" v-if="showRejectModal" @click="showRejectModal = false">
        <view class="modal-content" @click.stop>
          <view class="modal-title">拒单原因</view>
          <textarea
            v-model="rejectReason"
            class="reject-textarea"
            placeholder="请输入拒单原因..."
            maxlength="200"
          ></textarea>
          <view class="modal-actions">
            <button class="btn btn-default" @click="showRejectModal = false">取消</button>
            <button class="btn btn-reject" @click="confirmReject">确认拒单</button>
          </view>
        </view>
      </view>
    </template>

    <!-- 空状态 -->
    <view class="empty-state" v-else>
      <text>工单不存在</text>
    </view>
  </view>
</template>

<script>
import {
  getWorkOrderDetail,
  acceptWorkOrder,
  rejectWorkOrder,
  startWorkOrder,
  getWorkOrderCheckIns
} from '@/api/request.js'

export default {
  data() {
    return {
      loading: false,
      detail: null,
      checkIns: [],
      showRejectModal: false,
      rejectReason: ''
    }
  },
  computed: {
    statusSteps() {
      return [
        { label: '待接单', value: 0 },
        { label: '处理中', value: 1 },
        { label: '维修中', value: 2 },
        { label: '待验收', value: 3 },
        { label: '已完成', value: 4 }
      ]
    }
  },
  onLoad(options) {
    if (options.id) {
      this.loadDetail(options.id)
      this.loadCheckIns(options.id)
    }
  },
  methods: {
    async loadDetail(id) {
      this.loading = true
      try {
        const res = await getWorkOrderDetail(id)
        this.detail = res.data || res
      } catch (e) {
        uni.showToast({ title: '加载工单详情失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    async loadCheckIns(id) {
      try {
        const res = await getWorkOrderCheckIns(id)
        const list = Array.isArray(res) ? res : (res.rows || res.data || [])
        this.checkIns = list.map(item => ({
          ...item,
          typeLabel: this.getCheckInTypeLabel(item.checkType)
        }))
      } catch (e) {
        // 打卡记录加载失败不提示
      }
    },
    getCheckInTypeLabel(type) {
      const map = { 'arrival': '到达打卡', 'start': '开始维修', 'complete': '完工打卡' }
      return map[type] || '打卡'
    },
    async handleAccept() {
      try {
        await acceptWorkOrder(this.detail.id || this.detail.workOrderId)
        uni.showToast({ title: '接单成功', icon: 'success' })
        this.loadDetail(this.detail.id || this.detail.workOrderId)
      } catch (e) {
        uni.showToast({ title: e.msg || '接单失败', icon: 'none' })
      }
    },
    handleReject() {
      this.rejectReason = ''
      this.showRejectModal = true
    },
    async confirmReject() {
      if (!this.rejectReason.trim()) {
        uni.showToast({ title: '请输入拒单原因', icon: 'none' })
        return
      }
      try {
        await rejectWorkOrder(this.detail.id || this.detail.workOrderId, {
          reason: this.rejectReason
        })
        uni.showToast({ title: '已拒单', icon: 'success' })
        this.showRejectModal = false
        uni.navigateBack()
      } catch (e) {
        uni.showToast({ title: e.msg || '拒单失败', icon: 'none' })
      }
    },
    handleCheckIn() {
      // 调用 GPS 打卡页面
      uni.navigateTo({
        url: `/pages/checkin/index?workOrderId=${this.detail.id || this.detail.workOrderId}`
      })
    },
    async handleStartRepair() {
      try {
        await startWorkOrder(this.detail.id || this.detail.workOrderId)
        uni.showToast({ title: '已开始维修', icon: 'success' })
        this.loadDetail(this.detail.id || this.detail.workOrderId)
      } catch (e) {
        uni.showToast({ title: e.msg || '操作失败', icon: 'none' })
      }
    },
    goToComplete() {
      const id = this.detail.id || this.detail.workOrderId
      uni.navigateTo({ url: `/pages/workOrder/complete?id=${id}` })
    },
    goToTrack() {
      const id = this.detail.id || this.detail.workOrderId
      const orderNo = this.detail.orderNo || ''
      const title = this.detail.title || this.detail.description || ''
      uni.navigateTo({ url: `/pages/workOrder/track?id=${id}&orderNo=${encodeURIComponent(orderNo)}&title=${encodeURIComponent(title)}` })
    },
    handleRemind() {
      uni.showToast({ title: '已提醒甲方验收', icon: 'success' })
    },
    getStatusClass(status) {
      const map = {
        '0': 'pending', '1': 'processing',
        '2': 'processing', '3': 'pending', '4': 'done'
      }
      return map[status] || ''
    },
    getStatusText(status) {
      const map = {
        '0': '待接单', '1': '处理中', '2': '维修中', '3': '待验收', '4': '已完成'
      }
      return map[status] || status
    },
    getPriorityText(priority) {
      const map = { '1': '低', '2': '中', '3': '高', '4': '紧急' }
      return map[priority] || priority
    }
  }
}
</script>

<style scoped>
.work-order-detail { min-height: 100vh; background: #f5f7fa; padding-bottom: 160rpx; }
.loading-state, .empty-state { text-align: center; padding: 100rpx; color: #909399; }

/* 状态进度条 */
.status-progress {
  display: flex;
  justify-content: space-between;
  background: #fff;
  padding: 40rpx 32rpx;
  margin-bottom: 24rpx;
}
.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  position: relative;
}
.step:not(:last-child)::after {
  content: '';
  position: absolute;
  top: 16rpx;
  left: 50%;
  width: 100%;
  height: 2rpx;
  background: #dcdfe6;
}
.step.active:not(:last-child)::after { background: #2563eb; }
.step-dot {
  width: 32rpx;
  height: 32rpx;
  border-radius: 50%;
  background: #dcdfe6;
  margin-bottom: 12rpx;
  position: relative;
  z-index: 1;
}
.step.active .step-dot { background: #2563eb; }
.step.current .step-dot { background: #2563eb; box-shadow: 0 0 0 8rpx rgba(102,126,234,0.2); }
.step-label { font-size: 22rpx; color: #c0c4cc; text-align: center; }
.step.active .step-label { color: #2563eb; font-weight: bold; }

/* 信息卡片 */
.info-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin: 0 24rpx 24rpx;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}
.work-order-no { font-family: monospace; font-size: 26rpx; color: #909399; }
.status-tag { font-size: 22rpx; padding: 4rpx 12rpx; border-radius: 6rpx; }
.status-tag.pending { background: #fdf6ec; color: #e6a23c; }
.status-tag.processing { background: #ecf5ff; color: #409eff; }
.status-tag.done { background: #f0f9eb; color: #67c23a; }
.card-title {
  font-size: 32rpx;
  color: #303133;
  font-weight: bold;
  margin-bottom: 24rpx;
}
.card-title-sm {
  font-size: 28rpx;
  color: #303133;
  font-weight: bold;
  margin-bottom: 16rpx;
}

.info-grid { display: flex; flex-direction: column; }
.info-row {
  display: flex;
  justify-content: space-between;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #ebeef5;
}
.info-row:last-child { border-bottom: none; }
.info-label { font-size: 26rpx; color: #909399; }
.info-value { font-size: 26rpx; color: #303133; }
.info-value.deadline { color: #f56c6c; }
.priority-value { font-size: 22rpx; padding: 4rpx 12rpx; border-radius: 6rpx; }
.priority-value.\30 { background: #fef0f0; color: #f56c6c; }
.priority-value.\31 { background: #fdf6ec; color: #e6a23c; }
.priority-value.\32 { background: #ecf5ff; color: #409eff; }
.priority-value.\33 { background: #f0f9eb; color: #67c23a; }

.desc-text { font-size: 28rpx; color: #606266; line-height: 1.6; }

/* 打卡记录 */
.checkin-item {
  display: flex;
  align-items: flex-start;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #ebeef5;
}
.checkin-item:last-child { border-bottom: none; }
.checkin-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: #2563eb;
  margin-top: 6rpx;
  margin-right: 16rpx;
  flex-shrink: 0;
}
.checkin-content { display: flex; flex-direction: column; }
.checkin-type { font-size: 26rpx; color: #303133; font-weight: bold; }
.checkin-time { font-size: 24rpx; color: #909399; margin-top: 6rpx; }
.checkin-location { font-size: 24rpx; color: #c0c4cc; margin-top: 4rpx; }

/* 操作按钮区 */
.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  display: flex;
  gap: 24rpx;
  box-shadow: 0 -2rpx 12rpx rgba(0,0,0,0.05);
}
.btn {
  flex: 1;
  height: 88rpx;
  border-radius: 44rpx;
  font-size: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}
.btn-primary { background: #2563eb; color: #fff; }
.btn-default { background: #f5f7fa; color: #606266; }
.btn-reject { background: #fef0f0; color: #f56c6c; }
.btn-block { flex: unset; width: 100%; }

/* 轨迹入口 */
.track-entry {
  padding: 0 24rpx 24rpx;
}
.track-icon {
  margin-right: 8rpx;
}

/* 拒单弹窗 */
.modal-mask {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}
.modal-content {
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
  width: 600rpx;
  max-width: 90vw;
}
.modal-title { font-size: 32rpx; font-weight: bold; color: #303133; margin-bottom: 24rpx; }
.reject-textarea {
  width: 100%;
  height: 200rpx;
  background: #f5f7fa;
  border-radius: 12rpx;
  padding: 20rpx;
  font-size: 28rpx;
  color: #303133;
  box-sizing: border-box;
  border: none;
  resize: none;
}
.modal-actions { display: flex; gap: 24rpx; margin-top: 32rpx; }
.modal-actions .btn { flex: 1; }
</style>
