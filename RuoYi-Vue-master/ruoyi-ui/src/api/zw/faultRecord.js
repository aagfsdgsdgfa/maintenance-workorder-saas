import request from '@/utils/request'

export function listFaultRecord(query) {
  return request({ url: '/zw/faultRecord/list', method: 'get', params: query })
}

export function getFaultRecord(faultId) {
  return request({ url: '/zw/faultRecord/' + faultId, method: 'get' })
}

export function addFaultRecord(data) {
  return request({ url: '/zw/faultRecord', method: 'post', data: data })
}

export function updateFaultRecord(data) {
  return request({ url: '/zw/faultRecord', method: 'put', data: data })
}

export function delFaultRecord(faultIds) {
  return request({ url: '/zw/faultRecord/' + faultIds, method: 'delete' })
}

export function toWorkOrder(faultId) {
  return request({ url: '/zw/faultRecord/toWorkOrder/' + faultId, method: 'post' })
}

export function addFaultRecordWithWorkOrder(data, assignedTo) {
  return request({ url: '/zw/faultRecord/withWorkOrder', method: 'post', params: { assignedTo }, data })
}
