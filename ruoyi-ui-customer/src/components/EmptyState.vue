<template>
  <div class="empty-state">
    <div class="empty-icon" :style="{ background: iconBg }">
      <el-icon :size="48" :color="iconColor">
        <component :is="iconComponent" />
      </el-icon>
    </div>
    <div class="empty-title">{{ title }}</div>
    <div class="empty-desc" v-if="description">{{ description }}</div>
    <div class="empty-action" v-if="actionText">
      <el-button v-if="actionTo" type="primary" @click="goTo">{{ actionText }}</el-button>
      <el-button v-else type="primary" @click="$emit('action')">{{ actionText }}</el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { Document, Box, Tickets, Bell, CircleCheck, WarningFilled } from '@element-plus/icons-vue'

const props = defineProps({
  icon: { type: String, default: 'document' },
  title: { type: String, required: true },
  description: { type: String, default: '' },
  actionText: { type: String, default: '' },
  actionTo: { type: String, default: '' }
})

defineEmits(['action'])

const router = useRouter()

const iconMap = {
  document: Document,
  box: Box,
  tickets: Tickets,
  bell: Bell,
  success: CircleCheck,
  warning: WarningFilled
}

const iconComponent = computed(() => iconMap[props.icon] || Document)
const iconColor = computed(() => props.icon === 'success' ? '#00C9A7' : '#94a3b8')
const iconBg = computed(() => props.icon === 'success' ? '#f0fdfa' : '#f1f5f9')

function goTo() {
  if (props.actionTo) router.push(props.actionTo)
}
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.empty-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.empty-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 6px;
}

.empty-desc {
  font-size: 13px;
  color: var(--text-muted);
  max-width: 320px;
  line-height: 1.5;
}

.empty-action {
  margin-top: 16px;
}
</style>