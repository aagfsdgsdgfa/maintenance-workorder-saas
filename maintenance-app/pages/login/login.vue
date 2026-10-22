<template>
  <view class="login-container">
    <!-- 背景装饰 -->
    <view class="bg-decoration">
      <view class="circle circle-1"></view>
      <view class="circle circle-2"></view>
      <view class="circle circle-3"></view>
    </view>

    <!-- Logo区域 -->
    <view class="logo-section">
      <view class="logo-wrapper">
        <image class="logo" src="/static/logo.png" mode="aspectFit" lazy-load></image>
      </view>
      <view class="brand-info">
        <text class="brand-name">智维云服</text>
        <text class="brand-tag">工程师端</text>
      </view>
    </view>

    <!-- 登录表单 -->
    <view class="form-section">
      <view class="form-title">
        <text class="title-text">欢迎回来</text>
        <text class="subtitle-text">请登录您的账号</text>
      </view>

      <!-- 账号输入 -->
      <view class="input-wrapper">
        <view class="input-label">账号</view>
        <view class="input-box" :class="{ focus: focusUsername }">
          <input
            class="input-field"
            type="text"
            v-model="formData.username"
            placeholder="请输入账号"
            placeholder-class="placeholder"
            :adjust-position="true"
            :cursor-spacing="20"
            @focus="focusUsername = true"
            @blur="focusUsername = false"
          />
        </view>
      </view>

      <!-- 密码输入 -->
      <view class="input-wrapper">
        <view class="input-label">密码</view>
        <view class="input-box" :class="{ focus: focusPassword }">
          <input
            class="input-field"
            :password="!showPassword"
            v-model="formData.password"
            placeholder="请输入密码"
            placeholder-class="placeholder"
            :adjust-position="true"
            :cursor-spacing="20"
            @focus="focusPassword = true"
            @blur="focusPassword = false"
          />
          <view class="eye-btn" @click="showPassword = !showPassword">
            <text class="eye-text">{{ showPassword ? '显示' : '隐藏' }}</text>
          </view>
        </view>
      </view>

      <!-- 验证码 -->
      <view class="input-wrapper">
        <view class="input-label">验证码</view>
        <view class="captcha-box" :class="{ focus: focusCaptcha }">
          <input
            class="input-field captcha-input"
            type="text"
            v-model="formData.code"
            placeholder="请输入右侧验证码"
            placeholder-class="placeholder"
            :adjust-position="true"
            :cursor-spacing="20"
            @focus="focusCaptcha = true"
            @blur="focusCaptcha = false"
            @confirm="handleLogin"
          />
          <!-- 验证码图片 -->
          <view class="captcha-img-wrapper" @click="refreshCaptcha">
            <image
              v-if="captchaImg"
              class="captcha-img"
              :src="captchaImg"
              mode="aspectFit"
             lazy-load></image>
            <view v-else class="captcha-loading">
              <text class="loading-text">点击刷新</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 登录按钮 -->
      <view class="login-btn-wrapper">
        <button
          class="login-btn"
          :class="{ loading: loading }"
          :loading="loading"
          :disabled="loading"
          @click="handleLogin"
        >
          <text v-if="!loading">登 录</text>
          <text v-else>登录中...</text>
        </button>
      </view>
    </view>

    <!-- 底部版权 -->
    <view class="footer">
      <text class="copyright">智维云服 版权所有</text>
    </view>
  </view>
</template>

<script>
import { login as loginApi, getCaptcha } from '@/api/request.js'

export default {
  data() {
    return {
      loading: false,
      showPassword: false,
      focusUsername: false,
      focusPassword: false,
      focusCaptcha: false,
      captchaImg: '',
      captchaUuid: '',
      formData: {
        username: '',
        password: '',
        code: '',
        uuid: ''
      }
    }
  },
  onLoad() {
    this.refreshCaptcha()
  },
  methods: {
    async refreshCaptcha() {
      try {
        const res = await getCaptcha()
        let img = res.img || ''
        this.captchaUuid = res.uuid || ''

        if (img.startsWith('data:')) {
          this.captchaImg = img
        } else if (img.startsWith('/9j/')) {
          this.captchaImg = 'data:image/jpeg;base64,' + img
        } else if (img.startsWith('iVBOR')) {
          this.captchaImg = 'data:image/png;base64,' + img
        } else if (img.length > 100) {
          this.captchaImg = 'data:image/jpeg;base64,' + img
        } else {
          uni.showToast({ title: '验证码格式异常', icon: 'none' })
          return
        }
      } catch (e) {
        uni.showToast({ title: '验证码加载失败', icon: 'none' })
      }
    },

    async handleLogin() {
      if (!this.formData.username) {
        uni.showToast({ title: '请输入账号', icon: 'none' })
        return
      }
      if (!this.formData.password) {
        uni.showToast({ title: '请输入密码', icon: 'none' })
        return
      }
      if (!this.formData.code) {
        uni.showToast({ title: '请输入验证码', icon: 'none' })
        return
      }

      this.formData.uuid = this.captchaUuid
      this.loading = true

      try {
        const res = await loginApi(this.formData)

        // 兼容处理：token 可能在 res.token 或 res.data.token
        const token = res.token || res.data?.token || ''
        const userInfo = res.userInfo || res.data?.userInfo || res.data || res

        if (!token) {
          uni.showToast({ title: '登录失败：Token获取异常', icon: 'none' })
          this.refreshCaptcha()
          this.formData.code = ''
          return
        }

        uni.setStorageSync('token', token)
        uni.setStorageSync('userInfo', userInfo)

        uni.showToast({ title: '登录成功', icon: 'success' })
        setTimeout(() => {
          uni.switchTab({ url: '/pages/workbench/index' })
        }, 500)
      } catch (e) {
        uni.showToast({ title: e.msg || '登录失败', icon: 'none' })
        this.refreshCaptcha()
        this.formData.code = ''
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(160deg, #eff6ff 0%, #dbeafe 50%, #e0f2fe 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.5;
}

.circle-1 {
  width: 600rpx;
  height: 600rpx;
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.15), rgba(29, 78, 216, 0.08));
  top: -200rpx;
  right: -200rpx;
}

.circle-2 {
  width: 400rpx;
  height: 400rpx;
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.1), rgba(99, 102, 241, 0.05));
  bottom: 100rpx;
  left: -150rpx;
}

.circle-3 {
  width: 300rpx;
  height: 300rpx;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.12), rgba(37, 99, 235, 0.06));
  top: 200rpx;
  left: -100rpx;
}

.logo-section {
  margin-top: 120rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 1;
}

.logo-wrapper {
  width: 140rpx;
  height: 140rpx;
  background: #fff;
  border-radius: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 32rpx rgba(37, 99, 235, 0.15);
}

.logo {
  width: 100rpx;
  height: 100rpx;
  border-radius: 24rpx;
}

.brand-info {
  margin-top: 24rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.brand-name {
  font-size: 44rpx;
  font-weight: 700;
  color: #1e3a8a;
  letter-spacing: 4rpx;
}

.brand-tag {
  font-size: 24rpx;
  color: #3b82f6;
  margin-top: 8rpx;
  padding: 4rpx 16rpx;
  background: #eff6ff;
  border-radius: 20rpx;
}

.form-section {
  width: 640rpx;
  margin-top: 60rpx;
  background: #fff;
  border-radius: 32rpx;
  padding: 48rpx 40rpx;
  box-shadow: 0 8rpx 40rpx rgba(37, 99, 235, 0.1);
  z-index: 1;
}

.form-title {
  margin-bottom: 40rpx;
}

.title-text {
  font-size: 44rpx;
  font-weight: 600;
  color: #1f2937;
  display: block;
}

.subtitle-text {
  font-size: 28rpx;
  color: #9ca3af;
  margin-top: 8rpx;
  display: block;
}

.input-wrapper {
  margin-bottom: 32rpx;
}

.input-label {
  font-size: 26rpx;
  color: #4b5563;
  margin-bottom: 12rpx;
  font-weight: 500;
}

.input-box,
.captcha-box {
  display: flex;
  align-items: center;
  background: #f8fafc;
  border: 2rpx solid #e5e7eb;
  border-radius: 16rpx;
  padding: 0 24rpx;
  height: 96rpx;
  transition: all 0.2s ease;
}

.input-box.focus,
.captcha-box.focus {
  background: #fff;
  border-color: #2563eb;
  box-shadow: 0 0 0 4rpx rgba(37, 99, 235, 0.1);
}

.input-field {
  flex: 1;
  font-size: 30rpx;
  color: #1f2937;
  height: 96rpx;
}

.eye-btn {
  padding: 8rpx 12rpx;
}

.eye-text {
  font-size: 24rpx;
  color: #9ca3af;
}

.captcha-box {
  padding-right: 8rpx;
}

.captcha-input {
  flex: 1;
}

.captcha-img-wrapper {
  width: 200rpx;
  height: 72rpx;
  border-radius: 12rpx;
  overflow: hidden;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
}

.captcha-img {
  width: 200rpx;
  height: 72rpx;
}

.captcha-loading {
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-text {
  font-size: 22rpx;
  color: #9ca3af;
}

.login-btn-wrapper {
  margin-top: 40rpx;
}

.login-btn {
  width: 100%;
  height: 96rpx;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  border-radius: 48rpx;
  color: #fff;
  font-size: 32rpx;
  font-weight: 600;
  letter-spacing: 8rpx;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 16rpx rgba(37, 99, 235, 0.3);
}

.login-btn:active {
  transform: scale(0.98);
}

.login-btn[disabled] {
  opacity: 0.7;
}

.login-btn::after {
  border: none;
}

.footer {
  margin-top: auto;
  margin-bottom: 40rpx;
  padding-bottom: constant(safe-area-inset-bottom);
}

.copyright {
  font-size: 22rpx;
  color: #9ca3af;
}

.placeholder {
  color: #d1d5db;
}
</style>
