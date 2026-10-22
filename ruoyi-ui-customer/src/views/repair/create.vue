<template>
  <div class="repair-create">
    <!-- 页面标题 -->
    <div class="page-header">
      <el-button text @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
      <div class="page-title">新建报修</div>
    </div>

    <!-- 步骤条 -->
    <div class="steps-wrapper">
      <el-steps :active="activeStep" finish-status="success" align-center>
        <el-step title="选择设备" description="选择故障设备" />
        <el-step title="故障描述" description="描述故障现象" />
        <el-step title="联系信息" description="填写联系方式" />
        <el-step title="提交完成" description="等待服务响应" />
      </el-steps>
    </div>

    <!-- 步骤1：选择设备 -->
    <div v-show="activeStep === 0" class="step-card fade-in-up">
      <div class="step-card-title">
        <el-icon style="color:var(--primary-light)"><Box /></el-icon>
        选择报修设备
      </div>
      <el-form label-width="100px" style="max-width:480px">
        <el-form-item label="选择设备">
          <el-select
            v-model="form.assetId"
            placeholder="请选择故障设备"
            filterable
            style="width:100%"
            @change="onAssetChange"
          >
            <el-option
              v-for="a in assetOptions"
              :key="a.assetId"
              :label="a.assetName + ' (' + a.assetCode + ')'"
              :value="a.assetId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="设备信息" v-if="form.assetId">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="设备名称">{{ selectedAsset?.assetName }}</el-descriptions-item>
            <el-descriptions-item label="SN码">{{ selectedAsset?.assetCode }}</el-descriptions-item>
            <el-descriptions-item label="安装位置">{{ selectedAsset?.location }}</el-descriptions-item>
            <el-descriptions-item label="品牌型号">{{ selectedAsset?.brand }} {{ selectedAsset?.model }}</el-descriptions-item>
          </el-descriptions>
        </el-form-item>
      </el-form>
    </div>

    <!-- 步骤2：故障描述 -->
    <div v-show="activeStep === 1" class="step-card fade-in-up">
      <div class="step-card-title">
        <el-icon style="color:var(--primary-light)"><WarnTriangleFilled /></el-icon>
        故障描述
      </div>
      <div class="form-layout">
        <el-form label-width="100px" style="flex:1">
          <el-form-item label="故障类型">
            <el-select v-model="form.faultType" placeholder="请选择故障类型" style="width:100%">
              <el-option label="无法开机" value="boot" />
              <el-option label="运行异常" value="runtime" />
              <el-option label="输出异常" value="output" />
              <el-option label="网络问题" value="network" />
              <el-option label="其他故障" value="other" />
            </el-select>
          </el-form-item>
          <el-form-item label="故障描述" required>
            <el-input
              v-model="form.faultDesc"
              type="textarea"
              :rows="5"
              placeholder="请详细描述故障现象，以便工程师做好准备"
              style="width:100%"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
          <el-form-item label="故障图片">
            <el-upload
              action="/api/common/upload"
              list-type="picture-card"
              :on-success="onUploadSuccess"
              :limit="5"
              :headers="{ Authorization: 'Bearer ' + token }"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </el-form-item>
        </el-form>

        <!-- 右侧提示卡 -->
        <div class="tips-card">
          <div class="tips-title">
            <el-icon><InfoFilled /></el-icon>报修提示
          </div>
          <ul class="tips-list">
            <li>请尽量详细描述故障现象和出现频率</li>
            <li>上传故障截图可大幅提升维修效率</li>
            <li>紧急故障请拨打客服热线：400-888-8888</li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 步骤3：联系人信息 -->
    <div v-show="activeStep === 2" class="step-card fade-in-up">
      <div class="step-card-title">
        <el-icon style="color:var(--primary-light)"><User /></el-icon>
        联系方式
      </div>
      <el-form label-width="100px" style="max-width:480px">
        <el-form-item label="联系人姓名" required>
          <el-input v-model="form.contactName" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" required>
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="联系地址">
          <el-input v-model="form.contactAddress" placeholder="详细地址（选填）" />
        </el-form-item>
        <el-form-item label="期望时间">
          <el-date-picker
            v-model="form.expectedTime"
            type="datetime"
            placeholder="选择期望上门时间（选填）"
            style="width:100%"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            :disabled-date="disabledDate"
          />
        </el-form-item>
      </el-form>
    </div>

    <!-- 步骤4：提交成功 -->
    <div v-show="activeStep === 3" class="step-card fade-in-up" style="text-align:center;padding:60px 40px">
      <div class="success-icon"><el-icon style="font-size:48px;color:var(--success)"><CircleCheck /></el-icon></div>
      <div class="success-title">提交成功！</div>
      <div class="success-desc">您的报修工单已提交，我们将在最短时间内安排工程师处理</div>
      <div style="display:flex;gap:12px;justify-content:center;margin-top:24px">
        <el-button type="primary" @click="$router.push('/repair/list')">查看工单</el-button>
        <el-button @click="resetForm">继续报修</el-button>
      </div>
    </div>

    <!-- 底部操作按钮 -->
    <div class="step-actions" v-if="activeStep < 3">
      <el-button @click="activeStep--" :disabled="activeStep === 0">
        <el-icon><ArrowLeft /></el-icon>上一步
      </el-button>
      <el-button type="primary" @click="nextStep">
        {{ activeStep === 2 ? '提交工单' : '下一步' }}
        <el-icon v-if="activeStep < 2"><ArrowRight /></el-icon>
        <el-icon v-else><Check /></el-icon>
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { submitRepair } from '@/api/zw/workOrder'
import { getAssetList } from '@/api/zw/asset'

const userStore = useUserStore()
const token = computed(() => userStore.token)

const activeStep = ref(0)
const assetOptions = ref([])
const selectedAsset = computed(() => assetOptions.value.find(a => a.assetId === form.value.assetId))

const form = ref({
  assetId: null, faultType: '', faultDesc: '', faultPhotos: '[]',
  contactName: '', contactPhone: '', contactAddress: '', expectedTime: ''
})

async function loadAssets() {
  try {
    const res = await getAssetList({ pageNum: 1, pageSize: 100 })
    if (res.code === 200 || res.code === 0) assetOptions.value = res.rows || []
  } catch (e) {
    console.error('设备列表加载失败', e)
  }
}

function onAssetChange(id) {
  const a = assetOptions.value.find(x => x.assetId === id)
  if (a) {
    form.value.contactName = userStore.userInfo.nickName || ''
  }
}

function onUploadSuccess(res) {
  if (res.code === 200) {
    let photos = form.value.faultPhotos === '[]' ? [] : JSON.parse(form.value.faultPhotos || '[]')
    photos.push(res.fileName)
    form.value.faultPhotos = JSON.stringify(photos)
  }
}

async function nextStep() {
  if (activeStep.value === 0 && !form.value.assetId) { ElMessage.warning('请先选择设备'); return }
  if (activeStep.value === 1 && !form.value.faultDesc) { ElMessage.warning('请填写故障描述'); return }
  if (activeStep.value === 2 && (!form.value.contactName || !form.value.contactPhone)) {
    ElMessage.warning('请填写联系人信息')
    return
  }
  if (activeStep.value === 2) {
    try {
      await submitRepair({
        assetId: form.value.assetId,
        applyTitle: form.value.faultDesc.substring(0, Math.min(50, form.value.faultDesc.length)),
        faultDesc: form.value.faultDesc,
        faultPhotos: form.value.faultPhotos || '[]',
        contactName: form.value.contactName,
        contactPhone: form.value.contactPhone,
        expectedTime: form.value.expectedTime || null
      })
      ElMessage.success('报修提交成功！工程师将尽快处理')
      activeStep.value = 3
    } catch {
      ElMessage.error('提交失败，请重试')
    }
    return
  }
  activeStep.value++
}

function resetForm() {
  form.value = { assetId: null, faultType: '', faultDesc: '', faultPhotos: '[]', contactName: '', contactPhone: '', contactAddress: '', expectedTime: '' }
  activeStep.value = 0
}

function disabledDate(date) {
  return date.getTime() < Date.now() - 8.64e7 // 禁用今天之前的日期
}

onMounted(() => { loadAssets() })
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

.steps-wrapper {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 24px 32px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-sm);
}

.step-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
  min-height: 320px;
}

.step-card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.form-layout {
  display: flex;
  gap: 20px;
}

.tips-card {
  width: 220px;
  background: linear-gradient(135deg, #eff6ff, #f0f9ff);
  border: 1px solid #dbeafe;
  border-radius: var(--radius-lg);
  padding: 16px;
  flex-shrink: 0;
}

.tips-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--primary-light);
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.tips-list {
  padding-left: 16px;
  margin: 0;
}

.tips-list li {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.8;
}

.success-icon {
  font-size: 56px;
  margin-bottom: 16px;
}

.success-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.success-desc {
  font-size: 14px;
  color: var(--text-secondary);
  max-width: 400px;
  margin: 0 auto;
  line-height: 1.6;
}

.step-actions {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 16px;
}
</style>
