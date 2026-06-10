import request from '@/utils/request'

export interface UserInfo {
  id: number
  username: string
  account: string
  avatar: string
  phone: string
  email: string
  role: string
  token: string
}

export interface Avatar {
  id: number
  url: string
}

export function login(account: string, password: string) {
  return request.post('/api/v1/user/login', { account, password })
}

export function register(data: { account: string; password: string; username: string }) {
  return request.post('/api/v1/user/register', data)
}

export function logout() {
  return request.post('/api/v1/user/logout')
}

export function updateUserInfo(data: Partial<UserInfo>) {
  return request.post('/api/v1/user/update', data)
}

export function updatePassword(oldPassword: string, newPassword: string) {
  return request.post('/api/v1/user/updatePassword', { oldPassword, newPassword })
}

export function getRandomAvatar() {
  return request.get('/api/v1/user/getRandomAvatar')
}

export function getAllAvatar() {
  return request.get('/api/v1/user/getAllAvatar')
}

export function getUserById(id: number) {
  return request.get(`/api/v1/user/${id}`)
}
