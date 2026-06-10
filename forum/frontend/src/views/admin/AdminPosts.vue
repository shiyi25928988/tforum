<template>
  <div>
    <h3>讨论管理</h3>
    <el-table :data="posts" style="margin-top: 16px" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="authorId" label="作者ID" width="100" />
      <el-table-column prop="viewCount" label="浏览" width="80" />
      <el-table-column prop="commentCount" label="评论" width="80" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
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
import { adminListPosts, adminDeletePost } from '@/api/admin'
import { ElMessage } from 'element-plus'

const posts = ref<any[]>([])

async function fetch() {
  try { const res = await adminListPosts(); posts.value = res.data || [] } catch { /* */ }
}

async function handleDelete(id: number) {
  await adminDeletePost(id)
  ElMessage.success('已删除')
  fetch()
}

onMounted(fetch)
</script>
