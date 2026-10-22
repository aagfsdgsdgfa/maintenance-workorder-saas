import request from '@/utils/request'

// 仪表盘真实接口（从数据库读取）
export function getDashboardSummary() {
  return request({ url: '/zw/dashboard/summary', method: 'get' })
}

export function getDashboardHealth() {
  return request({ url: '/zw/dashboard/health', method: 'get' })
}

export function getDashboardTrend() {
  return request({ url: '/zw/dashboard/trend', method: 'get' })
}

// 仪表盘 Mock 接口（保留备用）
export function getMockDashboardSummary() {
  return request({ url: '/zw/mock/dashboard/summary', method: 'get' })
}

export function getMockDashboardHealth() {
  return request({ url: '/zw/mock/dashboard/health', method: 'get' })
}

export function getMockDashboardTrend() {
  return request({ url: '/zw/mock/dashboard/trend', method: 'get' })
}

// 工单列表
export function listMockWorkOrder(query) {
  return request({ url: '/zw/mock/workOrder/list', method: 'get', params: query })
}

// 报修申请列表
export function listMockRepairApply(query) {
  return request({ url: '/zw/mock/repairApply/list', method: 'get', params: query })
}

// 巡检点列表
export function listMockInspectionPoint(query) {
  return request({ url: '/zw/mock/inspectionPoint/list', method: 'get', params: query })
}

// 资产列表
export function listMockAsset(query) {
  return request({ url: '/zw/mock/asset/list', method: 'get', params: query })
}

// 故障记录列表
export function listMockFaultRecord(query) {
  return request({ url: '/zw/mock/faultRecord/list', method: 'get', params: query })
}

// 日报列表
export function listMockDailyReport(query) {
  return request({ url: '/zw/mock/dailyReport/list', method: 'get', params: query })
}

// 巡检计划列表
export function listMockInspectionPlan(query) {
  return request({ url: '/zw/mock/inspectionPlan/list', method: 'get', params: query })
}

// 客户用户列表
export function listMockCustomerUser(query) {
  return request({ url: '/zw/mock/customerUser/list', method: 'get', params: query })
}

// 知识库列表
export function listMockKnowledgeBase(query) {
  return request({ url: '/zw/mock/knowledgeBase/list', method: 'get', params: query })
}

// 满意度列表
export function listMockSatisfaction(query) {
  return request({ url: '/zw/mock/satisfaction/list', method: 'get', params: query })
}

// 签到列表
export function listMockCheckin(query) {
  return request({ url: '/zw/mock/checkin/list', method: 'get', params: query })
}

// 通知列表
export function listMockNotification(query) {
  return request({ url: '/zw/mock/notification/list', method: 'get', params: query })
}
