<template>
  <view class="report-page">
    <!-- 关联设备 -->
    <view class="form-section">
      <view class="section-title">关联设备</view>
      <view class="asset-field">
        <input
          class="asset-input"
          v-model="form.assetCode"
          placeholder="扫描或输入设备编码"
          placeholder-class="placeholder"
          :adjust-position="true"
          :cursor-spacing="20"
        />
        <button class="scan-btn" size="mini" @click="scanAsset">
          <c-icon type="scan" :size="16" color="#fff" />
          <text>扫码</text>
        </button>
      </view>
      <view class="asset-info" v-if="selectedAsset">
        <text class="asset-name">{{ selectedAsset.assetName }}</text>
        <text class="asset-code">{{ selectedAsset.assetCode }}</text>
        <text class="asset-location" v-if="selectedAsset.locationName || selectedAsset.location">
          {{ selectedAsset.locationName || selectedAsset.location }}
        </text>
      </view>
    </view>

    <!-- 故障类型 -->
    <view class="form-section">
      <view class="section-title">故障类型</view>
      <view class="type-grid">
        <view
          class="type-item"
          v-for="t in faultTypes"
          :key="t.value"
          :class="{ selected: form.faultType === t.value }"
          @click="form.faultType = t.value"
        >
          <c-icon :type="t.icon" :size="28" :color="form.faultType === t.value ? '#2563eb' : '#909399'" />
          <text class="type-label">{{ t.label }}</text>
        </view>
      </view>
    </view>

    <!-- 故障描述 -->
    <view class="form-section">
      <view class="section-title">故障描述</view>
      <view class="textarea-wrapper">
        <textarea
          class="desc-textarea"
          v-model="form.description"
          placeholder="请详细描述故障现象..."
          placeholder-class="placeholder"
          :adjust-position="true"
          :cursor-spacing="20"
          :maxlength="500"
        ></textarea>
        <view class="voice-btn-wrap">
          <button
            class="voice-btn"
            :class="{ recording: isRecording }"
            size="mini"
            @click="toggleVoice"
          >
            <c-icon :type="isRecording ? 'close' : 'mic'" :size="16" :color="isRecording ? '#f56c6c' : '#606266'" />
            <text>{{ isRecording ? '录音中...' : 'AI语音录入' }}</text>
          </button>
          <text class="char-count">{{ form.description.length }}/500</text>
        </view>
      </view>
    </view>

    <!-- 故障照片 -->
    <view class="form-section">
      <view class="section-title">故障照片（最多5张）</view>
      <view class="photo-grid">
        <view
          class="photo-item"
          v-for="(photo, idx) in form.photos"
          :key="idx"
        >
          <image class="photo-img" :src="photo" mode="aspectFill" @click="previewPhoto(idx)" lazy-load />
          <view class="photo-del" @click="removePhoto(idx)">
            <c-icon type="close" :size="14" color="#fff" />
          </view>
        </view>
        <view class="photo-add" v-if="form.photos.length < 5" @click="takePhoto">
          <c-icon type="camera" :size="32" color="#c0c4cc" />
          <text class="add-text">添加照片</text>
        </view>
      </view>
    </view>

    <!-- 紧急程度 -->
    <view class="form-section">
      <view class="section-title">紧急程度</view>
      <view class="urgency-options">
        <view
          class="urgency-item"
          :class="{ selected: form.urgency === 'urgent', urgent: form.urgency === 'urgent' }"
          @click="form.urgency = 'urgent'"
        >
          <c-icon type="alert" :size="28" :color="form.urgency === 'urgent' ? '#f56c6c' : '#909399'" />
          <text class="urgency-label">紧急</text>
          <text class="urgency-desc">需要立即处理</text>
        </view>
        <view
          class="urgency-item"
          :class="{ selected: form.urgency === 'normal', normal: form.urgency === 'normal' }"
          @click="form.urgency = 'normal'"
        >
          <c-icon type="info" :size="28" :color="form.urgency === 'normal' ? '#67c23a' : '#909399'" />
          <text class="urgency-label">一般</text>
          <text class="urgency-desc">可择期处理</text>
        </view>
      </view>
    </view>

    <!-- 提交按钮 -->
    <view class="submit-section">
      <button
        class="submit-btn"
        :disabled="!canSubmit || submitting"
        :loading="submitting"
        @click="handleSubmit"
      >
        <c-icon type="paperplane" :size="20" color="#fff" />
        <text>{{ submitting ? '提交中...' : '提交故障' }}</text>
      </button>
    </view>
  </view>
</template>

<script>
import { createFaultRecord, scanAsset as scanAssetApi, uploadFile, voiceToText } from '@/api/request.js'

export default {
  data() {
    return {
      faultTypes: [
        { value: 'mechanical', label: '机械故障', icon: 'gear' },
        { value: 'electrical', label: '电气故障', icon: 'bolt' },
        { value: 'software', label: '软件故障', icon: 'cloud' },
        { value: 'network', label: '网络故障', icon: 'wifi' },
        { value: 'other', label: '其他故障', icon: 'help' }
      ],
      form: {
        assetCode: '',
        faultType: '',
        description: '',
        photos: [],
        urgency: 'normal'
      },
      selectedAsset: null,
      assetList: [],
      submitting: false,
      isRecording: false,
      recordFilePath: ''
    }
  },
  computed: {
    canSubmit() {
      return this.form.assetCode && this.form.faultType && this.form.description
    }
  },
  onLoad(options) {
    if (options.assetCode) {
      this.form.assetCode = options.assetCode
      this.lookupAsset(options.assetCode)
    }
  },
  methods: {
    scanAsset() {
      uni.scanCode({
        success: (res) => {
          this.form.assetCode = res.result
          this.lookupAsset(res.result)
        },
        fail: () => { uni.showToast({ title: '扫码失败', icon: 'none' }) }
      })
    },

    async lookupAsset(code) {
      try {
        uni.showLoading({ title: '查询设备...' })
        const res = await scanAssetApi(code)
        uni.hideLoading()
        this.selectedAsset = res.data || res
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: '未找到该设备，可手动描述', icon: 'none' })
      }
    },

    selectAsset(asset) {
      this.selectedAsset = asset
      this.form.assetCode = asset.assetCode || asset.code
    },

    takePhoto() {
      uni.chooseImage({
        count: this.form.photos.length < 5 ? (5 - this.form.photos.length) : 1,
        sourceType: ['camera', 'album'],
        success: (res) => {
          this.form.photos = [...this.form.photos, ...res.tempFilePaths].slice(0, 5)
        }
      })
    },

    previewPhoto(idx) {
      uni.previewImage({ urls: this.form.photos, current: this.form.photos[idx] })
    },

    removePhoto(idx) {
      this.form.photos.splice(idx, 1)
    },

    toggleVoice() {
      // #ifdef H5
      uni.showToast({ title: '语音功能仅在APP/小程序中可用', icon: 'none' })
      return
      // #endif
      if (this.isRecording) {
        this.stopRecord()
      } else {
        this.startRecord()
      }
    },

    startRecord() {
      // #ifndef H5
      uni.startRecord({
        success: () => {
          this.isRecording = true
          uni.showToast({ title: '开始录音，请说话', icon: 'none' })
          this._recordTimer = setTimeout(() => { this.stopRecord() }, 60000)
        },
        fail: () => { uni.showToast({ title: '录音失败，请检查权限', icon: 'none' }) }
      })
      // #endif
    },

    async stopRecord() {
      // #ifndef H5
      clearTimeout(this._recordTimer)
      uni.stopRecord({
        success: (res) => {
          this.isRecording = false
          this.recordFilePath = res.tempFilePath
          this.recognizeVoice(res.tempFilePath)
        },
        fail: () => { this.isRecording = false }
      })
      // #endif
    },

    async recognizeVoice(filePath) {
      uni.showLoading({ title: '识别中...' })
      try {
        const res = await voiceToText(filePath)
        uni.hideLoading()
        const text = res.data?.text || res.text || ''
        if (text) {
          this.form.description = (this.form.description + ' ' + text).trim()
          uni.showToast({ title: '识别成功', icon: 'success' })
        } else {
          uni.showToast({ title: '未识别到内容', icon: 'none' })
        }
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: '语音识别失败', icon: 'none' })
      }
    },

    async handleSubmit() {
      if (!this.canSubmit) {
        uni.showToast({ title: '请填写完整信息', icon: 'none' })
        return
      }
      this.submitting = true
      uni.showLoading({ title: '提交中...' })
      try {
        const photoUrls = []
        for (const photo of this.form.photos) {
          const res = await uploadFile(photo)
          photoUrls.push(res.url || res.data?.url)
        }
        const engineerId = uni.getStorageSync('userInfo')?.userId
        const data = {
          engineerId,
          assetCode: this.form.assetCode,
          assetId: this.selectedAsset?.assetId || null,
          assetName: this.selectedAsset?.assetName || this.form.assetCode,
          faultType: this.form.faultType,
          description: this.form.description,
          photoUrls: photoUrls.join(','),
          urgency: this.form.urgency
        }
        await createFaultRecord(data)
        uni.hideLoading()
        uni.showToast({ title: '提交成功', icon: 'success' })
        setTimeout(() => { uni.navigateBack() }, 1500)
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: e.msg || '提交失败', icon: 'none' })
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.report-page { min-height: 100vh; background: #f5f7fa; padding: 24rpx; padding-bottom: 160rpx; }

.form-section { background: #fff; border-radius: 16rpx; padding: 32rpx; margin-bottom: 24rpx; }
.section-title { font-size: 30rpx; font-weight: bold; color: #303133; margin-bottom: 20rpx; }

.asset-field { display: flex; gap: 16rpx; align-items: center; }
.asset-input {
  flex: 1; height: 80rpx; background: #f5f7fa; border-radius: 12rpx;
  padding: 0 24rpx; font-size: 28rpx;
}
.scan-btn {
  background: #2563eb; color: #fff; border: none; border-radius: 12rpx;
  white-space: nowrap; font-size: 26rpx; padding: 0 24rpx; height: 80rpx;
  display: flex; align-items: center; gap: 8rpx;
}
.scan-btn::after { border: none; }

.asset-info { margin-top: 16rpx; background: #f5f7fa; border-radius: 12rpx; padding: 20rpx; }
.asset-name { font-size: 28rpx; color: #303133; font-weight: bold; display: block; margin-bottom: 4rpx; }
.asset-code { font-size: 24rpx; color: #2563eb; display: block; margin-bottom: 4rpx; }
.asset-location { font-size: 24rpx; color: #909399; display: block; }

.type-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16rpx; }
.type-item {
  display: flex; flex-direction: column; align-items: center; padding: 24rpx 16rpx;
  border-radius: 16rpx; border: 2rpx solid #ebeef5; transition: all 0.3s; gap: 10rpx;
}
.type-item.selected { border-color: #2563eb; background: #f0f1ff; }
.type-label { font-size: 24rpx; color: #606266; }
.type-item.selected .type-label { color: #2563eb; font-weight: bold; }

.textarea-wrapper { position: relative; }
.desc-textarea {
  width: 100%; height: 200rpx; background: #f5f7fa; border-radius: 12rpx;
  padding: 24rpx; font-size: 28rpx; color: #303133; box-sizing: border-box; line-height: 1.6; border: none;
}
.voice-btn-wrap { display: flex; align-items: center; justify-content: space-between; margin-top: 16rpx; }
.voice-btn {
  background: #f5f7fa; border: 1rpx solid #dcdfe6; border-radius: 24rpx;
  color: #606266; font-size: 24rpx; padding: 8rpx 20rpx; display: flex; align-items: center; gap: 8rpx;
}
.voice-btn.recording { background: #fef0f0; border-color: #f56c6c; color: #f56c6c; }
.char-count { font-size: 24rpx; color: #909399; }

.photo-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16rpx; }
.photo-item { position: relative; aspect-ratio: 1; }
.photo-img { width: 100%; height: 100%; border-radius: 12rpx; }
.photo-del {
  position: absolute; top: -12rpx; right: -12rpx;
  width: 40rpx; height: 40rpx; background: rgba(0,0,0,0.6); border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
}
.photo-add {
  aspect-ratio: 1; border: 2rpx dashed #dcdfe6; border-radius: 12rpx;
  display: flex; flex-direction: column; align-items: center; justify-content: center; background: #fafafa; gap: 8rpx;
}
.add-text { font-size: 22rpx; color: #c0c4cc; }

.urgency-options { display: flex; gap: 16rpx; }
.urgency-item {
  flex: 1; padding: 24rpx; border-radius: 16rpx; border: 2rpx solid #ebeef5;
  display: flex; flex-direction: column; align-items: center; transition: all 0.3s; gap: 8rpx;
}
.urgency-item.selected.urgent { border-color: #f56c6c; background: #fef0f0; }
.urgency-item.selected.normal { border-color: #67c23a; background: #f0f9eb; }
.urgency-label { font-size: 28rpx; font-weight: bold; color: #303133; }
.urgency-item.selected.urgent .urgency-label { color: #f56c6c; }
.urgency-item.selected.normal .urgency-label { color: #67c23a; }
.urgency-desc { font-size: 22rpx; color: #909399; }

.submit-section {
  position: fixed; bottom: 0; left: 0; right: 0; background: #fff;
  padding: 24rpx 32rpx; padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -4rpx 16rpx rgba(0,0,0,0.08);
}
.submit-btn {
  width: 100%; height: 96rpx; background: linear-gradient(135deg, #f56c6c 0%, #e6a23c 100%);
  border-radius: 48rpx; color: #fff; font-size: 32rpx; font-weight: bold; border: none;
  display: flex; align-items: center; justify-content: center; gap: 12rpx;
}
.submit-btn::after { border: none; }
.submit-btn[disabled] { opacity: 0.5; }

.placeholder { color: #c0c4cc; }
</style>