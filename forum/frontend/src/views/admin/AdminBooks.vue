<template>
  <div>
    <h3>图书管理</h3>
    <el-table :data="books" style="margin-top: 16px" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="书名" />
      <el-table-column prop="author" label="作者" width="150" />
      <el-table-column prop="fileSize" label="大小" width="100">
        <template #default="{ row }">{{ formatSize(row.fileSize) }}</template>
      </el-table-column>
      <el-table-column prop="downloadCount" label="下载" width="80" />
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
import { adminListBooks, adminDeleteBook } from '@/api/admin'
import { ElMessage } from 'element-plus'
import { formatSize } from '@/utils/format'

const books = ref<any[]>([])

async function fetch() {
  try { const res = await adminListBooks(); books.value = res.data || [] } catch { /* */ }
}

async function handleDelete(id: number) {
  await adminDeleteBook(id)
  ElMessage.success('已删除')
  fetch()
}

onMounted(fetch)
</script>
