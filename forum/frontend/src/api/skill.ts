import request from '@/utils/request'

export interface Skill {
  id: number
  name: string
  description: string
  content: string
  iconUrl: string
  attachmentUrl: string
  category: string
  authorId: number
  downloadCount: number
  viewCount: number
  status: number
  tags: string
  createdTime: string
  updatedTime: string
}

export function getSkill(id: number) {
  return request.get(`/api/v1/skill/${id}`)
}

export function listSkills(pageNum = 1, pageSize = 12, category?: string, keyword?: string) {
  return request.get('/api/v1/skill/list', {
    params: { pageNum, pageSize, category, keyword },
  })
}

export function listMySkills(pageNum = 1, pageSize = 10) {
  return request.get('/api/v1/skill/my', {
    params: { pageNum, pageSize },
  })
}

export function saveSkill(data: FormData) {
  return request.post('/api/v1/skill/save', data, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function deleteSkill(id: number) {
  return request.post('/api/v1/skill/delete', null, { params: { id } })
}

export function downloadSkill(id: number) {
  return request.post('/api/v1/skill/download', null, { params: { id } })
}
