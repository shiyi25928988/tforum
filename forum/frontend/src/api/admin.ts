import request from '@/utils/request'

export interface DashboardStats {
  userCount: number
  articleCount: number
  postCount: number
  bookCount: number
}

export function getDashboard() {
  return request.get('/api/v1/admin/dashboard')
}

export function listUsers() {
  return request.get('/api/v1/admin/users')
}

export function toggleUserStatus(id: number) {
  return request.post('/api/v1/admin/user/toggleStatus', null, { params: { id } })
}

export function adminListArticles() {
  return request.get('/api/v1/admin/articles')
}

export function adminDeleteArticle(id: number) {
  return request.post('/api/v1/admin/article/delete', null, { params: { id } })
}

export function adminTogglePinArticle(id: number) {
  return request.post('/api/v1/admin/article/togglePin', null, { params: { id } })
}

export function adminListPosts() {
  return request.get('/api/v1/admin/posts')
}

export function adminDeletePost(id: number) {
  return request.post('/api/v1/admin/post/delete', null, { params: { id } })
}

export function adminUpdatePost(data: { id: number; title: string; content: string }) {
  return request.post('/api/v1/admin/post/update', data)
}

export function adminListBooks() {
  return request.get('/api/v1/admin/books')
}

export function adminDeleteBook(id: number) {
  return request.post('/api/v1/admin/book/delete', null, { params: { id } })
}

// ==========================================
// 标签管理
// ==========================================

export function adminListTags() {
  return request.get('/api/v1/admin/tags')
}

export function adminSaveTag(name: string) {
  return request.post('/api/v1/admin/tag/save', { name })
}

export function adminDeleteTag(id: number) {
  return request.post('/api/v1/admin/tag/delete', null, { params: { id } })
}

// ==========================================
// Milvus 管理
// ==========================================

export function createMilvusCollection() {
  return request.post('/api/v1/milvus/createCollectionIfNotExists')
}

export function dropAndRecreateMilvusCollection() {
  return request.post('/api/v1/milvus/dropAndCreateCollection')
}

export function uploadDocToMilvus(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/v1/admin/milvus/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

// 讨论区分组管理
export interface DiscussionCategory {
  id?: number
  name: string
  description?: string
  sortOrder?: number
  topicCount?: number
}

export function adminListDiscussionCategories() {
  return request.get('/api/v1/discussion/category/list')
}

export function adminSaveDiscussionCategory(data: DiscussionCategory) {
  return request.post('/api/v1/discussion/category/save', data)
}

export function adminDeleteDiscussionCategory(id: number) {
  return request.post('/api/v1/discussion/category/delete', null, { params: { id } })
}
