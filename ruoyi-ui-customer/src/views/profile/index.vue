<template>
  <div class="profile-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-title">个人中心</div>
    </div>

    <div class="profile-layout">
      <!-- 左侧：用户信息卡片 -->
      <div class="profile-card info-card">
        <!-- 头像区 -->
        <div class="profile-hero">
          <div class="avatar-ring">
            <el-avatar :size="72" style="background: var(--primary-gradient); font-size: 28px; font-weight: 700;">
              {{ userInfo.nickName?.[0]?.toUpperCase() || 'U' }}
            </el-avatar>
          </div>
          <div class="profile-meta">
            <div class="profile-name">{{ userInfo.nickName || '用户' }}</div>
            <div class="profile-badges">
              <span class="badge badge-primary">客户管理员</span>
              <span class="badge badge-success">已认证</span>
            </div>
          </div>
        </div>

        <!-- 账户信息 -->
        <div class="info-section-title">
          <el-icon><User /></el-icon>账户信息
        </div>
        <div class="info-list">
          <div class="info-row" v-for="item in infoItems" :key="item.label">
            <span class="info-label">{{ item.label }}</span>
            <span class="info-value">{{ item.value }}</span>
          </div>
        </div>
      </div>

      <!-- 右侧：操作卡片 -->
      <div class="profile-card actions-card">
        <div class="info-section-title">
          <el-icon><Setting /></el-icon>账户设置
        </div>

        <div class="action-card" @click="showPwdDialog = true">
          <div class="action-icon-wrap">
            <el-icon class="action-icon" style="color: var(--primary-light)"><Lock /></el-icon>
          </div>
          <div class="action-content">
            <span class="action-title">修改密码</span>
            <span class="action-desc">更新登录密码，保障账户安全</span>
          </div>
          <el-icon class="action-arrow"><ArrowRight /></el-icon>
        </div>

        <div class="action-card" @click="handleLogout">
          <div class="action-icon-wrap" style="background: #fef2f2;">
            <el-icon class="action-icon" style="color: var(--danger);"><SwitchButton /></el-icon>
          </div>
          <div class="action-content">
            <span class="action-title">退出登录</span>
            <span class="action-desc">退出当前账户，返回登录页</span>
          </div>
          <el-icon class="action-arrow"><ArrowRight /></el-icon>
        </div>

        <!-- 帮助提示 -->
        <div class="help-tip">
          <el-icon style="color: var(--warning)"><QuestionFilled /></el-icon>
          <span>如有疑问，请联系客服热线 <strong>400-888-8888</strong></span>
        </div>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="showPwdDialog" title="修改密码" width="420px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="pwdForm" :rules="rules" label-position="top">
        <el-form-item label="原密码" prop="oldPwd">
          <el-input v-model="pwdForm.oldPwd" type="password" placeholder="请输入原密码" show-password size="large" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPwd">
          <el-input v-model="pwdForm.newPwd" type="password" placeholder="请输入新密码（至少6位）" show-password size="large" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPwd">
          <el-input v-model="pwdForm.confirmPwd" type="password" placeholder="请再次输入新密码" show-password size="large" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPwdDialog = false" size="large">取消</el-button>
        <el-button type="primary" @click="handleChangePwd" size="large">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const userInfo = userStore.userInfo

const showPwdDialog = ref(false)
const formRef = ref(null)

const pwdForm = reactive({ oldPwd: '', newPwd: '', confirmPwd: '' })

const validateConfirm = (rule, value, callback) => {
  if (value !== pwdForm.newPwd) callback(new Error('两次输入的密码不一致'))
  else callback()
}

const rules = {
  oldPwd: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPwd: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPwd: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

const infoItems = [
  { label: '用户ID', value: userInfo.userId || '—' },
  { label: '用户名', value: userInfo.userName || userInfo.nickName || '—' },
  { label: '公司名称', value: userInfo.customerName || '—' },
  { label: '客户ID', value: userInfo.customerId || '—' }
]

function handleChangePwd() {
  formRef.value?.validate((valid) => {
    if (!valid) return
    ElMessage.success('密码修改成功（演示）')
    showPwdDialog.value = false
    Object.assign(pwdForm, { oldPwd: '', newPwd: '', confirmPwd: '' })
  })
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
  ElMessage.success('已退出登录')
}
</script>

<style scoped>
.page-header {
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

.profile-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  max-width: 960px;
}

.profile-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
}

.profile-hero {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border-light);
  margin-bottom: 20px;
}

.avatar-ring {
  padding: 3px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-light), var(--primary));
}

.profile-name {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.profile-badges {
  display: flex;
  gap: 8px;
}

.badge {
  font-size: 12px;
  padding: 2px 10px;
  border-radius: var(--radius-full);
}

.badge-primary {
  background: #eff6ff;
  color: var(--primary-light);
}

.badge-success {
  background: #f0fdf4;
  color: var(--success);
  border: 1px solid var(--success);
}

.info-section-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid var(--border-light);
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 13px;
  color: var(--text-muted);
}

.info-value {
  font-size: 13px;
  color: var(--text-primary);
  font-weight: 500;
}

.action-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  border: 1px solid var(--border-light);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 12px;
}

.action-card:hover {
  border-color: var(--primary-light);
  background: #f8fbff;
}

.action-card:last-of-type:hover {
  border-color: var(--danger);
  background: #fef2f2;
}

.action-icon-wrap {
  width: 44px;
  height: 44px;
  border-radius: var(--radius-md);
  background: #eff6ff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.action-icon {
  font-size: 20px;
}

.action-content {
  flex: 1;
}

.action-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  display: block;
  margin-bottom: 2px;
}

.action-desc {
  font-size: 12px;
  color: var(--text-muted);
}

.action-arrow {
  color: var(--text-muted);
  transition: transform 0.2s;
  font-size: 16px;
}

.action-card:hover .action-arrow {
  transform: translateX(4px);
  color: var(--primary-light);
}

.help-tip {
  margin-top: 20px;
  padding: 12px 16px;
  background: #fffbeb;
  border-radius: var(--radius-md);
  font-size: 13px;
  color: #92400e;
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
