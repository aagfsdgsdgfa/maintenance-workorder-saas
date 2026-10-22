<template>
  <view class="c-loading" :class="[fullscreen ? 'is-fullscreen' : 'is-inline']">
    <view class="loading-spinner"></view>
    <text class="loading-text" v-if="text">{{ text }}</text>
  </view>
</template>

<script>
export default {
  name: 'c-loading',
  props: {
    text: { type: String, default: '加载中...' },
    fullscreen: { type: Boolean, default: false }
  }
}
</script>

<style lang="scss" scoped>
.c-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40rpx 20rpx;
  gap: 16rpx;
}

.c-loading.is-fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.85);
  z-index: 1080;
}

.loading-spinner {
  width: 48rpx;
  height: 48rpx;
  border: 4rpx solid #e5e7eb;
  border-top-color: $uni-color-primary;
  border-radius: 50%;
  animation: c-loading-spin 0.8s linear infinite;
}

.loading-text {
  font-size: $uni-font-size-sm;
  color: $uni-text-color-grey;
}

@keyframes c-loading-spin {
  to { transform: rotate(360deg); }
}
</style>