<template>
  <div class="editor-page">
    <!-- AI 生成进度条 -->
    <div v-if="aiLoading" class="ai-progress-bar" />
    <!-- 顶部工具栏 -->
    <div class="editor-topbar">
      <el-input
        v-model="form.title"
        placeholder="请输入文章标题..."
        class="title-input"
        size="large"
      />
      <el-input
        v-model="form.summary"
        placeholder="摘要（可选）"
        class="summary-input"
        size="small"
        clearable
      />
      <div class="topbar-actions">
        <el-select
          v-model="selectedTags"
          multiple
          filterable
          allow-create
          default-first-option
          placeholder="选择标签"
          size="small"
          style="width: 220px"
          clearable
        >
          <el-option
            v-for="tag in articleTags"
            :key="tag.id"
            :label="tag.name"
            :value="tag.name"
          />
        </el-select>
        <el-radio-group v-model="form.status" size="small">
          <el-radio-button :value="1">发布</el-radio-button>
          <el-radio-button :value="0">草稿</el-radio-button>
        </el-radio-group>
        <el-button type="warning" :icon="MagicStick" @click="showAiDialog = true">AI 助手</el-button>
        <el-button type="success" :icon="DocumentChecked" @click="handleAiReview" :loading="reviewing">AI 审核</el-button>
        <el-button type="primary" :icon="Check" @click="handleSave" :loading="saving">
          {{ isEdit ? '更新' : '提交' }}
        </el-button>
        <el-button :icon="Close" @click="handleCancel" style="color: #606266; border-color: #c0c4cc">取消</el-button>
      </div>
    </div>

    <!-- AI 审核结果弹窗 -->
    <el-dialog v-model="reviewDialogVisible" title="AI 文章审核结果" width="560px" destroy-on-close>
      <div v-if="reviewResult" class="review-result">
        <div class="review-status" :class="{ approved: reviewResult.approved, rejected: reviewResult.approved === false }">
          <span v-if="reviewResult.approved === true">✅ 审核通过</span>
          <span v-else-if="reviewResult.approved === false">❌ 审核未通过</span>
          <span v-else>⚠️ 审核结果不确定</span>
        </div>
        <div class="review-score">
          综合评分：<span class="score-num">{{ reviewResult.score }}</span> / 10
          <el-progress :percentage="(reviewResult.score || 0) * 10" :color="scoreColor" :stroke-width="8" style="margin-top: 6px" />
        </div>
        <div class="review-feedback">
          <h4>审核意见</h4>
          <p>{{ reviewResult.feedback }}</p>
        </div>
        <div v-if="reviewResult.issues?.length" class="review-issues">
          <h4>⚠️ 发现的问题</h4>
          <ul>
            <li v-for="(item, i) in reviewResult.issues" :key="'i' + i">{{ item }}</li>
          </ul>
        </div>
        <div v-if="reviewResult.suggestions?.length" class="review-suggestions">
          <h4>💡 改进建议</h4>
          <ul>
            <li v-for="(item, i) in reviewResult.suggestions" :key="'s' + i">{{ item }}</li>
          </ul>
        </div>
      </div>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">关闭</el-button>
        <el-button v-if="!reviewResult?.approved" type="primary" @click="reviewDialogVisible = false; doSave()">仍然发布</el-button>
        <el-button v-else type="primary" @click="reviewDialogVisible = false">知道了</el-button>
      </template>
    </el-dialog>

    <!-- AI 助手内联区域 -->
    <div v-if="showAiDialog" class="ai-inline">
      <el-input
        v-model="aiPrompt"
        placeholder="描述你想写的内容，例如：帮我写一篇 Spring Boot 微服务架构的技术文章..."
        class="ai-input"
        clearable
        @keydown.ctrl.enter="handleAiGenerate"
      >
        <template #append>
          <el-button :loading="aiLoading" @click="aiLoading ? undefined : handleAiGenerate()">
            {{ aiLoading ? '生成中...' : '生成' }}
          </el-button>
        </template>
      </el-input>
      <div class="ai-inline-tip">
        Ctrl+Enter 发送 ·
        <span v-if="aiLoading" style="color: #409eff">正在生成...</span>
        <el-link v-else type="primary" underline="never" @click="showAiDialog = false; aiPrompt = ''">收起</el-link>
      </div>
    </div>

    <!-- Markdown 编辑器（占据剩余全部空间） -->
    <div class="editor-body">
      <!-- 封面图片区域（可收起） -->
      <div class="cover-bar">
        <div class="cover-bar-header" @click="coverExpanded = !coverExpanded">
          <el-icon :size="14" style="margin-right: 4px; transition: transform 0.3s" :style="{ transform: coverExpanded ? 'rotate(90deg)' : '' }"><ArrowRight /></el-icon>
          <span>封面图片</span>
          <span v-if="coverPreview" style="color: #67c23a; margin-left: 8px; font-size: 12px">已设置</span>
          <span v-else style="color: #909399; margin-left: 8px; font-size: 12px">未设置</span>
        </div>
        <div v-show="coverExpanded" class="cover-bar-body">
          <div v-if="coverPreview" class="cover-preview">
            <el-image :src="coverPreview" fit="cover" style="width: 240px; height: 135px; border-radius: 4px" />
            <div class="cover-actions">
              <el-button size="small" @click="triggerCoverUpload">更换</el-button>
              <el-button size="small" type="danger" @click="removeCover">移除</el-button>
            </div>
          </div>
          <div v-else class="cover-placeholder" @click="triggerCoverUpload">
            <el-icon :size="20"><Plus /></el-icon>
            <span>点击上传封面图片</span>
          </div>
          <input ref="coverInputRef" type="file" accept="image/*" style="display: none" @change="handleCoverFileChange" />
        </div>
      </div>
      <md-editor
        ref="mdEditorRef"
        v-model="form.content"
        :toolbars="toolbars"
        :preview="true"
        :footers="['markdownTotal']"
        style="height: 100%"
        :on-upload-img="handleUploadImg"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { saveArticle, getArticle, listArticleTags, saveArticleTag, reviewArticle, type ArticleTag, type AiReviewResponse } from '@/api/article'
import { uploadToFolder } from '@/api/oss'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, Close, MagicStick, Plus, ArrowRight, DocumentChecked } from '@element-plus/icons-vue'
import { MdEditor, type ToolbarNames } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
const showAiDialog = ref(false)
const aiPrompt = ref('')
const aiLoading = ref(false)
const aiContentBefore = ref('')

// AI 审核
const reviewing = ref(false)
const reviewDialogVisible = ref(false)
const reviewResult = ref<AiReviewResponse | null>(null)
const scoreColor = computed(() => {
  const s = reviewResult.value?.score || 0
  if (s >= 8) return '#67c23a'
  if (s >= 6) return '#e6a23c'
  return '#f56c6c'
})

async function handleAiReview() {
  if (!form.value.title.trim() && !form.value.content.trim()) {
    ElMessage.warning('请先输入文章内容')
    return
  }
  reviewing.value = true
  try {
    const res = await reviewArticle({ title: form.value.title, content: form.value.content })
    reviewResult.value = res.data
    reviewDialogVisible.value = true
  } catch {
    ElMessage.error('AI 审核失败，请稍后重试')
  } finally {
    reviewing.value = false
  }
}

async function handleAiGenerate() {
  if (!aiPrompt.value.trim() || aiLoading.value) return
  aiLoading.value = true
  aiContentBefore.value = form.value.content || ''

  const token = localStorage.getItem('tforum_token') || ''
  const base = import.meta.env.VITE_API_BASE_URL || ''
  const url = `${base}/api/v1/ai/chat`

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'token': token },
      body: JSON.stringify({ conversationId: 'editor-' + Date.now(), message: aiPrompt.value }),
    })

    const reader = response.body?.getReader()
    if (!reader) throw new Error('No stream')

    const decoder = new TextDecoder()
    let buffer = ''
    form.value.content = aiContentBefore.value + '\n\n'

    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })

      // 解析 SSE 格式: "data:文字\n\n"
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''

      for (const line of lines) {
        if (line === 'data:') {
          // 空的 data: 表示换行
          form.value.content += '\n'
        } else if (line.startsWith('data:')) {
          form.value.content += line.substring(5)
        }
      }
    }
  } catch {
    ElMessage.error('AI 生成失败')
    form.value.content = aiContentBefore.value
  } finally {
    scrollPreviewToBottom()
    aiLoading.value = false
  }
}

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const saving = ref(false)

const articleTags = ref<ArticleTag[]>([])
const selectedTags = ref<string[]>([])

const form = ref({
  title: '',
  content: '',
  summary: '',
  coverImage: '',
  categoryId: undefined as number | undefined,
  status: 1,
})

const coverPreview = ref('')
const coverInputRef = ref<HTMLInputElement>()
const coverExpanded = ref(false)
const mdEditorRef = ref<any>(null)

function scrollPreviewToBottom() {
  nextTick(() => {
    const editor = mdEditorRef.value?.$el
    if (!editor) return
    // 只滚动右侧预览区，左侧编辑区由 md-editor 自己管理
    const right = editor.querySelector('.md-editor-preview-wrapper')
    if (right) right.scrollTop = right.scrollHeight
  })
}

// 工具栏配置：所有 Markdown 语法按钮
const toolbars: ToolbarNames[] = [
  'bold',
  'underline',
  'italic',
  'strikeThrough',
  '-',
  'title',
  'sub',
  'sup',
  'quote',
  'unorderedList',
  'orderedList',
  'task',
  '-',
  'codeRow',
  'code',
  'link',
  'image',
  'table',
  'mermaid',
  'katex',
  '-',
  'revoke',
  'next',
  'save',
  '=',
  'pageFullscreen',
  'fullscreen',
  'preview',
  'htmlPreview',
  'catalog',
]

async function fetchTags() {
  try {
    const res = await listArticleTags()
    articleTags.value = res.data || []
  } catch { /* ignore */ }
}

async function fetchArticle() {
  if (!isEdit.value) return
  try {
    const res = await getArticle(Number(route.params.id))
    const data = res.data
    form.value = {
      title: data.title,
      content: data.content,
      summary: data.summary || '',
      coverImage: data.coverImage || '',
      categoryId: data.categoryId,
      status: data.status,
    }
    coverPreview.value = data.coverImage || ''
    // 已有标签转为数组
    if (data.tags) {
      selectedTags.value = data.tags.split(',').map((t: string) => t.trim()).filter(Boolean)
    }
  } catch {
    // ignore
  }
}

async function handleSave() {
  if (!form.value.title.trim()) {
    ElMessage.warning('请输入文章标题')
    return
  }

  // 发布文章前先调用 AI 审核，草稿则直接保存
  if (form.value.status === 1) {
    await doReviewThenSave()
  } else {
    await doSave()
  }
}

async function doReviewThenSave() {
  reviewing.value = true
  try {
    const res = await reviewArticle({ title: form.value.title, content: form.value.content })
    reviewResult.value = res.data
    reviewDialogVisible.value = true

    // 审核通过则自动保存，不通过则让用户确认是否仍要发布
    if (res.data.approved) {
      ElMessage.success('AI 审核通过，自动提交发布')
      await doSave()
    } else {
      ElMessage.warning('AI 审核未通过，请查看审核意见后决定是否仍要发布')
    }
  } catch {
    ElMessage.error('AI 审核失败，请稍后重试')
  } finally {
    reviewing.value = false
  }
}

async function doSave() {
  // 新增标签自动保存到库
  if (selectedTags.value.length > 0) {
    for (const name of selectedTags.value) {
      if (!articleTags.value.some(t => t.name === name)) {
        try { await saveArticleTag(name) } catch { /* ignore */ }
      }
    }
  }
  saving.value = true
  try {
    const payload: any = { ...form.value, tags: selectedTags.value.join(',') }
    if (isEdit.value) {
      payload.id = Number(route.params.id)
    }
    await saveArticle(payload)
    ElMessage.success(isEdit.value ? '更新成功' : '发布成功')
    router.push('/')
  } catch {
    // handled by interceptor
  } finally {
    saving.value = false
  }
}

async function handleUploadImg(files: File[], callback: (urls: string[]) => void) {
  const urls: string[] = []
  for (const file of files) {
    try {
      const folder = isEdit.value ? `image/${route.params.id}/` : `image/temp/`
      const res: any = await uploadToFolder(file, folder)
      urls.push(res.data || res || '')
    } catch {
      ElMessage.error('图片上传失败')
    }
  }
  callback(urls)
}

function handleCancel() {
  if (form.value.title || form.value.content) {
    ElMessageBox.confirm('内容未保存，确定离开？', '提示', { type: 'warning' })
      .then(() => router.back())
      .catch(() => {})
  } else {
    router.back()
  }
}

function triggerCoverUpload() {
  coverInputRef.value?.click()
}

async function handleCoverFileChange(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  // 先本地预览
  const reader = new FileReader()
  reader.onload = (ev) => {
    coverPreview.value = ev.target?.result as string
  }
  reader.readAsDataURL(file)

  // 上传到 OSS
  try {
    const folder = isEdit.value ? `cover/${route.params.id}/` : `cover/temp/`
    const res: any = await uploadToFolder(file, folder)
    form.value.coverImage = res.data || res || ''
    ElMessage.success('封面上传成功')
  } catch {
    ElMessage.error('封面上传失败')
  }

  // 重置 input 以便重复选择同一文件
  input.value = ''
}

function removeCover() {
  coverPreview.value = ''
  form.value.coverImage = ''
}

onMounted(() => { fetchTags(); fetchArticle() })
</script>

<style scoped>
/* AI 生成进度条 - 顶部无限循环动画 */
.ai-progress-bar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  z-index: 200;
  background: linear-gradient(90deg, #409eff, #67c23a, #e6a23c, #f56c6c, #409eff);
  background-size: 200% 100%;
  animation: ai-progress-slide 1.5s linear infinite;
}
@keyframes ai-progress-slide {
  0% { background-position: 200% 0; }
  100% { background-position: 0 0; }
}

.editor-page {
  position: fixed;
  inset: 0;
  display: flex;
  flex-direction: column;
  z-index: 100;
  background: #fff;
}
.editor-topbar {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 16px;
  border-bottom: 1px solid #e4e7ed;
  background: #fff;
  flex-wrap: wrap;
}
.title-input {
  min-width: 300px;
  flex: 1;
}
.title-input :deep(.el-input__inner) {
  border: none;
  font-size: 18px;
  font-weight: 600;
}
.summary-input {
  flex: 1 1 100%;
  min-width: 0;
}
.topbar-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}
.editor-body {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.cover-bar {
  flex-shrink: 0;
  border-bottom: 1px solid #e4e7ed;
  background: #fafafa;
}
.cover-bar-header {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  cursor: pointer;
  font-size: 13px;
  color: #606266;
  user-select: none;
}
.cover-bar-header:hover {
  background: #f0f0f0;
}
.cover-bar-body {
  padding: 0 16px 12px;
}
.cover-preview {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}
.cover-actions {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.cover-placeholder {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 240px;
  height: 60px;
  border: 2px dashed #d9d9d9;
  border-radius: 4px;
  cursor: pointer;
  color: #909399;
  font-size: 13px;
  justify-content: center;
  transition: border-color 0.3s, color 0.3s;
}
.cover-placeholder:hover {
  border-color: #409eff;
  color: #409eff;
}
.ai-inline {
  padding: 10px 16px;
  border-bottom: 1px solid #e4e7ed;
  background: #fafafa;
}
.ai-input {
  width: 100%;
}
.ai-inline-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
.ai-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

/* AI 审核结果样式 */
.review-result h4 {
  margin: 12px 0 6px;
  font-size: 14px;
  color: #303133;
}
.review-status {
  font-size: 18px;
  font-weight: 600;
  text-align: center;
  padding: 12px;
  border-radius: 8px;
  background: #f5f5f5;
}
.review-status.approved { background: #f0f9eb; color: #67c23a; }
.review-status.rejected { background: #fef0f0; color: #f56c6c; }
.review-score {
  margin-top: 12px;
  font-size: 14px;
}
.score-num {
  font-size: 24px;
  font-weight: 700;
  color: #409eff;
}
.review-feedback p {
  color: #606266;
  line-height: 1.6;
  margin: 0;
}
.review-issues ul, .review-suggestions ul {
  margin: 0;
  padding-left: 20px;
}
.review-issues li, .review-suggestions li {
  color: #606266;
  line-height: 1.8;
}
</style>
