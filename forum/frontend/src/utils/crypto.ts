/**
 * 前端参数编码（配合后端解码）
 * 使用 Base64 编码，避免明文出现在 URL 和日志中
 */

export function encode(str: string): string {
  return btoa(unescape(encodeURIComponent(str)))
}

export function decode(str: string): string {
  return decodeURIComponent(escape(atob(str)))
}
