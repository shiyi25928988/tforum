import request from '@/utils/request'

export interface MarkdownDoc {
  id: number
  title: string
  content: string
  authorId: number
  viewCount: number
  tags: string
  createdTime: string
  updatedTime: string
}

export interface MarkdownDocRequest {
  id?: number
  title: string
  content: string
  tags?: string
}

export function getMarkdownDoc(id: number) {
  return request.get(`/api/v1/markdown/${id}`)
}

export function listMarkdownDocs(pageNum = 1, pageSize = 10, keyword?: string) {
  return request.get('/api/v1/markdown/list', {
    params: { pageNum, pageSize, keyword },
  })
}

export function saveMarkdownDoc(data: MarkdownDocRequest) {
  return request.post('/api/v1/markdown/save', data)
}

export function deleteMarkdownDoc(id: number) {
  return request.post('/api/v1/markdown/delete', null, { params: { id } })
}
