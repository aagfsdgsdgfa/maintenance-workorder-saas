import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('customer_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('customer_user') || '{}'))

  function setToken(t) {
    token.value = t
    localStorage.setItem('customer_token', t)
  }

  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('customer_user', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('customer_token')
    localStorage.removeItem('customer_user')
  }

  return { token, userInfo, setToken, setUserInfo, logout }
})
