<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
      <h3>讨论管理</h3>
      <el-select v-model="selectedCategoryId" placeholder="选择分组筛选" clearable style="width: 200px" @change="fetch">
        <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
      </el-select>
    </div>
    <el-table :data="filteredPosts" style="width: 100%" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column label="分组" width="150">
        <template #default="{ row }">
          {{ getCategoryName(row.categoryId) }}
        </template>
      </el-table-column>
      <el-table-column prop="authorId" label="作者ID" width="100" />
      <el-table-column prop="viewCount" label="浏览" width="80" sortable />
      <el-table-column prop="commentCount" label="评论" width="80" sortable />
      <el-table-column prop="createdTime" label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createdTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
          <el-button type="warning" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="帖子详情" width="70%">
      <div v-if="currentPost">
        <h3>{{ currentPost.title }}</h3>
        <p style="color: #909399; font-size: 14px; margin: 8px 0;">
          作者ID: {{ currentPost.authorId }} | 浏览: {{ currentPost.viewCount }} | 评论: {{ currentPost.commentCount }}
        </p>
        <p style="color: #909399; font-size: 12px; margin-bottom: 16px;">
          创建时间: {{ formatDate(currentPost.createdTime) }}
        </p>
        <div style="border-top: 1px solid #eee; padding-top: 16px; white-space: pre-wrap;">
          {{ currentPost.content }}
        </div>
      </div>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑帖子" width="70%">
      <el-form v-if="currentPost" :model="editForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="editForm.title" style="width: 100%" />
        </el-form-item>
        <el-form-item label="分组">
          <el-select v-model="editForm.categoryId" placeholder="选择分组" clearable style="width: 100%">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="editForm.content" type="textarea" :rows="15" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEdit" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { adminListPosts, adminDeletePost, adminUpdatePost, adminListDiscussionCategories, type DiscussionCategory } from '@/api/admin'
import { ElMessage } from 'element-plus'

const posts = ref<any[]>([])
const categories = ref<DiscussionCategory[]>([])
const selectedCategoryId = ref<number | null>(null)
const viewDialogVisible = ref(false)
const editDialogVisible = ref(false)
const currentPost = ref<any>(null)
const editForm = ref<any>({})
const saving = ref(false)

const filteredPosts = computed(() => {
  if (!selectedCategoryId.value) return posts.value
  return posts.value.filter(p => p.categoryId === selectedCategoryId.value)
})

function getCategoryName(categoryId: number | null | undefined) {
  if (!categoryId) return '未分组'
  const cat = categories.value.find(c => c.id === categoryId)
  return cat ? cat.name : '未分组'
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString()
}

async function fetch() {
  try {
    const res = await adminListPosts();
    posts.value = res.data || []
  } catch { /* */ }
}

async function fetchCategories() {
  try {
    const res = await adminListDiscussionCategories()
    categories.value = res.data || []
  } catch { /* */ }
}

function handleView(row: any) {
  currentPost.value = row
  viewDialogVisible.value = true
}

function handleEdit(row: any) {
  currentPost.value = { ...row }
  editForm.value = {
    id: row.id,
    title: row.title,
    content: row.content,
    categoryId: row.categoryId
  }
  editDialogVisible.value = true
}

async function handleSaveEdit() {
  if (!editForm.value.title || !editForm.value.content) {
    ElMessage.warning('标题和内容不能为空')
    return
  }

  saving.value = true
  try {
    await adminUpdatePost(editForm.value)
    ElMessage.success('保存成功')
    editDialogVisible.value = false
    fetch()
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

async function handleDelete(id: number) {
  await adminDeletePost(id)
  ElMessage.success('已删除')
  fetch()
}

onMounted(() => {
  fetch()
  fetchCategories()
})
</script>
