<template>
  <div>
    <h3>Milvus 向量库管理</h3>
    <el-alert type="warning" :closable="false" style="margin: 16px 0" show-icon>
      重建集合会删除所有已存储的向量数据，请谨慎操作。
    </el-alert>
    <el-row :gutter="16">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>创建集合</template>
          <p style="color: #909399; font-size: 13px; margin-bottom: 16px">
            如果集合不存在则创建，已有集合不会受影响。
          </p>
          <el-button type="primary" :loading="creating" @click="handleCreate">
            {{ creating ? '创建中...' : '创建集合' }}
          </el-button>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>重建集合</template>
          <p style="color: #909399; font-size: 13px; margin-bottom: 16px">
            删除现有集合并重新创建，所有向量数据将丢失。
          </p>
          <el-popconfirm
            title="确定要重建集合吗？所有向量数据将被清除！"
            confirm-button-text="确定重建"
            cancel-button-text="取消"
            @confirm="handleDrop"
          >
            <template #reference>
              <el-button type="danger" :loading="dropping">
                {{ dropping ? '重建中...' : '重建集合' }}
              </el-button>
            </template>
          </el-popconfirm>
        </el-card>
      </el-col>
    </el-row>

    <!-- 上传文档 -->
    <el-card shadow="never" style="margin-top: 16px">
      <template #header>上传文档到向量库</template>
      <p style="color: #909399; font-size: 13px; margin-bottom: 12px">
        支持 PDF、Word、TXT、Markdown 等格式，解析后分片存入 Milvus 供 RAG 检索。
      </p>
      <div style="display: flex; gap: 12px; align-items: center">
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :limit="1"
          :on-change="onFileChange"
          :on-remove="() => uploadFile = null"
          accept=".pdf,.docx,.doc,.txt,.md,.ppt,.pptx"
        >
          <el-button type="primary">选择文件</el-button>
        </el-upload>
        <span v-if="uploadFile" style="font-size: 13px; color: #303133">{{ uploadFile.name }}</span>
        <el-button type="success" :loading="uploading" :disabled="!uploadFile" @click="handleUpload">
          {{ uploading ? '上传解析中...' : '上传并解析' }}
        </el-button>
      </div>
    </el-card>

    <!-- 文章存入向量库 -->
    <el-card shadow="never" style="margin-top: 16px">
      <template #header>文章管理 — 存入 / 移出向量库</template>
      <div style="display: flex; gap: 12px; margin-bottom: 12px">
        <el-select v-model="articleFilter" placeholder="筛选文章" clearable style="width: 180px" @change="fetchArticles">
          <el-option label="全部已发布" value="all" />
          <el-option label="已存入向量库" value="in" />
          <el-option label="未存入向量库" value="out" />
        </el-select>
        <el-button type="primary" :disabled="selectedArticleIds.length === 0" @click="handleStoreArticles">
          存入向量库 ({{ selectedArticleIds.length }})
        </el-button>
        <el-button type="danger" :disabled="selectedArticleIds.length === 0" @click="handleDeleteArticles">
          从向量库删除 ({{ selectedArticleIds.length }})
        </el-button>
      </div>
      <el-table
        ref="articleTableRef"
        :data="articles"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        v-loading="articleLoading"
        max-height="400"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="向量状态" width="100">
          <template #default="{ row }">
            <el-tag :type="storedArticleIds.has(row.id) ? 'success' : 'info'" size="small">
              {{ storedArticleIds.has(row.id) ? '已存入' : '未存入' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="70" />
        <el-table-column prop="createdTime" label="创建时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createdTime) }}</template>
        </el-table-column>
      </el-table>
    </el-card>

    <div v-if="result" style="margin-top: 16px">
      <el-alert :type="result.success ? 'success' : 'error'" :closable="false">
        {{ result.msg }}
      </el-alert>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  createMilvusCollection,
  dropAndRecreateMilvusCollection,
  uploadDocToMilvus,
  storeArticlesToMilvus,
  getStoredArticles,
  deleteArticlesFromMilvus,
  adminListArticles,
} from '@/api/admin'
import { ElMessage } from 'element-plus'

const creating = ref(false)
const dropping = ref(false)
const uploadRef = ref<any>(null)
const uploading = ref(false)
const uploadFile = ref<File | null>(null)
const result = ref<{ success: boolean; msg: string } | null>(null)

// 文章相关
const articles = ref<any[]>([])
const articleLoading = ref(false)
const selectedArticleIds = ref<number[]>([])
const storedArticleIds = ref<Set<number>>(new Set())
const articleFilter = ref('all')

function onFileChange(file: any) { uploadFile.value = file.raw as File }

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

async function handleUpload() {
  if (!uploadFile.value) return
  uploading.value = true; result.value = null
  try {
    const res: any = await uploadDocToMilvus(uploadFile.value)
    result.value = { success: true, msg: res?.data || res?.message || '上传成功' }
    uploadFile.value = null
    uploadRef.value?.clearFiles()
  } catch {
    result.value = { success: false, msg: '上传失败' }
  } finally { uploading.value = false }
}

async function handleCreate() {
  creating.value = true; result.value = null
  try {
    const res: any = await createMilvusCollection()
    const ok = res?.data?.status === 0 || res?.code === 0
    result.value = { success: ok, msg: ok ? '操作成功' : (res?.data?.reason || '操作失败') }
  } catch {
    result.value = { success: false, msg: '操作失败' }
  } finally { creating.value = false }
}

async function handleDrop() {
  dropping.value = true; result.value = null
  try {
    const res: any = await dropAndRecreateMilvusCollection()
    const ok = res?.data?.status === 0 || res?.code === 0
    result.value = { success: ok, msg: ok ? '重建成功' : (res?.data?.reason || '重建失败') }
    // 重建后刷新
    await fetchStoredArticles()
  } catch {
    result.value = { success: false, msg: '重建失败' }
  } finally { dropping.value = false }
}

// ========== 文章向量管理 ==========

async function fetchArticles() {
  articleLoading.value = true
  try {
    const res = await adminListArticles()
    let list = res.data || []
    // 只显示已发布文章 (status === 1)
    list = list.filter((a: any) => a.status === 1)
    if (articleFilter.value === 'in') {
      list = list.filter((a: any) => storedArticleIds.value.has(a.id))
    } else if (articleFilter.value === 'out') {
      list = list.filter((a: any) => !storedArticleIds.value.has(a.id))
    }
    articles.value = list
  } catch { /* */ }
  finally { articleLoading.value = false }
}

async function fetchStoredArticles() {
  try {
    const res = await getStoredArticles()
    storedArticleIds.value = new Set(res.data || [])
  } catch { /* */ }
}

function handleSelectionChange(rows: any[]) {
  selectedArticleIds.value = rows.map(r => r.id)
}

async function handleStoreArticles() {
  if (selectedArticleIds.value.length === 0) return
  result.value = null
  try {
    const res: any = await storeArticlesToMilvus(selectedArticleIds.value)
    result.value = { success: true, msg: res?.data || res?.message || '存入成功' }
    await fetchStoredArticles()
    await fetchArticles()
  } catch {
    result.value = { success: false, msg: '存入失败' }
  }
}

async function handleDeleteArticles() {
  if (selectedArticleIds.value.length === 0) return
  result.value = null
  try {
    const res: any = await deleteArticlesFromMilvus(selectedArticleIds.value)
    result.value = { success: true, msg: res?.data || res?.message || '删除成功' }
    await fetchStoredArticles()
    await fetchArticles()
  } catch {
    result.value = { success: false, msg: '删除失败' }
  }
}

onMounted(async () => {
  await fetchStoredArticles()
  await fetchArticles()
})
</script>
