import request from '@/utils/request'

export interface ForumPost {
  id: number
  title: string
  content: string
  categoryId: number
  authorId: number
  viewCount: number
  commentCount: number
  createdTime: string
  updatedTime: string
}

export interface ForumPostRequest {
  id?: number
  title: string
  content: string
  categoryId?: number
}

export interface ForumComment {
  id: number
  postId: number
  content: string
  parentId: number
  authorId: number
  commentType: string
  replyTo: number
  createdTime: string
}

export interface ForumCommentRequest {
  id?: number
  postId: number
  content: string
  parentId?: number
  commentType?: string
  replyTo?: number
}

// 帖子
export function getPost(id: number) {
  return request.get(`/api/v1/forum/post/${id}`)
}

export function listPosts(pageNum = 1, pageSize = 10, categoryId?: number) {
  return request.get('/api/v1/forum/post/list', {
    params: { pageNum, pageSize, categoryId },
  })
}

export function savePost(data: ForumPostRequest) {
  return request.post('/api/v1/forum/post/save', data)
}

export function deletePost(id: number) {
  return request.post('/api/v1/forum/post/delete', null, { params: { id } })
}

// 评论
export function listComments(postId: number, commentType?: string) {
  return request.get(`/api/v1/forum/comment/${postId}`, {
    params: { commentType },
  })
}

export function saveComment(data: ForumCommentRequest) {
  return request.post('/api/v1/forum/comment/save', data)
}

export function deleteComment(id: number) {
  return request.post('/api/v1/forum/comment/delete', null, { params: { id } })
}

// ==========================================
// 讨论区分组
// ==========================================

export interface DiscussionCategory {
  id: number
  name: string
  description: string
  sortOrder: number
  topicCount: number
}

export function listDiscussionCategories() {
  return request.get('/api/v1/discussion/category/list')
}

export function saveDiscussionCategory(data: Partial<DiscussionCategory>) {
  return request.post('/api/v1/discussion/category/save', data)
}

export function deleteDiscussionCategory(id: number) {
  return request.post('/api/v1/discussion/category/delete', null, { params: { id } })
}
