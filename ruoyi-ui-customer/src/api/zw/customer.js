/**
 * 客户门户 - 客户 Dashboard API
 */
import request from '@/utils/request'

/** 获取客户 Dashboard 汇总数据 */
export function getCustomerDashboard() {
  return request({ url: '/zw/dashboard/customer', method: 'get' })
}
