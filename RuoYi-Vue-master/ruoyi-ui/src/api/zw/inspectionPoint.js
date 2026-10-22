import request from '@/utils/request'

// 查询巡检点列表
export function listInspectionPoint(query) {
  return request({
    url: '/zw/inspectionPoint/list',
    method: 'get',
    params: query
  })
}

// 查询巡检点详细
export function getInspectionPoint(pointId) {
  return request({
    url: '/zw/inspectionPoint/' + pointId,
    method: 'get'
  })
}

// 新增巡检点
export function addInspectionPoint(data) {
  return request({
    url: '/zw/inspectionPoint',
    method: 'post',
    data: data
  })
}

// 修改巡检点
export function updateInspectionPoint(data) {
  return request({
    url: '/zw/inspectionPoint',
    method: 'put',
    data: data
  })
}

// 删除巡检点
export function delInspectionPoint(pointIds) {
  return request({
    url: '/zw/inspectionPoint/' + pointIds,
    method: 'delete'
  })
}

// 按资产查询巡检点
export function getPointsByAsset(assetId) {
  return request({
    url: '/zw/inspectionPoint/asset/' + assetId,
    method: 'get'
  })
}

// 启用/停用巡检点
export function toggleInspectionPoint(pointId) {
  return request({
    url: '/zw/inspectionPoint/' + pointId + '/toggle',
    method: 'put'
  })
}
