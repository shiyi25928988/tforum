import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo } from '@/api/user'
import { login as loginApi, logout as logoutApi } from '@/api/user'
import { encode } from '@/utils/crypto'

const TOKEN_KEY = 'tforum_token'
const USER_KEY = 'tforum_user'

function loadToken(): string {
  return localStorage.getItem(TOKEN_KEY) || ''
}

function loadUser(): UserInfo | null {
  try {
    const raw = localStorage.getItem(USER_KEY)
    return raw ? JSON.parse(raw) : null
  } catch { return null }
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(loadToken())
  const user = ref<UserInfo | null>(loadUser())

  const isLoggedIn = computed(() => !!token.value)

  function saveToken(t: string) {
    token.value = t
    localStorage.setItem(TOKEN_KEY, t)
  }

  function saveUser(u: UserInfo | null) {
    user.value = u
    if (u) {
      localStorage.setItem(USER_KEY, JSON.stringify(u))
    } else {
      localStorage.removeItem(USER_KEY)
    }
  }

  async function login(account: string, password: string) {
    const res = await loginApi(account, encode(password))
    const u = res.data as UserInfo
    saveToken(u.token)
    saveUser(u)
    return u
  }

  async function logout() {
    token.value = ''
    localStorage.removeItem(TOKEN_KEY)
    try { await logoutApi() } catch { /* ignore */ }
    saveUser(null)
  }

  function getToken(): string {
    return token.value || loadToken()
  }

  return { user, token, isLoggedIn, login, logout, getToken }
})
