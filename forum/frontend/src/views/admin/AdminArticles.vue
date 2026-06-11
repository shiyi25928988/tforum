<template>
  <div>
    <h3>文章管理</h3>
    <el-table :data="articles" style="margin-top: 16px" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="authorId" label="作者ID" width="100" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'info' : 'success'" size="small">
            {{ row.status === 0 ? '草稿' : '已发布' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览" width="80" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button
            size="small"
            :type="row.isPinned === 1 ? 'warning' : 'default'"
            @click="handleTogglePin(row)"
          >
            {{ row.isPinned === 1 ? '取消置顶' : '置顶' }}
          </el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { adminListArticles, adminDeleteArticle, adminTogglePinArticle } from '@/api/admin'
import { ElMessage } from 'element-plus'

const articles = ref<any[]>([])

async function fetch() {
  try { const res = await adminListArticles(); articles.value = res.data || [] } catch { /* */ }
}

async function handleTogglePin(row: any) {
  try {
    await adminTogglePinArticle(row.id)
    ElMessage.success(row.isPinned === 1 ? '已取消置顶' : '已置顶')
    fetch()
  } catch { /* */ }
}

async function handleDelete(id: number) {
  await adminDeleteArticle(id)
  ElMessage.success('已删除')
  fetch()
}

onMounted(fetch)
</script>
