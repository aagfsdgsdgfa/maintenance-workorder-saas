/**
 * 客户门户 - 资产管理 API
 */
import request from '@/utils/request'

/** 分页查询客户资产 */
export function getAssetList(params) {
  return request({ url: '/zw/customer/asset/list', method: 'get', params })
}

/** 获取资产详情 */
export function getAssetDetail(assetId) {
  return request({ url: '/zw/customer/asset/' + assetId, method: 'get' })
}

/** 获取资产维保日志 */
export function getAssetLogs(assetId) {
  return request({ url: '/zw/customer/asset/' + assetId + '/logs', method: 'get' })
}
