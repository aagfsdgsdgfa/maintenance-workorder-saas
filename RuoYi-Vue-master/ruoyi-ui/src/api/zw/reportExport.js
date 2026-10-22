import request from '@/utils/request'

export function listReportExport(query) {
  return request({ url: '/zw/report/list', method: 'get', params: query })
}

export function getReportExport(exportId) {
  return request({ url: '/zw/report/' + exportId, method: 'get' })
}

export function addReportExport(data) {
  return request({ url: '/zw/report', method: 'post', data: data })
}

export function delReportExport(exportIds) {
  return request({ url: '/zw/report/' + exportIds, method: 'delete' })
}
