import request from '@/utils/request'

export interface IndexedDocument {
  id: number
  title: string
  content: string
  url: string
  createTime: string
}

export function globalSearch(keyword: string, pageNum = 1, pageSize = 10) {
  return request.get('/api/v1/search', {
    params: { keyword, pageNum, pageSize },
  })
}
