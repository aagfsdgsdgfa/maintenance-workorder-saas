<template>
  <div class="login-page">
    <div class="login-box">
      <!-- 左侧品牌区 -->
      <div class="brand-area">
        <div class="brand-decoration">
          <div class="deco-circle deco-1"></div>
          <div class="deco-circle deco-2"></div>
          <div class="deco-circle deco-3"></div>
        </div>
        <img src="/img/小白人.png" alt="智维云服" class="brand-img" />
        <div class="brand-title">智维云服</div>
        <div class="brand-sub">客户门户</div>
        <div class="brand-desc">
          <div class="brand-feature">
            <div class="feature-dot"></div>
            专业 IT 维保服务管理
          </div>
          <div class="brand-feature">
            <div class="feature-dot"></div>
            全流程数字化运维
          </div>
          <div class="brand-feature">
            <div class="feature-dot"></div>
            7×24 小时技术支持
          </div>
        </div>
      </div>

      <!-- 右侧表单区 -->
      <div class="form-area">
        <div class="form-header">
          <h1 class="form-title">欢迎回来</h1>
          <p class="form-subtitle">请使用您的账号登录客户门户</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" size="large" @submit.native.prevent="handleLogin">
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              clearable
              autofocus
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
              clearable
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item prop="code" v-if="captchaEnabled">
            <el-input
              v-model="form.code"
              placeholder="请输入验证码"
              prefix-icon="CircleCheck"
              style="width:58%"
              maxlength="4"
              clearable
              @keyup.enter="handleLogin"
            />
            <div class="login-code">
              <img :src="codeUrl" @click="getCode" class="login-code-img" alt="验证码" />
            </div>
          </el-form-item>
          <el-form-item>
            <button
              type="button"
              class="btn-login"
              :class="{ 'is-loading': loading }"
              :disabled="loading"
              @click="handleLogin"
            >
              <span v-if="loading" class="loading-dots">登录中</span>
              <span v-else>登 录</span>
            </button>
          </el-form-item>
        </el-form>

        <div class="form-footer">
          <span class="footer-tip">使用管理员分配的系统账号登录</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getCodeImg } from '@/api/login'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const captchaEnabled = ref(true)
const codeUrl = ref('')
const form = ref({
  username: '',
  password: '',
  code: '',
  uuid: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'change' }]
}

function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled
    if (captchaEnabled.value) {
      codeUrl.value = 'data:image/gif;base64,' + res.img
      form.value.uuid = res.uuid
    }
  })
}

onMounted(() => {
  getCode()
})

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await request({
      url: '/login',
      method: 'post',
      headers: { isToken: false, repeatSubmit: false },
      data: {
        username: form.value.username,
        password: form.value.password,
        code: form.value.code,
        uuid: form.value.uuid
      }
    })
    if (res.code === 200) {
      const token = res.token || res.data?.token
      userStore.setToken(token)
      userStore.setUserInfo({
        userId: res.data?.userId,
        nickName: res.data?.nickName || form.value.username,
        customerId: res.data?.customerId,
        customerName: res.data?.customerName
      })
      ElMessage.success('登录成功')
      router.push('/home')
    } else {
      ElMessage.error(res.msg || '登录失败')
      if (captchaEnabled.value) getCode()
    }
  } catch (e) {
    ElMessage.error(e?.message || e?.msg || '登录失败，请检查账号密码')
    if (captchaEnabled.value) getCode()
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0F3460 0%, #1B4B8E 50%, #2563EB 100%);
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.login-page::before {
  content: '';
  position: absolute;
  top: -200px;
  right: -200px;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.15) 0%, transparent 70%);
  border-radius: 50%;
}

.login-page::after {
  content: '';
  position: absolute;
  bottom: -150px;
  left: -100px;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(0, 201, 167, 0.1) 0%, transparent 70%);
  border-radius: 50%;
}

.login-box {
  display: flex;
  width: 900px;
  max-width: 95vw;
  height: 540px;
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.35);
  position: relative;
  z-index: 1;
}

/* ============ 品牌区 ============ */
.brand-area {
  width: 380px;
  background: linear-gradient(160deg, #0F3460 0%, #1B4B8E 60%, #2563EB 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  padding: 48px 40px;
  position: relative;
  overflow: hidden;
  flex-shrink: 0;
}

.brand-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
}

.deco-1 {
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.04);
  top: -60px;
  right: -60px;
}

.deco-2 {
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.03);
  bottom: 40px;
  right: -30px;
}

.deco-3 {
  width: 80px;
  height: 80px;
  background: rgba(0, 201, 167, 0.1);
  bottom: -20px;
  left: 40px;
  border-radius: 50%;
}

.brand-img {
  width: 100px;
  height: auto;
  object-fit: contain;
  position: relative;
  z-index: 1;
  filter: drop-shadow(0 4px 16px rgba(0, 0, 0, 0.25));
  margin-bottom: 16px;
}

.brand-title {
  font-size: 28px;
  font-weight: 800;
  letter-spacing: 3px;
  position: relative;
  z-index: 1;
  background: linear-gradient(90deg, #fff, rgba(255,255,255,0.85));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.brand-sub {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  letter-spacing: 6px;
  margin-top: 4px;
  position: relative;
  z-index: 1;
}

.brand-desc {
  margin-top: 32px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  position: relative;
  z-index: 1;
}

.brand-feature {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.75);
}

.feature-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #00C9A7;
  flex-shrink: 0;
}

/* ============ 表单区 ============ */
.form-area {
  flex: 1;
  padding: 52px 48px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header {
  margin-bottom: 32px;
}

.form-title {
  font-size: 26px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 6px;
  background: linear-gradient(135deg, #1B4B8E, #2563EB);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.form-subtitle {
  font-size: 13px;
  color: #94a3b8;
}

.login-code {
  display: inline-block;
  vertical-align: middle;
  margin-left: 12px;
}

.login-code-img {
  height: 40px;
  border-radius: 8px;
  cursor: pointer;
  border: 1px solid #e2e8f0;
  transition: border-color 0.2s;
}

.login-code-img:hover {
  border-color: #1B4B8E;
}

/* 登录按钮 */
.btn-login {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #1B4B8E 0%, #2563EB 100%);
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  border: none;
  border-radius: 50px;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(27, 75, 142, 0.35);
  transition: all 0.2s;
  letter-spacing: 4px;
  font-family: inherit;
}

.btn-login:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 24px rgba(27, 75, 142, 0.4);
}

.btn-login:active:not(:disabled) {
  transform: translateY(0);
}

.btn-login:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.is-loading {
  opacity: 0.8;
}

.loading-dots::after {
  content: '...';
  animation: dots 1.2s infinite;
}

@keyframes dots {
  0% { content: ''; }
  33% { content: '.'; }
  66% { content: '..'; }
  100% { content: '...'; }
}

.form-footer {
  text-align: center;
  margin-top: 20px;
}

.footer-tip {
  font-size: 12px;
  color: #94a3b8;
}
</style>
