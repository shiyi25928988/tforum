<template>
  <div class="page-container">
    <div class="page-header" style="display: flex; justify-content: space-between; align-items: center">
      <h2><el-icon><Reading /></el-icon> 图书角</h2>
      <el-button v-if="userStore.isLoggedIn" type="primary" :icon="Upload" @click="showUploadDialog = true">上传图书</el-button>
    </div>

    <!-- 搜索栏 -->
    <div style="margin-bottom: 20px; display: flex; gap: 12px">
      <el-input
        v-model="keyword"
        placeholder="搜索图书名称或作者..."
        clearable
        style="width: 320px"
        @keyup.enter="search"
      >
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button @click="search">搜索</el-button>
    </div>

    <!-- 图书网格 -->
    <div v-loading="loading">
      <el-empty v-if="books.length === 0" description="暂无图书" />
      <el-row v-else :gutter="20">
        <el-col v-for="book in books" :key="book.id" :span="6" style="margin-bottom: 20px">
          <el-card shadow="hover" class="book-card" @click="handlePreview(book)">
            <div class="book-cover">
              <el-image v-if="book.coverImage" :src="book.coverImage" fit="cover" style="width: 100%; height: 100%; border-radius: 4px" />
              <span v-else style="font-size: 48px">📄</span>
            </div>
            <div class="book-card-title" :title="book.title">{{ book.title }}</div>
            <div class="book-card-author">{{ book.author || '佚名' }}</div>
            <div class="book-card-meta">
              <span>⬇ {{ book.downloadCount || 0 }}</span>
              <span>{{ formatSize(book.fileSize) }}</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 分页 -->
    <div v-if="total > pageSize" style="margin-top: 20px; text-align: center">
      <el-pagination
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchBooks"
      />
    </div>

    <!-- 上传弹窗 -->
    <el-dialog v-model="showUploadDialog" title="上传图书" width="480px" @close="resetUpload">
      <el-upload
        :auto-upload="false"
        :limit="1"
        accept=".pdf"
        :on-change="handleFileChange"
        :on-remove="handleRemove"
        drag
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">将 PDF 文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">仅支持 PDF 格式，最大 100MB。请勿重复上传同名图书</div>
        </template>
      </el-upload>
      <el-form :model="uploadForm" label-width="60px" style="margin-top: 16px">
        <el-form-item label="书名" required>
          <el-input v-model="uploadForm.title" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="uploadForm.author" placeholder="作者（选填）" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="uploadForm.description" type="textarea" :rows="2" placeholder="简介（选填）" />
        </el-form-item>
        <el-form-item label="封面">
          <div v-if="coverPreview" style="position: relative; width: 120px">
            <el-image :src="coverPreview" fit="cover" style="width: 120px; height: 160px; border-radius: 4px" />
            <el-button style="position: absolute; top: -8px; right: -8px" type="danger" size="small" circle @click="removeCover">×</el-button>
          </div>
          <div v-else style="width: 120px; height: 160px; border: 2px dashed #d9d9d9; border-radius: 4px; display: flex; align-items: center; justify-content: center; cursor: pointer; color: #909399; font-size: 13px" @click="triggerCoverInput">
            + 上传封面
          </div>
          <input ref="coverInputRef" type="file" accept="image/*" style="display: none" @change="handleCoverChange" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showUploadDialog = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="handleUpload">上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { listBooks, uploadBook, downloadBook, type Book } from '@/api/book'
import { ElMessage } from 'element-plus'
import { Reading, Upload, UploadFilled, Search } from '@element-plus/icons-vue'
import { formatSize } from '@/utils/format'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const books = ref<Book[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)
const keyword = ref('')

const showUploadDialog = ref(false)
const uploading = ref(false)
const uploadFile = ref<File | null>(null)
const uploadForm = ref({ title: '', author: '', description: '' })
const coverFile = ref<File | null>(null)
const coverPreview = ref('')
const coverInputRef = ref<HTMLInputElement>()

async function fetchBooks() {
  loading.value = true
  try {
    const res = await listBooks(pageNum.value, pageSize.value, undefined, keyword.value || undefined)
    books.value = res.data.records
    total.value = res.data.total
  } catch { /* ignore */ }
  finally { loading.value = false }
}

function search() { pageNum.value = 1; fetchBooks() }

function handleFileChange(file: any) { uploadFile.value = file.raw as File }
function handleRemove() { uploadFile.value = null }

function resetUpload() {
  uploadFile.value = null
  uploadForm.value = { title: '', author: '', description: '' }
  coverFile.value = null
  coverPreview.value = ''
}

function triggerCoverInput() {
  coverInputRef.value?.click()
}

function handleCoverChange(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  coverFile.value = file
  const reader = new FileReader()
  reader.onload = (ev) => { coverPreview.value = ev.target?.result as string }
  reader.readAsDataURL(file)
  input.value = ''
}

function removeCover() {
  coverFile.value = null
  coverPreview.value = ''
}

async function handleUpload() {
  if (!uploadFile.value) { ElMessage.warning('请选择文件'); return }
  if (!uploadForm.value.title.trim()) { ElMessage.warning('请输入书名'); return }
  uploading.value = true
  try {
    await uploadBook(uploadFile.value, uploadForm.value.title, uploadForm.value.author, uploadForm.value.description, undefined, coverFile.value || undefined)
    ElMessage.success('上传成功')
    showUploadDialog.value = false
    fetchBooks()
  } catch { /* ignore */ }
  finally { uploading.value = false }
}

function handlePreview(book: Book) {
  window.open(book.fileUrl, '_blank')
  downloadBook(book.id).catch(() => {})
}

onMounted(fetchBooks)
</script>

<style scoped>
.book-card {
  cursor: pointer;
  text-align: center;
  transition: box-shadow 0.3s;
  padding: 8px;
}
.book-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
.book-cover {
  width: 100%;
  height: 180px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.book-card-title {
  font-size: 14px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 4px;
}
.book-card-author {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}
.book-card-meta {
  display: flex;
  justify-content: center;
  gap: 16px;
  font-size: 12px;
  color: #c0c4cc;
}
</style>
