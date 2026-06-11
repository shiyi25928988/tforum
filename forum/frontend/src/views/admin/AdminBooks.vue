<template>
  <div>
    <h3>图书管理</h3>
    <el-table :data="books" style="margin-top: 16px" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="书名" min-width="150" show-overflow-tooltip />
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.isDeleted === 1 ? 'warning' : 'success'" size="small">
            {{ row.isDeleted === 1 ? '已下架' : '上架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="fileSize" label="大小" width="100">
        <template #default="{ row }">{{ formatSize(row.fileSize) }}</template>
      </el-table-column>
      <el-table-column prop="downloadCount" label="下载" width="80" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button
            size="small"
            :type="row.isDeleted === 1 ? 'success' : 'warning'"
            @click="handleToggleStatus(row)"
          >
            {{ row.isDeleted === 1 ? '上架' : '下架' }}
          </el-button>
          <el-popconfirm
            title="此操作将永久删除该图书及其文件，确定继续？"
            confirm-button-text="确认删除"
            cancel-button-text="取消"
            @confirm="handleDelete(row.id)"
          >
            <template #reference>
              <el-button type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑图书" width="500px">
      <el-form v-if="editForm.id" :model="editForm" label-width="60px">
        <el-form-item label="书名" required>
          <el-input v-model="editForm.title" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="editForm.author" placeholder="作者（选填）" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="editForm.description" type="textarea" :rows="3" placeholder="简介（选填）" />
        </el-form-item>
        <el-form-item label="封面">
          <div v-if="editCoverPreview" style="position: relative; width: 120px">
            <el-image :src="editCoverPreview" fit="cover" style="width: 120px; height: 160px; border-radius: 4px" />
            <el-button style="position: absolute; top: -8px; right: -8px" type="danger" size="small" circle @click="editCoverPreview = ''; editCoverFile = null">×</el-button>
          </div>
          <div v-else style="width: 120px; height: 160px; border: 2px dashed #d9d9d9; border-radius: 4px; display: flex; align-items: center; justify-content: center; cursor: pointer; color: #909399; font-size: 13px" @click="triggerEditCoverInput">
            + 上传封面
          </div>
          <input ref="editCoverInputRef" type="file" accept="image/*" style="display: none" @change="handleEditCoverChange" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { adminListBooks, adminDeleteBook, adminUpdateBook, adminToggleBookStatus } from '@/api/admin'
import { uploadToFolder } from '@/api/oss'
import { ElMessage } from 'element-plus'
import { formatSize } from '@/utils/format'

const books = ref<any[]>([])
const editDialogVisible = ref(false)
const editForm = ref<any>({})
const editCoverFile = ref<File | null>(null)
const editCoverPreview = ref('')
const editCoverInputRef = ref<HTMLInputElement>()
const saving = ref(false)

async function fetch() {
  try { const res = await adminListBooks(); books.value = res.data || [] } catch { /* */ }
}

function handleEdit(row: any) {
  editForm.value = {
    id: row.id,
    title: row.title,
    author: row.author || '',
    description: row.description || '',
    coverImage: row.coverImage || '',
  }
  editCoverPreview.value = row.coverImage || ''
  editCoverFile.value = null
  editDialogVisible.value = true
}

function triggerEditCoverInput() {
  editCoverInputRef.value?.click()
}

function handleEditCoverChange(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  editCoverFile.value = file
  const reader = new FileReader()
  reader.onload = (ev) => { editCoverPreview.value = ev.target?.result as string }
  reader.readAsDataURL(file)
  input.value = ''
}

async function handleSaveEdit() {
  if (!editForm.value.title.trim()) { ElMessage.warning('请输入书名'); return }
  saving.value = true
  try {
    // 如果有新封面上传
    if (editCoverFile.value) {
      const folder = `books/cover/`
      const res: any = await uploadToFolder(editCoverFile.value, folder)
      editForm.value.coverImage = res.data || res || ''
    }
    await adminUpdateBook(editForm.value)
    ElMessage.success('保存成功')
    editDialogVisible.value = false
    fetch()
  } catch { /* */ }
  finally { saving.value = false }
}

async function handleToggleStatus(row: any) {
  try {
    await adminToggleBookStatus(row.id)
    ElMessage.success(row.isDeleted === 1 ? '已上架' : '已下架')
    fetch()
  } catch { /* */ }
}

async function handleDelete(id: number) {
  await adminDeleteBook(id)
  ElMessage.success('已永久删除')
  fetch()
}

onMounted(fetch)
</script>
