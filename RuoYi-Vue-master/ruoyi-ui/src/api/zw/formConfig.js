import request from '@/utils/request'

export function listFormConfig(query) {
  return request({ url: '/zw/formConfig/list', method: 'get', params: query })
}

export function getFormConfig(configId) {
  return request({ url: '/zw/formConfig/' + configId, method: 'get' })
}

export function addFormConfig(data) {
  return request({ url: '/zw/formConfig', method: 'post', data: data })
}

export function updateFormConfig(data) {
  return request({ url: '/zw/formConfig', method: 'put', data: data })
}

export function delFormConfig(configIds) {
  return request({ url: '/zw/formConfig/' + configIds, method: 'delete' })
}
