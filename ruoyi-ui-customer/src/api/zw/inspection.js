/**
 * 客户门户 - 巡检 API
 */
import request from '@/utils/request'

/** 分页查询客户巡检任务 */
export function getInspectionList(params) {
  return request({ url: '/zw/customer/inspection/list', method: 'get', params })
}

/** 获取巡检报告 */
export function getInspectionReport(taskId, customerId = 1) {
  return request({ url: '/zw/customer/inspection/' + taskId + '/report', method: 'get', params: { customerId } })
}
