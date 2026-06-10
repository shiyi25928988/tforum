<template>
  <el-container class="layout">
    <el-header class="header">
      <div class="logo" @click="$router.push('/')">tForum</div>
      <el-menu
        :default-active="activeMenu"
        mode="horizontal"
        router
        class="nav-menu"
      >
        <el-menu-item index="/home">首页</el-menu-item>
        <el-menu-item index="/forum">讨论区</el-menu-item>
        <el-menu-item index="/books">图书角</el-menu-item>
      </el-menu>
      <div class="user-area">
        <template v-if="userStore.isLoggedIn">
          <el-dropdown>
            <span class="el-dropdown-link">
              <el-avatar :size="36" :src="avatarSrc || undefined" />
              <span class="username">{{ userStore.user?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/user/profile')">个人中心</el-dropdown-item>
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
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import multiavatar from '@multiavatar/multiavatar'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const avatarSrc = computed(() => {
  const a = userStore.user?.avatar
  if (!a) return ''
  if (a.startsWith('mva:')) return 'data:image/svg+xml,' + encodeURIComponent(multiavatar(a.substring(4)))
  return a
})

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/article')) return '/home'
  if (path.startsWith('/forum')) return '/forum'
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
  background: #f5f7fa;
}
.header {
  display: flex;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 24px;
}
.logo {
  font-size: 20px;
  font-weight: 700;
  color: #409eff;
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
}
.nav-avatar {
  width: 36px; height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e4e7ed;
}
</style>
