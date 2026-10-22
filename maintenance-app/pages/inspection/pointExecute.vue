<template>
  <scroll-view scroll-y class="execute-page">
    <!-- 加载状态 -->
    <view class="loading-wrap" v-if="loading">
      <view class="skeleton-card" v-for="i in 3" :key="i">
        <view class="skeleton-line long"></view>
        <view class="skeleton-line medium"></view>
        <view class="skeleton-line short"></view>
      </view>
    </view>

    <template v-else>
      <!-- 位置验证区 -->
      <view class="section-card">
        <view class="section-header">
          <text class="section-icon">@</text>
          <text class="section-title">位置验证</text>
          <view :class="['verify-status', verified ? 'verified' : 'unverified']">
            {{ verified ? '已验证' : '待验证' }}
          </view>
        </view>

        <!-- GPS 坐标 -->
        <view class="location-info">
          <view class="location-row">
            <text class="loc-label">当前位置</text>
            <text class="loc-value">
              {{ gpsLocation.lat ? `${gpsLocation.lat}, ${gpsLocation.lng}` : '正在获取定位...' }}
            </text>
          </view>
          <view class="location-row">
            <text class="loc-label">目标位置</text>
            <text class="loc-value">
              {{ pointInfo.targetLat ? `${pointInfo.targetLat}, ${pointInfo.targetLng}` : '未配置坐标' }}
            </text>
          </view>
          <view class="location-row" v-if="distance !== null">
            <text class="loc-label">距离目标</text>
            <text :class="['loc-value', 'distance-value', { 'near': isInRange, 'far': !isInRange }]">
              {{ distance < 1 ? (distance * 1000).toFixed(0) + ' 米' : distance.toFixed(1) + ' 米' }}
            </text>
          </view>
        </view>

        <!-- 操作按钮 -->
        <view class="verify-actions">
          <view class="action-btn primary" @click="verifyByGps">
            <text>{{ gpsLoading ? '定位中...' : 'GPS定位验证' }}</text>
          </view>
          <view class="action-btn secondary" @click="verifyByScan">
            <text>扫码验证</text>
          </view>
        </view>

        <!-- 扫码结果提示 -->
        <view class="scan-result" v-if="scanResult">
          <text :class="['scan-msg', scanResult.ok ? 'ok' : 'fail']">
            {{ scanResult.msg }}
          </text>
        </view>
      </view>

      <!-- 巡检点信息卡片 -->
      <view class="section-card point-info-card">
        <view class="point-info-header">
          <text class="point-title">{{ pointInfo.pointName }}</text>
          <text class="point-code">编码: {{ pointInfo.pointCode }}</text>
        </view>
        <view class="point-info-desc" v-if="pointInfo.description">
          {{ pointInfo.description }}
        </view>
      </view>

      <!-- 动态巡检表单区 -->
      <view class="section-card">
        <view class="section-header">
          <text class="section-icon">#</text>
          <text class="section-title">巡检表单</text>
        </view>

        <view class="form-area" v-if="formSchema && formSchema.fields && formSchema.fields.length">
          <view
            v-for="field in formSchema.fields"
            :key="field.fieldName"
            class="form-field"
          >
            <!-- 文本输入 -->
            <template v-if="field.fieldType === 'text' || !field.fieldType">
              <view class="field-label">
                {{ field.label }}
                <text class="required-star" v-if="field.required">*</text>
              </view>
              <input
                class="field-input"
                type="text"
                v-model="formData[field.fieldName]"
                :placeholder="field.placeholder || `请输入${field.label}`"
                @blur="validateField(field)"
              />
            </template>

            <!-- 数字输入 -->
            <template v-else-if="field.fieldType === 'number'">
              <view class="field-label">
                {{ field.label }}
                <text class="required-star" v-if="field.required">*</text>
              </view>
              <input
                class="field-input"
                type="digit"
                v-model="formData[field.fieldName]"
                :placeholder="field.placeholder || `请输入${field.label}`"
              />
            </template>

            <!-- 单选 -->
            <template v-else-if="field.fieldType === 'select'">
              <view class="field-label">
                {{ field.label }}
                <text class="required-star" v-if="field.required">*</text>
              </view>
              <view class="field-select">
                <view
                  v-for="option in (field.options || [])"
                  :key="option.value"
                  :class="['select-option', { active: formData[field.fieldName] === option.value }]"
                  @click="selectOption(field.fieldName, option.value)"
                >
                  {{ option.label }}
                </view>
              </view>
            </template>

            <!-- 多选 -->
            <template v-else-if="field.fieldType === 'multiselect'">
              <view class="field-label">
                {{ field.label }}
                <text class="required-star" v-if="field.required">*</text>
              </view>
              <view class="field-select">
                <view
                  v-for="option in (field.options || [])"
                  :key="option.value"
                  :class="['select-option', 'multi', { active: (formData[field.fieldName] || []).includes(option.value) }]"
                  @click="toggleMultiSelect(field.fieldName, option.value)"
                >
                  {{ option.label }}
                </view>
              </view>
            </template>

            <!-- 文本域 -->
            <template v-else-if="field.fieldType === 'textarea'">
              <view class="field-label">
                {{ field.label }}
                <text class="required-star" v-if="field.required">*</text>
              </view>
              <textarea
                class="field-textarea"
                v-model="formData[field.fieldName]"
                :placeholder="field.placeholder || `请输入${field.label}`"
                :maxlength="field.maxLength || 500"
              />
            </template>

            <!-- 异常标记 -->
            <template v-else-if="field.fieldType === 'fault'">
              <view class="field-label fault-label">
                <text class="fault-icon">!</text>
                {{ field.label }}
              </view>
              <view class="fault-toggle">
                <view
                  :class="['fault-btn', { active: formData[field.fieldName] === '1' }]"
                  @click="formData[field.fieldName] = '1'"
                >
                  有异常
                </view>
                <view
                  :class="['fault-btn', { active: formData[field.fieldName] === '0' }]"
                  @click="formData[field.fieldName] = '0'"
                >
                  正常
                </view>
              </view>
            </template>

            <view class="field-error" v-if="fieldErrors[field.fieldName]">
              {{ fieldErrors[field.fieldName] }}
            </view>
          </view>
        </view>

        <view class="form-empty" v-else>
          <text>该巡检点未配置表单字段</text>
        </view>
      </view>

      <!-- 巡检照片区 -->
      <view class="section-card">
        <view class="section-header">
          <text class="section-icon">[+]</text>
          <text class="section-title">巡检照片</text>
          <text class="photo-count">{{ photoList.length }}/{{ maxPhotos }}张</text>
        </view>

        <view class="photo-grid">
          <view
            v-for="(photo, index) in photoList"
            :key="index"
            class="photo-item"
          >
            <image
              class="photo-img"
              :src="photo.url"
              mode="aspectFill"
              @click="previewPhoto(photo.url)" lazy-load />
            <view class="photo-del" @click="removePhoto(index)">
              <text>×</text>
            </view>
            <view class="photo-watermark-tag" v-if="photo.watermarked">水印已添加</view>
          </view>

          <!-- 添加按钮 -->
          <view
            class="photo-add"
            v-if="photoList.length < maxPhotos"
            @click="choosePhoto"
          >
            <text class="add-icon">+</text>
            <text class="add-text">添加照片</text>
          </view>
        </view>

        <view class="photo-tip" v-if="photoList.length < maxPhotos">
          <text>已添加{{ photoList.length }}张照片，还需要 {{ maxPhotos - photoList.length }} 张</text>
        </view>
      </view>

      <!-- 备注区 -->
      <view class="section-card">
        <view class="section-header">
          <text class="section-icon">#</text>
          <text class="section-title">备注</text>
        </view>
        <textarea
          class="remark-textarea"
          v-model="remark"
          placeholder="请输入备注信息（选填）"
          :maxlength="500"
        />
        <view class="remark-counter">
          <text>{{ remark.length }}/500</text>
        </view>
      </view>

      <!-- 提交按钮 -->
      <view class="submit-section">
        <view
          :class="['submit-btn', { disabled: !canSubmit }]"
          @click="handleSubmit"
        >
          <text>{{ submitting ? '提交中...' : '提交巡检结果' }}</text>
        </view>
      </view>
    </template>
  </scroll-view>
</template>

<script>
import {
  getInspectionTemplate,
  getInspectionPointsByAsset,
  submitInspectionResult,
  uploadFile
} from '@/api/request.js'

export default {
  data() {
    return {
      loading: false,
      submitting: false,
      pointId: '',
      taskId: '',
      assetId: '',

      // 巡检点信息
      pointInfo: {
        pointName: '',
        pointCode: '',
        description: '',
        targetLat: null,
        targetLng: null,
        scanRadius: 50 // 默认50米
      },

      // GPS 相关
      gpsLocation: { lat: null, lng: null },
      gpsLoading: false,
      distance: null,
      isInRange: false,

      // 扫码验证
      verified: false,
      scanResult: null,

      // 表单相关
      formSchema: { fields: [] },
      formData: {},
      fieldErrors: {},

      // 照片
      photoList: [],
      maxPhotos: 6,

      // 备注
      remark: ''
    }
  },

  computed: {
    canSubmit() {
      return (
        this.verified &&
        !this.submitting &&
        this.validateForm()
      )
    }
  },

  onLoad(options) {
    this.pointId = options.pointId
    this.taskId = options.taskId
    this.assetId = options.assetId || ''
    this.loadPointInfo()
  },

  methods: {
    // ========== 位置验证 ==========

    async verifyByGps() {
      if (this.gpsLoading) return
      this.gpsLoading = true
      this.scanResult = null

      try {
        await this.getGpsLocation()

        if (!this.gpsLocation.lat || !this.gpsLocation.lng) {
          uni.showToast({ title: '获取定位失败', icon: 'none' })
          return
        }

        // 计算距离
        if (this.pointInfo.targetLat && this.pointInfo.targetLng) {
          this.distance = this.calcDistance(
            this.gpsLocation.lat,
            this.gpsLocation.lng,
            this.pointInfo.targetLat,
            this.pointInfo.targetLng
          )
          const radius = this.pointInfo.scanRadius || 50
          this.isInRange = this.distance <= radius

          if (this.isInRange) {
            this.verified = true
            uni.showToast({ title: 'GPS验证通过', icon: 'success' })
          } else {
            this.verified = false
            uni.showToast({
              title: `距离目标 ${this.distance.toFixed(0)}米，超出范围(${radius}米)`,
              icon: 'none',
              duration: 3000
            })
          }
        } else {
          // 没有配置目标坐标时，仅记录当前位置
          this.verified = true
          uni.showToast({ title: 'GPS位置已记录', icon: 'success' })
        }
      } catch (e) {
        uni.showToast({ title: 'GPS定位失败', icon: 'none' })
        uni.showToast({ title: '定位失败，请检查权限', icon: 'none' })
      } finally {
        this.gpsLoading = false
      }
    },

    verifyByScan() {
      uni.scanCode({
        success: async (res) => {
          const scannedCode = res.result || res.scanStr || ''
          this.scanResult = null

          if (!scannedCode) {
            this.scanResult = { ok: false, msg: '扫码内容为空' }
            return
          }

          // 验证扫码结果是否匹配 point_code
          if (this.pointInfo.pointCode && scannedCode !== this.pointInfo.pointCode) {
            // 兼容: 扫码结果可能包含编码
            const normalized = scannedCode.trim().toUpperCase()
            const targetCode = this.pointInfo.pointCode.trim().toUpperCase()
            if (!normalized.includes(targetCode) && !targetCode.includes(normalized)) {
              this.scanResult = {
                ok: false,
                msg: `扫码编码(${scannedCode})与巡检点编码(${this.pointInfo.pointCode})不匹配`
              }
              return
            }
          }

          this.scanResult = { ok: true, msg: '扫码验证成功' }
          this.verified = true
          await this.getGpsLocation()
          uni.showToast({ title: '扫码验证通过', icon: 'success' })
        },
        fail: () => {
          this.scanResult = { ok: false, msg: '扫码失败，请重试' }
          uni.showToast({ title: '扫码失败', icon: 'none' })
        }
      })
    },

    getGpsLocation() {
      return new Promise((resolve, reject) => {
        uni.getLocation({
          type: 'gcj02',
          success: (loc) => {
            this.gpsLocation = { lat: loc.latitude, lng: loc.longitude }
            resolve(loc)
          },
          fail: (err) => {
            // 尝试重新获取
            uni.showToast({ title: '获取位置失败，请开启定位权限', icon: 'none' })
            reject(err)
          }
        })
      })
    },

    // Haversine 公式计算距离（米）
    calcDistance(lat1, lng1, lat2, lng2) {
      const R = 6371000
      const dLat = this.toRad(lat2 - lat1)
      const dLng = this.toRad(lng2 - lng1)
      const a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(this.toRad(lat1)) *
          Math.cos(this.toRad(lat2)) *
          Math.sin(dLng / 2) *
          Math.sin(dLng / 2)
      const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
      return R * c
    },

    toRad(deg) {
      return (deg * Math.PI) / 180
    },

    // ========== 巡检点信息 ==========

    async loadPointInfo() {
      this.loading = true
      try {
        // 加载巡检模板（包含表单schema）
        if (this.assetId) {
          const [templateRes, pointsRes] = await Promise.all([
            getInspectionTemplate(this.assetId).catch(() => null),
            getInspectionPointsByAsset(this.assetId).catch(() => null)
          ])

          if (templateRes) {
            const schema = templateRes.data || templateRes
            this.formSchema = schema.fields ? schema : { fields: schema.fields || [] }
            // 初始化表单数据
            this.initFormData()
          }

          if (pointsRes) {
            const points = Array.isArray(pointsRes) ? pointsRes : (pointsRes.data || pointsRes.rows || [])
            const targetPoint = points.find(p => (p.pointId || p.id) == this.pointId)
            if (targetPoint) {
              this.pointInfo = { ...this.pointInfo, ...targetPoint }
              this.assetId = targetPoint.assetId || targetPoint.id || this.assetId
            }
          }
        }
      } catch (e) {
        uni.showToast({ title: '加载巡检点信息失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },

    // ========== 表单处理 ==========

    initFormData() {
      if (!this.formSchema || !this.formSchema.fields) return
      this.formSchema.fields.forEach(field => {
        if (field.fieldType === 'multiselect') {
          this.$set(this.formData, field.fieldName, [])
        } else if (field.fieldType === 'fault') {
          this.$set(this.formData, field.fieldName, '0')
        } else {
          this.$set(this.formData, field.fieldName, '')
        }
      })
    },

    validateField(field) {
      const value = this.formData[field.fieldName]
      if (field.required && !value) {
        this.$set(this.fieldErrors, field.fieldName, `请填写${field.label}`)
      } else {
        this.$set(this.fieldErrors, field.fieldName, '')
      }
    },

    validateForm() {
      if (!this.formSchema || !this.formSchema.fields) return true
      let valid = true
      this.formSchema.fields.forEach(field => {
        this.validateField(field)
        if (this.fieldErrors[field.fieldName]) {
          valid = false
        }
      })
      return valid
    },

    selectOption(fieldName, value) {
      this.$set(this.formData, fieldName, value)
      this.$set(this.fieldErrors, fieldName, '')
    },

    toggleMultiSelect(fieldName, value) {
      const list = this.formData[fieldName] || []
      const idx = list.indexOf(value)
      if (idx > -1) {
        list.splice(idx, 1)
      } else {
        list.push(value)
      }
      this.$set(this.formData, fieldName, [...list])
      this.$set(this.fieldErrors, fieldName, '')
    },

    // ========== 照片处理 ==========

    choosePhoto() {
      if (this.photoList.length >= this.maxPhotos) {
        uni.showToast({ title: `最多${this.maxPhotos}张照片`, icon: 'none' })
        return
      }

      uni.chooseImage({
        count: this.maxPhotos - this.photoList.length,
        sizeType: ['compressed'],
        sourceType: ['camera'],
        success: async (res) => {
          const tempFiles = res.tempFilePaths || res.tempFiles

          for (const filePath of tempFiles) {
            if (this.photoList.length >= this.maxPhotos) break
            uni.showLoading({ title: '添加水印...' })

            try {
              const watermarkedUrl = await this.addWatermark(filePath)
              this.photoList.push({ url: watermarkedUrl, watermarked: true })
            } catch (e) {
              // 水印失败时使用原图
              this.photoList.push({ url: filePath, watermarked: false })
            } finally {
              uni.hideLoading()
            }
          }
        },
        fail: () => {
          // 用户取消不提示
        }
      })
    },

    async addWatermark(filePath) {
      return new Promise((resolve, reject) => {
        const now = new Date()
        const dateStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:${String(now.getSeconds()).padStart(2, '0')}`

        const ctx = uni.createCanvasContext('watermarkCanvas')
        if (!ctx) {
          reject(new Error('Canvas not available'))
          return
        }

        // 获取图片信息以设置 canvas 尺寸
        uni.getImageInfo({
          src: filePath,
          success: (imgRes) => {
            const W = imgRes.width
            const H = imgRes.height
            const canvasW = Math.min(W, 1080)
            const canvasH = Math.round((canvasW / W) * H)

            // 设置水印内容
            const fontSize = Math.round(canvasW / 25)
            const lineHeight = fontSize * 1.6
            const watermarkText = [
              dateStr,
              this.gpsLocation.lat ? `坐标: ${this.gpsLocation.lat.toFixed(6)},${this.gpsLocation.lng.toFixed(6)}` : '',
              this.pointInfo.pointName ? `巡检点: ${this.pointInfo.pointName}` : ''
            ].filter(Boolean)

            // 使用 canvas 绘制水印
            const query = uni.createSelectorQuery().in(this)
            query.select('#watermarkCanvas')
              .fields({ node: true, size: true })
              .exec((res) => {
                if (!res || !res[0] || !res[0].node) {
                  // 降级: 返回原图
                  resolve(filePath)
                  return
                }

                const canvas = res[0].node
                canvas.width = canvasW
                canvas.height = canvasH

                const dCtx = canvas.getContext('2d')
                dCtx.drawImage(filePath, 0, 0, canvasW, canvasH)

                // 半透明黑色背景
                dCtx.globalAlpha = 0.5
                dCtx.fillStyle = '#000000'
                const boxH = lineHeight * watermarkText.length + 20
                dCtx.fillRect(0, canvasH - boxH, canvasW, boxH)

                // 白色文字
                dCtx.globalAlpha = 0.9
                dCtx.fillStyle = '#ffffff'
                dCtx.font = `${fontSize}px sans-serif`
                dCtx.textAlign = 'left'
                watermarkText.forEach((line, i) => {
                  dCtx.fillText(line, 16, canvasH - boxH + fontSize + i * lineHeight + 10)
                })

                // 导出
                uni.canvasToTempFilePath({
                  canvas,
                  success: (r) => resolve(r.tempFilePath),
                  fail: () => resolve(filePath)
                })
              })
          },
          fail: () => reject(new Error('getImageInfo failed'))
        })
      })
    },

    previewPhoto(url) {
      uni.previewImage({ urls: [url] })
    },

    removePhoto(index) {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这张照片吗？',
        success: (res) => {
          if (res.confirm) {
            this.photoList.splice(index, 1)
          }
        }
      })
    },

    // ========== 提交 ==========

    async handleSubmit() {
      if (!this.canSubmit) {
        if (!this.verified) {
          uni.showToast({ title: '请先完成位置验证', icon: 'none' })
        } else {
          uni.showToast({ title: '请完善表单信息', icon: 'none' })
        }
        return
      }

      uni.showModal({
        title: '确认提交',
        content: '提交后将无法修改，是否确认提交？',
        success: async (res) => {
          if (!res.confirm) return
          await this.doSubmit()
        }
      })
    },

    async doSubmit() {
      this.submitting = true

      try {
        // 1. 先上传照片获取 URL 列表
        uni.showLoading({ title: '上传照片...' })
        const photoUrls = []
        for (const photo of this.photoList) {
          try {
            const uploadRes = await uploadFile(photo.url)
            const url = uploadRes.url || uploadRes.data?.url || uploadRes.filePath || photo.url
            photoUrls.push(url)
          } catch (e) {
            // 照片上传失败不影响提交，使用原路径
            photoUrls.push(photo.url)
          }
        }
        uni.hideLoading()

        // 2. 提交巡检结果
        uni.showLoading({ title: '提交中...' })
        const submitData = {
          taskId: this.taskId,
          pointId: this.pointId,
          engineerId: uni.getStorageSync('userInfo')?.userId,
          formData: this.formData,
          photoUrls,
          remark: this.remark,
          location: this.gpsLocation.lat
            ? { lat: this.gpsLocation.lat, lng: this.gpsLocation.lng }
            : null,
          verifiedAt: new Date().toISOString()
        }

        await submitInspectionResult(submitData)
        uni.hideLoading()

        uni.showToast({ title: '提交成功', icon: 'success' })

        // 返回上一页
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: e.msg || '提交失败，请重试', icon: 'none' })
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.execute-page {
  height: 100vh;
  background: #f5f7fa;
  padding-bottom: 40rpx;
}

/* 隐藏 canvas（水印用） */
:global(.watermark-canvas) {
  position: fixed;
  left: -9999px;
  top: -9999px;
}

/* 加载骨架屏 */
.loading-wrap {
  padding: 24rpx;
}

.skeleton-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
  margin-bottom: 20rpx;
}

.skeleton-line {
  height: 28rpx;
  background: linear-gradient(90deg, #f0f2f5 25%, #e6e8eb 50%, #f0f2f5 75%);
  background-size: 200% 100%;
  border-radius: 8rpx;
  margin-bottom: 20rpx;
  animation: shimmer 1.5s infinite;
}

.skeleton-line.long { width: 70%; }
.skeleton-line.medium { width: 50%; }
.skeleton-line.short { width: 35%; }

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* 卡片区块 */
.section-card {
  background: #fff;
  margin: 24rpx;
  border-radius: 16rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-icon {
  font-size: 32rpx;
  margin-right: 10rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #303133;
  flex: 1;
}

/* 验证状态 */
.verify-status {
  font-size: 22rpx;
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
}

.verify-status.verified {
  background: #f0f9eb;
  color: #67c23a;
}

.verify-status.unverified {
  background: #fef0e6;
  color: #e6a23c;
}

/* GPS 位置信息 */
.location-info {
  background: #f5f7fa;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
}

.location-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.location-row:last-child {
  margin-bottom: 0;
}

.loc-label {
  font-size: 26rpx;
  color: #909399;
}

.loc-value {
  font-size: 26rpx;
  color: #303133;
  font-family: monospace;
}

.distance-value.near {
  color: #67c23a;
  font-weight: 600;
}

.distance-value.far {
  color: #f56c6c;
}

/* 验证按钮 */
.verify-actions {
  display: flex;
  gap: 20rpx;
}

.action-btn {
  flex: 1;
  height: 80rpx;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  font-weight: 500;
  transition: opacity 0.2s;
}

.action-btn:active {
  opacity: 0.8;
}

.action-btn.primary {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  color: #fff;
}

.action-btn.secondary {
  background: #f5f7fa;
  color: #2563eb;
  border: 2rpx solid #2563eb;
}

/* 扫码结果 */
.scan-result {
  margin-top: 16rpx;
  padding: 12rpx 20rpx;
  border-radius: 8rpx;
  text-align: center;
}

.scan-msg.ok {
  color: #67c23a;
  background: #f0f9eb;
}

.scan-msg.fail {
  color: #f56c6c;
  background: #fef0f0;
}

/* 巡检点信息 */
.point-info-card {
  margin-top: 0;
}

.point-info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.point-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #303133;
}

.point-code {
  font-size: 24rpx;
  color: #909399;
}

.point-info-desc {
  font-size: 26rpx;
  color: #606266;
  line-height: 1.6;
}

/* 表单区 */
.form-area {
  /* 表单样式 */
}

.form-field {
  margin-bottom: 28rpx;
}

.form-field:last-child {
  margin-bottom: 0;
}

.field-label {
  font-size: 28rpx;
  color: #303133;
  font-weight: 500;
  margin-bottom: 12rpx;
}

.required-star {
  color: #f56c6c;
  margin-left: 4rpx;
}

.field-input {
  height: 80rpx;
  background: #f5f7fa;
  border-radius: 12rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  color: #303133;
  border: 2rpx solid transparent;
  transition: border-color 0.2s;
}

.field-input:focus {
  border-color: #2563eb;
}

.field-textarea {
  width: 100%;
  min-height: 160rpx;
  background: #f5f7fa;
  border-radius: 12rpx;
  padding: 20rpx;
  font-size: 28rpx;
  color: #303133;
  box-sizing: border-box;
  border: 2rpx solid transparent;
  transition: border-color 0.2s;
}

.field-textarea:focus {
  border-color: #2563eb;
}

/* 单选/多选选项 */
.field-select {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.select-option {
  padding: 12rpx 24rpx;
  background: #f5f7fa;
  border-radius: 8rpx;
  font-size: 26rpx;
  color: #606266;
  border: 2rpx solid #ebeef5;
  transition: all 0.2s;
}

.select-option.multi {
  border-radius: 40rpx;
}

.select-option.active {
  background: #2563eb;
  color: #fff;
  border-color: #2563eb;
}

.field-error {
  font-size: 22rpx;
  color: #f56c6c;
  margin-top: 8rpx;
}

/* 异常标记 */
.fault-label {
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.fault-icon {
  font-size: 28rpx;
}

.fault-toggle {
  display: flex;
  gap: 20rpx;
}

.fault-btn {
  flex: 1;
  height: 80rpx;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  background: #f5f7fa;
  color: #606266;
  border: 2rpx solid #ebeef5;
}

.fault-btn.active:first-child {
  background: #fef0f0;
  color: #f56c6c;
  border-color: #f56c6c;
}

.fault-btn.active:last-child {
  background: #f0f9eb;
  color: #67c23a;
  border-color: #67c23a;
}

.form-empty {
  text-align: center;
  padding: 40rpx;
  font-size: 26rpx;
  color: #909399;
}

/* 照片区 */
.photo-count {
  font-size: 24rpx;
  color: #909399;
}

.photo-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.photo-item {
  width: calc((100% - 32rpx) / 3);
  aspect-ratio: 1;
  border-radius: 12rpx;
  overflow: hidden;
  position: relative;
}

.photo-img {
  width: 100%;
  height: 100%;
}

.photo-del {
  position: absolute;
  top: 4rpx;
  right: 4rpx;
  width: 40rpx;
  height: 40rpx;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  line-height: 1;
}

.photo-watermark-tag {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  font-size: 18rpx;
  text-align: center;
  padding: 4rpx;
}

.photo-add {
  width: calc((100% - 32rpx) / 3);
  aspect-ratio: 1;
  border-radius: 12rpx;
  border: 2rpx dashed #c0c4cc;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #fafafa;
}

.add-icon {
  font-size: 56rpx;
  color: #c0c4cc;
  line-height: 1;
}

.add-text {
  font-size: 22rpx;
  color: #909399;
  margin-top: 8rpx;
}

.photo-tip {
  margin-top: 16rpx;
  font-size: 24rpx;
  color: #909399;
  text-align: center;
}

/* 备注 */
.remark-textarea {
  width: 100%;
  min-height: 160rpx;
  background: #f5f7fa;
  border-radius: 12rpx;
  padding: 20rpx;
  font-size: 28rpx;
  color: #303133;
  box-sizing: border-box;
}

.remark-counter {
  text-align: right;
  font-size: 22rpx;
  color: #c0c4cc;
  margin-top: 8rpx;
}

/* 提交按钮 */
.submit-section {
  padding: 0 24rpx 40rpx;
}

.submit-btn {
  height: 96rpx;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  font-weight: 600;
  color: #fff;
  transition: opacity 0.2s;
}

.submit-btn:active {
  opacity: 0.85;
}

.submit-btn.disabled {
  background: #c0c4cc;
  opacity: 0.7;
}
</style>
