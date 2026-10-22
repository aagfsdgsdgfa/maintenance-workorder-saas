<template>
  <view class="c-skeleton">
    <view v-for="i in count" :key="i" class="skeleton-row" :style="{ marginBottom: gap + 'rpx' }">
      <view v-if="type === 'text'" class="skeleton-block skeleton-text" :style="{ width: widths[i % widths.length] }"></view>
      <view v-else-if="type === 'circle'" class="skeleton-block skeleton-circle" :style="{ width: size + 'rpx', height: size + 'rpx' }"></view>
      <view v-else class="skeleton-block skeleton-rect" :style="{ width: '100%', height: size + 'rpx' }"></view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'c-skeleton',
  props: {
    count: { type: Number, default: 3 },
    type: { type: String, default: 'text' },
    size: { type: Number, default: 32 },
    gap: { type: Number, default: 16 }
  },
  data() {
    return {
      widths: ['60%', '80%', '40%', '70%', '50%']
    }
  }
}
</script>

<style lang="scss" scoped>
.c-skeleton {
  padding: 24rpx;
}

.skeleton-row {
  display: flex;
}

.skeleton-block {
  background: linear-gradient(90deg, #f3f4f6 25%, #e5e7eb 50%, #f3f4f6 75%);
  background-size: 200% 100%;
  animation: c-skeleton-shimmer 1.4s ease-in-out infinite;
}

.skeleton-text {
  height: 28rpx;
  border-radius: 8rpx;
}

.skeleton-circle {
  border-radius: 50%;
}

.skeleton-rect {
  border-radius: 12rpx;
}

@keyframes c-skeleton-shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>