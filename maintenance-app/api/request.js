/**
 * API 请求封装
 * - 基础地址由 VITE_API_BASE 注入（dev: localhost:8080；prod: 反向代理 /prod-api）
 * - 所有业务路径以 /zw/ 开头；后端 context-path=/，无需再加前缀
 */

const BASE_URL = import.meta.env.VITE_API_BASE

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token') || ''

    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      },
      success: (res) => {
        if (res.statusCode === 401) {
          uni.removeStorageSync('token')
          uni.removeStorageSync('userInfo')
          uni.reLaunch({ url: '/pages/login/login' })
          reject({ code: 401, msg: '登录已过期，请重新登录' })
          return
        }
        if (res.statusCode !== 200) {
          reject(res.data || { code: res.statusCode, msg: '请求失败' })
          return
        }
        resolve(res.data)
      },
      fail: (err) => {
        uni.showToast({ title: '网络请求失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

export default request

// ============ 登录模块 ============
export const login = (data) => request({
  url: '/zw/login',
  method: 'POST',
  data
})

export const getLoginInfo = () => request({
  url: '/zw/login/info',
  method: 'GET'
})

export const logout = () => request({
  url: '/zw/logout',
  method: 'POST'
})

// 验证码（无鉴权）
export const getCaptcha = () => request({
  url: '/captchaImage',
  method: 'GET'
})

// ============ 工单模块 ============
export const getMyTasks = (params) => request({
  url: '/der/myTasks',
  method: 'GET',
  data: params
})

export const getWorkOrderDetail = (id) => request({
  url: `/zw/workOrder/${id}`,
  method: 'GET'
})

export const acceptWorkOrder = (id) => request({
  url: `/zw/workOrder/accept/${id}`,
  method: 'PUT'
})

export const rejectWorkOrder = (id, data) => request({
  url: `/zw/workOrder/reject/${id}`,
  method: 'PUT',
  data
})

export const startWorkOrder = (id) => request({
  url: `/zw/workOrder/start/${id}`,
  method: 'PUT'
})

export const completeWorkOrder = (id, data) => request({
  url: `/zw/workOrder/complete/${id}`,
  method: 'PUT',
  data
})

export const getCheckInList = (params) => request({
  url: '/zw/checkin/list',
  method: 'GET',
  data: params
})

export const getWorkOrderCheckIns = (workOrderId) => request({
  url: `/zw/checkin/listByWorkOrder/${workOrderId}`,
  method: 'GET'
})

// ============ 巡检模块 ============
export const getInspectionTaskList = (params) => request({
  url: '/zw/inspection/task/list',
  method: 'GET',
  data: params
})

export const getInspectionTaskDetail = (taskId) => request({
  url: `/zw/inspection/task/${taskId}`,
  method: 'GET'
})

export const getInspectionTemplate = (assetId) => request({
  url: `/zw/inspection/template/${assetId}`,
  method: 'GET'
})

export const submitInspectionResult = (data) => request({
  url: '/zw/inspection/result',
  method: 'POST',
  data
})

export const getInspectionPointsByAsset = (assetId) => request({
  url: `/zw/inspectionPoint/asset/${assetId}`,
  method: 'GET'
})

export const executeInspectionTask = (data) => request({
  url: '/zw/inspection/task/execute',
  method: 'PUT',
  data
})

export const verifyInspectionPoint = (id) => request({
  url: `/zw/inspectionPoint/${id}/verify`,
  method: 'PUT'
})

// ============ GPS 打卡 ============
export const createCheckIn = (data) => request({
  url: '/zw/checkin',
  method: 'POST',
  data
})

// ============ 故障 ============
export const createFaultRecord = (data) => request({
  url: '/zw/faultRecord',
  method: 'POST',
  data
})

export const resolveFaultRecord = (id) => request({
  url: `/zw/faultRecord/${id}/resolve`,
  method: 'PUT'
})

export const getFaultDetail = (id) => request({
  url: `/zw/faultRecord/${id}`,
  method: 'GET'
})

// ============ 日报 ============
export const getDailyReportList = (params) => request({
  url: '/zw/dailyReport/list',
  method: 'GET',
  data: params
})

export const generateDailyReport = (data) => request({
  url: '/zw/dailyReport/generate',
  method: 'POST',
  data
})

export const submitDailyReport = (data) => request({
  url: '/zw/dailyReport',
  method: 'POST',
  data
})

export const getDailyReportDetail = (id) => request({
  url: `/zw/dailyReport/${id}`,
  method: 'GET'
})

// ============ 知识库 ============
export const searchKnowledgeBase = (keyword) => request({
  url: `/zw/knowledgeBase/search?keyword=${encodeURIComponent(keyword)}`,
  method: 'GET'
})

export const getKnowledgeDetail = (kbId) => request({
  url: `/zw/knowledgeBase/${kbId}`,
  method: 'GET'
})

// ============ 消息通知 ============
export const getNotificationList = (params) => request({
  url: '/zw/notification/list',
  method: 'GET',
  data: params
})

export const getUnreadCount = () => request({
  url: '/zw/notification/unreadCount',
  method: 'GET'
})

export const markNotificationRead = (id) => request({
  url: `/zw/notification/read/${id}`,
  method: 'PUT'
})

export const markAllNotificationRead = () => request({
  url: '/zw/notification/markAllRead',
  method: 'PUT'
})

// ============ 资产 ============
export const scanAsset = (qrCode) => request({
  url: `/zw/asset/scan/${qrCode}`,
  method: 'GET'
})

// ============ 文件上传（uni.uploadFile 走全 URL） ============
export const uploadFile = (filePath) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token') || ''
    uni.uploadFile({
      url: BASE_URL + '/common/upload',
      filePath: filePath,
      name: 'file',
      header: {
        'Authorization': `Bearer ${token}`
      },
      success: (res) => {
        const data = JSON.parse(res.data)
        if (data.code === 200) {
          resolve(data)
        } else {
          reject(data)
        }
      },
      fail: reject
    })
  })
}

// ============ 配件 ============
export const getPartList = () => request({
  url: '/zw/part/list',
  method: 'GET'
})

// ============ 用户 ============
export const updateUserProfile = (data) => request({
  url: '/zw/user/profile',
  method: 'PUT',
  data
})

export const changePassword = (data) => request({
  url: '/zw/user/password',
  method: 'PUT',
  data
})

// ============ AI 语音转文字 ============
export const voiceToText = (filePath) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token') || ''
    uni.uploadFile({
      url: BASE_URL + '/ai/voice/toText',
      filePath: filePath,
      name: 'file',
      header: {
        'Authorization': `Bearer ${token}`
      },
      success: (res) => {
        const data = JSON.parse(res.data)
        if (data.code === 200) {
          resolve(data)
        } else {
          reject(data)
        }
      },
      fail: reject
    })
  })
}
