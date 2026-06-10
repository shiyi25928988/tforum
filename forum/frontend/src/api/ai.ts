import request from '@/utils/request'

export interface ChatRequest {
  conversationId: string
  message: string
}

export interface SystemChatRequest {
  conversationId: string
  systemMessage: string
  userMessage: string
}

export interface ParameterizedChatRequest {
  conversationId: string
  template: string
  parameters: Record<string, any>
}

// 简单聊天（非流式）
export function simpleChat(data: ChatRequest) {
  return request.post('/api/v1/ai/simpleChat', data)
}

// 流式聊天（需要 EventSource 处理）
export function getChatUrl(): string {
  return '/api/v1/ai/chat'
}

// RAG 聊天（需要 EventSource 处理）
export function getRagUrl(): string {
  return '/api/v1/ai/rag'
}

// 带系统提示的聊天（需要 EventSource 处理）
export function getSystemChatUrl(): string {
  return '/api/v1/ai/chatWithSystemPrompt'
}

// 向量相似搜索
export function similarSearch(query: string, topK = 5) {
  return request.get('/api/v1/vector/similarSearch', {
    params: { query, topK },
  })
}

// 存储文档到向量库
export function storeDocument(content: string, metadata?: Record<string, any>) {
  return request.post('/api/v1/vector/storeDocument', { content, metadata })
}
