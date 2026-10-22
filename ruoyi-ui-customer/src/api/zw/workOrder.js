/**
 * 客户门户 - 工单 API（报修/完工/评价）
 */
import request from '@/utils/request'

/** 分页查询客户工单 */
export function getWorkOrderList(params) {
  return request({ url: '/zw/customer/workorder/list', method: 'get', params })
}

/** 分页查询报修申请 */
export function getRepairApplyList(params) {
  return request({ url: '/zw/customer/repairApply/list', method: 'get', params })
}

/** 获取工单详情 */
export function getWorkOrderDetail(workOrderId) {
  return request({ url: '/zw/customer/workorder/' + workOrderId, method: 'get' })
}

/** 获取报修申请详情 */
export function getRepairApplyDetail(applyId) {
  return request({ url: '/zw/repairApply/' + applyId, method: 'get' })
}

/** 提交报修申请 */
export function submitRepair(data) {
  return request({ url: '/zw/repairApply', method: 'post', data })
}

/** 提交满意度评价 */
export function submitSatisfaction(data) {
  return request({ url: '/zw/satisfaction', method: 'post', data })
}
