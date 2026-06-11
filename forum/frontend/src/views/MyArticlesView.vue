<template>
  <div class="my-articles">
    <div class="page-header">
      <h2>我的文章</h2>
      <el-button type="primary" @click="$router.push('/article/edit')">写文章</el-button>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="已发布" name="published" />
      <el-tab-pane label="草稿" name="draft" />
    </el-tabs>

    <el-table v-if="articles.length > 0" :data="articles" style="width: 100%" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="200">
        <template #default="{ row }">
          <el-link type="primary" @click="$router.push(`/article/${row.id}`)">{{ row.title }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览" width="80" />
      <el-table-column prop="likeCount" label="点赞" width="80" />
      <el-table-column prop="commentCount" label="评论" width="80" />
      <el-table-column prop="createdTime" label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createdTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="$router.push(`/article/edit/${row.id}`)">编辑</el-button>
          <el-button
            size="small"
            :type="row.status === 1 ? 'warning' : 'success'"
            @click="handleToggleStatus(row)"
          >
            {{ row.status === 1 ? '撤下' : '发布' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-else description="暂无文章" />

    <div v-if="total > pageSize" class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="fetchArticles"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { listMyArticles, toggleArticleStatus } from '@/api/article'
import { ElMessage } from 'element-plus'

const articles = ref<any[]>([])
const activeTab = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString()
}

function getStatusFilter(): number | undefined {
  if (activeTab.value === 'published') return 1
  if (activeTab.value === 'draft') return 0
  return undefined
}

async function fetchArticles() {
  try {
    const res = await listMyArticles(currentPage.value, pageSize.value, getStatusFilter())
    const data = res.data
    if (data && data.records) {
      articles.value = data.records
      total.value = data.total
    } else {
      articles.value = []
      total.value = 0
    }
  } catch {
    // ignore
  }
}

function handleTabChange() {
  currentPage.value = 1
  fetchArticles()
}

async function handleToggleStatus(row: any) {
  try {
    await toggleArticleStatus(row.id)
    ElMessage.success(row.status === 1 ? '已撤下' : '已发布')
    fetchArticles()
  } catch {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchArticles()
})
</script>

<style scoped>
.my-articles {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-header h2 {
  margin: 0;
  font-size: 22px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
