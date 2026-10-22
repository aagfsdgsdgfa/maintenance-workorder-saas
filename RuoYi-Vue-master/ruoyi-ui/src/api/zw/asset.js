import request from '@/utils/request'

// 查询资产列表
export function listAsset(query) {
  return request({
    url: '/zw/asset/list',
    method: 'get',
    params: query
  })
}

// 查询资产详细
export function getAsset(assetId) {
  return request({
    url: '/zw/asset/' + assetId,
    method: 'get'
  })
}

// 新增资产
export function addAsset(data) {
  return request({
    url: '/zw/asset',
    method: 'post',
    data: data
  })
}

// 修改资产
export function updateAsset(data) {
  return request({
    url: '/zw/asset',
    method: 'put',
    data: data
  })
}

// 删除资产
export function delAsset(assetIds) {
  return request({
    url: '/zw/asset/' + assetIds,
    method: 'delete'
  })
}

// 导出资产
export function exportAsset(query) {
  return request({
    url: '/zw/asset/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

// 扫码查询资产
export function scanAsset(qrCode) {
  return request({
    url: '/zw/asset/scan/' + qrCode,
    method: 'get'
  })
}
