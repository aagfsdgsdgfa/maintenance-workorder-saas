import request from '@/utils/request'

export function listKbCategory(query) {
  return request({ url: '/zw/kbCategory/list', method: 'get', params: query })
}

export function getKbCategory(categoryId) {
  return request({ url: '/zw/kbCategory/' + categoryId, method: 'get' })
}

export function addKbCategory(data) {
  return request({ url: '/zw/kbCategory', method: 'post', data: data })
}

export function updateKbCategory(data) {
  return request({ url: '/zw/kbCategory', method: 'put', data: data })
}

export function delKbCategory(categoryIds) {
  return request({ url: '/zw/kbCategory/' + categoryIds, method: 'delete' })
}

export function listAllKbCategory() {
  return request({ url: '/zw/kbCategory/all', method: 'get' })
}
