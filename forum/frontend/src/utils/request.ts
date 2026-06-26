import axios from 'axios'
import type { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

// 防止重复处理401的标志
let isHandling401 = false

const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 300000,
  headers: { 'Content-Type': 'application/json' },
})

// 请求拦截器 —— 自动携带 token
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const userStore = useUserStore()
    const t = userStore.getToken()
    if (t) {
      config.headers['token'] = t
    }
    return config
  },
  (error) => Promise.reject(error),
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    const data = response.data
    // 如果返回code 401，表示token无效
    if (data.code === 401 && !isHandling401) {
      isHandling401 = true
      const userStore = useUserStore()
      // 直接清除本地状态，不调用logoutApi避免循环
      userStore.token = ''
      localStorage.removeItem('tforum_token')
      localStorage.removeItem('tforum_user')
      userStore.user = null
      ElMessage.error(data.message || '登录已过期，请重新登录')
      router.push('/login')
      // 延迟重置标志，避免短时间内重复处理
      setTimeout(() => { isHandling401 = false }, 2000)
      return Promise.reject(new Error(data.message || '登录已过期'))
    }
    if (data.code !== 0) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return data
  },
  (error) => {
    if (error.response) {
      const status = error.response.status
      if (status === 401 && !isHandling401) {
        isHandling401 = true
        const userStore = useUserStore()
        // 直接清除本地状态，不调用logoutApi避免循环
        userStore.token = ''
        localStorage.removeItem('tforum_token')
        localStorage.removeItem('tforum_user')
        userStore.user = null
        ElMessage.error('登录已过期，请重新登录')
        router.push('/login')
        // 延迟重置标志，避免短时间内重复处理
        setTimeout(() => { isHandling401 = false }, 2000)
        return Promise.reject(error)
      }
      ElMessage.error(error.response.data?.message || '请求失败')
    } else {
      ElMessage.error('网络错误')
    }
    return Promise.reject(error)
  },
)

export default request
