<template>
  <div class="page-loading" :class="{ 'is-fullscreen': fullscreen }">
    <el-icon class="is-loading" :size="32" color="#2563EB">
      <Loading />
    </el-icon>
    <div class="loading-text" v-if="text">{{ text }}</div>
  </div>
</template>

<script setup>
import { Loading } from '@element-plus/icons-vue'

defineProps({
  text: { type: String, default: '加载中...' },
  fullscreen: { type: Boolean, default: false }
})
</script>

<style scoped>
.page-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  gap: 12px;
}

.page-loading.is-fullscreen {
  position: fixed;
  inset: 0;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(2px);
  z-index: var(--zi-modal);
}

.loading-text {
  font-size: 13px;
  color: var(--text-muted);
}

.is-loading {
  animation: rotating 1.5s linear infinite;
}

@keyframes rotating {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>