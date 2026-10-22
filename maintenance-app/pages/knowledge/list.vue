<template>
  <view class="knowledge-list">
    <!-- 搜索框 -->
    <view class="search-bar">
      <view class="search-input-wrap">
        <c-icon type="search" :size="18" color="#909399" />
        <input
          class="search-input"
          v-model="keyword"
          placeholder="搜索知识库..."
          confirm-type="search"
          @confirm="handleSearch"
        />
        <view class="clear-icon" v-if="keyword" @click="clearKeyword">
          <c-icon type="close" :size="14" color="#c0c4cc" />
        </view>
      </view>
      <button class="search-btn" @click="handleSearch">搜索</button>
    </view>

    <!-- 热门标签 -->
    <view class="hot-tags" v-if="!hasSearched && hotTags.length > 0">
      <view class="tags-title">
        <c-icon type="star" :size="14" color="yellow" />
        <text>热门标签</text>
      </view>
      <view class="tags-wrap">
        <text
          v-for="tag in hotTags"
          :key="tag"
          class="tag-item"
          @click="searchByTag(tag)"
        >
          {{ tag }}
        </text>
      </view>
    </view>

    <!-- 搜索结果 -->
    <scroll-view scroll-y class="result-list" v-if="hasSearched">
      <view
        v-for="item in searchResults"
        :key="item.id"
        class="kb-card"
        @click="goDetail(item)"
      >
        <view class="kb-header">
          <c-icon type="bookmark" :size="18" color="purple" class="kb-icon" />
          <text class="kb-title">{{ item.title }}</text>
        </view>
        <view class="kb-tags" v-if="item.tags?.length > 0">
          <text v-for="tag in item.tags" :key="tag" class="kb-tag">{{ tag }}</text>
        </view>
        <view class="kb-summary" v-if="item.summary || item.description">
          {{ item.summary || item.description }}
        </view>
        <view class="kb-footer">
          <text class="kb-category" v-if="item.category">
            <c-icon type="folder" :size="12" color="green" />
            {{ item.category }}
          </text>
          <text class="kb-date" v-if="item.updateTime">{{ formatDate(item.updateTime) }}</text>
        </view>
      </view>

      <view class="empty" v-if="searchResults.length === 0 && !loading">
        <c-icon type="search" :size="64" color="#d1d5db" />
        <text class="empty-text">未找到相关内容</text>
        <text class="empty-tip">换个关键词试试吧</text>
      </view>

      <view class="loading" v-if="loading">
        <c-icon type="spinnercycle" :size="24" color="#909399" />
        <text>搜索中...</text>
      </view>
    </scroll-view>

    <!-- 默认列表 -->
    <scroll-view scroll-y class="result-list" v-if="!hasSearched" @scrolltolower="loadMore">
      <view
        v-for="item in defaultList"
        :key="item.id"
        class="kb-card"
        @click="goDetail(item)"
      >
        <view class="kb-header">
          <c-icon type="bookmark" :size="18" color="purple" class="kb-icon" />
          <text class="kb-title">{{ item.title }}</text>
        </view>
        <view class="kb-tags" v-if="item.tags?.length > 0">
          <text v-for="tag in item.tags" :key="tag" class="kb-tag">{{ tag }}</text>
        </view>
        <view class="kb-summary" v-if="item.summary || item.description">
          {{ item.summary || item.description }}
        </view>
        <view class="kb-footer">
          <text class="kb-category" v-if="item.category">
            <c-icon type="folder" :size="12" color="green" />
            {{ item.category }}
          </text>
          <text class="kb-date" v-if="item.updateTime">{{ formatDate(item.updateTime) }}</text>
        </view>
      </view>

      <view class="empty" v-if="defaultList.length === 0 && !loading">
        <c-icon type="bookmark" :size="64" color="#d1d5db" />
        <text class="empty-text">暂无知识库内容</text>
      </view>

      <view class="loading" v-if="loading">
        <c-icon type="spinnercycle" :size="24" color="#909399" />
        <text>加载中...</text>
      </view>

      <view class="no-more" v-if="!hasMore && defaultList.length > 0">
        <text>没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { searchKnowledgeBase } from '@/api/request.js'

export default {
  data() {
    return {
      keyword: '',
      hotTags: ['设备故障', '网络问题', '系统维护', '安全规范', '操作流程'],
      hasSearched: false,
      searchResults: [],
      defaultList: [],
      loading: false,
      pageNum: 1,
      pageSize: 20,
      hasMore: true
    }
  },
  onLoad() {
    this.loadDefault()
  },
  onPullDownRefresh() {
    if (this.hasSearched) {
      this.pageNum = 1
      this.searchResults = []
      this.hasMore = true
      this.doSearch().finally(() => uni.stopPullDownRefresh())
    } else {
      this.pageNum = 1
      this.defaultList = []
      this.hasMore = true
      this.loadDefault().finally(() => uni.stopPullDownRefresh())
    }
  },
  methods: {
    handleSearch() {
      if (!this.keyword.trim()) {
        uni.showToast({ title: '请输入搜索关键词', icon: 'none' })
        return
      }
      this.hasSearched = true
      this.pageNum = 1
      this.searchResults = []
      this.hasMore = true
      this.doSearch()
    },
    searchByTag(tag) {
      this.keyword = tag
      this.handleSearch()
    },
    clearKeyword() {
      this.keyword = ''
    },
    async doSearch() {
      this.loading = true
      try {
        const res = await searchKnowledgeBase(this.keyword)
        const list = res.rows || res.data?.rows || res.data || []
        if (this.pageNum === 1) {
          this.searchResults = list
        } else {
          this.searchResults = [...this.searchResults, ...list]
        }
        this.hasMore = list.length >= this.pageSize
      } catch (e) {
        uni.showToast({ title: e.msg || '搜索失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    async loadDefault() {
      this.loading = true
      try {
        const res = await searchKnowledgeBase('')
        const list = res.rows || res.data?.rows || res.data || []
        if (this.pageNum === 1) {
          this.defaultList = list
        } else {
          this.defaultList = [...this.defaultList, ...list]
        }
        this.hasMore = list.length >= this.pageSize
      } catch (e) {
        // 静默失败
      } finally {
        this.loading = false
      }
    },
    loadMore() {
      if (!this.hasMore || this.loading) return
      if (this.hasSearched) {
        this.pageNum++
        this.doSearch()
      } else {
        this.pageNum++
        this.loadDefault()
      }
    },
    goDetail(item) {
      uni.navigateTo({ url: `/pages/knowledge/detail?id=${item.id}` })
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      return dateStr.substring(0, 10)
    }
  }
}
</script>

<style scoped>
.knowledge-list { min-height: 100vh; background: #f5f7fa; }

.search-bar {
  display: flex; align-items: center; gap: 16rpx;
  padding: 20rpx 24rpx; background: #fff; border-bottom: 1rpx solid #ebeef5;
}

.search-input-wrap {
  flex: 1; display: flex; align-items: center;
  background: #f5f7fa; border-radius: 40rpx; padding: 0 20rpx; height: 72rpx; gap: 10rpx;
}

.search-input { flex: 1; font-size: 28rpx; color: #303133; }
.clear-icon { padding: 4rpx; }

.search-btn {
  width: 120rpx; height: 72rpx;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  border-radius: 36rpx; color: #fff; font-size: 28rpx; border: none;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.search-btn::after { border: none; }

.hot-tags { background: #fff; padding: 24rpx; margin-bottom: 16rpx; }
.tags-title { display: flex; align-items: center; gap: 8rpx; font-size: 26rpx; color: #909399; margin-bottom: 16rpx; }
.tags-wrap { display: flex; flex-wrap: wrap; gap: 12rpx; }
.tag-item {
  padding: 10rpx 20rpx; background: #f0f2f5; border-radius: 30rpx;
  font-size: 26rpx; color: #606266; transition: all 0.2s;
}
.tag-item:active { background: #2563eb; color: #fff; }

.result-list { height: calc(100vh - 220rpx); padding: 0 24rpx 24rpx; }

.kb-card {
  background: #fff; border-radius: 16rpx; padding: 28rpx;
  margin-bottom: 16rpx; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}

.kb-header { display: flex; align-items: center; gap: 10rpx; margin-bottom: 10rpx; }
.kb-icon { flex-shrink: 0; }
.kb-title { font-size: 30rpx; font-weight: bold; color: #303133; line-height: 1.4; flex: 1; }

.kb-tags { display: flex; flex-wrap: wrap; gap: 8rpx; margin-bottom: 12rpx; }
.kb-tag { padding: 4rpx 14rpx; background: #f5f3ff; color: #7c3aed; border-radius: 6rpx; font-size: 22rpx; }

.kb-summary {
  font-size: 26rpx; color: #606266; line-height: 1.6; margin-bottom: 14rpx;
  display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden;
}

.kb-footer { display: flex; justify-content: space-between; align-items: center; }
.kb-category { display: flex; align-items: center; gap: 6rpx; font-size: 22rpx; color: #67c23a; background: #f0f9eb; padding: 4rpx 12rpx; border-radius: 6rpx; }
.kb-date { font-size: 22rpx; color: #c0c4cc; }

.empty { text-align: center; padding: 120rpx 0; display: flex; flex-direction: column; align-items: center; gap: 12rpx; }
.empty-text { font-size: 28rpx; color: #909399; }
.empty-tip { font-size: 24rpx; color: #c0c4cc; }

.loading { display: flex; align-items: center; justify-content: center; gap: 12rpx; padding: 24rpx; color: #909399; font-size: 24rpx; }
.no-more { text-align: center; padding: 24rpx; color: #c0c4cc; font-size: 24rpx; }
</style>