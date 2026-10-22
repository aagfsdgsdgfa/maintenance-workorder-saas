/**
 * 客户门户 - 合同管理 API
 */
import request from '@/utils/request'

/** 获取合同列表 */
export function getContractList() {
  return request({ url: '/zw/customer/contract/list', method: 'get' })
}

/** 获取合同详情 */
export function getContractDetail(contractId) {
  return request({ url: '/zw/customer/contract/' + contractId, method: 'get' })
}

/** 新增合同 */
export function addContract(data) {
  return request({ url: '/zw/customer/contract/add', method: 'post', data })
}
