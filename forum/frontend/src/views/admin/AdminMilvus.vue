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

    <div v-if="result" style="margin-top: 16px">
      <el-alert :type="result.success ? 'success' : 'error'" :closable="false">
        {{ result.msg }}
      </el-alert>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { createMilvusCollection, dropAndRecreateMilvusCollection, uploadDocToMilvus } from '@/api/admin'

const creating = ref(false)
const dropping = ref(false)
const uploadRef = ref<any>(null)
const uploading = ref(false)
const uploadFile = ref<File | null>(null)
const result = ref<{ success: boolean; msg: string } | null>(null)

function onFileChange(file: any) { uploadFile.value = file.raw as File }

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
  } catch {
    result.value = { success: false, msg: '重建失败' }
  } finally { dropping.value = false }
}
</script>
