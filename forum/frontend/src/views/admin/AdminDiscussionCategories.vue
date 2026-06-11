<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
      <h3>讨论区分组管理</h3>
      <el-button type="primary" @click="handleAdd">新增分组</el-button>
    </div>

    <el-table :data="categories" style="width: 100%" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分组名称" min-width="150" />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="sortOrder" label="排序" width="100" sortable />
      <el-table-column prop="topicCount" label="话题数" width="100" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-popconfirm title="确定删除该分组吗？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分组' : '新增分组'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分组名称" required>
          <el-input v-model="form.name" placeholder="请输入分组名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { DiscussionCategory } from '@/api/admin'
import {
  adminListDiscussionCategories,
  adminSaveDiscussionCategory,
  adminDeleteDiscussionCategory,
} from '@/api/admin'

const categories = ref<DiscussionCategory[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const form = ref<DiscussionCategory>({
  name: '',
  description: '',
  sortOrder: 0,
})

async function fetchCategories() {
  try {
    const res = await adminListDiscussionCategories()
    categories.value = res.data || []
  } catch {
    ElMessage.error('获取分组列表失败')
  }
}

function handleAdd() {
  isEdit.value = false
  form.value = {
    name: '',
    description: '',
    sortOrder: 0,
  }
  dialogVisible.value = true
}

function handleEdit(row: DiscussionCategory) {
  isEdit.value = true
  form.value = {
    id: row.id,
    name: row.name,
    description: row.description,
    sortOrder: row.sortOrder || 0,
  }
  dialogVisible.value = true
}

async function handleSave() {
  if (!form.value.name) {
    ElMessage.warning('请输入分组名称')
    return
  }

  saving.value = true
  try {
    await adminSaveDiscussionCategory(form.value)
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    fetchCategories()
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

async function handleDelete(id: number) {
  try {
    await adminDeleteDiscussionCategory(id)
    ElMessage.success('删除成功')
    fetchCategories()
  } catch {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  fetchCategories()
})
</script>
