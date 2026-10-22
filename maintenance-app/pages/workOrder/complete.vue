<template>
  <view class="complete-page">
    <!-- 维修结果 -->
    <view class="section-card">
      <view class="section-title">维修结果描述</view>
      <textarea
        v-model="form.repairResult"
        class="textarea"
        placeholder="请详细描述维修过程和结果..."
        :adjust-position="true"
        :cursor-spacing="20"
        maxlength="500"
      ></textarea>
      <text class="char-count">{{ form.repairResult.length }}/500</text>
    </view>

    <!-- 更换配件 -->
    <view class="section-card">
      <view class="section-header">
        <view class="section-title">更换配件</view>
        <view class="add-btn" @click="openAddPartModal">
          <text>+ 添加配件</text>
        </view>
      </view>

      <view class="part-list" v-if="form.parts.length > 0">
        <view
          v-for="(part, index) in form.parts"
          :key="index"
          class="part-item"
        >
          <view class="part-info">
            <text class="part-name">{{ part.nameLabel || part.name }}</text>
            <text class="part-spec">规格: {{ part.spec || '-' }}</text>
            <text class="part-qty">x{{ part.quantity }}</text>
          </view>
          <view class="part-delete" @click="removePart(index)">
            <text>删除</text>
          </view>
        </view>
      </view>

      <view class="empty-parts" v-else>
        <text>暂无更换配件</text>
      </view>
    </view>

    <!-- 完工照片 -->
    <view class="section-card">
      <view class="section-title">完工照片</view>
      <view class="photo-grid">
        <view
          v-for="(photo, index) in form.photos"
          :key="index"
          class="photo-item"
        >
          <image :src="photo" mode="aspectFill" class="photo-img" lazy-load></image>
          <view class="photo-delete" @click="removePhoto(index)">
            <text>x</text>
          </view>
        </view>
        <view class="photo-add" @click="choosePhoto" v-if="form.photos.length < 9">
          <text class="add-icon">+</text>
          <text class="add-text">添加照片</text>
        </view>
      </view>
      <text class="photo-tip">最多上传9张图片</text>
    </view>

    <!-- 甲方电子签名 -->
    <view class="section-card">
      <view class="section-title">甲方电子签名</view>
      <view class="signature-area" @click="openSignaturePad">
        <canvas
          v-if="showCanvas"
          canvas-id="signatureCanvas"
          id="signatureCanvas"
          class="signature-canvas"
        ></canvas>
        <view class="signature-placeholder" v-if="!form.signature">
          <text class="placeholder-icon">E</text>
          <text class="placeholder-text">点击此处进行签名</text>
        </view>
        <image
          v-else
          :src="form.signature"
          mode="aspectFill"
          class="signature-preview"
         lazy-load></image>
      </view>
      <view class="signature-actions">
        <view class="clear-btn" @click="clearSignature" v-if="form.signature">
          <text>清除签名</text>
        </view>
        <view class="sign-btn" @click="openSignaturePad">
          <text>{{ form.signature ? '重新签名' : '去签名' }}</text>
        </view>
      </view>
    </view>

    <!-- 提交按钮 -->
    <view class="submit-bar">
      <button
        class="submit-btn"
        :disabled="submitting"
        @click="handleSubmit"
      >
        {{ submitting ? '提交中...' : '确认完工' }}
      </button>
    </view>

    <!-- 添加配件弹窗 -->
    <view class="modal-mask" v-if="showAddPartModal" @click="showAddPartModal = false">
      <view class="modal-content" @click.stop>
        <view class="modal-title">添加配件</view>
        <view class="modal-form">
          <view class="form-item">
            <text class="form-label">配件名称</text>
            <picker
              mode="selector"
              :range="partOptions"
              range-key="partName"
              :value="partPickerIndex"
              @change="onPartChange"
            >
              <view class="form-input form-picker">
                <text :class="partForm.name ? 'picker-value' : 'picker-placeholder'">
                  {{ partForm.nameLabel || '请选择配件' }}
                </text>
                <text class="picker-arrow">▼</text>
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">规格型号</text>
            <input
              v-model="partForm.spec"
              class="form-input"
              placeholder="请输入规格型号（选填）"
              :adjust-position="true"
              :cursor-spacing="20"
            />
          </view>
          <view class="form-item">
            <text class="form-label">数量</text>
            <input
              v-model="partForm.quantity"
              class="form-input"
              type="number"
              placeholder="请输入数量"
              :adjust-position="true"
              :cursor-spacing="20"
              min="1"
            />
          </view>
        </view>
        <view class="modal-actions">
          <button class="btn btn-default" @click="showAddPartModal = false">取消</button>
          <button class="btn btn-primary" @click="confirmAddPart">确定</button>
        </view>
      </view>
    </view>

    <!-- 签名画板 -->
    <view class="modal-mask signature-modal" v-if="showSignaturePad" @click="closeSignaturePad">
      <view class="signature-pad-wrap" @click.stop>
        <view class="pad-header">
          <text class="pad-title">请签名</text>
          <view class="pad-actions">
            <text class="pad-clear" @click="clearPadCanvas">清除</text>
            <text class="pad-confirm" @click="confirmSignature">确认</text>
          </view>
        </view>
        <canvas
          canvas-id="signPadCanvas"
          id="signPadCanvas"
          class="pad-canvas"
          disable-scroll="true"
          @touchstart="onTouchStart"
          @touchmove="onTouchMove"
          @touchend="onTouchEnd"
        ></canvas>
      </view>
    </view>
  </view>
</template>

<script>
import { uploadFile, completeWorkOrder, getPartList } from '@/api/request.js'

export default {
  data() {
    return {
      workOrderId: '',
      submitting: false,
      showAddPartModal: false,
      showSignaturePad: false,
      showCanvas: false,
      ctx: null,
      points: [],
      isDrawing: false,

      partForm: {
        name: '',
        nameLabel: '',
        spec: '',
        quantity: 1
      },

      partOptions: [],
      partPickerIndex: 0,

      form: {
        repairResult: '',
        parts: [],
        photos: [],
        signature: ''
      }
    }
  },
  onLoad(options) {
    if (options.id) {
      this.workOrderId = options.id
    }
  },
  methods: {
    // ========== 配件管理 ==========
    async openAddPartModal() {
      await this.loadPartOptions()
      this.partForm = { name: '', nameLabel: '', spec: '', quantity: 1 }
      this.partPickerIndex = 0
      this.showAddPartModal = true
    },

    async loadPartOptions() {
      try {
        const res = await getPartList()
        const list = res.data?.rows || res.rows || []
        this.partOptions = list.filter(item => item.status === '0')
      } catch (e) {
        this.partOptions = []
      }
    },

    onPartChange(e) {
      const index = e.detail.value
      this.partPickerIndex = index
      const selected = this.partOptions[index]
      if (selected) {
        this.partForm.name = String(selected.partId)
        this.partForm.nameLabel = selected.partName
      }
    },

    confirmAddPart() {
      if (!this.partForm.name) {
        uni.showToast({ title: '请选择配件', icon: 'none' })
        return
      }
      const qty = parseInt(this.partForm.quantity) || 1
      this.form.parts.push({
        name: this.partForm.name,
        nameLabel: this.partForm.nameLabel,
        spec: this.partForm.spec.trim(),
        quantity: qty
      })
      this.partForm = { name: '', nameLabel: '', spec: '', quantity: 1 }
      this.partPickerIndex = 0
      this.showAddPartModal = false
    },
    removePart(index) {
      this.form.parts.splice(index, 1)
    },

    // ========== 照片上传 ==========
    choosePhoto() {
      const remain = 9 - this.form.photos.length
      if (remain <= 0) return
      uni.chooseImage({
        count: remain,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: async (res) => {
          uni.showLoading({ title: '上传中...' })
          try {
            const uploadPromises = res.tempFilePaths.map(path => uploadFile(path))
            const results = await Promise.all(uploadPromises)
            const urls = results.map(r => r.data?.url || r.filePath || r.url)
            this.form.photos = [...this.form.photos, ...urls]
          } catch (e) {
            uni.showToast({ title: '图片上传失败', icon: 'none' })
          } finally {
            uni.hideLoading()
          }
        }
      })
    },
    removePhoto(index) {
      this.form.photos.splice(index, 1)
    },

    // ========== 签名画板 ==========
    openSignaturePad() {
      this.showSignaturePad = true
      this.showCanvas = true
      this.$nextTick(() => {
        this.initCanvas()
      })
    },
    closeSignaturePad() {
      this.showSignaturePad = false
    },
    initCanvas() {
      const query = uni.createSelectorQuery().in(this)
      query.select('#signPadCanvas').boundingClientRect(rect => {
        if (!rect) return
        const systemInfo = uni.getSystemInfoSync()
        const dpr = systemInfo.pixelRatio || 2
        const canvasWidth = rect.width
        const canvasHeight = rect.height

        this.padCanvasInfo = { width: canvasWidth, height: canvasHeight, dpr }

        const cvs = uni.createCanvasContext('signPadCanvas', this)
        cvs.setLineWidth(3 * dpr)
        cvs.setStrokeStyle('#303133')
        cvs.setLineCap('round')
        cvs.setLineJoin('round')
        this.padCtx = cvs
      }).exec()
    },
    getTouchPos(e) {
      const touch = e.touches[0]
      const rect = this.padCanvasInfo
      return {
        x: touch.clientX * rect.dpr,
        y: touch.clientY * rect.dpr
      }
    },
    onTouchStart(e) {
      const pos = this.getTouchPos(e)
      this.isDrawing = true
      this.points = [pos]
      this.padCtx.moveTo(pos.x, pos.y)
    },
    onTouchMove(e) {
      if (!this.isDrawing) return
      const pos = this.getTouchPos(e)
      this.points.push(pos)
      this.padCtx.lineTo(pos.x, pos.y)
      this.padCtx.stroke()
      this.padCtx.draw()
    },
    onTouchEnd() {
      this.isDrawing = false
      this.points = []
    },
    clearPadCanvas() {
      if (!this.padCtx || !this.padCanvasInfo) return
      const { width, height, dpr } = this.padCanvasInfo
      this.padCtx.clearRect(0, 0, width * dpr, height * dpr)
      this.padCtx.draw()
    },
    confirmSignature() {
      if (!this.padCtx) {
        this.showSignaturePad = false
        return
      }
      uni.canvasToTempFilePath({
        canvasId: 'signPadCanvas',
        success: (res) => {
          uni.showLoading({ title: '上传签名...' })
          uploadFile(res.tempFilePath)
            .then(uploadRes => {
              this.form.signature = uploadRes.data?.url || uploadRes.filePath || res.tempFilePath
              this.showSignaturePad = false
            })
            .catch(() => {
              uni.showToast({ title: '签名上传失败', icon: 'none' })
            })
            .finally(() => uni.hideLoading())
        },
        fail: () => {
          uni.showToast({ title: '签名保存失败', icon: 'none' })
        }
      }, this)
    },

    clearSignature() {
      this.form.signature = ''
    },

    // ========== 提交完工 ==========
    async handleSubmit() {
      if (!this.form.repairResult.trim()) {
        uni.showToast({ title: '请填写维修结果描述', icon: 'none' })
        return
      }
      if (this.form.photos.length === 0) {
        uni.showToast({ title: '请上传至少一张完工照片', icon: 'none' })
        return
      }
      if (!this.form.signature) {
        uni.showToast({ title: '请完成甲方电子签名', icon: 'none' })
        return
      }

      this.submitting = true
      try {
        await completeWorkOrder(this.workOrderId, {
          repairResult: this.form.repairResult.trim(),
          parts: this.form.parts.length > 0 ? JSON.stringify(this.form.parts) : null,
          photos: this.form.photos.join(','),
          signature: this.form.signature
        })
        uni.showToast({ title: '完工确认成功', icon: 'success' })
        // 跳回工单列表，不允许返回本页面
        setTimeout(() => {
          uni.redirectTo({ url: '/pages/workOrder/list' })
        }, 1500)
      } catch (e) {
        uni.showToast({ title: e.msg || '提交失败', icon: 'none', duration: 3000 })
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.complete-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24rpx;
  padding-bottom: 160rpx;
}

.section-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #303133;
  margin-bottom: 16rpx;
}

.section-header .section-title { margin-bottom: 0; }

/* 维修结果 */
.textarea {
  width: 100%;
  height: 200rpx;
  background: #f5f7fa;
  border-radius: 12rpx;
  padding: 20rpx;
  font-size: 28rpx;
  color: #303133;
  box-sizing: border-box;
  border: none;
  resize: none;
}

.char-count {
  display: block;
  text-align: right;
  font-size: 22rpx;
  color: #c0c4cc;
  margin-top: 8rpx;
}

/* 添加配件按钮 */
.add-btn {
  font-size: 26rpx;
  color: #2563eb;
}

/* 配件列表 */
.part-list { display: flex; flex-direction: column; }
.part-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #ebeef5;
}
.part-item:last-child { border-bottom: none; }
.part-info { display: flex; flex-direction: column; flex: 1; }
.part-name { font-size: 28rpx; color: #303133; font-weight: bold; }
.part-spec { font-size: 24rpx; color: #909399; margin-top: 6rpx; }
.part-qty { font-size: 24rpx; color: #2563eb; margin-top: 6rpx; }
.part-delete { font-size: 24rpx; color: #f56c6c; padding: 8rpx 0 8rpx 24rpx; }
.empty-parts { text-align: center; padding: 40rpx; color: #c0c4cc; font-size: 26rpx; }

/* 照片 */
.photo-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}
.photo-item {
  width: 200rpx;
  height: 200rpx;
  border-radius: 12rpx;
  overflow: hidden;
  position: relative;
}
.photo-img { width: 100%; height: 100%; }
.photo-delete {
  position: absolute;
  top: 0; right: 0;
  width: 44rpx;
  height: 44rpx;
  background: rgba(0,0,0,0.5);
  color: #fff;
  font-size: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom-left-radius: 12rpx;
}
.photo-add {
  width: 200rpx;
  height: 200rpx;
  background: #f5f7fa;
  border-radius: 12rpx;
  border: 2rpx dashed #dcdfe6;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.add-icon { font-size: 60rpx; color: #c0c4cc; }
.add-text { font-size: 24rpx; color: #909399; margin-top: 8rpx; }
.photo-tip { font-size: 22rpx; color: #c0c4cc; margin-top: 12rpx; }

/* 签名 */
.signature-area {
  height: 300rpx;
  background: #fafafa;
  border: 2rpx dashed #dcdfe6;
  border-radius: 12rpx;
  position: relative;
  overflow: hidden;
}
.signature-placeholder {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.placeholder-icon { font-size: 60rpx; color: #c0c4cc; }
.placeholder-text { font-size: 28rpx; color: #909399; margin-top: 16rpx; }
.signature-canvas, .signature-preview {
  width: 100%;
  height: 100%;
}
.signature-preview { position: absolute; top: 0; left: 0; }
.signature-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16rpx;
  margin-top: 16rpx;
}
.clear-btn, .sign-btn {
  font-size: 26rpx;
  padding: 10rpx 24rpx;
  border-radius: 8rpx;
}
.clear-btn { color: #f56c6c; }
.sign-btn { color: #2563eb; }

/* 提交按钮 */
.submit-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  background: #fff;
  box-shadow: 0 -2rpx 12rpx rgba(0,0,0,0.05);
}
.submit-btn {
  width: 100%;
  height: 88rpx;
  background: #2563eb;
  color: #fff;
  border-radius: 44rpx;
  font-size: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}
.submit-btn[disabled] { background: #c0c4cc; }

/* 通用弹窗 */
.modal-mask {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}
.modal-content {
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
  width: 600rpx;
  max-width: 90vw;
}
.modal-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #303133;
  margin-bottom: 32rpx;
}
.modal-form { display: flex; flex-direction: column; gap: 24rpx; }
.form-item { display: flex; flex-direction: column; }
.form-label { font-size: 26rpx; color: #606266; margin-bottom: 12rpx; }
.form-input {
  height: 80rpx;
  background: #f5f7fa;
  border-radius: 12rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  color: #303133;
  border: none;
}
.form-picker {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.picker-value { color: #303133; }
.picker-placeholder { color: #c0c4cc; }
.picker-arrow { font-size: 20rpx; color: #c0c4cc; }
.modal-actions {
  display: flex;
  gap: 24rpx;
  margin-top: 32rpx;
}
.btn {
  flex: 1;
  height: 80rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}
.btn-primary { background: #2563eb; color: #fff; }
.btn-default { background: #f5f7fa; color: #606266; }

/* 签名画板 */
.signature-modal { align-items: flex-end; justify-content: flex-end; }
.signature-pad-wrap {
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  width: 100%;
  height: 60vh;
  display: flex;
  flex-direction: column;
}
.pad-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #ebeef5;
}
.pad-title { font-size: 32rpx; font-weight: bold; color: #303133; }
.pad-actions { display: flex; gap: 32rpx; }
.pad-clear { font-size: 28rpx; color: #909399; }
.pad-confirm { font-size: 28rpx; color: #2563eb; font-weight: bold; }
.pad-canvas {
  flex: 1;
  width: 100%;
  background: #fff;
}
</style>
