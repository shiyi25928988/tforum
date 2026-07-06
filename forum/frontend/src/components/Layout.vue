<template>
  <el-container class="layout">
    <el-header class="header">
      <div class="logo" @click="$router.push('/')">{{ siteName }}</div>
      <el-menu
        :default-active="activeMenu"
        mode="horizontal"
        class="nav-menu"
      >
        <template v-for="item in navItems" :key="item.id">
          <el-menu-item
            v-if="item.type === 'internal'"
            :index="item.url"
            @click="handleNavClick(item)"
          >
            <span v-if="item.icon" style="margin-right: 4px">{{ item.icon }}</span>
            {{ item.name }}
          </el-menu-item>
          <el-menu-item
            v-else
            :index="'ext-' + item.id"
            @click="openExternal(item.url)"
          >
            <span v-if="item.icon" style="margin-right: 4px">{{ item.icon }}</span>
            {{ item.name }}
          </el-menu-item>
        </template>
      </el-menu>
      <div class="user-area">
        <!-- 主题切换 -->
        <el-dropdown trigger="click">
          <el-button text size="small" style="font-size: 18px; padding: 4px">
            {{ themeIcon }}
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="themeStore.setTheme('light')">
                ☀️ 浅色
              </el-dropdown-item>
              <el-dropdown-item @click="themeStore.setTheme('dark')">
                🌙 深色
              </el-dropdown-item>
              <el-dropdown-item @click="themeStore.setTheme('macaron')">
                🍬 马卡龙
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <template v-if="userStore.isLoggedIn">
          <el-dropdown>
            <span class="el-dropdown-link">
              <el-avatar :size="36" :src="avatarSrc || undefined" />
              <span class="username">{{ userStore.user?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/user/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/user/articles')">我的文章</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/user/skills')">我的 Skills</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/article/edit')">写文章</el-dropdown-item>
                <el-dropdown-item v-if="userStore.user?.role === 'admin'" @click="$router.push('/admin')">控制台</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" size="small" @click="$router.push('/login')">登录</el-button>
          <el-button size="small" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useThemeStore } from '@/stores/theme'
import { ElMessage } from 'element-plus'
import multiavatar from '@multiavatar/multiavatar'
import { siteConfig } from '@/config/site'
import { getNavList, type NavItem } from '@/api/nav'

const siteName = siteConfig.name

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const themeStore = useThemeStore()

const navItems = ref<NavItem[]>([])

onMounted(async () => {
  try {
    const r = await getNavList()
    navItems.value = (r as any).data || []
  } catch { /* keep defaults */ }
})

function handleNavClick(item: NavItem) {
  router.push(item.url)
}

function openExternal(url: string) {
  window.open(url, '_blank')
}

const themeIcon = computed(() => {
  switch (themeStore.current.value) {
    case 'dark': return '🌙'
    case 'macaron': return '🍬'
    default: return '☀️'
  }
})

const avatarSrc = computed(() => {
  const a = userStore.user?.avatar
  if (!a) return ''
  if (a.startsWith('mva:')) return 'data:image/svg+xml,' + encodeURIComponent(multiavatar(a.substring(4)))
  return a
})

const activeMenu = computed(() => {
  const path = route.path
  for (const item of navItems.value) {
    if (item.type === 'internal' && item.url !== '/' && path.startsWith(item.url)) {
      return item.url
    }
  }
  if (path.startsWith('/article')) return '/home'
  return '/home'
})

async function handleLogout() {
  try {
    await userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/')
  } catch {
    // ignore
  }
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  background: var(--bg-primary);
}
.header {
  display: flex;
  align-items: center;
  background: var(--bg-header);
  border-bottom: 1px solid var(--border-base);
  padding: 0 24px;
}
.logo {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-primary);
  cursor: pointer;
  margin-right: 40px;
}
.nav-menu {
  flex: 1;
  border-bottom: none !important;
}
.user-area {
  display: flex;
  align-items: center;
  gap: 8px;
}
.el-dropdown-link {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.username {
  font-size: 14px;
  color: var(--text-primary);
}
.nav-avatar {
  width: 36px; height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--border-base);
}
</style>
