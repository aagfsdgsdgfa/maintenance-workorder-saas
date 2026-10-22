import request from '@/utils/request'

export function listCheckin(query) {
  return request({ url: '/zw/checkin/list', method: 'get', params: query })
}

export function getCheckin(checkId) {
  return request({ url: '/zw/checkin/' + checkId, method: 'get' })
}

export function addCheckin(data) {
  return request({ url: '/zw/checkin', method: 'post', data: data })
}

export function delCheckin(ids) {
  return request({ url: '/zw/checkin/' + ids, method: 'delete' })
}

export function listCheckinByWorkOrder(workOrderId) {
  return request({ url: '/zw/checkin/listByWorkOrder/' + workOrderId, method: 'get' })
}