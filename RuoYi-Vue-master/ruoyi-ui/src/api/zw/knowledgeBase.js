import request from '@/utils/request'

export function listKnowledgeBase(query) {
  return request({ url: '/zw/knowledgeBase/list', method: 'get', params: query })
}

export function getKnowledgeBase(kbId) {
  return request({ url: '/zw/knowledgeBase/' + kbId, method: 'get' })
}

export function addKnowledgeBase(data) {
  return request({ url: '/zw/knowledgeBase', method: 'post', data: data })
}

export function updateKnowledgeBase(data) {
  return request({ url: '/zw/knowledgeBase', method: 'put', data: data })
}

export function delKnowledgeBase(kbIds) {
  return request({ url: '/zw/knowledgeBase/' + kbIds, method: 'delete' })
}

export function suggestKnowledgeBase(keyword) {
  return request({ url: '/zw/knowledgeBase/suggest', method: 'get', params: { keyword } })
}
