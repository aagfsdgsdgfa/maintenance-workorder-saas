/**
 * 客户门户 - 消息通知 API
 */
import request from '@/utils/request'

/** 分页查询消息通知 */
export function getNotificationList(params) {
  return request({ url: '/zw/notification/list', method: 'get', params })
}

/** 标记单条已读 */
export function markRead(notifId) {
  return request({ url: '/zw/notification/read/' + notifId, method: 'put' })
}

/** 获取未读消息总数 */
export function getUnreadCount() {
  return request({ url: '/zw/notification/unreadCount', method: 'get' })
}

/** 全部标记已读 */
export function markAllRead() {
  return request({ url: '/zw/notification/markAllRead', method: 'put' })
}
