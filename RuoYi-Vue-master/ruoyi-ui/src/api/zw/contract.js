import request from '@/utils/request'

// 查询合同列表
export function listContract(query) {
  return request({
    url: '/zw/contract/list',
    method: 'get',
    params: query
  })
}

// 查询合同详细
export function getContract(contractId) {
  return request({
    url: '/zw/contract/' + contractId,
    method: 'get'
  })
}

// 新增合同
export function addContract(data) {
  return request({
    url: '/zw/contract',
    method: 'post',
    data: data
  })
}

// 修改合同
export function updateContract(data) {
  return request({
    url: '/zw/contract',
    method: 'put',
    data: data
  })
}

// 删除合同
export function delContract(contractIds) {
  return request({
    url: '/zw/contract/' + contractIds,
    method: 'delete'
  })
}

// OCR图片扫描 - 识别合同内容
export function ocrContractImage(data) {
  return request({
    url: '/zw/contract/ocrImage',
    method: 'post',
    data: data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// Excel扫描导入 - 解析合同Excel
export function parseContractExcel(data) {
  return request({
    url: '/zw/contract/parseExcel',
    method: 'post',
    data: data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
