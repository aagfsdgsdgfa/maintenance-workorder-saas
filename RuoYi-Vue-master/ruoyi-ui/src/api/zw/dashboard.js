import request from '@/utils/request'

// 查询总览数据
export function getDashboardSummary() {
  return request({
    url: '/zw/dashboard/summary',
    method: 'get'
  })
}

// 查询健康度分布
export function getDashboardHealth() {
  return request({
    url: '/zw/dashboard/health',
    method: 'get'
  })
}

// 查询趋势数据
export function getDashboardTrend() {
  return request({
    url: '/zw/dashboard/trend',
    method: 'get'
  })
}
