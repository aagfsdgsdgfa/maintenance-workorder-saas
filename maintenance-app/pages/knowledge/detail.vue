<template>
  <view class="knowledge-detail">
    <!-- 加载状态 -->
    <view class="loading-wrap" v-if="loading">
      <text>加载中...</text>
    </view>

    <!-- 内容区域 -->
    <view class="detail-content" v-if="!loading && detail">
      <!-- 头部 -->
      <view class="detail-header">
        <text class="detail-title">{{ detail.title }}</text>
        <view class="detail-tags" v-if="detail.tags?.length > 0">
          <text
            v-for="tag in detail.tags"
            :key="tag"
            class="detail-tag"
          >
            {{ tag }}
          </text>
        </view>
        <view class="detail-meta">
          <text class="meta-item" v-if="detail.category">
            分类：{{ detail.category }}
          </text>
          <text class="meta-item" v-if="detail.updateTime">
            更新：{{ formatDate(detail.updateTime) }}
          </text>
          <text class="meta-item" v-if="detail.views">
            浏览：{{ detail.views }}
          </text>
        </view>
      </view>

      <!-- 问题描述 -->
      <view class="section-block" v-if="detail.problem || detail.question">
        <view class="section-title">
          <text class="title-icon">?</text>
          <text>问题描述</text>
        </view>
        <view class="section-body">
          <text class="content-text">{{ detail.problem || detail.question }}</text>
        </view>
      </view>

      <!-- 解决方案 -->
      <view class="section-block" v-if="detail.solution || detail.answer">
        <view class="section-title">
          <text class="title-icon">+</text>
          <text>解决方案</text>
        </view>
        <view class="section-body solution-body">
          <text class="content-text">{{ detail.solution || detail.answer }}</text>
        </view>
      </view>

      <!-- 相关设备型号 -->
      <view class="section-block" v-if="detail.relatedModels?.length > 0">
        <view class="section-title">
          <text class="title-icon">#</text>
          <text>相关设备型号</text>
        </view>
        <view class="model-list">
          <view
            v-for="model in detail.relatedModels"
            :key="model"
            class="model-item"
          >
            <text class="model-name">{{ model }}</text>
          </view>
        </view>
      </view>

      <!-- 附件 -->
      <view class="section-block" v-if="detail.attachments?.length > 0">
        <view class="section-title">
          <text class="title-icon">@</text>
          <text>附件</text>
        </view>
        <view class="attach-list">
          <view
            v-for="file in detail.attachments"
            :key="file.id"
            class="attach-item"
            @click="downloadFile(file)"
          >
            <view class="attach-left">
              <text class="attach-icon">#</text>
              <view class="attach-info">
                <text class="attach-name">{{ file.fileName }}</text>
                <text class="attach-size" v-if="file.fileSize">{{ file.fileSize }}</text>
              </view>
            </view>
            <text class="attach-download">下载</text>
          </view>
        </view>
      </view>

      <!-- 相关知识 -->
      <view class="section-block" v-if="detail.relatedKnowledge?.length > 0">
        <view class="section-title">
          <text class="title-icon">K</text>
          <text>相关知识</text>
        </view>
        <view
          v-for="item in detail.relatedKnowledge"
          :key="item.id"
          class="related-item"
          @click="goRelated(item)"
        >
          <text class="related-title">{{ item.title }}</text>
          <text class="related-arrow">›</text>
        </view>
      </view>
    </view>

    <!-- 加载失败 -->
    <view class="empty" v-if="!loading && !detail">
      <text class="empty-icon">?</text>
      <text class="empty-text">知识内容不存在</text>
      <button class="retry-btn" @click="loadDetail">重新加载</button>
    </view>

    <!-- 底部反馈 -->
    <view class="feedback-bar" v-if="!loading && detail">
      <view class="feedback-btn" @click="markUseful(true)">
        <text class="feedback-icon">{{ usefulMarked ? '+' : '-' }}</text>
        <text class="feedback-text">有用 ({{ usefulCount }})</text>
      </view>
      <view class="feedback-divider"></view>
      <view class="feedback-btn" @click="markUseful(false)">
        <text class="feedback-icon">-</text>
        <text class="feedback-text">一般</text>
      </view>
      <view class="feedback-divider"></view>
      <view class="feedback-btn" @click="goReport">
        <text class="feedback-icon">!</text>
        <text class="feedback-text">纠错</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getKnowledgeDetail } from '@/api/request.js'

export default {
  data() {
    return {
      id: '',
      detail: null,
      loading: false,
      usefulCount: 0,
      usefulMarked: false
    }
  },
  onLoad(options) {
    this.id = options.id
    this.loadDetail()
  },
  methods: {
    async loadDetail() {
      if (!this.id) return
      this.loading = true
      try {
        const res = await getKnowledgeDetail(this.id)
        this.detail = res.data || res
        this.usefulCount = this.detail.usefulCount || 0
      } catch (e) {
        uni.showToast({ title: e.msg || '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      return dateStr.substring(0, 10)
    },
    downloadFile(file) {
      if (!file.url) {
        uni.showToast({ title: '文件地址无效', icon: 'none' })
        return
      }
      uni.showLoading({ title: '下载中...' })
      uni.downloadFile({
        url: file.url,
        success: (res) => {
          uni.hideLoading()
          if (res.statusCode === 200) {
            uni.saveFile({
              tempFilePath: res.tempFilePath,
              success: (save) => {
                uni.showToast({ title: '已保存到相册', icon: 'success' })
              },
              fail: () => {
                uni.openDocument({
                  filePath: res.tempFilePath,
                  showMenu: true,
                  success: () => {},
                  fail: () => {
                    uni.showToast({ title: '打开失败', icon: 'none' })
                  }
                })
              }
            })
          } else {
            uni.showToast({ title: '下载失败', icon: 'none' })
          }
        },
        fail: () => {
          uni.hideLoading()
          uni.showToast({ title: '下载失败', icon: 'none' })
        }
      })
    },
    markUseful(useful) {
      if (useful && !this.usefulMarked) {
        this.usefulCount++
        this.usefulMarked = true
        uni.showToast({ title: '感谢您的反馈', icon: 'success' })
      } else if (useful) {
        uni.showToast({ title: '您已标记过', icon: 'none' })
      }
    },
    goReport() {
      uni.showModal({
        title: '纠错反馈',
        editable: true,
        placeholderText: '请描述错误内容...',
        success: (res) => {
          if (res.confirm && res.content?.trim()) {
            uni.showToast({ title: '感谢您的反馈', icon: 'success' })
          }
        }
      })
    },
    goRelated(item) {
      uni.navigateTo({ url: `/pages/knowledge/detail?id=${item.id}` })
    }
  }
}
</script>

<style scoped>
.knowledge-detail {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 120rpx;
}

.loading-wrap {
  text-align: center;
  padding: 200rpx;
  color: #909399;
}

.detail-content {
  padding: 24rpx;
}

.detail-header {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 16rpx;
}

.detail-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #303133;
  line-height: 1.5;
  display: block;
  margin-bottom: 16rpx;
}

.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-bottom: 16rpx;
}

.detail-tag {
  padding: 6rpx 16rpx;
  background: #ecf5ff;
  color: #409eff;
  border-radius: 8rpx;
  font-size: 24rpx;
}

.detail-meta {
  display: flex;
  gap: 20rpx;
  flex-wrap: wrap;
}

.meta-item {
  font-size: 24rpx;
  color: #909399;
}

.section-block {
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
  margin-bottom: 16rpx;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10rpx;
  font-size: 30rpx;
  font-weight: bold;
  color: #303133;
  margin-bottom: 16rpx;
}

.title-icon {
  font-size: 32rpx;
}

.section-body {
  padding-left: 8rpx;
}

.solution-body {
  border-left: 4rpx solid #67c23a;
  padding-left: 20rpx;
}

.content-text {
  font-size: 28rpx;
  color: #606266;
  line-height: 1.9;
  white-space: pre-wrap;
  word-break: break-all;
}

.model-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.model-item {
  padding: 10rpx 20rpx;
  background: #f5f7fa;
  border-radius: 8rpx;
  border: 1rpx solid #ebeef5;
}

.model-name {
  font-size: 26rpx;
  color: #303133;
  font-family: monospace;
}

.attach-list {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.attach-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx;
  background: #f5f7fa;
  border-radius: 10rpx;
}

.attach-left {
  display: flex;
  align-items: center;
  gap: 12rpx;
  flex: 1;
}

.attach-icon {
  font-size: 40rpx;
  flex-shrink: 0;
}

.attach-info {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.attach-name {
  font-size: 26rpx;
  color: #303133;
  max-width: 380rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.attach-size {
  font-size: 22rpx;
  color: #909399;
}

.attach-download {
  font-size: 26rpx;
  color: #2563eb;
  font-weight: 500;
}

.related-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #ebeef5;
}

.related-item:last-child {
  border-bottom: none;
}

.related-title {
  font-size: 28rpx;
  color: #303133;
  flex: 1;
}

.related-arrow {
  font-size: 40rpx;
  color: #c0c4cc;
}

.empty {
  text-align: center;
  padding: 200rpx 0;
}

.empty-icon {
  font-size: 80rpx;
  display: block;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #909399;
  display: block;
  margin-bottom: 32rpx;
}

.retry-btn {
  width: 200rpx;
  height: 80rpx;
  background: #2563eb;
  border-radius: 40rpx;
  color: #fff;
  font-size: 28rpx;
  border: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.retry-btn::after {
  border: none;
}

.feedback-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100rpx;
  background: #fff;
  display: flex;
  align-items: center;
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.feedback-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  height: 100%;
}

.feedback-icon {
  font-size: 32rpx;
}

.feedback-text {
  font-size: 26rpx;
  color: #606266;
}

.feedback-divider {
  width: 1rpx;
  height: 48rpx;
  background: #ebeef5;
}
</style>
