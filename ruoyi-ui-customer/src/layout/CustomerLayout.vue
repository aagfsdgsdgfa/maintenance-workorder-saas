<template>
  <div class="customer-layout">
    <!-- 左侧深蓝侧边栏 -->
    <div class="sidebar" :style="{ width: collapsed ? '64px' : '220px' }">
      <!-- Logo 区域 -->
      <div class="sidebar-logo">
        <img src="/img/小白人.png" alt="logo" class="logo-img" />
        <div v-if="!collapsed" class="logo-info">
          <span class="logo-text">智维云服</span>
          <span class="logo-sub">客户门户</span>
        </div>
        <el-icon class="collapse-btn" @click="collapsed = !collapsed">
          <Fold v-if="!collapsed" />
          <Expand v-else />
        </el-icon>
      </div>

      <!-- 主菜单 -->
      <el-menu
        :default-active="activeMenu"
        :collapse="collapsed"
        :collapse-transition="false"
        background-color="#1a3a6b"
        text-color="rgba(255,255,255,0.6)"
        active-text-color="#fff"
        router
      >
        <el-menu-item index="/home">
          <el-icon><Odometer /></el-icon>
          <template #title>工作台</template>
        </el-menu-item>
        <el-menu-item index="/asset/list">
          <el-icon><Box /></el-icon>
          <template #title>设备管理</template>
        </el-menu-item>
        <el-menu-item index="/repair/list">
          <el-icon><WarnTriangleFilled /></el-icon>
          <template #title>报修中心</template>
        </el-menu-item>
        <el-menu-item index="/inspection/list">
          <el-icon><Document /></el-icon>
          <template #title>巡检报告</template>
        </el-menu-item>
        <el-menu-item index="/contract/list">
          <el-icon><Tickets /></el-icon>
          <template #title>合同中心</template>
        </el-menu-item>
        <el-menu-item index="/notification/list">
          <el-icon><Bell /></el-icon>
          <template #title>
            消息中心
            <el-badge v-if="unreadCount > 0" :value="unreadCount" :max="99" class="menu-badge" />
          </template>
        </el-menu-item>

        <!-- 底部个人中心 -->
        <div class="sidebar-bottom">
          <el-menu-item index="/profile">
            <el-icon><User /></el-icon>
            <template #title>个人中心</template>
          </el-menu-item>
        </div>
      </el-menu>
    </div>

    <!-- 右侧主区域 -->
    <div class="main-area">
      <!-- 顶部导航 -->
      <div class="top-navbar">
        <!-- 左侧：面包屑 + 搜索 -->
        <div class="navbar-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/home' }">
              <el-icon><HomeFilled /></el-icon>
            </el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRouteName">{{ currentRouteName }}</el-breadcrumb-item>
          </el-breadcrumb>

          <!-- 胶囊搜索框 -->
          <div class="search-pill">
            <el-icon class="search-icon"><Search /></el-icon>
            <input
              v-model="searchKeyword"
              placeholder="搜索资产、工单、合同..."
              class="search-input"
              @keydown.enter="handleSearch"
            />
          </div>
        </div>

        <!-- 右侧：快捷操作 + 通知 + 用户 -->
        <div class="navbar-right">
          <!-- 快捷操作 -->
          <el-dropdown @command="handleCommand" trigger="click">
            <span class="nav-action">
              <el-icon><Plus /></el-icon>
              快捷操作
              <el-icon class="arrow"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="quickRepair">
                  <el-icon><WarnTriangleFilled /></el-icon>快速创建报修
                </el-dropdown-item>
                <el-dropdown-item command="quickInspection">
                  <el-icon><Document /></el-icon>查看巡检报告
                </el-dropdown-item>
                <el-dropdown-item command="exportReport">
                  <el-icon><Download /></el-icon>导出报告
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <!-- 快捷入口按钮组 -->
          <div class="quick-btns">
            <el-button type="primary" size="small" @click="$router.push('/repair/create')">
              <el-icon><Plus /></el-icon>快速报修
            </el-button>
          </div>

          <!-- 通知铃铛 -->
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99" class="nav-badge">
            <div class="nav-icon-btn" @click="$router.push('/notification/list')">
              <el-icon size="18"><Bell /></el-icon>
            </div>
          </el-badge>

          <!-- 用户头像下拉 -->
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-avatar-btn">
              <el-avatar :size="34" class="user-avatar">
                {{ userInfo.nickName?.[0] || 'C' }}
              </el-avatar>
              <div class="user-info" v-if="!collapsed">
                <span class="user-name">{{ userInfo.nickName || '客户' }}</span>
                <span class="user-company">{{ userInfo.companyName || '' }}</span>
              </div>
              <el-icon class="arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 页面内容 -->
      <div class="content-area">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { getNotificationList } from '@/api/zw/notification'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const collapsed = ref(false)
const searchKeyword = ref('')
const userInfo = computed(() => userStore.userInfo)
const unreadCount = ref(0)
let pollTimer = null

const activeMenu = computed(() => route.path)

// 路由名称映射（用于面包屑）
const routeNameMap = {
  '/home': '工作台',
  '/asset/list': '设备管理',
  '/repair/list': '报修中心',
  '/repair/create': '创建报修',
  '/inspection/list': '巡检报告',
  '/contract/list': '合同中心',
  '/notification/list': '消息中心',
  '/profile': '个人中心'
}
const currentRouteName = computed(() => routeNameMap[route.path] || '')

async function fetchUnreadCount() {
  try {
    const res = await getNotificationList({ isRead: 'N' })
    if (res.code === 200) {
      unreadCount.value = res.rows?.length || 0
    }
  } catch (e) {
    console.error('获取未读数失败', e)
  }
}

function handleCommand(cmd) {
  if (cmd === 'logout') {
    userStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  } else if (cmd === 'profile') {
    router.push('/profile')
  } else if (cmd === 'quickRepair') {
    router.push('/repair/create')
  } else if (cmd === 'quickInspection') {
    router.push('/inspection/list')
  } else if (cmd === 'exportReport') {
    router.push('/report/export')
  }
}

function handleSearch() {
  if (!searchKeyword.value.trim()) return
  ElMessage.info(`搜索：${searchKeyword.value}`)
}

onMounted(() => {
  fetchUnreadCount()
  pollTimer = setInterval(fetchUnreadCount, 30000)
})

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
})
</script>

<style scoped>
.customer-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* ==================== 侧边栏 ==================== */
.sidebar {
  background: #1B4B8E;
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;
  overflow: hidden;
  position: relative;
}

/* 顶部 Logo */
.sidebar-logo {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 14px;
  gap: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  flex-shrink: 0;
  background: linear-gradient(135deg, #0F3460, #1B4B8E);
  position: relative;
  overflow: hidden;
}

.sidebar-logo::after {
  content: '';
  position: absolute;
  right: -20px;
  top: 0;
  bottom: 0;
  width: 60px;
  background: rgba(255, 255, 255, 0.04);
  transform: skewX(-15deg);
}

.logo-img {
  height: 34px;
  width: auto;
  object-fit: contain;
  flex-shrink: 0;
  border-radius: 8px;
}

.logo-info {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 0;
}

.logo-text {
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  white-space: nowrap;
  letter-spacing: 1px;
}

.logo-sub {
  color: rgba(255, 255, 255, 0.45);
  font-size: 10px;
  white-space: nowrap;
}

.collapse-btn {
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  padding: 5px;
  border-radius: 6px;
  transition: all 0.2s;
  font-size: 16px;
  flex-shrink: 0;
}

.collapse-btn:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.12);
}

/* 菜单 */
.el-menu {
  border: none !important;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.el-menu-item {
  height: 48px;
  line-height: 48px;
  font-size: 14px;
  margin: 2px 8px;
  border-radius: 8px;
  transition: all 0.2s;
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
}

.el-menu-item:hover {
  background: rgba(255, 255, 255, 0.08) !important;
}

.el-menu-item.is-active {
  background: rgba(37, 99, 235, 0.35) !important;
  color: #fff !important;
  font-weight: 600;
}

.el-menu-item.is-active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 22px;
  background: #00C9A7;
  border-radius: 0 4px 4px 0;
  box-shadow: 0 2px 8px rgba(0, 201, 167, 0.4);
}

.menu-badge {
  margin-left: 6px;
  vertical-align: middle;
}

.sidebar-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding: 8px 0;
}

/* ==================== 主区域 ==================== */
.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
  background: var(--bg-page);
}

/* ==================== 顶部导航 ==================== */
.top-navbar {
  height: 60px;
  background: var(--bg-card);
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  flex-shrink: 0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 胶囊搜索 */
.search-pill {
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--bg-page);
  border: 1px solid var(--border);
  border-radius: var(--radius-full);
  padding: 0 14px;
  height: 36px;
  transition: all 0.2s;
  width: 240px;
}

.search-pill:focus-within {
  border-color: var(--primary-light);
  background: #fff;
  box-shadow: 0 0 0 3px rgba(33, 85, 164, 0.08);
  width: 280px;
}

.search-icon {
  color: var(--text-muted);
  font-size: 14px;
  flex-shrink: 0;
}

.search-input {
  border: none;
  outline: none;
  background: transparent;
  font-size: 13px;
  color: var(--text-primary);
  width: 100%;
  font-family: inherit;
}

.search-input::placeholder {
  color: var(--text-muted);
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-action {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 6px 10px;
  border-radius: var(--radius-md);
  transition: all 0.2s;
}

.nav-action:hover {
  color: var(--primary-light);
  background: var(--bg-page);
}

.nav-action .arrow {
  font-size: 12px;
}

.quick-btns {
  display: flex;
  gap: 8px;
}

.nav-badge {
  display: flex;
  align-items: center;
}

.nav-icon-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.nav-icon-btn:hover {
  background: var(--bg-page);
  color: var(--primary-light);
}

.user-avatar-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 10px;
  border-radius: var(--radius-md);
  transition: all 0.2s;
}

.user-avatar-btn:hover {
  background: var(--bg-page);
}

.user-avatar {
  background: var(--primary-gradient) !important;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.3;
}

.user-company {
  font-size: 11px;
  color: var(--text-muted);
  line-height: 1.3;
}

/* ==================== 内容区 ==================== */
.content-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
}

/* ==================== 页面切换动画 ==================== */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(6px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}
</style>
