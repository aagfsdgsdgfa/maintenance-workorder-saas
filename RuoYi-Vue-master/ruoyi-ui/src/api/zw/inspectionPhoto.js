import request from '@/utils/request'

export function listInspectionPhoto(query) {
  return request({ url: '/zw/inspectionPhoto/list', method: 'get', params: query })
}

export function getInspectionPhoto(photoId) {
  return request({ url: '/zw/inspectionPhoto/' + photoId, method: 'get' })
}

export function addInspectionPhoto(data) {
  return request({ url: '/zw/inspectionPhoto', method: 'post', data: data })
}

export function delInspectionPhoto(photoIds) {
  return request({ url: '/zw/inspectionPhoto/' + photoIds, method: 'delete' })
}
