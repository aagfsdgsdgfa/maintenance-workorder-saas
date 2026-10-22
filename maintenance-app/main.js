import App from './App'
import { createSSRApp, reactive, readonly } from 'vue'

/**
 * 全局用户状态：基于 Vue3 reactive（兼容 uni-app 多端）。
 * 数据持久化用 uni.storage；不要 Pinia，因为没有 defineStore 模块。
 */
const state = reactive({
  token: uni.getStorageSync('token') || '',
  userInfo: uni.getStorageSync('userInfo') || null,
  captchaImg: '',
  captchaUuid: '',
  isLoggedIn: !!uni.getStorageSync('token')
})

const userStore = {
  state: readonly(state),

  setToken(token) {
    state.token = token
    state.isLoggedIn = !!token
    uni.setStorageSync('token', token)
  },

  setUserInfo(userInfo) {
    state.userInfo = userInfo
    uni.setStorageSync('userInfo', userInfo)
  },

  setCaptcha(img, uuid) {
    if (img.startsWith('data:')) {
      state.captchaImg = img
    } else if (img.startsWith('/9j/')) {
      state.captchaImg = 'data:image/jpeg;base64,' + img
    } else if (img.startsWith('iVBOR')) {
      state.captchaImg = 'data:image/png;base64,' + img
    } else if (img.length > 100) {
      state.captchaImg = 'data:image/jpeg;base64,' + img
    }
    state.captchaUuid = uuid
  },

  clearAuth() {
    state.token = ''
    state.userInfo = null
    state.isLoggedIn = false
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
  },

  getToken() {
    return state.token
  },

  getUserInfo() {
    return state.userInfo
  },

  getCaptcha() {
    return { img: state.captchaImg, uuid: state.captchaUuid }
  }
}

export function createApp() {
  const app = createSSRApp(App)
  app.config.globalProperties.$userStore = userStore
  return { app }
}

export { userStore }
