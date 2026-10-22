<template>
  <div class="contract-list">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-title">合同中心</div>
      <div style="display:flex;gap:10px;align-items:center">
        <el-tag type="info" size="default">共 {{ list.length }} 份合同</el-tag>
        <el-button type="primary" size="small" @click="showAddDialog">
          <el-icon><Plus /></el-icon>添加合同
        </el-button>
      </div>
    </div>

    <!-- 合同卡片网格 -->
    <div class="contract-grid" v-if="list.length">
      <div
        v-for="(c, i) in list"
        :key="c.contractId"
        class="contract-card fade-in-up"
        :style="{ animationDelay: (i * 0.05) + 's', opacity: 0 }"
        @click="$router.push('/contract/detail/' + c.contractId)"
      >
        <!-- 卡片顶栏：状态标签 -->
        <div class="card-topbar">
          <el-tag :type="contractStatusTag(c)" size="small" effect="dark">
            {{ contractStatusLabel(c) }}
          </el-tag>
          <span class="card-period">{{ formatDate(c.startDate) }} ~ {{ formatDate(c.endDate) }}</span>
        </div>

        <!-- 合同名称 -->
        <div class="contract-name">{{ c.contractName || c.contractNo }}</div>
        <div class="contract-no">合同编号：{{ c.contractNo }}</div>

        <!-- 统计数据 -->
        <div class="contract-stats">
          <div class="stat-item">
            <div class="stat-icon" style="background:#eff6ff;color:#3b82f6">
              <el-icon><Box /></el-icon>
            </div>
            <div>
              <div class="stat-value">{{ c.assetCount || 0 }}</div>
              <div class="stat-label">覆盖设备</div>
            </div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item" v-if="c.slaResponseHours">
            <div class="stat-icon" style="background:#f0fdf4;color:#10b981">
              <el-icon><Timer /></el-icon>
            </div>
            <div>
              <div class="stat-value">{{ c.slaResponseHours }}h</div>
              <div class="stat-label">SLA响应</div>
            </div>
          </div>
        </div>

        <!-- 配额进度 -->
        <div class="quota-section" v-if="c.includedInspectionsPerYear">
          <div class="quota-header">
            <span class="quota-icon"><el-icon><Search /></el-icon></span>
            <span class="quota-title">例行巡检</span>
            <span class="quota-count">{{ c.usedInspections || 0 }}/{{ c.includedInspectionsPerYear }} 次</span>
          </div>
          <el-progress
            :percentage="inspectionPct(c)"
            :stroke-width="6"
            :show-text="false"
            :color="inspectionPct(c) > 80 ? '#ef4444' : '#3b82f6'"
          />
        </div>

        <div class="quota-section" v-if="c.includedRepairsPerYear">
          <div class="quota-header">
            <span class="quota-icon"><el-icon><Tools /></el-icon></span>
            <span class="quota-title">紧急报修</span>
            <span class="quota-count">{{ c.usedRepairs || 0 }}/{{ c.includedRepairsPerYear }} 次</span>
          </div>
          <el-progress
            :percentage="repairPct(c)"
            :stroke-width="6"
            :show-text="false"
            :color="repairPct(c) > 80 ? '#ef4444' : '#f59e0b'"
          />
        </div>

        <!-- 底部操作 -->
        <div class="card-footer">
          <el-button type="primary" link size="small" @click.stop="$router.push('/contract/detail/' + c.contractId)">
            查看详情 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <EmptyState
      v-else-if="!loading"
      icon="document"
      title="暂无合同"
      description="您的公司尚未签订维保合同"
      action-text="添加合同"
      @action="showAddDialog"
    />

    <!-- 添加合同弹窗 -->
    <el-dialog v-model="addDialogVisible" title="添加合同" width="520px" :close-on-click-modal="false">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
        <el-form-item label="合同编号" prop="contractNo">
          <el-input v-model="form.contractNo" placeholder="请输入合同编号" />
        </el-form-item>
        <el-form-item label="合同名称" prop="contractName">
          <el-input v-model="form.contractName" placeholder="请输入合同名称" />
        </el-form-item>
        <el-form-item label="合同开始日期" prop="startDate">
          <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="合同结束日期" prop="endDate">
          <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="SLA响应时长" prop="slaResponseHours">
          <el-input-number v-model="form.slaResponseHours" :min="1" :max="168" placeholder="小时" style="width:100%" />
        </el-form-item>
        <el-form-item label="SLA到场时长" prop="slaArrivalHours">
          <el-input-number v-model="form.slaArrivalHours" :min="1" :max="720" placeholder="小时" style="width:100%" />
        </el-form-item>
        <el-form-item label="含巡检次数" prop="includedInspections">
          <el-input-number v-model="form.includedInspections" :min="0" placeholder="次/年" style="width:100%" />
        </el-form-item>
        <el-form-item label="含维修次数" prop="includedRepairs">
          <el-input-number v-model="form.includedRepairs" :min="0" placeholder="次/年" style="width:100%" />
        </el-form-item>
        <el-form-item label="免费配件额度" prop="freePartsQuota">
          <el-input-number v-model="form.freePartsQuota" :min="0" :precision="2" placeholder="元" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">提交</el-button>
      </template>
    </el-dialog>

    <div v-if="loading" style="text-align:center;padding:60px;color:var(--text-muted)">
      <el-icon class="is-loading" size="24"><Loading /></el-icon>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getContractList, addContract } from '@/api/zw/contract'
import { ElMessage } from 'element-plus'
import EmptyState from '@/components/EmptyState.vue'

const list = ref([])
const loading = ref(false)

// 添加合同
const addDialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref()
const form = ref({
  contractNo: '',
  contractName: '',
  startDate: '',
  endDate: '',
  slaResponseHours: 4,
  slaArrivalHours: 8,
  includedInspections: 0,
  includedRepairs: 0,
  freePartsQuota: 0
})
const rules = {
  contractNo: [{ required: true, message: '请输入合同编号', trigger: 'blur' }],
  contractName: [{ required: true, message: '请输入合同名称', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
}

function showAddDialog() {
  form.value = {
    contractNo: '',
    contractName: '',
    startDate: '',
    endDate: '',
    slaResponseHours: 4,
    slaArrivalHours: 8,
    includedInspections: 0,
    includedRepairs: 0,
    freePartsQuota: 0
  }
  addDialogVisible.value = true
}

async function submitForm() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    await addContract(form.value)
    ElMessage.success('合同提交成功')
    addDialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error(e?.msg || '提交失败')
  } finally {
    submitting.value = false
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getContractList()
    if (res.code === 200) list.value = res.rows || []
  } catch (e) {
    console.error('合同列表加载失败', e)
  } finally {
    loading.value = false
  }
}

function inspectionPct(c) {
  return c.includedInspectionsPerYear ? Math.min(100, Math.round((c.usedInspections / c.includedInspectionsPerYear) * 100)) : 0
}
function repairPct(c) {
  return c.includedRepairsPerYear ? Math.min(100, Math.round((c.usedRepairs / c.includedRepairsPerYear) * 100)) : 0
}
function contractStatusTag(c) {
  if (!c.endDate) return 'info'
  const diff = (new Date(c.endDate) - new Date()) / (1000 * 60 * 60 * 24)
  if (diff < 0) return 'info'
  if (diff <= 30) return 'danger'
  return 'success'
}
function contractStatusLabel(c) {
  if (!c.endDate) return '状态未知'
  const diff = (new Date(c.endDate) - new Date()) / (1000 * 60 * 60 * 24)
  if (diff < 0) return '已到期'
  if (diff <= 30) return '即将到期'
  return '生效中'
}
function formatDate(d) {
  if (!d) return '-'
  return d.substring ? d.substring(0, 10) : String(d).substring(0, 10)
}

onMounted(() => { loadData() })
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
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

.contract-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.contract-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 20px;
  cursor: pointer;
  box-shadow: var(--shadow-sm);
  transition: box-shadow 0.2s ease, transform 0.2s ease;
  border: 1px solid var(--border-light);
}

.contract-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.card-topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.card-period {
  font-size: 12px;
  color: var(--text-muted);
}

.contract-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.contract-no {
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 14px;
  font-family: monospace;
}

.contract-stats {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 14px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.stat-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-value {
  font-size: 20px;
  font-weight: 800;
  color: var(--text-primary);
  line-height: 1.2;
}

.stat-label {
  font-size: 12px;
  color: var(--text-muted);
}

.stat-divider {
  width: 1px;
  height: 36px;
  background: var(--border);
}

.quota-section {
  margin-top: 10px;
}

.quota-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 6px;
}

.quota-icon {
  font-size: 14px;
}

.quota-title {
  font-size: 13px;
  color: var(--text-secondary);
  flex: 1;
}

.quota-count {
  font-size: 12px;
  color: var(--text-muted);
}

.card-footer {
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid var(--border-light);
  display: flex;
  justify-content: flex-end;
}
</style>
