<template>
  <div class="login-wrapper">
    <!-- 左侧品牌区域 -->
    <div class="login-left">
      <div class="brand-area">
        <!-- Logo -->
        <div class="brand-logo">
          <img src="小蓝人.png" class="brand-logo-img" />
          <span class="brand-name">智维云服</span>
        </div>
        <!-- 标语 -->
        <div class="brand-slogan">
          <p class="slogan-line">智能运维&nbsp;&nbsp;·&nbsp;&nbsp;安全可控&nbsp;&nbsp;·&nbsp;&nbsp;高效便捷</p>
          <p class="slogan-sub">Intelligent Maintenance Cloud Service</p>
        </div>
        <!-- 装饰图形 -->
        <div class="deco-circles">
          <div class="deco-circle deco-circle-1"></div>
          <div class="deco-circle deco-circle-2"></div>
          <div class="deco-circle deco-circle-3"></div>
        </div>
        <div class="deco-lines">
          <div class="deco-line deco-line-1"></div>
          <div class="deco-line deco-line-2"></div>
          <div class="deco-line deco-line-3"></div>
        </div>
      </div>
    </div>

    <!-- 右侧表单区域 -->
    <div class="login-right">
      <div class="login-card">
        <!-- 系统名称 -->
        <div class="card-header">
          <h2 class="card-title">欢迎登录</h2>
          <p class="card-subtitle">智维云服 · 智能运维管理平台</p>
        </div>

        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" size="medium">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="请输入账号"
              prefix-icon="el-icon-user"
              clearable
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              auto-complete="off"
              placeholder="请输入密码"
              prefix-icon="el-icon-lock"
              show-password
              clearable
              @keyup.enter.native="handleLogin"
            />
          </el-form-item>
          <el-form-item prop="code" v-if="captchaEnabled">
            <el-input
              v-model="loginForm.code"
              auto-complete="off"
              placeholder="请输入验证码"
              prefix-icon="el-icon-circle-check"
              style="width: 58%"
              @keyup.enter.native="handleLogin"
              clearable
            />
            <div class="login-code">
              <img :src="codeUrl" @click="getCode" class="login-code-img" alt="验证码"/>
            </div>
          </el-form-item>
          <el-form-item>
            <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
          </el-form-item>
          <el-form-item>
            <el-button
              :loading="loading"
              size="medium"
              type="primary"
              class="login-btn"
              @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 底部版权 -->
      <div class="login-footer">
        <span>© {{ new Date().getFullYear() }} 智维云服 版权所有</span>
      </div>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from '@/utils/jsencrypt'
import defaultSettings from '@/settings'

export default {
  name: "Login",
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      footerContent: defaultSettings.footerContent,
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      captchaEnabled: true,
      register: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get("username")
      const password = Cookies.get("password")
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 })
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove("username")
            Cookies.remove("password")
            Cookies.remove('rememberMe')
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.login-wrapper {
  display: flex;
  width: 100%;
  height: 100vh;
  min-height: 600px;
}

/* ========== 左侧品牌区域 ========== */
.login-left {
  flex: 0 0 58%;
  background: linear-gradient(140deg, #0F1E3C 0%, #1B3A6B 45%, #0D4F8B 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand-area {
  position: relative;
  z-index: 2;
  text-align: center;
  padding: 0 40px;
}

.brand-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  margin-bottom: 40px;
}

.brand-logo-img {
  width: 48px;
  height: 48px;
  object-fit: contain;
  border-radius: 12px;
}

.brand-name {
  font-size: 36px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 4px;
  font-family: "PingFang SC", "Microsoft YaHei", sans-serif;
}

.brand-slogan {
  .slogan-line {
    font-size: 18px;
    color: rgba(255, 255, 255, 0.85);
    letter-spacing: 3px;
    margin: 0 0 10px;
  }
  .slogan-sub {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.45);
    letter-spacing: 2px;
    margin: 0;
  }
}

/* 装饰圆圈 */
.deco-circles {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.deco-circle {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(14, 165, 233, 0.15);
}
.deco-circle-1 {
  width: 320px; height: 320px;
  top: -80px; right: -80px;
  border-color: rgba(14, 165, 233, 0.12);
}
.deco-circle-2 {
  width: 200px; height: 200px;
  bottom: 60px; left: -60px;
  border-color: rgba(59, 130, 246, 0.15);
}
.deco-circle-3 {
  width: 120px; height: 120px;
  top: 40%; right: 15%;
  background: rgba(14, 165, 233, 0.05);
  border-color: rgba(14, 165, 233, 0.2);
}

/* 装饰线条 */
.deco-lines {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.deco-line {
  position: absolute;
  background: linear-gradient(90deg, transparent, rgba(14,165,233,0.2), transparent);
  height: 1px;
}
.deco-line-1 {
  width: 60%;
  top: 30%;
  left: 10%;
  transform: rotate(-15deg);
}
.deco-line-2 {
  width: 40%;
  top: 65%;
  right: 10%;
  transform: rotate(10deg);
}
.deco-line-3 {
  width: 30%;
  bottom: 25%;
  left: 20%;
  transform: rotate(-5deg);
  background: linear-gradient(90deg, transparent, rgba(59,130,246,0.2), transparent);
}

/* ========== 右侧表单区域 ========== */
.login-right {
  flex: 0 0 42%;
  background: #F0F2F8;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 40px 20px;
}

.login-card {
  width: 100%;
  max-width: 380px;
  background: #ffffff;
  border-radius: 16px;
  padding: 40px 36px 20px;
  box-shadow: 0 8px 40px rgba(27, 58, 107, 0.1);
}

.card-header {
  text-align: center;
  margin-bottom: 32px;
}
.card-title {
  font-size: 26px;
  font-weight: 700;
  color: #1B3A6B;
  margin: 0 0 8px;
  letter-spacing: 2px;
}
.card-subtitle {
  font-size: 13px;
  color: #6B7A99;
  margin: 0;
  letter-spacing: 1px;
}

/* 表单样式 */
.login-form {
  .el-form-item {
    margin-bottom: 20px;
  }
  .el-input {
    .el-input__inner {
      border-radius: 8px;
      border: 1.5px solid #E2E8F0;
      height: 42px;
      font-size: 14px;
      color: #1A1F36;
      transition: border-color 0.2s, box-shadow 0.2s;
      &::placeholder {
        color: #B0B8C8;
      }
      &:focus {
        border-color: #0EA5E9;
        box-shadow: 0 0 0 3px rgba(14, 165, 233, 0.12);
      }
    }
    .el-input__prefix {
      color: #6B7A99;
    }
  }
}

/* 验证码区域 */
.login-code {
  display: inline-block;
  vertical-align: middle;
  margin-left: 12px;
}
.login-code-img {
  height: 40px;
  border-radius: 6px;
  cursor: pointer;
  border: 1px solid #E2E8F0;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 44px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #0EA5E9 0%, #0284C7 100%);
  border: none;
  color: #ffffff;
  transition: all 0.3s;
  box-shadow: 0 4px 16px rgba(14, 165, 233, 0.35);
  &:hover {
    background: linear-gradient(135deg, #38BDF8 0%, #0EA5E9 100%);
    box-shadow: 0 6px 20px rgba(14, 165, 233, 0.45);
    transform: translateY(-1px);
  }
  &:active {
    transform: translateY(0);
    box-shadow: 0 2px 8px rgba(14, 165, 233, 0.3);
  }
  &.is-loading {
    background: linear-gradient(135deg, #0EA5E9 0%, #0284C7 100%);
  }
}

/* 底部版权 */
.login-footer {
  position: absolute;
  bottom: 24px;
  font-size: 12px;
  color: #6B7A99;
  letter-spacing: 1px;
  text-align: center;
}
</style>
