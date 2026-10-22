<template>
  <view class="work-order-list">
    <!-- Tab分类 -->
    <view class="tabs">
      <view
        v-for="tab in tabs"
        :key="tab.value"
        :class="['tab', { active: currentTab === tab.value }]"
        @click="switchTab(tab.value)"
      >
        {{ tab.label }}
      </view>
    </view>

    <!-- 工单列表 -->
    <scroll-view scroll-y class="order-list" @scrolltolower="loadMore">
      <view
        v-for="item in orderList"
        :key="item.id"
        class="order-card"
        @click="goToDetail(item)"
      >
        <view class="order-header">
          <text class="order-no">{{ item.workOrderNo }}</text>
          <text :class="['status-tag', getStatusClass(item.status)]">
            {{ getStatusText(item.status) }}
          </text>
        </view>
        <view class="order-title">{{ item.title }}</view>
        <view class="order-info">
          <text class="info-item">U {{ item.customerName }}</text>
          <text class="info-item">E {{ item.assetName }}</text>
        </view>
        <view class="order-footer">
          <text :class="['priority', item.priority]">
            {{ getPriorityText(item.priority) }}
          </text>
          <text class="deadline" v-if="item.deadline">
            {{ item.deadline }}
          </text>
        </view>
      </view>

      <view class="empty" v-if="orderList.length === 0 && !loading">
        <text>暂无工单</text>
      </view>

      <view class="loading" v-if="loading">
        <text>加载中...</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { getMyTasks } from '@/api/request.js'

export default {
  data() {
    return {
      currentTab: '0',
      tabs: [
        { label: '待接单', value: '0' },
        { label: '处理中', value: '1' },
        { label: '已完成', value: '4' },
        { label: '全部', value: '' }
      ],
      orderList: [],
      loading: false,
      pageNum: 1,
      pageSize: 20,
      hasMore: true
    }
  },
  onLoad(options) {
    this.currentTab = options.status || ''
    this.loadData()
  },
  onPullDownRefresh() {
    this.pageNum = 1
    this.orderList = []
    this.hasMore = true
    this.loadData().finally(() => uni.stopPullDownRefresh())
  },
  methods: {
    switchTab(value) {
      this.currentTab = value
      this.pageNum = 1
      this.orderList = []
      this.hasMore = true
      this.loadData()
    },
    async loadData() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
        if (this.currentTab) {
          params.status = this.currentTab
        }
        const res = await getMyTasks(params)
        const list = res.data?.rows || res.rows || []
        if (this.pageNum === 1) {
          this.orderList = list
        } else {
          this.orderList = [...this.orderList, ...list]
        }
        this.hasMore = list.length >= this.pageSize
      } finally {
        this.loading = false
      }
    },
    loadMore() {
      if (this.hasMore && !this.loading) {
        this.pageNum++
        this.loadData()
      }
    },
    goToDetail(item) {
      const id = item.workOrderId || item.id
      uni.navigateTo({ url: `/pages/workOrder/detail?id=${id}` })
    },
    getStatusClass(status) {
      const map = { '0': 'pending', '1': 'processing', '2': 'processing', '3': 'pending', '4': 'done' }
      return map[status] || ''
    },
    getStatusText(status) {
      const map = { '0': '待接单', '1': '处理中', '2': '维修中', '3': '待验收', '4': '已完成' }
      return map[status] || status
    },
    getPriorityText(priority) {
      const map = { '0': '紧急', '1': '高', '2': '中', '3': '低' }
      return map[priority] || priority
    }
  }
}
</script>

<style scoped>
.work-order-list { height: 100vh; background: #f5f7fa; }
.tabs { display: flex; background: #fff; padding: 0 24rpx; border-bottom: 1rpx solid #ebeef5; }
.tab { flex: 1; text-align: center; padding: 24rpx 0; font-size: 28rpx; color: #606266; border-bottom: 4rpx solid transparent; }
.tab.active { color: #2563eb; border-bottom-color: #2563eb; font-weight: bold; }
.order-list { height: calc(100vh - 100rpx); padding: 24rpx; }
.order-card { background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.order-header { display: flex; justify-content: space-between; margin-bottom: 16rpx; }
.order-no { font-family: monospace; font-size: 26rpx; color: #909399; }
.status-tag { font-size: 22rpx; padding: 4rpx 12rpx; border-radius: 6rpx; }
.status-tag.pending { background: #fdf6ec; color: #e6a23c; }
.status-tag.processing { background: #ecf5ff; color: #409eff; }
.status-tag.done { background: #f0f9eb; color: #67c23a; }
.order-title { font-size: 30rpx; color: #303133; font-weight: bold; margin-bottom: 12rpx; }
.order-info { display: flex; flex-wrap: wrap; gap: 16rpx; margin-bottom: 12rpx; }
.info-item { font-size: 24rpx; color: #606266; }
.order-footer { display: flex; justify-content: space-between; align-items: center; }
.priority { font-size: 22rpx; padding: 4rpx 12rpx; border-radius: 6rpx; }
.priority.\30 { background: #fef0f0; color: #f56c6c; }
.priority.\31 { background: #fdf6ec; color: #e6a23c; }
.priority.\32 { background: #ecf5ff; color: #409eff; }
.priority.\33 { background: #f0f9eb; color: #67c23a; }
.deadline { font-size: 22rpx; color: #f56c6c; }
.empty, .loading { text-align: center; padding: 100rpx; color: #909399; }
</style>
