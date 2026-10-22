<template>
  <view class="profile-page">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <image class="avatar" :src="userInfo?.avatar || '/static/default-avatar.png'" mode="aspectFill" lazy-load />
      <view class="user-info">
        <text class="user-name">{{ userInfo?.nickName || userInfo?.userName || '工程师' }}</text>
        <text class="user-role">{{ userInfo?.roleName || userInfo?.deptName || '维保工程师' }}</text>
        <text class="user-phone" v-if="userInfo?.phone">{{ userInfo.phone }}</text>
      </view>
      <view class="edit-btn" @click="goEdit">
        <c-icon type="compose" :size="18" color="active" />
        <text class="edit-text">编辑</text>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-group">
        <view class="menu-item" @click="goEdit">
          <view class="menu-left">
            <view class="menu-icon-wrap" style="background:#eff6ff">
              <c-icon type="person" :size="20" color="active" />
            </view>
            <text class="menu-text">个人信息</text>
          </view>
          <c-icon type="arrowright" :size="16" color="#d1d5db" />
        </view>

        <view class="menu-item" @click="goPassword">
          <view class="menu-left">
            <view class="menu-icon-wrap" style="background:#fff7ed">
              <c-icon type="locked" :size="20" color="yellow" />
            </view>
            <text class="menu-text">修改密码</text>
          </view>
          <c-icon type="arrowright" :size="16" color="#d1d5db" />
        </view>

        <view class="menu-item" @click="goCheckInHistory">
          <view class="menu-left">
            <view class="menu-icon-wrap" style="background:#ecfdf5">
              <c-icon type="location" :size="20" color="green" />
            </view>
            <text class="menu-text">打卡记录</text>
          </view>
          <c-icon type="arrowright" :size="16" color="#d1d5db" />
        </view>

        <view class="menu-item" @click="goFaultList">
          <view class="menu-left">
            <view class="menu-icon-wrap" style="background:#fef2f2">
              <c-icon type="flag" :size="20" color="red" />
            </view>
            <text class="menu-text">故障记录</text>
          </view>
          <c-icon type="arrowright" :size="16" color="#d1d5db" />
        </view>
      </view>

      <view class="menu-group">
        <view class="menu-item" @click="goKnowledge">
          <view class="menu-left">
            <view class="menu-icon-wrap" style="background:#f5f3ff">
              <c-icon type="bookmark" :size="20" color="purple" />
            </view>
            <text class="menu-text">知识库</text>
          </view>
          <c-icon type="arrowright" :size="16" color="#d1d5db" />
        </view>

        <view class="menu-item" @click="goDailyReport">
          <view class="menu-left">
            <view class="menu-icon-wrap" style="background:#eff6ff">
              <c-icon type="paperclip" :size="20" color="active" />
            </view>
            <text class="menu-text">我的日报</text>
          </view>
          <c-icon type="arrowright" :size="16" color="#d1d5db" />
        </view>

        <view class="menu-item" @click="goAbout">
          <view class="menu-left">
            <view class="menu-icon-wrap" style="background:#f5f7fa">
              <c-icon type="info" :size="20" color="#909399" />
            </view>
            <text class="menu-text">关于</text>
          </view>
          <view class="menu-right">
            <text class="version-text">v{{ version }}</text>
            <c-icon type="arrowright" :size="16" color="#d1d5db" />
          </view>
        </view>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-section">
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </view>
  </view>
</template>

<script>
import { logout as logoutApi, getLoginInfo } from '@/api/request.js'

export default {
  data() {
    return { version: '1.0.0' }
  },
  computed: {
    userInfo() { return uni.getStorageSync('userInfo') || null }
  },
  onShow() {
    if (!uni.getStorageSync('token')) {
      uni.reLaunch({ url: '/pages/login/login' })
      return
    }
    this.fetchUserInfo()
  },
  methods: {
    async fetchUserInfo() {
      try {
        const res = await getLoginInfo()
        const info = res.data || res
        uni.setStorageSync('userInfo', info)
      } catch (e) {}
    },

    goEdit() { uni.navigateTo({ url: '/pages/profile/edit' }) },
    goPassword() { uni.navigateTo({ url: '/pages/profile/password' }) },
    goCheckInHistory() { uni.navigateTo({ url: '/pages/checkin/history' }) },
    goFaultList() { uni.navigateTo({ url: '/pages/fault/report' }) },
    goKnowledge() { uni.navigateTo({ url: '/pages/knowledge/list' }) },
    goDailyReport() { uni.switchTab({ url: '/pages/dailyReport/list' }) },

    goAbout() {
      uni.navigateTo({ url: '/pages/profile/about' })
    },

    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) this.doLogout()
        }
      })
    },

    async doLogout() {
      try { await logoutApi() } catch (e) {}
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
      uni.reLaunch({ url: '/pages/login/login' })
    }
  }
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 60rpx;
}

/* 用户卡片 */
.user-card {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  padding: 48rpx 32rpx;
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.avatar {
  width: 112rpx;
  height: 112rpx;
  border-radius: 50%;
  background: rgba(255,255,255,0.2);
  border: 4rpx solid rgba(255,255,255,0.4);
  flex-shrink: 0;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.user-name {
  font-size: 36rpx;
  font-weight: 700;
  color: #fff;
}

.user-role {
  font-size: 26rpx;
  color: rgba(255,255,255,0.8);
}

.user-phone {
  font-size: 24rpx;
  color: rgba(255,255,255,0.7);
}

.edit-btn {
  display: flex;
  align-items: center;
  gap: 6rpx;
  background: rgba(255,255,255,0.2);
  border-radius: 24rpx;
  padding: 10rpx 20rpx;
}

.edit-text {
  font-size: 24rpx;
  color: #fff;
  font-weight: 500;
}

/* 菜单 */
.menu-section {
  padding: 24rpx;
}

.menu-group {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 24rpx;
  border-bottom: 1rpx solid #f5f5f5;
  transition: background 0.15s;
}

.menu-item:last-child { border-bottom: none; }
.menu-item:active { background: #fafafa; }

.menu-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.menu-icon-wrap {
  width: 64rpx;
  height: 64rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.menu-text {
  font-size: 30rpx;
  color: #1a1a1a;
}

.menu-right {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.version-text {
  font-size: 24rpx;
  color: #909399;
}

/* 退出登录 */
.logout-section {
  padding: 0 24rpx;
}

.logout-btn {
  width: 100%;
  height: 96rpx;
  background: #fff;
  border-radius: 16rpx;
  color: #ef4444;
  font-size: 32rpx;
  font-weight: 600;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logout-btn::after { border: none; }
.logout-btn:active { background: #fef2f2; }
</style>
