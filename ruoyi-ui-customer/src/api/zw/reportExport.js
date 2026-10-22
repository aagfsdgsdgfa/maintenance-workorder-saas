/**
 * 客户门户 - 报告导出 API
 */
import request from '@/utils/request'

/** 提交报告导出任务 */
export function exportReport(data) {
  return request({ url: '/zw/report/export', method: 'post', data })
}

/** 下载已生成的报告（Blob 流） */
export function downloadReport(exportId) {
  return request({ url: '/zw/report/download/' + exportId, method: 'get', responseType: 'blob' })
}

/** 查询报告导出历史 */
export function listReportExport(params) {
  return request({ url: '/zw/report/list', method: 'get', params })
}
