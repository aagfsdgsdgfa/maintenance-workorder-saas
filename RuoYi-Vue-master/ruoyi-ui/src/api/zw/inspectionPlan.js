import request from '@/utils/request'

export function listInspectionPlan(query) {
  return request({ url: '/zw/inspectionPlan/list', method: 'get', params: query })
}

export function getInspectionPlan(planId) {
  return request({ url: '/zw/inspectionPlan/' + planId, method: 'get' })
}

export function addInspectionPlan(data) {
  return request({ url: '/zw/inspectionPlan', method: 'post', data: data })
}

export function updateInspectionPlan(data) {
  return request({ url: '/zw/inspectionPlan', method: 'put', data: data })
}

export function delInspectionPlan(planIds) {
  return request({ url: '/zw/inspectionPlan/' + planIds, method: 'delete' })
}
