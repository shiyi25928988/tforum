import request from '@/utils/request'

export interface Book {
  id: number
  title: string
  author: string
  description: string
  coverImage: string
  fileUrl: string
  fileSize: number
  categoryId: number
  uploaderId: number
  downloadCount: number
  viewCount: number
  createdTime: string
}

export function getBook(id: number) {
  return request.get(`/api/v1/book/${id}`)
}

export function listBooks(pageNum = 1, pageSize = 12, categoryId?: number, keyword?: string) {
  return request.get('/api/v1/book/list', {
    params: { pageNum, pageSize, categoryId, keyword },
  })
}

export function uploadBook(file: File, title: string, author?: string, description?: string, categoryId?: number, coverImage?: File) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('title', title)
  if (author) formData.append('author', author)
  if (description) formData.append('description', description)
  if (categoryId) formData.append('categoryId', String(categoryId))
  if (coverImage) formData.append('coverImage', coverImage)
  return request.post('/api/v1/book/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function deleteBook(id: number) {
  return request.post('/api/v1/book/delete', null, { params: { id } })
}

export function downloadBook(id: number) {
  return request.post('/api/v1/book/download', null, { params: { id } })
}
