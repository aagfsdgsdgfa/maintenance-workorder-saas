<template>
  <view class="edit-profile">
    <!-- 头像 -->
    <view class="avatar-section" @click="chooseAvatar">
      <text class="section-label">头像</text>
      <view class="avatar-right">
        <image
          class="avatar-img"
          :src="formData.avatar || '/static/default-avatar.png'"
          mode="aspectFill" lazy-load />
        <text class="arrow">›</text>
      </view>
    </view>

    <!-- 表单 -->
    <view class="form-wrap">
      <view class="form-item">
        <text class="form-label">昵称</text>
        <input
          class="form-input"
          v-model="formData.nickName"
          placeholder="请输入昵称"
          maxlength="30"
        />
      </view>
      <view class="form-item">
        <text class="form-label">手机号</text>
        <input
          class="form-input"
          v-model="formData.phone"
          type="number"
          placeholder="请输入手机号"
          maxlength="11"
        />
      </view>
      <view class="form-item">
        <text class="form-label">邮箱</text>
        <input
          class="form-input"
          v-model="formData.email"
          type="text"
          placeholder="请输入邮箱"
        />
      </view>
      <view class="form-item">
        <text class="form-label">工号</text>
        <text class="form-value">{{ formData.userName || '-' }}</text>
      </view>
      <view class="form-item">
        <text class="form-label">部门</text>
        <text class="form-value">{{ formData.deptName || '-' }}</text>
      </view>
      <view class="form-item">
        <text class="form-label">角色</text>
        <text class="form-value">{{ formData.roleName || '-' }}</text>
      </view>
    </view>

    <!-- 保存按钮 -->
    <view class="save-section">
      <button
        class="save-btn"
        :loading="saving"
        :disabled="saving"
        @click="handleSave"
      >
        保存修改
      </button>
    </view>
  </view>
</template>

<script>
import { uploadFile, updateUserProfile, getLoginInfo } from '@/api/request.js'

export default {
  data() {
    return {
      formData: {
        nickName: '',
        phone: '',
        email: '',
        userName: '',
        deptName: '',
        roleName: '',
        avatar: ''
      },
      saving: false
    }
  },
  onLoad() {
    this.initForm()
  },
  methods: {
    initForm() {
      const info = uni.getStorageSync('userInfo') || {}
      this.formData = {
        nickName: info.nickName || info.nickname || '',
        phone: info.phone || '',
        email: info.email || '',
        userName: info.userName || info.username || '',
        deptName: info.deptName || info.dept_name || '',
        roleName: info.roleName || info.role_name || '',
        avatar: info.avatar || ''
      }
    },
    chooseAvatar() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: async (res) => {
          const tempFile = res.tempFilePaths[0]
          uni.showLoading({ title: '上传中...' })
          try {
            const uploadRes = await uploadFile(tempFile)
            this.formData.avatar = uploadRes.url || uploadRes.data?.url || tempFile
            uni.showToast({ title: '头像已更新', icon: 'success' })
          } catch (e) {
            // 上传失败也允许手动填入路径
            this.formData.avatar = tempFile
            uni.showToast({ title: '上传失败，使用原图', icon: 'none' })
          } finally {
            uni.hideLoading()
          }
        }
      })
    },
    async handleSave() {
      // 昵称验证
      if (!this.formData.nickName.trim()) {
        uni.showToast({ title: '请输入昵称', icon: 'none' })
        return
      }
      // 手机号格式验证
      if (this.formData.phone && !/^1[3-9]\d{9}$/.test(this.formData.phone)) {
        uni.showToast({ title: '手机号格式不正确', icon: 'none' })
        return
      }
      // 邮箱格式验证
      if (this.formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.formData.email)) {
        uni.showToast({ title: '邮箱格式不正确', icon: 'none' })
        return
      }

      this.saving = true
      try {
        await updateUserProfile({
          nickName: this.formData.nickName,
          phone: this.formData.phone,
          email: this.formData.email,
          avatar: this.formData.avatar
        })
        // 更新本地缓存
        try {
          const infoRes = await getLoginInfo()
          const userData = infoRes.data || infoRes
          uni.setStorageSync('userInfo', userData)
        } catch (e) { /* ignore */ }
        uni.showToast({ title: '保存成功', icon: 'success' })
        setTimeout(() => uni.navigateBack(), 1500)
      } catch (e) {
        uni.showToast({ title: e.msg || '保存失败', icon: 'none' })
      } finally {
        this.saving = false
      }
    }
  }
}
</script>

<style scoped>
.edit-profile {
  min-height: 100vh;
  background: #f5f7fa;
}

.avatar-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  padding: 32rpx 32rpx;
  margin-bottom: 16rpx;
}

.section-label {
  font-size: 30rpx;
  color: #303133;
  font-weight: 500;
}

.avatar-right {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.avatar-img {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background: #f0f2f5;
  border: 2rpx solid #ebeef5;
}

.arrow {
  font-size: 40rpx;
  color: #c0c4cc;
}

.form-wrap {
  background: #fff;
}

.form-item {
  display: flex;
  align-items: center;
  padding: 32rpx 32rpx;
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

.form-input {
  flex: 1;
  font-size: 30rpx;
  color: #303133;
  text-align: right;
}

.form-value {
  flex: 1;
  font-size: 30rpx;
  color: #909399;
  text-align: right;
}

.save-section {
  padding: 48rpx 32rpx;
}

.save-btn {
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

.save-btn::after {
  border: none;
}

.save-btn[disabled] {
  opacity: 0.6;
}
</style>
