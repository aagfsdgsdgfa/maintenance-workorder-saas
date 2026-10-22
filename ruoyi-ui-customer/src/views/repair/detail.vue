<template>
  <div class="repair-detail-page">
    <!-- 顶部导航 -->
    <div class="back-bar">
      <el-button text @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
      <span class="back-title">工单详情</span>
    </div>

    <div v-if="order" class="detail-layout">
      <!-- 状态横幅 -->
      <div class="status-hero" :class="'status-' + order.status">
        <div class="status-info">
          <div class="status-label">{{ statusLabel(order.status) }}</div>
          <div class="status-order-no">{{ order.orderNo }}</div>
        </div>
        <el-tag :type="statusTag(order.status)" size="large" effect="dark" style="font-size:14px">
          {{ statusLabel(order.status) }}
        </el-tag>
      </div>

      <!-- 五步进度 -->
      <div class="step-progress-card">
        <div class="step-progress">
          <div v-for="(step, i) in steps" :key="i" class="step-item">
            <div class="step-dot" :class="{ done: currentStep > i, active: currentStep === i }">
              <el-icon v-if="currentStep > i" size="12"><Check /></el-icon>
              <span v-else>{{ i + 1 }}</span>
            </div>
            <div class="step-label">{{ step }}</div>
            <div class="step-line" v-if="i < steps.length - 1" :class="{ done: currentStep > i }"></div>
          </div>
        </div>
      </div>

      <!-- 工程师名片 -->
      <div class="engineer-card" v-if="order.engineerName">
        <div class="engineer-avatar">
          <el-avatar :size="52" style="background:var(--primary-gradient);font-size:20px">
            {{ order.engineerName?.[0] }}
          </el-avatar>
        </div>
        <div class="engineer-info">
          <div class="engineer-name">{{ order.engineerName }}</div>
          <div class="engineer-phone">
            <el-icon size="13"><Phone /></el-icon> {{ order.engineerPhone }}
          </div>
        </div>
        <el-button type="primary" @click="callEngineer">
          <el-icon><Phone /></el-icon>联系工程师
        </el-button>
      </div>

      <el-row :gutter="16">
        <!-- 左侧：工单信息 -->
        <el-col :span="14">
          <div class="info-card">
            <div class="info-card-title">
              <el-icon><InfoFilled /></el-icon>工单信息
            </div>
            <el-descriptions :column="1" border size="small">
              <el-descriptions-item label="故障描述">{{ order.description || order.title }}</el-descriptions-item>
              <el-descriptions-item label="设备ID">{{ order.assetId }}</el-descriptions-item>
              <el-descriptions-item label="联系人">{{ order.contactName }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ order.contactPhone }}</el-descriptions-item>
              <el-descriptions-item label="优先级">
                <el-tag size="small" :type="priorityTag(order.priority)">{{ priorityLabel(order.priority) }}</el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-col>

        <!-- 右侧：处理时间线 -->
        <el-col :span="10">
          <div class="info-card" v-if="order.checkInRecords?.length">
            <div class="info-card-title">
              <el-icon><Clock /></el-icon>处理进度
            </div>
            <el-timeline style="padding-left:4px">
              <el-timeline-item
                v-for="ci in order.checkInRecords"
                :key="ci.checkId"
                :icon="timelineIcon(ci.checkType)"
                :color="timelineColor(ci.checkType)"
              >
                <div class="tl-title">{{ checkTypeLabel(ci.checkType) }}</div>
                <div class="tl-desc" v-if="ci.address">{{ ci.address }}</div>
                <div class="tl-time">{{ ci.checkTime }}</div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </el-col>
      </el-row>

      <!-- 满意度评价 -->
      <div class="eval-card" v-if="order.satisfaction">
        <div class="info-card-title">
          <el-icon><Star /></el-icon>我的评价
        </div>
        <div class="eval-display">
          <el-rate v-model="order.satisfaction.overallScore" disabled show-score text-color="#f59e0b" />
          <div class="eval-comment" v-if="order.satisfaction.comment">"{{ order.satisfaction.comment }}"</div>
        </div>
      </div>

      <div class="eval-prompt-card" v-else-if="order.status === '3'">
        <div class="eval-prompt-icon"><el-icon style="font-size:28px;color:var(--warning)"><Star /></el-icon></div>
        <div class="eval-prompt-body">
          <div class="eval-prompt-title">服务完成啦，来评价一下吧</div>
          <div class="eval-prompt-sub">您的评价是我们提升服务的动力</div>
        </div>
        <el-button type="success" @click="showEvalDialog = true">确认完成</el-button>
      </div>
    </div>

    <div v-else-if="!loading" style="text-align:center;padding:60px;color:var(--text-muted)">
      <el-empty description="工单不存在" />
    </div>

    <!-- 评价弹窗 -->
    <el-dialog v-model="showEvalDialog" title="服务满意度评价" width="420px">
      <el-form label-position="top">
        <el-form-item label="响应速度"><el-rate v-model="evalForm.responseScore" /></el-form-item>
        <el-form-item label="技术水平"><el-rate v-model="evalForm.techScore" /></el-form-item>
        <el-form-item label="综合评价">
          <el-rate v-model="evalForm.overallScore" show-text :texts="['很差','较差','一般','良好','优秀']" />
        </el-form-item>
        <el-form-item label="文字评价">
          <el-input v-model="evalForm.comment" type="textarea" :rows="3" placeholder="请输入评价（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEvalDialog = false">取消</el-button>
        <el-button type="primary" @click="submitEval">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getWorkOrderDetail, submitSatisfaction } from '@/api/zw/workOrder'

const route = useRoute()
const steps = ['待接单', '已接单', '工程师到达', '维修中', '待验收']
const showEvalDialog = ref(false)
const evalForm = ref({ responseScore: 5, techScore: 5, overallScore: 5, comment: '' })
const order = ref(null)
const loading = ref(false)

const currentStep = computed(() => {
  return { '0': 0, '1': 2, '2': 3, '3': 4 }[order.value?.status] ?? 0
})

async function loadDetail() {
  loading.value = true
  try {
    const res = await getWorkOrderDetail(route.params.id)
    if (res.code === 200 && res.data) order.value = res.data
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

function statusTag(status) {
  return { '0': 'warning', '1': 'primary', '2': 'warning', '3': 'success', '4': 'info' }[status] || 'info'
}

function statusLabel(status) {
  return { '0': '待接单', '1': '处理中', '2': '待确认', '3': '已完成', '4': '已取消' }[status] || status
}

function priorityLabel(p) {
  return { '1': '低', '2': '中', '3': '高', '4': '紧急' }[p] || p
}

function priorityTag(p) {
  return { '1': 'info', '2': 'warning', '3': 'danger', '4': 'danger' }[p] || 'info'
}

function checkTypeLabel(type) {
  return { 'accept': '接单', 'dispatch': '派单', 'arrive': '到达', 'leave': '离开', 'complete': '完工' }[type] || type
}

function timelineIcon(type) {
  const map = { 'accept': 'Check', 'dispatch': 'Promotion', 'arrive': 'Location', 'leave': 'Position', 'complete': 'CircleCheck' }
  return map[type] || 'Check'
}

function timelineColor(type) {
  const map = { 'accept': '#10b981', 'dispatch': '#3b82f6', 'arrive': '#3b82f6', 'leave': '#f59e0b', 'complete': '#10b981' }
  return map[type] || '#64748b'
}

function callEngineer() {
  if (order.value?.engineerPhone) window.open('tel:' + order.value.engineerPhone)
}

async function submitEval() {
  try {
    await submitSatisfaction({ workOrderId: order.value.workOrderId, ...evalForm.value })
    order.value.satisfaction = { ...evalForm.value }
    showEvalDialog.value = false
    ElMessage.success('评价成功，感谢您的反馈')
  } catch (e) {
    if (e?.msg?.includes('已评价')) {
      showEvalDialog.value = false
      ElMessage.warning('该工单已评价过')
      loadDetail()
    } else {
      ElMessage.error('提交失败')
    }
  }
}

onMounted(() => { loadDetail() })
</script>

<style scoped>
.back-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.back-title {
  font-size: 15px;
  color: var(--text-secondary);
}

.detail-layout {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 状态横幅 */
.status-hero {
  border-radius: var(--radius-lg);
  padding: 20px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #fff;
  position: relative;
  overflow: hidden;
}

.status-hero::before {
  content: '';
  position: absolute;
  right: -20px;
  top: -20px;
  width: 120px;
  height: 120px;
  background: rgba(255,255,255,0.1);
  border-radius: 50%;
}

.status-0, .status-2 { background: linear-gradient(135deg, #f59e0b, #d97706); }
.status-1 { background: linear-gradient(135deg, #3b82f6, #1a3a6b); }
.status-3 { background: linear-gradient(135deg, #10b981, #059669); }
.status-4 { background: linear-gradient(135deg, #94a3b8, #64748b); }

.status-label {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 4px;
}

.status-order-no {
  font-size: 13px;
  opacity: 0.8;
  font-family: monospace;
}

/* 步骤进度卡片 */
.step-progress-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 20px 32px;
  box-shadow: var(--shadow-sm);
}

/* 工程师卡片 */
.engineer-card {
  background: linear-gradient(135deg, #eff6ff, #e8f4fd);
  border: 1px solid #dbeafe;
  border-radius: var(--radius-lg);
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 14px;
}

.engineer-info { flex: 1; }

.engineer-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 2px;
}

.engineer-phone {
  font-size: 13px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 信息卡片 */
.info-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--shadow-sm);
}

.info-card-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.tl-title { font-size: 14px; font-weight: 600; color: var(--text-primary); }
.tl-desc { font-size: 13px; color: var(--text-secondary); margin-top: 2px; }
.tl-time { font-size: 12px; color: var(--text-muted); margin-top: 2px; }

/* 评价 */
.eval-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--shadow-sm);
}

.eval-display {
  padding: 12px 0 0;
}

.eval-comment {
  font-size: 13px;
  color: var(--text-secondary);
  font-style: italic;
  margin-top: 8px;
}

.eval-prompt-card {
  background: linear-gradient(135deg, #fff7f6, #fff0ee);
  border: 1px solid #ffc0b4;
  border-radius: var(--radius-lg);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.eval-prompt-icon {
  font-size: 36px;
  flex-shrink: 0;
}

.eval-prompt-body { flex: 1; }

.eval-prompt-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.eval-prompt-sub {
  font-size: 13px;
  color: var(--text-secondary);
}
</style>
