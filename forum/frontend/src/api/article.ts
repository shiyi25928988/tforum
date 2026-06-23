import request from '@/utils/request'

export interface Article {
  id: number
  title: string
  content: string
  summary: string
  coverImage: string
  categoryId: number
  authorId: number
  status: number
  viewCount: number
  likeCount: number
  commentCount: number
  isPinned: number
  tags: string
  createdTime: string
  updatedTime: string
}

export interface ArticleRequest {
  id?: number
  title: string
  content: string
  summary?: string
  coverImage?: string
  categoryId?: number
  status?: number
  isPinned?: number
  tags?: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export function getArticle(id: number) {
  return request.get(`/api/v1/article/${id}`)
}

export function listArticles(pageNum = 1, pageSize = 10, categoryId?: number) {
  return request.get('/api/v1/article/list', {
    params: { pageNum, pageSize, categoryId },
  })
}

export function searchArticles(pageNum = 1, pageSize = 10, keyword?: string, categoryId?: number) {
  return request.get('/api/v1/article/search', {
    params: { pageNum, pageSize, keyword, categoryId },
  })
}

export function saveArticle(data: ArticleRequest) {
  return request.post('/api/v1/article/save', data)
}

export function deleteArticle(id: number) {
  return request.post('/api/v1/article/delete', null, { params: { id } })
}

export function likeArticle(id: number) {
  return request.post('/api/v1/article/like', null, { params: { id } })
}

// ==========================================
// 文章标签
// ==========================================

export interface ArticleTag {
  id: number
  name: string
}

export function listArticleTags() {
  return request.get('/api/v1/article/tag/list')
}

export function saveArticleTag(name: string) {
  return request.post('/api/v1/article/tag/save', { name })
}

export function listHotArticles(limit = 10) {
  return request.get('/api/v1/article/hot', { params: { limit } })
}

// ==========================================
// 我的文章
// ==========================================

export function listMyArticles(pageNum = 1, pageSize = 10, status?: number) {
  return request.get('/api/v1/article/my', {
    params: { pageNum, pageSize, status },
  })
}

export function toggleArticleStatus(id: number) {
  return request.post('/api/v1/article/toggleStatus', null, { params: { id } })
}

// ==========================================
// AI 审核
// ==========================================

export interface AiReviewResponse {
  approved: boolean | null
  score: number
  feedback: string
  suggestions: string[]
  issues: string[]
}

export function reviewArticle(data: { title: string; content: string }) {
  return request.post('/api/v1/article/review', data)
}
