import request from '@/utils/request'

export function listNotification(query) {
  return request({ url: '/zw/notification/list', method: 'get', params: query })
}

export function getNotification(notifId) {
  return request({ url: '/zw/notification/' + notifId, method: 'get' })
}

export function addNotification(data) {
  return request({ url: '/zw/notification', method: 'post', data: data })
}

export function markRead(notifId) {
  return request({ url: '/zw/notification/read/' + notifId, method: 'put' })
}

export function delNotification(notifIds) {
  return request({ url: '/zw/notification/' + notifIds, method: 'delete' })
}
