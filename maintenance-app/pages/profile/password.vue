<template>
  <view class="password-page">
    <!-- 安全提示 -->
    <view class="security-tips">
      <text class="tips-icon">P</text>
      <text class="tips-text">为保障账户安全，密码需包含字母和数字，长度8-20位</text>
    </view>

    <!-- 密码表单 -->
    <view class="form-wrap">
      <!-- 旧密码 -->
      <view class="form-item">
        <text class="form-label">旧密码</text>
        <view class="input-wrap">
          <input
            class="form-input"
            :type="showOld ? 'text' : 'password'"
            v-model="formData.oldPassword"
            placeholder="请输入旧密码"
            maxlength="20"
          />
          <text class="toggle-icon" @click="showOld = !showOld">
            {{ showOld ? 'X' : 'X' }}
          </text>
        </view>
      </view>

      <!-- 新密码 -->
      <view class="form-item">
        <text class="form-label">新密码</text>
        <view class="input-wrap">
          <input
            class="form-input"
            :type="showNew ? 'text' : 'password'"
            v-model="formData.newPassword"
            placeholder="请输入新密码"
            maxlength="20"
          />
          <text class="toggle-icon" @click="showNew = !showNew">
            {{ showNew ? 'X' : 'X' }}
          </text>
        </view>
      </view>

      <!-- 确认密码 -->
      <view class="form-item">
        <text class="form-label">确认密码</text>
        <view class="input-wrap">
          <input
            class="form-input"
            :type="showConfirm ? 'text' : 'password'"
            v-model="formData.confirmPassword"
            placeholder="请再次输入新密码"
            maxlength="20"
          />
          <text class="toggle-icon" @click="showConfirm = !showConfirm">
            {{ showConfirm ? 'X' : 'X' }}
          </text>
        </view>
      </view>
    </view>

    <!-- 密码强度 -->
    <view class="strength-section" v-if="formData.newPassword">
      <text class="strength-label">密码强度：</text>
      <view class="strength-bars">
        <view :class="['bar', { active: strengthLevel >= 1, weak: strengthLevel === 1, medium: strengthLevel === 2, strong: strengthLevel === 3 }]"></view>
        <view :class="['bar', { active: strengthLevel >= 2, medium: strengthLevel === 2, strong: strengthLevel === 3 }]"></view>
        <view :class="['bar', { active: strengthLevel >= 3, strong: strengthLevel === 3 }]"></view>
      </view>
      <text :class="['strength-text', { weak: strengthLevel === 1, medium: strengthLevel === 2, strong: strengthLevel === 3 }]">
        {{ strengthLabel }}
      </text>
    </view>

    <!-- 提交按钮 -->
    <view class="submit-section">
      <button
        class="submit-btn"
        :loading="submitting"
        :disabled="submitting"
        @click="handleSubmit"
      >
        确认修改
      </button>
    </view>

    <!-- 其他登录方式提示 -->
    <view class="help-tip">
      <text class="help-text">忘记旧密码？请联系管理员重置</text>
    </view>
  </view>
</template>

<script>
import { changePassword } from '@/api/request.js'

export default {
  data() {
    return {
      formData: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      showOld: false,
      showNew: false,
      showConfirm: false,
      submitting: false,
      strengthLevel: 0
    }
  },
  computed: {
    strengthLabel() {
      const map = { 0: '', 1: '弱', 2: '中', 3: '强' }
      return map[this.strengthLevel] || ''
    }
  },
  watch: {
    'formData.newPassword'(val) {
      this.checkStrength(val)
    }
  },
  methods: {
    checkStrength(password) {
      if (!password) {
        this.strengthLevel = 0
        return
      }
      let score = 0
      if (password.length >= 8) score++
      if (/[a-zA-Z]/.test(password) && /[0-9]/.test(password)) score++
      if (/[^a-zA-Z0-9]/.test(password)) score++
      this.strengthLevel = score
    },
    handleSubmit() {
      // 旧密码验证
      if (!this.formData.oldPassword) {
        uni.showToast({ title: '请输入旧密码', icon: 'none' })
        return
      }
      // 新密码验证
      if (!this.formData.newPassword) {
        uni.showToast({ title: '请输入新密码', icon: 'none' })
        return
      }
      if (this.formData.newPassword.length < 8) {
        uni.showToast({ title: '新密码长度不能少于8位', icon: 'none' })
        return
      }
      if (this.formData.newPassword.length > 20) {
        uni.showToast({ title: '新密码长度不能超过20位', icon: 'none' })
        return
      }
      if (!/[a-zA-Z]/.test(this.formData.newPassword)) {
        uni.showToast({ title: '新密码需包含字母', icon: 'none' })
        return
      }
      if (!/[0-9]/.test(this.formData.newPassword)) {
        uni.showToast({ title: '新密码需包含数字', icon: 'none' })
        return
      }
      // 确认密码验证
      if (!this.formData.confirmPassword) {
        uni.showToast({ title: '请确认新密码', icon: 'none' })
        return
      }
      if (this.formData.newPassword !== this.formData.confirmPassword) {
        uni.showToast({ title: '两次输入的密码不一致', icon: 'none' })
        return
      }
      // 新旧密码相同
      if (this.formData.oldPassword === this.formData.newPassword) {
        uni.showToast({ title: '新密码不能与旧密码相同', icon: 'none' })
        return
      }

      uni.showModal({
        title: '修改密码',
        content: '确定要修改密码吗？修改后需使用新密码登录。',
        success: async (res) => {
          if (!res.confirm) return

          this.submitting = true
          try {
            await changePassword({
              oldPassword: this.formData.oldPassword,
              newPassword: this.formData.newPassword
            })
            uni.showToast({ title: '密码修改成功', icon: 'success' })
            // 清除登录状态，跳转登录页
            setTimeout(() => {
              uni.removeStorageSync('token')
              uni.removeStorageSync('userInfo')
              uni.reLaunch({ url: '/pages/login/login' })
            }, 1500)
          } catch (e) {
            uni.showToast({ title: e.msg || '修改失败', icon: 'none' })
          } finally {
            this.submitting = false
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.password-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-top: 16rpx;
}

.security-tips {
  display: flex;
  align-items: flex-start;
  gap: 10rpx;
  background: #ecf5ff;
  margin: 0 24rpx 24rpx;
  padding: 20rpx 24rpx;
  border-radius: 12rpx;
}

.tips-icon {
  font-size: 28rpx;
  flex-shrink: 0;
  margin-top: 2rpx;
}

.tips-text {
  font-size: 24rpx;
  color: #409eff;
  line-height: 1.6;
}

.form-wrap {
  background: #fff;
  margin: 0 24rpx;
  border-radius: 16rpx;
  overflow: hidden;
}

.form-item {
  display: flex;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #ebeef5;
}

.form-item:last-child {
  border-bottom: none;
}

.form-label {
  font-size: 30rpx;
  color: #303133;
  font-weight: 500;
  width: 140rpx;
  flex-shrink: 0;
}

.input-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.form-input {
  flex: 1;
  font-size: 30rpx;
  color: #303133;
  text-align: right;
}

.toggle-icon {
  font-size: 32rpx;
  padding: 8rpx 12rpx;
  margin-left: 8rpx;
  flex-shrink: 0;
}

.strength-section {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin: 20rpx 48rpx;
}

.strength-label {
  font-size: 24rpx;
  color: #909399;
  flex-shrink: 0;
}

.strength-bars {
  display: flex;
  gap: 8rpx;
  flex: 1;
}

.bar {
  height: 8rpx;
  flex: 1;
  border-radius: 4rpx;
  background: #ebeef5;
  transition: background 0.3s;
}

.bar.active.weak {
  background: #f56c6c;
}

.bar.active.medium {
  background: #e6a23c;
}

.bar.active.strong {
  background: #67c23a;
}

.strength-text {
  font-size: 24rpx;
  color: #c0c4cc;
  width: 40rpx;
}

.strength-text.weak {
  color: #f56c6c;
}

.strength-text.medium {
  color: #e6a23c;
}

.strength-text.strong {
  color: #67c23a;
}

.submit-section {
  padding: 48rpx 24rpx 24rpx;
}

.submit-btn {
  width: 100%;
  height: 96rpx;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  border-radius: 48rpx;
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.submit-btn::after {
  border: none;
}

.submit-btn[disabled] {
  opacity: 0.6;
}

.help-tip {
  text-align: center;
  padding: 16rpx 24rpx;
}

.help-text {
  font-size: 24rpx;
  color: #909399;
}
</style>
