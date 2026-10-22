<template>
  <view class="generate-page">
    <!-- 顶部说明 -->
    <view class="tips-card" v-if="!generated">
      <view class="tips-title">AI日报生成</view>
      <view class="tips-desc">
        系统将聚合您今日的工单、巡检、打卡数据，由AI智能生成日报内容。
      </view>
      <view class="tips-info">
        <text class="info-item">D {{ today }}</text>
        <text class="info-item"># {{ taskCount }} 个工单</text>
        <text class="info-item">X {{ inspectionCount }} 个巡检</text>
        <text class="info-item">@ {{ checkInCount }} 次打卡</text>
      </view>
    </view>

    <!-- 生成日报按钮 -->
    <view class="generate-section" v-if="!generated && !loading">
      <button class="generate-btn" @click="handleGenerate" :disabled="generating">
        <text class="btn-icon">|</text>
        <text class="btn-text">{{ generating ? '生成中...' : '生成日报' }}</text>
      </button>
    </view>

    <!-- 加载状态 -->
    <view class="loading-section" v-if="loading">
      <view class="loading-card">
        <view class="loading-icon">...</view>
        <view class="loading-text">正在聚合数据并生成日报...</view>
        <view class="loading-steps">
          <view :class="['step', loadStep >= 1 ? 'done' : '']">
            <text v-if="loadStep < 1">1</text>
            <text v-else>+</text>
            <text class="step-text">获取工单数据</text>
          </view>
          <view :class="['step', loadStep >= 2 ? 'done' : '']">
            <text v-if="loadStep < 2">2</text>
            <text v-else>+</text>
            <text class="step-text">获取巡检数据</text>
          </view>
          <view :class="['step', loadStep >= 3 ? 'done' : '']">
            <text v-if="loadStep < 3">3</text>
            <text v-else>+</text>
            <text class="step-text">获取打卡数据</text>
          </view>
          <view :class="['step', loadStep >= 4 ? 'done' : '']">
            <text v-if="loadStep < 4">4</text>
            <text v-else>+</text>
            <text class="step-text">AI生成中</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 日报预览 -->
    <view class="report-preview" v-if="generated">
      <view class="preview-header">
        <text class="preview-title">日报预览</text>
        <view class="preview-actions">
          <text class="action-btn" @click="handleEdit">E</text>
          <text class="action-btn" @click="regenerate">~</text>
        </view>
      </view>

      <!-- 编辑区域 -->
      <view class="form-section">
        <view class="form-label">日报标题</view>
        <input
          class="form-input"
          v-model="formData.title"
          placeholder="请输入日报标题"
        />
      </view>

      <view class="form-section">
        <view class="form-label">摘要</view>
        <textarea
          class="form-textarea"
          v-model="formData.summary"
          placeholder="请输入日报摘要"
          :auto-height="true"
        />
      </view>

      <!-- 日报内容Sections -->
      <view class="sections-wrap">
        <view class="section-title">详细内容</view>
        <view
          v-for="(section, index) in formData.sections"
          :key="index"
          class="section-card"
        >
          <view class="section-header">
            <input
              class="section-name-input"
              v-model="section.name"
              placeholder="小节标题"
            />
            <text class="section-del" @click="removeSection(index)">X</text>
          </view>
          <textarea
            class="section-content"
            v-model="section.content"
            placeholder="请输入内容"
            :auto-height="true"
          />
        </view>
        <view class="add-section" @click="addSection">
          <text class="add-icon">+</text>
          <text class="add-text">添加小节</text>
        </view>
      </view>

      <!-- 附件信息 -->
      <view class="form-section" v-if="attachedWorkOrders.length > 0">
        <view class="form-label">关联工单</view>
        <view
          v-for="wo in attachedWorkOrders"
          :key="wo.id"
          class="attach-item"
        >
          <text class="attach-name">{{ wo.workOrderNo }}</text>
          <text class="attach-title">{{ wo.title }}</text>
        </view>
      </view>
    </view>

    <!-- 底部操作 -->
    <view class="bottom-bar" v-if="generated">
      <button
        class="submit-btn"
        :loading="submitting"
        :disabled="submitting"
        @click="handleSubmit"
      >
        确认提交
      </button>
    </view>
  </view>
</template>

<script>
import {
  getDailyReportList,
  getCheckInList,
  getMyTasks,
  getInspectionTaskList,
  generateDailyReport,
  submitDailyReport
} from '@/api/request.js'

export default {
  data() {
    return {
      generating: false,
      loading: false,
      loadStep: 0,
      generated: false,
      submitting: false,
      taskCount: 0,
      inspectionCount: 0,
      checkInCount: 0,
      attachedWorkOrders: [],
      rawData: {},
      formData: {
        title: '',
        summary: '',
        sections: [
          { name: '今日工作总结', content: '' },
          { name: '明日工作计划', content: '' }
        ]
      }
    }
  },
  computed: {
    today() {
      const d = new Date()
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    }
  },
  onLoad(options) {
    if (options.id) {
      this.loadExisting(options.id)
    } else {
      this.preloadStats()
    }
  },
  methods: {
    async preloadStats() {
      try {
        const today = this.today
        const [tasks, inspections, checkins] = await Promise.all([
          getMyTasks({ pageNum: 1, pageSize: 1 }).catch(() => ({ total: 0 })),
          getInspectionTaskList({ pageNum: 1, pageSize: 1 }).catch(() => ({ total: 0 })),
          getCheckInList({ pageNum: 1, pageSize: 1, date: today }).catch(() => ({ total: 0 }))
        ])
        this.taskCount = tasks.total || tasks.data?.total || 0
        this.inspectionCount = inspections.total || inspections.data?.total || 0
        this.checkInCount = checkins.total || checkins.data?.total || 0
      } catch (e) {
        // ignore
      }
    },
    async loadExisting(id) {
      uni.showLoading({ title: '加载中...' })
      try {
        const res = await getDailyReportList({ id })
        const list = res.rows || res.data?.rows || []
        const item = list.find(r => String(r.id) === String(id)) || {}
        this.formData = {
          title: item.title || '',
          summary: item.summary || '',
          sections: item.sections?.length > 0 ? item.sections : [
            { name: '今日工作总结', content: '' },
            { name: '明日工作计划', content: '' }
          ]
        }
        this.generated = true
      } finally {
        uni.hideLoading()
      }
    },
    async handleGenerate() {
      this.generating = true
      this.loading = true
      this.loadStep = 0

      try {
        const today = this.today

        // Step 1: 获取工单
        this.loadStep = 1
        const tasksRes = await getMyTasks({ pageNum: 1, pageSize: 50 })
        const tasks = tasksRes.rows || tasksRes.data?.rows || []
        this.taskCount = tasks.length
        this.rawData.tasks = tasks

        // Step 2: 获取巡检
        this.loadStep = 2
        const inspRes = await getInspectionTaskList({ pageNum: 1, pageSize: 50 })
        const inspections = inspRes.rows || inspRes.data?.rows || []
        this.inspectionCount = inspections.length
        this.rawData.inspections = inspections

        // Step 3: 获取打卡
        this.loadStep = 3
        const checkinRes = await getCheckInList({ pageNum: 1, pageSize: 100, date: today })
        const checkins = checkinRes.rows || checkinRes.data?.rows || []
        this.checkInCount = checkins.length
        this.rawData.checkins = checkins

        // Step 4: AI生成
        this.loadStep = 4
        uni.showLoading({ title: 'AI生成中...' })
        const aiRes = await generateDailyReport({
          date: today,
          tasks,
          inspections,
          checkins
        })
        uni.hideLoading()

        // 填充表单
        this.formData = {
          title: aiRes.title || `${today} 工作日报`,
          summary: aiRes.summary || '',
          sections: aiRes.sections?.length > 0 ? aiRes.sections : [
            { name: '今日工作总结', content: '' },
            { name: '明日工作计划', content: '' }
          ]
        }

        this.attachedWorkOrders = tasks.slice(0, 5)
        this.generated = true
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: e.msg || '生成失败', icon: 'none' })
      } finally {
        this.loading = false
        this.generating = false
      }
    },
    regenerate() {
      uni.showModal({
        title: '重新生成',
        content: '确定要重新生成日报吗？',
        success: (res) => {
          if (res.confirm) {
            this.generated = false
            this.handleGenerate()
          }
        }
      })
    },
    addSection() {
      this.formData.sections.push({ name: '', content: '' })
    },
    removeSection(index) {
      if (this.formData.sections.length <= 1) {
        uni.showToast({ title: '至少保留一个小节', icon: 'none' })
        return
      }
      this.formData.sections.splice(index, 1)
    },
    handleEdit() {
      uni.showToast({ title: '可编辑日报内容', icon: 'none' })
    },
    async handleSubmit() {
      if (!this.formData.title.trim()) {
        uni.showToast({ title: '请输入日报标题', icon: 'none' })
        return
      }
      if (!this.formData.summary.trim()) {
        uni.showToast({ title: '请输入日报摘要', icon: 'none' })
        return
      }

      uni.showModal({
        title: '提交日报',
        content: '确认提交日报？提交后将无法修改。',
        success: async (res) => {
          if (!res.confirm) return

          this.submitting = true
          try {
            await submitDailyReport({
              title: this.formData.title,
              summary: this.formData.summary,
              sections: this.formData.sections,
              reportDate: this.today
            })
            uni.showToast({ title: '提交成功', icon: 'success' })
            setTimeout(() => {
              uni.navigateBack()
            }, 1500)
          } catch (e) {
            uni.showToast({ title: e.msg || '提交失败', icon: 'none' })
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
.generate-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 160rpx;
}

.tips-card {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  margin: 24rpx;
  border-radius: 16rpx;
  padding: 32rpx;
}

.tips-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 12rpx;
}

.tips-desc {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.6;
  margin-bottom: 20rpx;
}

.tips-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.info-item {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.15);
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
}

.generate-section {
  padding: 32rpx 24rpx;
}

.generate-btn {
  width: 100%;
  height: 100rpx;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  border-radius: 50rpx;
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  border: none;
}

.generate-btn::after {
  border: none;
}

.generate-btn[disabled] {
  opacity: 0.6;
}

.btn-icon {
  font-size: 36rpx;
}

.loading-section {
  padding: 24rpx;
}

.loading-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 48rpx 32rpx;
  text-align: center;
}

.loading-icon {
  font-size: 80rpx;
  margin-bottom: 24rpx;
}

.loading-text {
  font-size: 28rpx;
  color: #303133;
  margin-bottom: 32rpx;
}

.loading-steps {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  text-align: left;
}

.step {
  display: flex;
  align-items: center;
  gap: 12rpx;
  font-size: 26rpx;
  color: #909399;
}

.step.done {
  color: #67c23a;
}

.step-text {
  flex: 1;
}

.report-preview {
  padding: 0 24rpx;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0 16rpx;
}

.preview-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #303133;
}

.preview-actions {
  display: flex;
  gap: 16rpx;
}

.action-btn {
  font-size: 36rpx;
  padding: 8rpx;
  background: #f5f7fa;
  border-radius: 8rpx;
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-section {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
}

.form-label {
  font-size: 26rpx;
  color: #606266;
  margin-bottom: 12rpx;
  font-weight: 500;
}

.form-input {
  font-size: 30rpx;
  color: #303133;
}

.form-textarea {
  font-size: 28rpx;
  color: #303133;
  line-height: 1.6;
  width: 100%;
  min-height: 120rpx;
}

.sections-wrap {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
}

.section-title {
  font-size: 26rpx;
  color: #606266;
  font-weight: 500;
  margin-bottom: 16rpx;
}

.section-card {
  background: #f9fafb;
  border-radius: 10rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
  border: 1rpx solid #ebeef5;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10rpx;
}

.section-name-input {
  font-size: 28rpx;
  font-weight: bold;
  color: #303133;
  flex: 1;
  background: transparent;
}

.section-del {
  font-size: 28rpx;
  color: #f56c6c;
  padding: 4rpx 8rpx;
}

.section-content {
  font-size: 26rpx;
  color: #606266;
  line-height: 1.8;
  width: 100%;
  min-height: 80rpx;
  background: transparent;
}

.add-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  padding: 20rpx;
  border: 2rpx dashed #dcdfe6;
  border-radius: 10rpx;
  color: #909399;
  font-size: 28rpx;
}

.add-icon {
  font-size: 36rpx;
}

.attach-item {
  padding: 12rpx 0;
  border-bottom: 1rpx solid #ebeef5;
}

.attach-item:last-child {
  border-bottom: none;
}

.attach-name {
  font-size: 22rpx;
  color: #909399;
  font-family: monospace;
}

.attach-title {
  font-size: 26rpx;
  color: #303133;
  margin-left: 8rpx;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 24rpx;
  background: #fff;
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.06);
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
</style>
