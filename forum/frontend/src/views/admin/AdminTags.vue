<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center">
      <h3>标签管理</h3>
      <div style="display: flex; gap: 8px">
        <el-input v-model="newTagName" placeholder="新标签名" size="small" style="width: 160px" @keyup.enter="handleAdd" />
        <el-button type="primary" size="small" @click="handleAdd" :disabled="!newTagName.trim()">添加</el-button>
      </div>
    </div>
    <el-table :data="tags" style="margin-top: 16px" stripe>
      <el-table-column prop="id" label="ID" width="100" />
      <el-table-column prop="name" label="标签名" />
      <el-table-column label="操作" width="120">
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
import { adminListTags, adminSaveTag, adminDeleteTag } from '@/api/admin'
import { ElMessage } from 'element-plus'

const tags = ref<any[]>([])
const newTagName = ref('')

async function fetch() { try { const r = await adminListTags(); tags.value = r.data || [] } catch { /* */ } }

async function handleAdd() {
  if (!newTagName.value.trim()) return
  try {
    await adminSaveTag(newTagName.value.trim())
    ElMessage.success('已添加')
    newTagName.value = ''
    fetch()
  } catch { /* */ }
}

async function handleDelete(id: number) {
  await adminDeleteTag(id)
  ElMessage.success('已删除')
  fetch()
}

onMounted(fetch)
</script>
