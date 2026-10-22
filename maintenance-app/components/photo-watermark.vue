<template>
  <view class="photo-watermark-wrapper">
    <!-- 图片预览 -->
    <view class="photo-preview" @click="handlePreview">
      <image
        v-if="processedSrc || src"
        :src="processedSrc || src"
        class="preview-img"
        mode="aspectFill"
      ></image>
      <view class="photo-placeholder" v-else @click="chooseImage">
        <text class="placeholder-icon">📷</text>
        <text class="placeholder-text">{{ placeholderText }}</text>
      </view>

      <!-- 水印处理中的遮罩 -->
      <view class="processing-mask" v-if="processing">
        <view class="processing-inner">
          <text class="loading-icon">⏳</text>
          <text class="loading-text">添加水印中...</text>
        </view>
      </view>
    </view>

    <!-- 水印信息 -->
    <view class="watermark-info" v-if="watermarkText || showTime">
      <text class="info-item" v-if="showTime">
        <text class="info-icon">🕐</text>
        <text class="info-text">{{ formatTime }}</text>
      </text>
      <text class="info-item" v-if="watermarkText">
        <text class="info-icon">📍</text>
        <text class="info-text">{{ watermarkText }}</text>
      </text>
    </view>

    <!-- 重新选择/重新处理按钮 -->
    <view class="action-row" v-if="src && showActions">
      <button class="action-btn" size="mini" @click="chooseImage">重新选择</button>
      <button class="action-btn action-btn-primary" size="mini" @click="processImage" v-if="showReprocess">
        重新添加水印
      </button>
    </view>

    <!-- 隐藏的 canvas -->
    <view style="position:fixed;left:-9999px;top:-9999px;">
      <canvas
        canvas-id="watermarkCanvas"
        :style="{ width: canvasWidth + 'px', height: canvasHeight + 'px' }"
      ></canvas>
    </view>
  </view>
</template>

<script>
/**
 * photo-watermark.vue - 照片水印组件
 * 功能：给图片添加水印
 * props: src (图片路径), watermarkText (水印文字)
 * 水印内容：时间 + 文字
 * 水印位置：右下角，半透明背景
 * methods: processImage() 返回处理后的图片路径或 base64
 */
export default {
  name: 'photo-watermark',
  props: {
    // 图片路径
    src: {
      type: String,
      default: ''
    },
    // 水印文字（地址等）
    watermarkText: {
      type: String,
      default: ''
    },
    // 是否显示时间水印
    showTime: {
      type: Boolean,
      default: true
    },
    // 是否显示操作按钮
    showActions: {
      type: Boolean,
      default: true
    },
    // 是否显示重新处理按钮
    showReprocess: {
      type: Boolean,
      default: true
    },
    // 提示文字
    placeholderText: {
      type: String,
      default: '点击选择图片'
    },
    // 水印字体大小倍数（相对于图片宽度）
    fontSizeRatio: {
      type: Number,
      default: 28
    },
    // 水印透明度
    opacity: {
      type: Number,
      default: 0.7
    }
  },
  data() {
    return {
      processedSrc: '',
      processing: false,
      canvasWidth: 300,
      canvasHeight: 400,
      _currentSrc: ''
    }
  },
  computed: {
    formatTime() {
      const now = new Date()
      const pad = n => String(n).padStart(2, '0')
      return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`
    }
  },
  watch: {
    src: {
      immediate: true,
      handler(newVal) {
        if (newVal && newVal !== this._currentSrc) {
          this._currentSrc = newVal
          this.processImage()
        }
      }
    }
  },
  methods: {
    /**
     * 选择图片
     */
    chooseImage() {
      uni.chooseImage({
        count: 1,
        sourceType: ['album', 'camera'],
        success: (res) => {
          const path = res.tempFilePaths[0]
          this.$emit('update:src', path)
          this.processImage(path)
        }
      })
    },

    /**
     * 预览图片
     */
    handlePreview() {
      const previewSrc = this.processedSrc || this.src
      if (previewSrc) {
        uni.previewImage({
          urls: [previewSrc],
          current: previewSrc
        })
      }
    },

    /**
     * 处理图片，添加水印
     * @param {string} filePath - 可选，指定图片路径，默认使用 props.src
     * @returns {Promise<string>} 返回处理后的临时文件路径
     */
    processImage(filePath) {
      const path = filePath || this.src
      if (!path) return Promise.reject(new Error('No image source'))

      this.processing = true

      return new Promise((resolve, reject) => {
        uni.getImageInfo({
          src: path,
          success: (imgInfo) => {
            this.canvasWidth = imgInfo.width
            this.canvasHeight = imgInfo.height

            this.$nextTick(() => {
              const ctx = uni.createCanvasContext('watermarkCanvas', this)

              // 绘制原图
              ctx.drawImage(path, 0, 0, imgInfo.width, imgInfo.height)

              // 水印参数
              const padding = Math.max(20, Math.floor(imgInfo.width * 0.02))
              const fontSize = Math.max(20, Math.floor(imgInfo.width / this.fontSizeRatio))
              const lineHeight = fontSize * 1.4

              // 水印内容
              const timeStr = this.showTime ? this.formatTime : ''
              const addrLine1 = this.watermarkText ? this.watermarkText.substring(0, 20) : ''
              const addrLine2 = this.watermarkText && this.watermarkText.length > 20
                ? this.watermarkText.substring(20, 40)
                : ''

              const lines = [timeStr, addrLine1, addrLine2].filter(l => l)
              const bgHeight = lines.length * lineHeight + padding * 2
              const bgY = imgInfo.height - bgHeight

              // 半透明背景
              ctx.setFillStyle(`rgba(0, 0, 0, ${this.opacity})`)
              ctx.fillRect(0, bgY, imgInfo.width, bgHeight)

              // 绘制文字
              ctx.setFillStyle('#ffffff')
              ctx.font = `${fontSize}px sans-serif`

              lines.forEach((line, index) => {
                const y = bgY + padding + (index + 1) * lineHeight - fontSize * 0.3
                ctx.fillText(line, padding, y)
              })

              ctx.draw(false, () => {
                uni.canvasToTempFilePath({
                  canvasId: 'watermarkCanvas',
                  x: 0,
                  y: 0,
                  width: imgInfo.width,
                  height: imgInfo.height,
                  destWidth: imgInfo.width,
                  destHeight: imgInfo.height,
                  fileType: 'jpg',
                  quality: 0.92,
                  success: (r) => {
                    this.processedSrc = r.tempFilePath
                    this.processing = false
                    this.$emit('processed', r.tempFilePath)
                    resolve(r.tempFilePath)
                  },
                  fail: (err) => {
                    this.processing = false
                    this.processedSrc = path
                    reject(err)
                  }
                }, this)
              })
            })
          },
          fail: (err) => {
            this.processing = false
            reject(err)
          }
        })
      })
    },

    /**
     * 获取处理后的图片路径（需先调用 processImage）
     */
    getProcessedSrc() {
      return this.processedSrc
    },

    /**
     * 获取 base64 格式
     */
    getBase64() {
      return new Promise((resolve, reject) => {
        const path = this.processedSrc || this.src
        if (!path) {
          reject(new Error('No processed image'))
          return
        }
        const fs = uni.getFileSystemManager()
        fs.readFile({
          filePath: path,
          encoding: 'base64',
          success: (res) => {
            const ext = path.toLowerCase().includes('.png') ? 'png' : 'jpeg'
            resolve(`data:image/${ext};base64,${res.data}`)
          },
          fail: reject
        })
      })
    }
  }
}
</script>

<style scoped>
.photo-watermark-wrapper {
  display: flex;
  flex-direction: column;
}

.photo-preview {
  position: relative;
  border-radius: 12rpx;
  overflow: hidden;
  min-height: 400rpx;
  background: #f5f7fa;
}

.preview-img {
  width: 100%;
  height: 400rpx;
  display: block;
}

.photo-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400rpx;
  border: 2rpx dashed #dcdfe6;
  border-radius: 12rpx;
}

.placeholder-icon {
  font-size: 80rpx;
  margin-bottom: 16rpx;
}

.placeholder-text {
  font-size: 28rpx;
  color: #909399;
}

.processing-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.processing-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 16rpx;
  padding: 32rpx 48rpx;
}

.loading-icon {
  font-size: 56rpx;
  margin-bottom: 12rpx;
}

.loading-text {
  font-size: 26rpx;
  color: #fff;
}

.watermark-info {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  margin-top: 12rpx;
  padding: 0 4rpx;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 22rpx;
  color: #909399;
}

.info-icon {
  font-size: 22rpx;
}

.info-text {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.action-row {
  display: flex;
  gap: 16rpx;
  margin-top: 16rpx;
  justify-content: flex-end;
}

.action-btn {
  background: #f5f7fa;
  color: #606266;
  font-size: 24rpx;
  border: none;
}

.action-btn-primary {
  background: #667eea;
  color: #fff;
}
</style>
