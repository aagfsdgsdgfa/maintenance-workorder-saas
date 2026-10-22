<template>
  <view class="c-empty">
    <view class="empty-icon" :style="{ background: iconBg }">
      <text :style="{ fontSize: '56rpx', color: iconColor, lineHeight: '1' }">{{ iconChar }}</text>
    </view>
    <view class="empty-title">{{ title }}</view>
    <view class="empty-desc" v-if="description">{{ description }}</view>
    <view class="empty-action" v-if="actionText" @click="onAction">
      <text class="action-btn">{{ actionText }}</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'c-empty',
  props: {
    icon: { type: String, default: 'document' },
    title: { type: String, required: true },
    description: { type: String, default: '' },
    actionText: { type: String, default: '' }
  },
  computed: {
    iconChar() {
      const map = {
        document: '📄', box: '📦', tickets: '🎫', bell: '🔔',
        success: '✓', warning: '⚠', search: '🔍'
      }
      return map[this.icon] || '📄'
    },
    iconColor() {
      return this.icon === 'success' ? '#00C9A7' : '#94a3b8'
    },
    iconBg() {
      return this.icon === 'success' ? '#f0fdfa' : '#f1f5f9'
    }
  },
  methods: {
    onAction() {
      this.$emit('action')
    }
  }
}
</script>

<style lang="scss" scoped>
.c-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 40rpx;
  text-align: center;
}

.empty-icon {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24rpx;
}

.empty-title {
  font-size: $uni-font-size-lg;
  font-weight: 600;
  color: $uni-text-color;
  margin-bottom: 12rpx;
}

.empty-desc {
  font-size: $uni-font-size-sm;
  color: $uni-text-color-grey;
  max-width: 480rpx;
  line-height: 1.5;
}

.empty-action {
  margin-top: 32rpx;
}

.action-btn {
  display: inline-block;
  padding: 16rpx 48rpx;
  background: $uni-color-primary;
  color: #fff;
  border-radius: $uni-border-radius-lg;
  font-size: $uni-font-size-base;
}
</style>