import request from '@/utils/request'

// 查询工程师列表
export function listEngineer(query) {
  return request({
    url: '/zw/engineer/list',
    method: 'get',
    params: query
  })
}

// 查询工程师详情
export function getEngineer(engineerId) {
  return request({
    url: '/zw/engineer/' + engineerId,
    method: 'get'
  })
}

// 新增工程师
export function addEngineer(data) {
  return request({
    url: '/zw/engineer',
    method: 'post',
    data: data
  })
}

// 修改工程师
export function updateEngineer(data) {
  return request({
    url: '/zw/engineer',
    method: 'put',
    data: data
  })
}

// 删除工程师
export function delEngineer(engineerId) {
  return request({
    url: '/zw/engineer/' + engineerId,
    method: 'delete'
  })
}

// 获取可派发工程师列表
export function listDispatchableEngineer(query) {
  return request({
    url: '/zw/engineer/dispatchable',
    method: 'get',
    params: query
  })
}

// 工单派发
export function dispatchWorkOrder(workOrderId, engineerId) {
  return request({
    url: '/zw/engineer/dispatch/' + workOrderId,
    method: 'post',
    data: { engineerId }
  })
}

// 工程师派发历史
export function dispatchHistory(engineerId, query) {
  return request({
    url: '/zw/engineer/dispatchHistory/' + engineerId,
    method: 'get',
    params: query
  })
}
