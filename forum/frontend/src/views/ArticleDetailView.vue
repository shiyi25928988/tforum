<template>
  <div class="page-container">
    <el-card v-loading="loading">
      <template #header>
        <h1>{{ article?.title }}</h1>
        <div style="margin-top: 12px; display: flex; gap: 16px; color: #909399; font-size: 13px; flex-wrap: wrap">
          <span v-if="article?.tags">
            <el-tag v-for="t in article.tags.split(',')" :key="t" size="small" style="margin-right: 4px">{{ t }}</el-tag>
          </span>
          <span v-if="authorName">作者 {{ authorName }}</span>
          <span>浏览 {{ article?.viewCount || 0 }}</span>
          <span>点赞 {{ article?.likeCount || 0 }}</span>
          <span>{{ formatTime(article?.createdTime) }}</span>
          <el-button type="warning" size="small" :icon="Star" @click="handleLike">点赞</el-button>
        </div>
      </template>
      <div class="markdown-body" v-html="renderedContent" />
    </el-card>

    <!-- 评论区 -->
    <el-card style="margin-top: 20px" header="评论">
      <div v-for="c in comments" :key="c.id" style="padding: 12px 0; border-bottom: 1px solid #eee">
        <p>{{ c.content }}</p>
        <span style="color: #909399; font-size: 12px">{{ formatTime(c.createdTime) }}</span>
      </div>
      <el-empty v-if="comments.length === 0" description="暂无评论" />
      <div style="margin-top: 16px; display: flex; gap: 8px">
        <el-input v-model="commentContent" placeholder="写下你的评论..." />
        <el-button type="primary" @click="submitComment">发表</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getArticle, likeArticle, type Article } from '@/api/article'
import { saveComment, listComments, type ForumComment } from '@/api/forum'
import { getUserById } from '@/api/user'
import { ElMessage } from 'element-plus'
import { Star } from '@element-plus/icons-vue'
import { formatTime } from '@/utils/format'
import { marked } from 'marked'

const route = useRoute()
const article = ref<Article | null>(null)
const authorName = ref('')
const loading = ref(true)
const comments = ref<ForumComment[]>([])
const commentContent = ref('')
const renderedContent = ref('')

async function fetchArticle() {
  try {
    const id = Number(route.params.id)
    const res = await getArticle(id)
    article.value = res.data
    renderedContent.value = await marked.parse(article.value?.content || '')
    // 加载作者信息
    if (article.value?.authorId) {
      try {
        const u = await getUserById(article.value.authorId)
        authorName.value = u.data?.username || ''
      } catch { /* */ }
    }
    // 加载评论
    const cRes = await listComments(id)
    comments.value = cRes.data || []
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

async function handleLike() {
  if (!article.value) return
  try {
    await likeArticle(article.value.id)
    article.value.likeCount++
    ElMessage.success('点赞成功')
  } catch {
    // ignore
  }
}

async function submitComment() {
  if (!commentContent.value.trim() || !article.value) return
  try {
    await saveComment({ postId: article.value.id, content: commentContent.value })
    ElMessage.success('评论成功')
    commentContent.value = ''
    // 刷新评论
    const res = await listComments(article.value.id)
    comments.value = res.data || []
  } catch {
    // ignore
  }
}

onMounted(fetchArticle)
</script>
