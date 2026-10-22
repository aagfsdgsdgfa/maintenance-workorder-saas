<template>
  <view class="signature-canvas-wrapper">
    <view class="canvas-label" v-if="label">{{ label }}</view>
    <view class="canvas-container" :class="{ readonly: readonly }">
      <canvas
        canvas-id="signatureCanvas"
        class="signature-canvas"
        :style="{ width: canvasWidth, height: canvasHeight }"
        @touchstart="onTouchStart"
        @touchmove="onTouchMove"
        @touchend="onTouchEnd"
        @mousedown="onMouseDown"
        @mousemove="onMouseMove"
        @mouseup="onMouseUp"
        @mouseleave="onMouseUp"
      ></canvas>
      <view class="canvas-placeholder" v-if="!hasDrawn && showPlaceholder" @click="focusCanvas">
        <text class="placeholder-icon">✍️</text>
        <text class="placeholder-text">{{ placeholderText }}</text>
      </view>
    </view>
    <view class="canvas-actions" v-if="!readonly">
      <button class="clear-btn" size="mini" @click="handleClear">清除</button>
      <button class="confirm-btn" size="mini" @click="handleConfirm" v-if="showConfirm">确认</button>
    </view>
  </view>
</template>

<script>
/**
 * signature-canvas.vue - 手写签名组件
 * 功能：canvas 手绘签名
 * 笔画样式：黑色、圆头、线宽3
 * methods: clear() 清空画布, getImage() 返回 base64 图片
 * 暴露 canvas context 给父组件
 */
export default {
  name: 'signature-canvas',
  props: {
    // 画布宽度
    width: {
      type: String,
      default: '100%'
    },
    // 画布高度
    height: {
      type: String,
      default: '300rpx'
    },
    // 标签文字
    label: {
      type: String,
      default: ''
    },
    // 提示文字
    placeholderText: {
      type: String,
      default: '点击此处签名'
    },
    // 是否只读模式
    readonly: {
      type: Boolean,
      default: false
    },
    // 是否显示确认按钮
    showConfirm: {
      type: Boolean,
      default: false
    },
    // 是否显示占位提示
    showPlaceholder: {
      type: Boolean,
      default: true
    },
    // 线条颜色
    strokeColor: {
      type: String,
      default: '#000000'
    },
    // 线条宽度
    lineWidth: {
      type: Number,
      default: 3
    }
  },
  data() {
    return {
      canvasWidth: '100%',
      canvasHeight: '300rpx',
      ctx: null,
      canvasContext: null,
      hasDrawn: false,
      isDrawing: false,
      lastPoint: null,
      _canvasReady: false
    }
  },
  mounted() {
    this.initCanvas()
  },
  onReady() {
    this.initCanvas()
  },
  methods: {
    initCanvas() {
      if (this._canvasReady) return
      // 延迟确保 canvas 已挂载
      setTimeout(() => {
        this.canvasWidth = this.width
        this.canvasHeight = this.height

        this.$nextTick(() => {
          this.ctx = uni.createCanvasContext('signatureCanvas', this)
          if (this.ctx) {
            this.canvasContext = this.ctx
            // 设置绘制样式
            this.ctx.lineCap = 'round'
            this.ctx.lineJoin = 'round'
            this.ctx.lineWidth = this.lineWidth
            this.ctx.strokeStyle = this.strokeColor
            this._canvasReady = true
          }
        })
      }, 50)
    },

    // 获取触摸/鼠标位置
    getPoint(e) {
      if (e.touches && e.touches.length > 0) {
        return {
          x: e.touches[0].x,
          y: e.touches[0].y
        }
      }
      return {
        x: e.detail ? e.detail.x : e.offsetX,
        y: e.detail ? e.detail.y : e.offsetY
      }
    },

    onTouchStart(e) {
      if (this.readonly) return
      e.preventDefault()
      this.startDraw(e)
    },

    onTouchMove(e) {
      if (this.readonly) return
      e.preventDefault()
      this.continueDraw(e)
    },

    onTouchEnd(e) {
      if (this.readonly) return
      this.endDraw()
    },

    onMouseDown(e) {
      if (this.readonly) return
      this.startDraw(e)
    },

    onMouseMove(e) {
      if (this.readonly) return
      this.continueDraw(e)
    },

    onMouseUp() {
      if (this.readonly) return
      this.endDraw()
    },

    startDraw(e) {
      if (!this.ctx) return
      this.isDrawing = true
      this.hasDrawn = true
      const point = this.getPoint(e)
      this.lastPoint = point
      this.ctx.moveTo(point.x, point.y)
    },

    continueDraw(e) {
      if (!this.isDrawing || !this.ctx) return
      const point = this.getPoint(e)
      this.ctx.lineTo(point.x, point.y)
      this.ctx.stroke()
      this.ctx.draw(true)
      this.lastPoint = point
    },

    endDraw() {
      if (!this.isDrawing) return
      this.isDrawing = false
      this.lastPoint = null
      // 通知父组件有绘制内容
      this.$emit('draw-end')
    },

    focusCanvas() {
      // 触发 canvas 点击，隐式开始绘制
    },

    /**
     * 清空画布
     */
    clear() {
      if (!this.ctx) return
      this.ctx.clearRect(0, 0, 1000, 1000)
      this.ctx.draw()
      this.hasDrawn = false
      this.isDrawing = false
      this.lastPoint = null
      this.$emit('clear')
    },

    /**
     * 返回 base64 图片
     * @param {string} fileType - 图片格式，默认 png
     * @param {number} quality - 图片质量 0-1
     * @returns {Promise<string>}
     */
    getImage(fileType = 'png', quality = 1) {
      return new Promise((resolve, reject) => {
        if (!this.ctx) {
          reject(new Error('Canvas not initialized'))
          return
        }
        uni.canvasToTempFilePath({
          canvasId: 'signatureCanvas',
          fileType: fileType,
          quality: quality,
          success: (res) => {
            // 如果需要 base64，手动转换
            if (fileType === 'base64') {
              uni.getFileSystemManager().readFile({
                filePath: res.tempFilePath,
                encoding: 'base64',
                success: (readRes) => {
                  resolve(`data:image/png;base64,${readRes.data}`)
                },
                fail: (err) => {
                  reject(err)
                }
              })
            } else {
              resolve(res.tempFilePath)
            }
          },
          fail: (err) => {
            reject(err)
          }
        }, this)
      })
    },

    /**
     * 返回 base64 data URL
     */
    getImageDataURL() {
      return this.getImage('png', 1)
    },

    /**
     * 获取 canvas context，暴露给父组件
     */
    getContext() {
      return this.ctx
    }
  }
}
</script>

<style scoped>
.signature-canvas-wrapper {
  display: flex;
  flex-direction: column;
}

.canvas-label {
  font-size: 26rpx;
  color: #909399;
  margin-bottom: 12rpx;
}

.canvas-container {
  position: relative;
  background: #ffffff;
  border: 2rpx solid #dcdfe6;
  border-radius: 12rpx;
  overflow: hidden;
  min-height: 300rpx;
}

.canvas-container.readonly {
  pointer-events: none;
}

.signature-canvas {
  display: block;
  width: 100%;
  height: 300rpx;
  touch-action: none;
}

.canvas-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}

.placeholder-icon {
  font-size: 64rpx;
  margin-bottom: 12rpx;
  opacity: 0.4;
}

.placeholder-text {
  font-size: 26rpx;
  color: #c0c4cc;
}

.canvas-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16rpx;
  margin-top: 16rpx;
}

.clear-btn {
  background: #fdf6ec;
  color: #e6a23c;
  font-size: 24rpx;
  border: none;
}

.confirm-btn {
  background: #667eea;
  color: #fff;
  font-size: 24rpx;
  border: none;
}
</style>
