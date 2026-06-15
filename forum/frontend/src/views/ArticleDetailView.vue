<template>
  <div class="page-container">
    <el-card v-loading="loading">
      <template #header>
        <el-image v-if="article?.coverImage" :src="article.coverImage" fit="cover" style="width: 100%; max-height: 300px; border-radius: 8px; margin-bottom: 16px" />
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
      <CommentItem
        v-for="c in topLevelComments"
        :key="c.id"
        :comment="c"
        :all-comments="comments"
        :show-author="true"
        :author-names="commentAuthorNames"
        @reply="replyTo"
      />
      <el-empty v-if="comments.length === 0" description="暂无评论" />
      <div style="margin-top: 16px; display: flex; gap: 8px">
        <el-input v-model="commentContent" :placeholder="replyTarget ? '回复 #' + replyTarget + '：' : '写下你的评论...'" />
        <el-button v-if="replyTarget" size="small" @click="cancelReply">取消</el-button>
        <el-button type="primary" @click="submitComment">发表</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getArticle, likeArticle, type Article } from '@/api/article'
import { saveComment, listComments, type ForumComment } from '@/api/forum'
import { getUserById } from '@/api/user'
import { ElMessage } from 'element-plus'
import { Star } from '@element-plus/icons-vue'
import { formatTime } from '@/utils/format'
import { marked } from 'marked'
import CommentItem from '@/components/CommentItem.vue'

const route = useRoute()
const article = ref<Article | null>(null)
const authorName = ref('')
const loading = ref(true)
const comments = ref<ForumComment[]>([])
const commentContent = ref('')
const replyTarget = ref<number | null>(null)
const renderedContent = ref('')
const commentAuthorNames = ref<Record<number, string>>({})

const topLevelComments = computed(() => comments.value.filter(c => !c.replyTo))

function replyTo(c: ForumComment) {
  replyTarget.value = c.id
  commentContent.value = ''
}

function cancelReply() {
  replyTarget.value = null
  commentContent.value = ''
}

async function fetchArticle() {
  try {
    const id = Number(route.params.id)
    const res = await getArticle(id)
    article.value = res.data
    renderedContent.value = await marked.parse(article.value?.content || '')
    if (article.value?.authorId) {
      try {
        const u = await getUserById(article.value.authorId)
        authorName.value = u.data?.username || ''
      } catch { /* */ }
    }
    const cRes = await listComments(id, 'article')
    comments.value = cRes.data || []
    loadCommentAuthorNames(comments.value)
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

async function loadCommentAuthorNames(commentList: ForumComment[]) {
  const ids = [...new Set(commentList.map(c => c.authorId))].filter(id => id && !commentAuthorNames.value[id])
  for (const id of ids) {
    try {
      const res = await getUserById(id)
      commentAuthorNames.value[id] = res.data?.username || ('用户' + id)
    } catch { commentAuthorNames.value[id] = '用户' + id }
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
    await saveComment({ postId: article.value.id, content: commentContent.value, commentType: 'article', replyTo: replyTarget.value ?? undefined })
    ElMessage.success('评论成功')
    commentContent.value = ''
    replyTarget.value = null
    const res = await listComments(article.value.id, 'article')
    comments.value = res.data || []
    loadCommentAuthorNames(comments.value)
  } catch {
    // ignore
  }
}

onMounted(fetchArticle)
</script>
