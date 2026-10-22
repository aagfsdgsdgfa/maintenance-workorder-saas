import request from '@/utils/request'

// 巡检任务
export function listTask(query) {
  return request({ url: '/zw/inspection/task/list', method: 'get', params: query })
}

export function getTask(taskId) {
  return request({ url: '/zw/inspection/task/' + taskId, method: 'get' })
}

export function listTaskByWorkOrder(workOrderId) {
  return request({ url: '/zw/inspection/task/listByWorkOrder/' + workOrderId, method: 'get' })
}

export function addTask(data) {
  return request({ url: '/zw/inspection/task', method: 'post', data: data })
}

export function updateTask(data) {
  return request({ url: '/zw/inspection/task', method: 'put', data: data })
}

export function delTask(taskIds) {
  return request({ url: '/zw/inspection/task/' + taskIds, method: 'delete' })
}

export function executeTask(taskId, checkInLocation, checkInPhoto, checkInAddress) {
  return request({
    url: '/zw/inspection/task/execute',
    method: 'put',
    params: { taskId, checkInLocation, checkInPhoto, checkInAddress }
  })
}

export function completeTask(taskId) {
  return request({ url: '/zw/inspection/task/complete/' + taskId, method: 'put' })
}

export function batchAssign(taskIds, engineerId) {
  return request({
    url: '/zw/inspection/task/assign',
    method: 'put',
    data: { taskIds, engineerId }
  })
}

// 检查结果
export function listResult(query) {
  return request({ url: '/zw/inspection/result/list', method: 'get', params: query })
}

export function getResult(resultId) {
  return request({ url: '/zw/inspection/result/' + resultId, method: 'get' })
}

export function getResultByTask(taskId) {
  return request({ url: '/zw/inspection/result/byTask/' + taskId, method: 'get' })
}

export function addResult(data) {
  return request({ url: '/zw/inspection/result', method: 'post', data: data })
}

export function updateResult(data) {
  return request({ url: '/zw/inspection/result', method: 'put', data: data })
}

export function delResult(resultIds) {
  return request({ url: '/zw/inspection/result/' + resultIds, method: 'delete' })
}