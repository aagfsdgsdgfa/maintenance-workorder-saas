import request from '@/utils/request'

export function listPart(query) {
  return request({ url: '/zw/part/list', method: 'get', params: query })
}

export function getPart(partId) {
  return request({ url: '/zw/part/' + partId, method: 'get' })
}

export function addPart(data) {
  return request({ url: '/zw/part', method: 'post', data: data })
}

export function updatePart(data) {
  return request({ url: '/zw/part', method: 'put', data: data })
}

export function delPart(partIds) {
  return request({ url: '/zw/part/' + partIds, method: 'delete' })
}
