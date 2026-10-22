<template>
  <view class="status-step-wrapper">
    <!-- 横向步骤条（默认） -->
    <template v-if="direction === 'horizontal'">
      <view class="steps-horizontal">
        <view
          v-for="(step, index) in displaySteps"
          :key="index"
          :class="['step-item', getStepClass(index)]"
          :style="{ flex: equalWidth ? 1 : 'none' }"
          @click="handleStepClick(index)"
        >
          <!-- 连接线（左侧） -->
          <view class="step-line step-line-left" v-if="index > 0" :class="{ active: index <= currentStep }"></view>

          <!-- 步骤点 -->
          <view class="step-dot-wrapper">
            <view class="step-dot">
              <text class="step-check" v-if="index < currentStep">✓</text>
              <text class="step-num" v-else>{{ index + 1 }}</text>
            </view>
            <!-- 激活脉冲效果 -->
            <view class="step-pulse" v-if="index === currentStep"></view>
          </view>

          <!-- 连接线（右侧） -->
          <view class="step-line step-line-right" v-if="index < displaySteps.length - 1" :class="{ active: index < currentStep }"></view>

          <!-- 标签 -->
          <view class="step-label-wrapper">
            <text class="step-title">{{ step.label || step.name || step.title || `步骤${index + 1}` }}</text>
            <text class="step-desc" v-if="step.desc">{{ step.desc }}</text>
            <text class="step-time" v-if="step.time">{{ step.time }}</text>
          </view>
        </view>
      </view>
    </template>

    <!-- 纵向步骤条 -->
    <template v-else>
      <view class="steps-vertical">
        <view
          v-for="(step, index) in displaySteps"
          :key="index"
          :class="['step-item-vertical', getStepClass(index)]"
          @click="handleStepClick(index)"
        >
          <!-- 左侧线 + 点 -->
          <view class="step-indicator">
            <view class="step-line-v" :class="{ active: index < displaySteps.length - 1 && index < currentStep }"></view>
            <view class="step-dot-v">
              <text class="step-check" v-if="index < currentStep">✓</text>
              <text class="step-num" v-else>{{ index + 1 }}</text>
            </view>
          </view>

          <!-- 右侧内容 -->
          <view class="step-content">
            <text class="step-title-v">{{ step.label || step.name || step.title || `步骤${index + 1}` }}</text>
            <text class="step-desc-v" v-if="step.desc">{{ step.desc }}</text>
            <text class="step-time-v" v-if="step.time">{{ step.time }}</text>
            <text class="step-extra-v" v-if="step.extra">{{ step.extra }}</text>
          </view>
        </view>
      </view>
    </template>
  </view>
</template>

<script>
/**
 * status-step.vue - 状态步骤条组件
 * 功能：展示工单/任务的状态进度
 * props: steps (步骤数组), currentStep (当前步骤索引)
 * 已完成步骤显示绿色勾选，当前步骤高亮，未完成步骤灰色
 * 步骤之间有连接线
 */
export default {
  name: 'status-step',
  props: {
    // 步骤数组
    steps: {
      type: Array,
      default: () => []
    },
    // 当前步骤索引
    currentStep: {
      type: Number,
      default: 0
    },
    // 排列方向：horizontal | vertical
    direction: {
      type: String,
      default: 'horizontal'
    },
    // 是否等宽
    equalWidth: {
      type: Boolean,
      default: false
    },
    // 激活颜色
    activeColor: {
      type: String,
      default: '#667eea'
    },
    // 完成颜色
    doneColor: {
      type: String,
      default: '#67c23a'
    },
    // 未完成颜色
    inactiveColor: {
      type: String,
      default: '#dcdfe6'
    },
    // 是否可点击
    clickable: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    displaySteps() {
      return this.steps.length > 0 ? this.steps : this.defaultSteps
    }
  },
  data() {
    return {
      defaultSteps: [
        { label: '待处理', value: 0 },
        { label: '处理中', value: 1 },
        { label: '已完成', value: 2 }
      ]
    }
  },
  methods: {
    getStepClass(index) {
      return {
        'is-done': index < this.currentStep,
        'is-current': index === this.currentStep,
        'is-pending': index > this.currentStep,
        'is-last': index === this.displaySteps.length - 1,
        'is-first': index === 0
      }
    },

    handleStepClick(index) {
      if (!this.clickable) return
      const step = this.displaySteps[index]
      this.$emit('step-click', { index, step })
    }
  }
}
</script>

<style scoped>
.status-step-wrapper {
  width: 100%;
}

/* ========================
   横向步骤条
   ======================== */
.steps-horizontal {
  display: flex;
  align-items: flex-start;
  width: 100%;
  padding: 16rpx 0;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  flex-shrink: 0;
}

.step-dot-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.step-dot {
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  background: #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 2;
  transition: all 0.3s;
}

.step-item.is-done .step-dot {
  background: #67c23a;
}

.step-item.is-current .step-dot {
  background: #667eea;
  box-shadow: 0 0 0 6rpx rgba(102, 126, 234, 0.2);
}

.step-pulse {
  position: absolute;
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  background: rgba(102, 126, 234, 0.3);
  animation: pulse 1.5s ease-out infinite;
  z-index: 1;
}

@keyframes pulse {
  0% { transform: scale(1); opacity: 0.6; }
  100% { transform: scale(1.8); opacity: 0; }
}

.step-check {
  font-size: 22rpx;
  color: #fff;
  font-weight: bold;
}

.step-num {
  font-size: 24rpx;
  color: #fff;
  font-weight: bold;
}

.step-label-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 12rpx;
  max-width: 120rpx;
}

.step-title {
  font-size: 22rpx;
  color: #c0c4cc;
  text-align: center;
  line-height: 1.3;
  transition: color 0.3s;
}

.step-item.is-done .step-title {
  color: #67c23a;
}

.step-item.is-current .step-title {
  color: #667eea;
  font-weight: bold;
}

.step-desc {
  font-size: 20rpx;
  color: #c0c4cc;
  margin-top: 4rpx;
  text-align: center;
}

.step-time {
  font-size: 18rpx;
  color: #c0c4cc;
  margin-top: 4rpx;
}

/* 横向连接线 */
.step-line {
  position: absolute;
  top: 24rpx;
  height: 2rpx;
  background: #dcdfe6;
  transition: background 0.3s;
  z-index: 1;
}

.step-line-left {
  right: calc(50% + 24rpx);
  left: 0;
}

.step-line-right {
  left: calc(50% + 24rpx);
  right: 0;
}

.step-item.is-last .step-line-right {
  display: none;
}

.step-item.is-first .step-line-left {
  display: none;
}

.step-line.active {
  background: #67c23a;
}

/* ========================
   纵向步骤条
   ======================== */
.steps-vertical {
  display: flex;
  flex-direction: column;
  padding-left: 16rpx;
}

.step-item-vertical {
  display: flex;
  align-items: flex-start;
  min-height: 100rpx;
  position: relative;
}

.step-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex-shrink: 0;
  width: 48rpx;
  position: relative;
}

.step-line-v {
  position: absolute;
  top: 48rpx;
  bottom: -100rpx;
  width: 2rpx;
  background: #dcdfe6;
  left: 50%;
  transform: translateX(-50%);
  transition: background 0.3s;
}

.step-item-vertical:last-child .step-line-v {
  display: none;
}

.step-line-v.active {
  background: #67c23a;
}

.step-dot-v {
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  background: #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s;
  position: relative;
  z-index: 2;
}

.step-item-vertical.is-done .step-dot-v {
  background: #67c23a;
}

.step-item-vertical.is-current .step-dot-v {
  background: #667eea;
  box-shadow: 0 0 0 6rpx rgba(102, 126, 234, 0.2);
}

.step-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding-left: 20rpx;
  padding-bottom: 40rpx;
}

.step-title-v {
  font-size: 28rpx;
  color: #c0c4cc;
  font-weight: normal;
  transition: color 0.3s;
  line-height: 48rpx;
}

.step-item-vertical.is-done .step-title-v {
  color: #67c23a;
}

.step-item-vertical.is-current .step-title-v {
  color: #303133;
  font-weight: bold;
}

.step-desc-v {
  font-size: 24rpx;
  color: #909399;
  margin-top: 6rpx;
  line-height: 1.4;
}

.step-time-v {
  font-size: 22rpx;
  color: #c0c4cc;
  margin-top: 6rpx;
}

.step-extra-v {
  font-size: 22rpx;
  color: #909399;
  margin-top: 4rpx;
}
</style>
