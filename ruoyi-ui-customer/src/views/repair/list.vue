<template>
  <div class="repair-list">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-title">报修中心</div>
      <el-button type="primary" @click="$router.push('/repair/create')">
        <el-icon><Plus /></el-icon>新建报修
      </el-button>
    </div>

    <div class="repair-layout">
      <!-- 左侧工单列表 -->
      <div class="order-list">
        <!-- 筛选 -->
        <div class="list-filter">
          <el-input
            v-model="keyword"
            placeholder="搜索工单号、描述"
            clearable
            @keyup.enter="loadOrderList"
          >
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
        </div>

        <div v-if="loading" class="loading-tip">
          <el-icon class="is-loading" size="20"><Loading /></el-icon>
        </div>

        <div
          v-for="order in orderList"
          :key="order.workOrderId"
          class="order-card"
          :class="{ active: selectedId === order.workOrderId }"
          @click="selectOrder(order)"
        >
          <div class="order-top">
            <span class="order-no">{{ order.orderNo }}</span>
            <el-tag :type="statusTag(order.status)" size="small" effect="dark">
              {{ statusLabel(order.status) }}
            </el-tag>
          </div>
          <div class="order-title">{{ order.title || order.description }}</div>
          <div class="order-meta">
            <span class="meta-item">
              <el-icon size="12"><Clock /></el-icon>
              {{ formatTime(order.createTime) }}
            </span>
            <span class="meta-item">
              <el-icon size="12"><Box /></el-icon>
              #{{ order.assetId }}
            </span>
            <el-button
              v-if="order.status === '3'"
              type="success"
              size="small"
              style="margin-left:auto; font-size:12px; padding: 2px 8px; height: 22px;"
              @click.stop="openEvalForOrder(order)"
            >
              确认完成
            </el-button>
          </div>
        </div>

        <EmptyState
          v-if="!orderList.length && !loading"
          icon="tickets"
          title="暂无报修工单"
          description="还没有任何报修记录"
          action-text="立即报修"
          @action="$router.push('/repair/create')"
        />
      </div>

      <!-- 右侧详情 -->
      <div class="order-detail" v-if="currentOrder">
        <!-- 五步进度条 -->
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

        <!-- 工程师名片 -->
        <div class="engineer-card" v-if="currentOrder.engineerName">
          <div class="engineer-avatar">
            <el-avatar :size="44" style="background:var(--primary-gradient)">
              {{ currentOrder.engineerName?.[0] }}
            </el-avatar>
          </div>
          <div class="engineer-info">
            <div class="engineer-name">{{ currentOrder.engineerName }}</div>
            <div class="engineer-phone">
              <el-icon size="12"><Phone /></el-icon> {{ currentOrder.engineerPhone }}
            </div>
          </div>
          <el-button type="primary" plain size="small" @click="callEngineer">
            <el-icon><Phone /></el-icon>联系
          </el-button>
        </div>

        <!-- 工单信息 -->
        <div class="info-section">
          <div class="info-section-title">
            <el-icon><InfoFilled /></el-icon>工单信息
          </div>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="工单编号">
              <span style="font-family:monospace;color:var(--primary-light)">{{ currentOrder.orderNo }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="设备ID">{{ currentOrder.assetId }}</el-descriptions-item>
            <el-descriptions-item label="故障描述">{{ currentOrder.description || currentOrder.title }}</el-descriptions-item>
            <el-descriptions-item label="优先级">
              <el-tag size="small" :type="priorityTag(currentOrder.priority)">
                {{ priorityLabel(currentOrder.priority) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="联系人">{{ currentOrder.contactName }} {{ currentOrder.contactPhone }}</el-descriptions-item>
            <el-descriptions-item label="地址">{{ currentOrder.contactAddress }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 处理进度时间线 -->
        <div class="info-section" v-if="currentOrder.checkInRecords?.length">
          <div class="info-section-title">
            <el-icon><Clock /></el-icon>处理进度
          </div>
          <el-timeline>
            <el-timeline-item
              v-for="ci in currentOrder.checkInRecords"
              :key="ci.checkId"
              :icon="timelineIcon(ci.checkType)"
              :color="timelineColor(ci.checkType)"
            >
              <div class="timeline-title">{{ checkTypeLabel(ci.checkType) }}</div>
              <div class="timeline-desc" v-if="ci.address">{{ ci.address }}</div>
              <div class="timeline-time">{{ ci.checkTime }}</div>
            </el-timeline-item>
          </el-timeline>
        </div>

        <!-- 满意度评价 -->
        <div class="eval-section" v-if="currentOrder.satisfaction">
          <div class="info-section-title">
            <el-icon><Star /></el-icon>我的评价
          </div>
          <div class="eval-stars">
            <el-rate v-model="currentOrder.satisfaction.overallScore" disabled show-score text-color="#f59e0b" />
          </div>
          <div class="eval-comment" v-if="currentOrder.satisfaction.comment">
            "{{ currentOrder.satisfaction.comment }}"
          </div>
        </div>

        <!-- 待评价提示 -->
        <div class="eval-prompt" v-else-if="currentOrder.status === '3'">
          <div class="eval-prompt-icon"><el-icon style="font-size:28px;color:var(--warning)"><Star /></el-icon></div>
          <div class="eval-prompt-text">服务完成啦，来评价一下吧</div>
          <el-button type="success" size="small" @click="showEvalDialog = true">确认完成</el-button>
        </div>
      </div>

      <div v-else class="order-detail">
        <EmptyState
          icon="document"
          title="请选择左侧工单"
          description="点击工单卡片查看详情和处理进度"
        />
      </div>
    </div>

    <!-- 评价弹窗 -->
    <el-dialog v-model="showEvalDialog" title="服务满意度评价" width="420px" :close-on-click-modal="false">
      <el-form label-position="top">
        <el-form-item label="响应速度">
          <el-rate v-model="evalForm.responseScore" />
        </el-form-item>
        <el-form-item label="技术水平">
          <el-rate v-model="evalForm.techScore" />
        </el-form-item>
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
import { getWorkOrderList, getWorkOrderDetail, submitSatisfaction, getRepairApplyList, getRepairApplyDetail } from '@/api/zw/workOrder'
import { ElMessage } from 'element-plus'
import EmptyState from '@/components/EmptyState.vue'

const route = useRoute()

const orderList = ref([])
const selectedId = ref(null)
const currentOrder = ref(null)
const loading = ref(false)
const showEvalDialog = ref(false)
const keyword = ref('')
const evalForm = ref({ responseScore: 5, techScore: 5, overallScore: 5, comment: '' })
const steps = ['待接单', '已接单', '工程师到达', '维修中', '待验收']

const currentStep = computed(() => {
  if (currentOrder.value?.isApply) return 0 // 报修申请待受理
  return { '0': 0, '1': 2, '2': 3, '3': 4 }[currentOrder.value?.status] ?? 0
})

async function loadOrderList() {
  loading.value = true
  try {
    // 加载工单列表
    const res = await getWorkOrderList({ pageNum: 1, pageSize: 50 })
    if (res.code === 200 || res.code === 0) {
      orderList.value = res.rows || []
    }
    // 加载报修申请列表（合并显示）
    try {
      const applyRes = await getRepairApplyList({ pageNum: 1, pageSize: 50 })
      if (applyRes.code === 200 || applyRes.code === 0) {
        const applies = (applyRes.rows || []).map(a => ({
          workOrderId: a.applyId,
          orderNo: 'RP' + a.applyId,
          title: a.applyTitle,
          description: a.faultDesc,
          status: a.status === '0' ? '待受理' : a.status === '1' ? '已受理' : a.status === '2' ? '已转工单' : a.status,
          createTime: a.createTime,
          assetId: a.assetId,
          isApply: true,
          applyData: a
        }))
        orderList.value = [...applies, ...orderList.value]
      }
    } catch (e) { /* 报修申请接口可能不存在 */ }
  } catch (e) {
    console.error('工单列表加载失败', e)
  } finally {
    loading.value = false
  }
}

async function selectOrder(order) {
  selectedId.value = order.workOrderId
  if (order.isApply) {
    // 报修申请详情
    try {
      const res = await getRepairApplyDetail(order.workOrderId)
      if ((res.code === 200 || res.code === 0) && res.data) {
        const a = res.data
        currentOrder.value = {
          workOrderId: a.applyId,
          orderNo: 'RP' + a.applyId,
          title: a.applyTitle,
          description: a.faultDesc,
          status: a.status,
          createTime: a.createTime,
          assetId: a.assetId,
          contactName: a.contactName,
          contactPhone: a.contactPhone,
          contactAddress: a.expectedTime,
          isApply: true
        }
      }
    } catch (e) {
      currentOrder.value = order
    }
  } else {
    // 工单详情
    try {
      const res = await getWorkOrderDetail(order.workOrderId)
      if (res.code === 200 && res.data) currentOrder.value = res.data
      // 已完成（status=3/4）且未评价，自动弹出评价
      if (res.data?.status === '3' && !res.data?.satisfaction) {
        setTimeout(() => {
          showEvalDialog.value = true
          evalForm.value = { responseScore: 5, techScore: 5, overallScore: 5, comment: '' }
        }, 100)
      }
    } catch (e) {
      currentOrder.value = order
    }
  }
}

function statusTag(status) {
  return { '0': 'warning', '1': 'primary', '2': 'warning', '3': 'success', '4': 'info', '待受理': 'warning', '已受理': 'primary', '已转工单': 'success' }[status] || 'info'
}

function statusLabel(status) {
  return { '0': '待接单', '1': '处理中', '2': '待确认', '3': '已完成', '4': '已取消', '待受理': '待受理', '已受理': '已受理', '已转工单': '已转工单' }[status] || status
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

function formatTime(t) {
  if (!t) return ''
  return t.substring ? t.substring(0, 16) : String(t).substring(0, 16)
}

function callEngineer() {
  if (currentOrder.value?.engineerPhone) {
    window.open('tel:' + currentOrder.value.engineerPhone)
  }
}

async function submitEval() {
  try {
    await submitSatisfaction({ workOrderId: currentOrder.value.workOrderId, ...evalForm.value })
    showEvalDialog.value = false
    ElMessage.success('评价成功，感谢您的反馈')
    // 标记该工单已评价，防止重复弹框
    currentOrder.value.status = '4'
    currentOrder.value.satisfaction = { ...evalForm.value }
    // 重新加载工单详情（包含最新satisfaction状态）
    const res = await getWorkOrderDetail(currentOrder.value.workOrderId)
    if (res.code === 200 && res.data) {
      currentOrder.value = res.data
    }
    loadOrderList()
  } catch (e) {
    if (e?.msg?.includes('已评价')) {
      showEvalDialog.value = false
      ElMessage.warning('该工单已评价过')
      currentOrder.value.satisfaction = { overallScore: 5 }
      currentOrder.value.status = '4'
      loadOrderList()
    } else {
      ElMessage.error('提交失败')
    }
  }
}

function openEvalForOrder(order) {
  selectOrder(order)
  showEvalDialog.value = true
  evalForm.value = { responseScore: 5, techScore: 5, overallScore: 5, comment: '' }
}

onMounted(() => {
  loadOrderList().then(() => {
    // 从首页跳转过来带 pendingEval 参数，自动弹出评价
    const pendingId = route.query.pendingEval
    if (pendingId) {
      const order = orderList.value.find(o => String(o.workOrderId) === String(pendingId))
      if (order) {
        selectOrder(order)
        showEvalDialog.value = true
        evalForm.value = { responseScore: 5, techScore: 5, overallScore: 5, comment: '' }
      }
    }
  })
})
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-title::before {
  content: '';
  display: block;
  width: 4px;
  height: 20px;
  background: var(--primary-gradient);
  border-radius: 2px;
}

.repair-layout {
  display: flex;
  gap: 16px;
  height: calc(100vh - 180px);
}

.order-list {
  width: 320px;
  overflow-y: auto;
  flex-shrink: 0;
}

.list-filter {
  margin-bottom: 12px;
}

.loading-tip {
  text-align: center;
  padding: 40px 0;
  color: var(--text-muted);
}

.order-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 14px 16px;
  margin-bottom: 10px;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.2s;
  box-shadow: var(--shadow-sm);
}

.order-card:hover {
  border-color: var(--primary-light);
  transform: translateX(2px);
}

.order-card.active {
  border-color: var(--primary-light);
  background: #f8fbff;
}

.order-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.order-no {
  font-size: 12px;
  color: var(--text-muted);
  font-family: monospace;
}

.order-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.order-meta {
  display: flex;
  gap: 12px;
}

.meta-item {
  font-size: 12px;
  color: var(--text-muted);
  display: flex;
  align-items: center;
  gap: 3px;
}

.order-detail {
  flex: 1;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 20px;
  overflow-y: auto;
  min-width: 0;
  box-shadow: var(--shadow-sm);
}

/* 工程师卡片 */
.engineer-card {
  background: linear-gradient(135deg, #f0f9ff, #e8f4fd);
  border-radius: var(--radius-lg);
  padding: 14px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  border: 1px solid #dbeafe;
}

.engineer-info {
  flex: 1;
}

.engineer-name {
  font-size: 15px;
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

.info-section {
  margin-top: 16px;
}

.info-section-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.timeline-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.timeline-desc {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 2px;
}

.timeline-time {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 2px;
}

.eval-stars {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
}

.eval-comment {
  font-size: 13px;
  color: var(--text-secondary);
  font-style: italic;
  margin-top: 4px;
}

.eval-prompt {
  background: linear-gradient(135deg, #fff7f6, #fff0ee);
  border: 1px solid #ffc0b4;
  border-radius: var(--radius-lg);
  padding: 20px;
  text-align: center;
  margin-top: 16px;
}

.eval-prompt-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.eval-prompt-text {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 12px;
}
</style>
