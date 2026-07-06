import request from '@/utils/request'

export interface NavItem {
  id: number
  name: string
  url: string
  icon?: string
  type: string
  isVisible: number
  sortOrder: number
  isSystem: number
}

export function getNavList() {
  return request.get('/api/v1/nav/list')
}
