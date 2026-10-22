import request from '@/utils/request'

export function listSatisfaction(query) {
  return request({ url: '/zw/satisfaction/list', method: 'get', params: query })
}

export function getSatisfaction(evalId) {
  return request({ url: '/zw/satisfaction/' + evalId, method: 'get' })
}

export function addSatisfaction(data) {
  return request({ url: '/zw/satisfaction', method: 'post', data: data })
}

export function updateSatisfaction(data) {
  return request({ url: '/zw/satisfaction', method: 'put', data: data })
}

export function delSatisfaction(evalIds) {
  return request({ url: '/zw/satisfaction/' + evalIds, method: 'delete' })
}
