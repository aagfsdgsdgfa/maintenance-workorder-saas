import request from '@/utils/request'

// 查询工单列表
export function listWorkOrder(query) {
  return request({
    url: '/zw/workOrder/list',
    method: 'get',
    params: query
  })
}

// 查询工单详细
export function getWorkOrder(workOrderId) {
  return request({
    url: '/zw/workOrder/' + workOrderId,
    method: 'get'
  })
}

// 新增工单
export function addWorkOrder(data) {
  return request({
    url: '/zw/workOrder',
    method: 'post',
    data: data
  })
}

// 修改工单
export function updateWorkOrder(data) {
  return request({
    url: '/zw/workOrder',
    method: 'put',
    data: data
  })
}

// 删除工单
export function delWorkOrder(workOrderIds) {
  return request({
    url: '/zw/workOrder/' + workOrderIds,
    method: 'delete'
  })
}

// 工单状态流转
export function changeStatus(workOrderId, status) {
  return request({
    url: '/zw/workOrder/changeStatus',
    method: 'put',
    params: { workOrderId, status }
  })
}

// 工单派单
export function assignWorkOrder(workOrderId, engineerId) {
  return request({
    url: '/zw/workOrder/assign',
    method: 'put',
    params: { workOrderId, engineerId }
  })
}

// 导出工单
export function exportWorkOrder(query) {
  return request({
    url: '/zw/workOrder/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

// 导出工单Word文档
export function exportWorkOrderWord(id) {
  return request({
    url: '/zw/workOrder/exportWord/' + id,
    method: 'get',
    responseType: 'blob'
  })
}
