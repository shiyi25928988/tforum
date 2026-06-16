<template>
  <el-container style="min-height: 100vh">
    <el-aside width="200px" style="background: #304156">
      <div style="padding: 20px; color: #fff; font-size: 18px; font-weight: 600; text-align: center">
        Admin
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon><span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon><span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/articles">
          <el-icon><Document /></el-icon><span>文章管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/posts">
          <el-icon><ChatDotRound /></el-icon><span>讨论管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/discussion-categories">
          <el-icon><Folder /></el-icon><span>讨论分组管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/books">
          <el-icon><Reading /></el-icon><span>图书管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/tags">
          <el-icon><PriceTag /></el-icon><span>标签管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/skills">
          <el-icon><MagicStick /></el-icon><span>Skills 管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/milvus">
          <el-icon><Coin /></el-icon><span>Milvus 管理</span>
        </el-menu-item>
        <el-menu-item index="/">
          <el-icon><Back /></el-icon><span>返回前台</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background: var(--bg-header); border-bottom: 1px solid var(--border-base); display: flex; align-items: center; justify-content: space-between">
        <span style="font-size: 16px; color: var(--text-primary)">管理后台</span>
        <el-button size="small" @click="$router.push('/')">返回前台</el-button>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { DataAnalysis, User, Document, ChatDotRound, Folder, Reading, PriceTag, Coin, Back, MagicStick } from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>
