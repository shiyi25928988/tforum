<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-row">
        <h2>文章列表</h2>
        <el-button type="primary" :icon="Edit" @click="$router.push('/article/edit')">
          发布文章
        </el-button>
      </div>
      <el-input
        v-model="keyword"
        placeholder="搜索文章..."
        clearable
        style="width: 300px; margin-top: 12px"
        @keyup.enter="search"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>
    <div class="card-list">
      <el-card
        v-for="item in articles"
        :key="item.id"
        class="article-card"
        shadow="hover"
        @click="$router.push(`/article/${item.id}`)"
      >
        <template #header>
          <span style="font-size: 16px; font-weight: 600">{{ item.title }}</span>
        </template>
        <p style="color: #909399; margin-bottom: 12px">{{ item.summary || item.content?.substring(0, 200) }}</p>
        <div style="display: flex; gap: 16px; color: #909399; font-size: 13px">
          <span v-if="item.tags">
            <el-tag v-for="t in item.tags.split(',')" :key="t" size="small" style="margin-right: 4px">{{ t }}</el-tag>
          </span>
          <span><el-icon><View /></el-icon> {{ item.viewCount || 0 }}</span>
          <span><el-icon><Star /></el-icon> {{ item.likeCount || 0 }}</span>
          <span>{{ formatTime(item.createdTime) }}</span>
        </div>
      </el-card>
      <el-empty v-if="articles.length === 0" description="暂无文章" />
    </div>
    <div style="margin-top: 20px; text-align: center">
      <el-pagination
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchData"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { searchArticles, type Article } from '@/api/article'
import { Edit } from '@element-plus/icons-vue'
import { formatTime } from '@/utils/format'

const route = useRoute()
const articles = ref<Article[]>([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const keyword = ref((route.query.keyword as string) || '')

async function fetchData() {
  try {
    const res = await searchArticles(pageNum.value, pageSize.value, keyword.value || undefined)
    articles.value = res.data.records
    total.value = res.data.total
  } catch {
    // handled by interceptor
  }
}

function search() {
  pageNum.value = 1
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
