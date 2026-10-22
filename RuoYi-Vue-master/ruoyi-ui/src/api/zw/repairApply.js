import request from '@/utils/request'

// 查询报修申请列表
export function listRepairApply(query) {
  return request({
    url: '/zw/repairApply/admin/list',
    method: 'get',
    params: query
  })
}

// 查询报修申请详细
export function getRepairApply(applyId) {
  return request({
    url: '/zw/repairApply/admin/' + applyId,
    method: 'get'
  })
}

// 新增报修申请
export function addRepairApply(data) {
  return request({
    url: '/zw/repairApply/admin',
    method: 'post',
    data: data
  })
}

// 修改报修申请
export function updateRepairApply(data) {
  return request({
    url: '/zw/repairApply/admin',
    method: 'put',
    data: data
  })
}

// 删除报修申请
export function delRepairApply(applyIds) {
  return request({
    url: '/zw/repairApply/admin/' + applyIds,
    method: 'delete'
  })
}

// 受理报修
export function acceptRepairApply(applyId) {
  return request({
    url: '/zw/repairApply/admin/' + applyId + '/accept',
    method: 'put'
  })
}

// 转工单
export function transferRepairApply(applyId, assignedTo, priority) {
  return request({
    url: '/zw/repairApply/admin/' + applyId + '/transfer',
    method: 'put',
    params: { assignedTo, priority }
  })
}

// 受理并派发工程师（一键，支持批量）
export function acceptAndTransferBatchRepairApply(applyIds, assignedTo) {
  return request({
    url: '/zw/repairApply/admin/acceptAndTransfer/batch',
    method: 'post',
    data: applyIds,
    params: { assignedTo }
  })
}

// 兼容旧名
export const acceptAndTransferRepairApply = acceptAndTransferBatchRepairApply

// 批量删除报修申请
export function batchDeleteRepairApply(applyIds) {
  return request({
    url: '/zw/repairApply/admin/' + applyIds,
    method: 'delete'
  })
}
