<template>
  <view
    class="notification-item"
    :class="{ unread: !item.read && !item.isRead, dot: !item.read && !item.isRead }"
    @click="handleClick"
  >
    <!-- 未读红点 -->
    <view class="unread-dot" v-if="!item.read && !item.isRead"></view>

    <!-- 图标区 -->
    <view class="icon-wrapper" :style="{ background: iconBg }">
      <text class="icon-text">{{ iconEmoji }}</text>
    </view>

    <!-- 内容区 -->
    <view class="content-wrapper">
      <view class="content-header">
        <text class="item-title" :class="{ bold: !item.read && !item.isRead }">
          {{ item.title || item.noticeTitle || '通知' }}
        </text>
        <text class="item-time">{{ formatTime }}</text>
      </view>
      <text class="item-content" v-if="item.content || item.noticeContent">
        {{ item.content || item.noticeContent }}
      </text>
      <view class="content-meta" v-if="showMeta">
        <text class="meta-tag" v-if="item.typeName" :style="{ color: typeColor }">
          {{ item.typeName }}
        </text>
        <text class="meta-source" v-if="item.source || item.sender">
          {{ item.source || item.sender }}
        </text>
      </view>
      <!-- 附加信息 -->
      <view class="content-extra" v-if="item.extra">
        <text class="extra-text" v-for="(val, key) in item.extra" :key="key">
          {{ key }}: {{ val }}
        </text>
      </view>
    </view>

    <!-- 箭头 -->
    <view class="arrow-wrapper" v-if="showArrow">
      <text class="arrow-icon">›</text>
    </view>
  </view>
</template>

<script>
/**
 * notification-item.vue - 通知项组件
 * 功能：显示单条通知
 * props: item (通知对象)
 * 显示：图标（按类型）、标题、时间、已读/未读状态
 * 点击事件，未读显示红点
 */
export default {
  name: 'notification-item',
  props: {
    // 通知数据对象
    item: {
      type: Object,
      required: true,
      default: () => ({})
    },
    // 是否显示箭头
    showArrow: {
      type: Boolean,
      default: true
    },
    // 是否显示元信息
    showMeta: {
      type: Boolean,
      default: true
    }
  },
  computed: {
    iconEmoji() {
      // 根据类型映射 emoji
      const typeMap = {
        // 工单相关
        work_order: '📋',
        workorder: '📋',
        'work-order': '📋',
        repair: '🔧',
        fault: '⚠️',
        // 巡检相关
        inspection: '🔍',
        patrol: '🛡️',
        // 打卡相关
        check_in: '📍',
        checkin: '📍',
        attendance: '🕐',
        // 系统通知
        system: '🔔',
        notice: '📢',
        // 消息
        message: '💬',
        chat: '💬',
        // 审批
        approval: '✅',
        approve: '✅',
        // 告警
        alarm: '🚨',
        alert: '🚨',
        warning: '⚠️',
        // 其他
        default: '📌'
      }
      const type = (this.item.type || '').toLowerCase()
      return typeMap[type] || typeMap[type.replace(/_/g, '-')] || typeMap['default']
    },
    iconBg() {
      // 根据类型映射背景色
      const bgMap = {
        work_order: '#ecf5ff',
        workorder: '#ecf5ff',
        'work-order': '#ecf5ff',
        repair: '#fdf6ec',
        fault: '#fef0f0',
        inspection: '#f0f9eb',
        patrol: '#f0f9eb',
        check_in: '#ecf5ff',
        checkin: '#ecf5ff',
        attendance: '#f5f7fa',
        system: '#f0f1ff',
        notice: '#f0f1ff',
        message: '#fff9f0',
        chat: '#fff9f0',
        approval: '#f0f9eb',
        approve: '#f0f9eb',
        alarm: '#fef0f0',
        alert: '#fef0f0',
        warning: '#fdf6ec',
        default: '#f5f7fa'
      }
      const type = (this.item.type || '').toLowerCase()
      return bgMap[type] || bgMap[type.replace(/_/g, '-')] || bgMap['default']
    },
    typeColor() {
      const colorMap = {
        work_order: '#409eff',
        workorder: '#409eff',
        'work-order': '#409eff',
        repair: '#e6a23c',
        fault: '#f56c6c',
        inspection: '#67c23a',
        patrol: '#67c23a',
        check_in: '#409eff',
        checkin: '#409eff',
        system: '#667eea',
        notice: '#667eea',
        message: '#e6a23c',
        approval: '#67c23a',
        alarm: '#f56c6c',
        warning: '#e6a23c',
        default: '#909399'
      }
      const type = (this.item.type || '').toLowerCase()
      return colorMap[type] || colorMap[type.replace(/_/g, '-')] || colorMap['default']
    },
    formatTime() {
      const time = this.item.createTime || this.item.createTimeStr || this.item.time || this.item.noticeTime || ''
      if (!time) return ''
      try {
        const d = new Date(time)
        if (isNaN(d.getTime())) return time

        const now = new Date()
        const diff = now - d
        const seconds = Math.floor(diff / 1000)
        const minutes = Math.floor(seconds / 60)
        const hours = Math.floor(minutes / 60)
        const days = Math.floor(hours / 24)

        if (seconds < 60) return '刚刚'
        if (minutes < 60) return `${minutes}分钟前`
        if (hours < 24) return `${hours}小时前`
        if (days < 7) return `${days}天前`

        const pad = n => String(n).padStart(2, '0')
        return `${d.getFullYear()}/${pad(d.getMonth() + 1)}/${pad(d.getDate())}`
      } catch (e) {
        return time
      }
    }
  },
  methods: {
    handleClick() {
      this.$emit('click', this.item)
      // 标记为已读（如果未读）
      if (!this.item.read && !this.item.isRead) {
        this.$emit('read', this.item)
      }
    }
  }
}
</script>

<style scoped>
.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 24rpx 32rpx;
  background: #fff;
  position: relative;
  transition: background 0.2s;
  cursor: pointer;
}

.notification-item:active {
  background: #f5f7fa;
}

/* 未读状态 */
.notification-item.unread {
  background: #f8f9ff;
}

.notification-item.unread::before {
  content: '';
}

/* 未读红点 */
.unread-dot {
  position: absolute;
  top: 32rpx;
  right: 32rpx;
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: #f56c6c;
  flex-shrink: 0;
}

/* 图标区 */
.icon-wrapper {
  width: 80rpx;
  height: 80rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-right: 20rpx;
}

.icon-text {
  font-size: 40rpx;
}

/* 内容区 */
.content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 8rpx;
}

.item-title {
  font-size: 28rpx;
  color: #606266;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 16rpx;
}

.item-title.bold {
  color: #303133;
  font-weight: bold;
}

.item-time {
  font-size: 22rpx;
  color: #c0c4cc;
  flex-shrink: 0;
  margin-top: 4rpx;
}

.item-content {
  font-size: 24rpx;
  color: #909399;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.content-meta {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-top: 8rpx;
}

.meta-tag {
  font-size: 20rpx;
  padding: 2rpx 8rpx;
  border-radius: 4rpx;
  background: currentColor;
  opacity: 0.1;
}

.meta-source {
  font-size: 20rpx;
  color: #c0c4cc;
}

.content-extra {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
  margin-top: 8rpx;
}

.extra-text {
  font-size: 22rpx;
  color: #c0c4cc;
}

/* 箭头 */
.arrow-wrapper {
  display: flex;
  align-items: center;
  padding-left: 12rpx;
  flex-shrink: 0;
}

.arrow-icon {
  font-size: 36rpx;
  color: #c0c4cc;
}
</style>
