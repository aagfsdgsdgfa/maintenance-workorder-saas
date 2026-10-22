<template>
  <div class="notification-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-title">消息中心</div>
      <div style="display:flex;gap:10px;align-items:center">
        <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99">
          <el-tag type="info">未读消息</el-tag>
        </el-badge>
        <el-button v-if="hasUnread" type="primary" plain size="small" @click="markAllAsRead">
          <el-icon><Check /></el-icon>全部标为已读
        </el-button>
      </div>
    </div>

    <!-- 筛选标签 -->
    <div class="filter-tabs">
      <el-radio-group v-model="filterType" size="default">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="N">
          未读
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99" />
        </el-radio-button>
        <el-radio-button label="Y">已读</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 消息列表 -->
    <div class="notif-list" v-if="filteredList.length">
      <div
        v-for="(n, i) in filteredList"
        :key="n.notifId"
        class="notif-item fade-in-up"
        :class="{ unread: n.isRead === 'N' }"
        :style="{ animationDelay: (i * 0.03) + 's', opacity: 0 }"
        @click="readNotif(n)"
      >
        <!-- 左侧类型指示条 -->
        <div class="notif-indicator" :class="indicatorClass(n.type)"></div>

        <!-- 类型图标 -->
        <div class="notif-icon-wrap" :class="iconClass(n.type)">
          <el-icon size="16"><Bell /></el-icon>
        </div>

        <!-- 内容区 -->
        <div class="notif-body">
          <div class="notif-header">
            <span class="notif-title">{{ n.title }}</span>
            <el-tag v-if="n.isRead === 'N'" size="small" type="danger" effect="dark">未读</el-tag>
          </div>
          <div class="notif-content">{{ n.content }}</div>
          <div class="notif-time">
            <el-icon size="12"><Clock /></el-icon>
            {{ formatTime(n.createTime) }}
          </div>
        </div>

        <!-- 操作区 -->
        <div class="notif-action">
          <el-button type="primary" link size="small" @click.stop="goDetail(n)">
            查看详情 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <EmptyState
      v-else-if="!loading"
      icon="bell"
      :title="filterType === 'N' ? '暂无未读消息' : filterType === 'Y' ? '暂无已读消息' : '暂无消息'"
      :description="filterType === 'N' ? '所有消息都已读，干得漂亮！' : '消息列表为空'"
    />

    <div v-if="loading" style="text-align:center;padding:60px;color:var(--text-muted)">
      <el-icon class="is-loading" size="24"><Loading /></el-icon>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getNotificationList, markRead, markAllRead, getUnreadCount } from '@/api/zw/notification'
import { ElMessage } from 'element-plus'
import EmptyState from '@/components/EmptyState.vue'

const router = useRouter()
const filterType = ref('')
const list = ref([])

async function loadList() {
  try {
    const res = await getNotificationList({ isRead: filterType.value || undefined })
    if (res.code === 200) list.value = res.rows || []
  } catch (e) {
    console.error('消息列表加载失败', e)
  }
}

const filteredList = computed(() => {
  if (!filterType.value) return list.value
  return list.value.filter(n => n.isRead === filterType.value)
})

const hasUnread = computed(() => list.value.some(n => n.isRead === 'N'))
const unreadCount = computed(() => list.value.filter(n => n.isRead === 'N').length)

function iconClass(type) {
  return {
    workorder_complete: 'icon-success',
    workorder_accept: 'icon-primary',
    workorder: 'icon-primary',
    inspection_remind: 'icon-warning',
    contract_expiring: 'icon-danger',
    system: 'icon-default'
  }[type] || 'icon-default'
}

function indicatorClass(type) {
  return {
    workorder_complete: 'indicator-success',
    workorder_accept: 'indicator-primary',
    workorder: 'indicator-primary',
    inspection_remind: 'indicator-warning',
    contract_expiring: 'indicator-danger',
    system: 'indicator-default'
  }[type] || 'indicator-default'
}

async function readNotif(n) {
  if (n.isRead === 'N') {
    try {
      await markRead(n.notifId)
      n.isRead = 'Y'
    } catch (e) { console.error('标记已读失败', e) }
  }
}

async function markAllAsRead() {
  const count = list.value.filter(n => n.isRead === 'N').length
  if (count === 0) {
    ElMessage.info('没有未读通知')
    return
  }
  try {
    await markAllRead()
    list.value.forEach(n => { if (n.isRead === 'N') n.isRead = 'Y' })
    ElMessage.success('已全部标为已读')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

function goDetail(n) {
  readNotif(n)
  if (['workorder_complete', 'workorder_accept', 'workorder'].includes(n.type)) {
    if (n.relatedId) router.push('/repair/detail/' + n.relatedId)
  } else if (n.type === 'contract_expiring') {
    if (n.relatedId) router.push('/contract/detail/' + n.relatedId)
  }
}

function formatTime(t) {
  if (!t) return ''
  return t.substring ? t.substring(0, 16) : String(t).substring(0, 16)
}

const loading = ref(false)
onMounted(() => { loadList() })
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-title::before {
  content: '';
  display: block;
  width: 4px;
  height: 20px;
  background: var(--primary-gradient);
  border-radius: 2px;
}

.filter-tabs {
  margin-bottom: 16px;
}

.notif-list {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.notif-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-light);
  cursor: pointer;
  transition: background var(--transition-fast);
  position: relative;
}

.notif-item:last-child {
  border-bottom: none;
}

.notif-item:hover {
  background: var(--bg-page);
}

.notif-item.unread {
  background: #f8fbff;
}

.notif-indicator {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  border-radius: 0 3px 3px 0;
}

.indicator-success { background: var(--success); }
.indicator-primary { background: var(--primary-light); }
.indicator-warning { background: var(--warning); }
.indicator-danger { background: var(--danger); }
.indicator-default { background: var(--text-muted); }

.notif-icon-wrap {
  width: 38px;
  height: 38px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon-success { background: #f0fdf4; color: var(--success); }
.icon-primary { background: #eff6ff; color: var(--primary-light); }
.icon-warning { background: #fffbeb; color: var(--warning); }
.icon-danger { background: #fef2f2; color: var(--danger); }
.icon-default { background: var(--bg-page); color: var(--text-muted); }

.notif-body {
  flex: 1;
  min-width: 0;
}

.notif-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.notif-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.notif-content {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.notif-time {
  font-size: 12px;
  color: var(--text-muted);
  display: flex;
  align-items: center;
  gap: 4px;
}

.notif-action {
  flex-shrink: 0;
  display: flex;
  align-items: center;
}
</style>
