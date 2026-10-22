<template>
  <div class="topbar-wrap">
    <el-menu class="topbar-menu" :default-active="activeMenu" :active-text-color="theme" mode="horizontal">
      <sidebar-item :key="route.path + index" v-for="(route, index) in topMenus" :item="route" :base-path="route.path" />

      <el-submenu index="more" class="el-submenu__hide-arrow" v-if="moreRoutes.length > 0">
        <template slot="title">更多菜单</template>
        <sidebar-item :key="route.path + index" v-for="(route, index) in moreRoutes" :item="route" :base-path="route.path" />
      </el-submenu>
    </el-menu>

    <!-- 右侧：快捷操作 + 通知 + 用户 -->
    <div class="topbar-right">
      <div class="topbar-quick-btns">
        <el-button size="small" icon="el-icon-plus" type="primary" plain @click="quickAction('/zw/asset')">新增资产</el-button>
        <el-button size="small" icon="el-icon-document" type="primary" plain @click="quickAction('/zw/workOrder')">新建工单</el-button>
      </div>

      <!-- 通知铃铛 -->
      <div class="topbar-icon-btn" title="通知公告" @click="$router.push('/system/notice')">
        <el-badge :value="unreadNotice" :hidden="!unreadNotice" :max="99" type="danger">
          <i class="el-icon-bell" style="font-size:16px"></i>
        </el-badge>
      </div>

      <!-- 用户下拉 -->
      <el-dropdown trigger="hover" @command="handleUserCommand">
        <div class="topbar-user">
          <el-avatar :size="30" style="background:#1B3A6B; font-size:13px">{{ nickName ? nickName[0] : 'A' }}</el-avatar>
          <span class="topbar-username">{{ nickName || '管理员' }}</span>
          <i class="el-icon-arrow-down" style="font-size:11px;color:#909399"></i>
        </div>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="profile" icon="el-icon-user">个人中心</el-dropdown-item>
          <el-dropdown-item command="notice" icon="el-icon-bell">我的通知</el-dropdown-item>
          <el-dropdown-item divided command="logout" icon="el-icon-switch-button">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import SidebarItem from '../Sidebar/SidebarItem'
import { listNoticeTop } from '@/api/system/notice'

export default {
  components: { SidebarItem },
  data() {
    return {
      visibleNumber: 5,
      unreadNotice: 0
    }
  },
  computed: {
    theme() {
      return this.$store.state.settings.theme
    },
    nickName() {
      return this.$store.getters.nickName
    },
    topMenus() {
      return this.$store.state.permission.sidebarRouters.filter((f) => !f.hidden).slice(0, this.visibleNumber)
    },
    moreRoutes() {
      const sidebarRouters = this.$store.state.permission.sidebarRouters;
      return sidebarRouters.filter((f) => !f.hidden).slice(this.visibleNumber, sidebarRouters.length - this.visibleNumber)
    },
    activeMenu() {
      const { meta, path } = this.$route
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
  },
  mounted() {
    this.setVisibleNumber()
    this.loadUnreadNotice()
    window.addEventListener('resize', this.setVisibleNumber)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.setVisibleNumber)
  },
  methods: {
    setVisibleNumber() {
      const width = document.body.getBoundingClientRect().width / 3
      this.visibleNumber = parseInt(width / 85)
    },
    quickAction(path) {
      this.$router.push(path)
    },
    loadUnreadNotice() {
      listNoticeTop().then(res => {
        const list = res.data || []
        this.unreadNotice = list.filter(n => !n.isRead).length
      }).catch(() => {})
    },
    handleUserCommand(cmd) {
      if (cmd === 'logout') {
        this.$confirm('确定注销并退出系统吗？', '提示', { type: 'warning' })
          .then(() => {
            this.$store.dispatch('LogOut').then(() => { location.href = '/index' })
          }).catch(() => {})
      } else if (cmd === 'profile') {
        this.$router.push('/user/profile')
      } else if (cmd === 'notice') {
        this.$router.push('/system/notice')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.topbar-wrap {
  display: flex;
  align-items: center;
  height: 50px;
  flex: 1;
  min-width: 0;
}

.topbar-menu {
  flex: 1;
  min-width: 0;
  border-bottom: none;
  background: transparent;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-right: 8px;
  flex-shrink: 0;
}

.topbar-quick-btns {
  display: flex;
  gap: 6px;
}

.topbar-icon-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 6px;
  cursor: pointer;
  color: #606266;
  transition: all 0.2s;
  &:hover {
    background: rgba(27, 58, 107, 0.08);
    color: #1B3A6B;
  }
}

.topbar-user {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background 0.2s;
  &:hover {
    background: rgba(27, 58, 107, 0.06);
  }
}
.topbar-username {
  font-size: 13px;
  color: #303133;
  font-weight: 500;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>

<style lang="scss">
/* menu item */
.topbar-menu.el-menu--horizontal .el-submenu__title, .topbar-menu.el-menu--horizontal .el-menu-item {
  padding: 0 12px !important;
  font-size: 14px;
  color: #303133;
  border-bottom: 2px solid transparent;
  transition: color 0.2s, border-color 0.2s;
  &:hover {
    color: #1B3A6B !important;
    border-bottom-color: #1B3A6B;
  }
}

.el-menu--horizontal .el-menu--popup .el-menu-item:hover {
  background-color: #f0f5ff !important;
}

/* submenu item */
.topbar-menu.el-menu--horizontal > .el-submenu .el-submenu__title {
  float: left;
  height: 50px !important;
  line-height: 50px !important;
  color: #303133;
  margin: 0 12px !important;
  border-bottom: 2px solid transparent;
  transition: color 0.2s, border-color 0.2s;
  &:hover {
    color: #1B3A6B !important;
    border-bottom-color: #1B3A6B;
  }
}

/* topbar more arrow */
.topbar-menu .el-submenu .el-submenu__icon-arrow {
  position: static;
  vertical-align: middle;
  margin-left: 6px;
  margin-top: 0px;
}

/* active top menu item */
.topbar-menu.el-menu--horizontal .el-menu-item.is-active {
  color: #1B3A6B !important;
  border-bottom-color: #1B3A6B !important;
  border-bottom-width: 2px;
}
.topbar-menu.el-menu--horizontal .el-submenu.is-active > .el-submenu__title {
  color: #1B3A6B !important;
  border-bottom-color: #1B3A6B;
}

.el-menu--horizontal .el-menu .el-menu-item, .el-menu--horizontal .el-menu .el-submenu__title{
  color: #303133;
}
</style>

