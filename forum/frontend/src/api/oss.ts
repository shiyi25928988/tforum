import request from '@/utils/request'

export interface FileItem {
  name: string
  bucket: string
  folderName: string
  downloadUrl: string
  size: number
  folder: boolean
}

export function uploadFile(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/v1/oss/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function uploadToFolder(file: File, folder: string) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('folder', folder)
  return request.post('/api/v1/oss/uploadToFolder', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function listFiles() {
  return request.get('/api/v1/oss/list')
}

export function listFilesByFolder(folder: string) {
  return request.get('/api/v1/oss/listByFolder', { params: { folder } })
}

export function deleteFile(fileName: string) {
  return request.post('/api/v1/oss/delete', null, { params: { fileName } })
}

export function downloadFile(url: string) {
  return request.get('/api/v1/oss/download', { params: { url } })
}
