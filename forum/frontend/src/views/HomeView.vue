<template>
  <div class="page-container">
    <el-row :gutter="24">
      <el-col :span="16">
        <div class="page-header">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px">
            <h2>最新文章</h2>
            <el-button v-if="userStore.isLoggedIn" type="primary" :icon="Edit" @click="$router.push('/article/edit')">发布文章</el-button>
          </div>
          <el-input
            v-model="keyword"
            placeholder="搜索文章..."
            clearable
            style="width: 300px"
            @keyup.enter="search"
          >
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
        </div>
        <div class="card-list">
          <el-card
            v-for="item in articles"
            :key="item.id"
            class="article-card"
            shadow="hover"
          >
            <template #header>
              <div style="display: flex; justify-content: space-between; align-items: center">
                <span style="font-size: 16px; font-weight: 600">{{ item.title }}</span>
                <el-tag v-if="item.isPinned" type="danger" size="small">置顶</el-tag>
              </div>
            </template>
            <div style="display: flex; gap: 16px; cursor: pointer" @click="$router.push(`/article/${item.id}`)">
              <div v-if="item.coverImage" style="flex: 0 0 25%; min-width: 0">
                <el-image :src="item.coverImage" fit="cover" style="width: 100%; aspect-ratio: 16 / 10; border-radius: 6px" />
              </div>
              <div style="flex: 1; min-width: 0">
                <p style="color: #909399; margin-bottom: 12px">{{ item.summary || item.content?.substring(0, 200) }}</p>
                <div style="display: flex; gap: 16px; color: #909399; font-size: 13px">
                  <span>{{ item.viewCount || 0 }} 浏览</span>
                  <span>{{ item.likeCount || 0 }} 点赞</span>
                  <span>{{ item.commentCount || 0 }} 评论</span>
                  <span>{{ formatTime(item.createdTime) }}</span>
                </div>
              </div>
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
            @current-change="fetchArticles"
          />
        </div>
      </el-col>
      <el-col :span="8">
        <el-card header="热门标签">
          <el-tag v-for="tag in hotTags" :key="tag" style="margin: 4px" @click="searchByTag(tag)">
            {{ tag }}
          </el-tag>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { searchArticles, listArticles, type Article } from '@/api/article'
import { formatTime } from '@/utils/format'
import { Edit, Search } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const router = useRouter()
const articles = ref<Article[]>([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const keyword = ref('')
const hotTags = ref(['Vue', 'Java', 'Spring Boot', 'MyBatis', 'MySQL'])

async function fetchArticles() {
  try {
    const api = keyword.value ? searchArticles : listArticles
    const params: any = [pageNum.value, pageSize.value]
    if (keyword.value) params.push(keyword.value)
    const res = await api(...params as any)
    articles.value = res.data.records
    total.value = res.data.total
  } catch {
    // handled by interceptor
  }
}

function search() {
  pageNum.value = 1
  fetchArticles()
}

function searchByTag(tag: string) {
  keyword.value = tag
  search()
}

onMounted(fetchArticles)
</script>
