<template>
  <div class="error-state">
    <div class="error-icon">
      <el-icon :size="56" color="#ef4444">
        <WarningFilled />
      </el-icon>
    </div>
    <div class="error-title">{{ title }}</div>
    <div class="error-desc" v-if="message">{{ message }}</div>
    <div class="error-action" v-if="showRetry">
      <el-button type="primary" @click="$emit('retry')">
        <el-icon><Refresh /></el-icon>重试
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { WarningFilled, Refresh } from '@element-plus/icons-vue'

const props = defineProps({
  error: { type: [Error, String], default: '' },
  showRetry: { type: Boolean, default: true }
})

defineEmits(['retry'])

const title = computed(() => '加载失败')
const message = computed(() => {
  if (typeof props.error === 'string') return props.error
  return props.error?.message || '请检查网络后重试'
})
</script>

<style scoped>
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.error-icon {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  background: #fef2f2;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.error-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 6px;
}

.error-desc {
  font-size: 13px;
  color: var(--text-muted);
  max-width: 360px;
  line-height: 1.5;
}

.error-action {
  margin-top: 16px;
}
</style>