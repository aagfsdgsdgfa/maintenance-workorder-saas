import request from '@/utils/request'

export function listCustomerUser(query) {
  return request({ url: '/zw/customerUser/list', method: 'get', params: query })
}

export function getCustomerUser(custUserId) {
  return request({ url: '/zw/customerUser/' + custUserId, method: 'get' })
}

export function addCustomerUser(data) {
  return request({ url: '/zw/customerUser', method: 'post', data: data })
}

export function updateCustomerUser(data) {
  return request({ url: '/zw/customerUser', method: 'put', data: data })
}

export function delCustomerUser(custUserIds) {
  return request({ url: '/zw/customerUser/' + custUserIds, method: 'delete' })
}
