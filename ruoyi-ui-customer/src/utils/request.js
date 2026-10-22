import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/prod-api',
  timeout: 15000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('customer_token')
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  // 登录等无 token 请求不做重复提交限制
  if (config.headers.isToken === false) {
    config.headers['Authorization'] = ''
  }
  return config
})

request.interceptors.response.use(
  res => {
    const code = res.data?.code || 200
    if (code === 401) {
      localStorage.removeItem('customer_token')
      ElMessage.warning('登录已过期，请重新登录')
      router.push('/login')
      return Promise.reject({ code: 401, msg: '登录已过期' })
    } else if (code !== 200) {
      return Promise.reject(res.data)
    }
    return res.data
  },
  err => {
    // silent: true 时不弹错误消息，由业务侧自定义提示
    if (!err?.config?.silent) {
      const msg = err?.response?.data?.msg || err?.msg || err?.message || '网络请求失败'
      ElMessage.error(msg)
    }
    return Promise.reject(err)
  }
)

export default request