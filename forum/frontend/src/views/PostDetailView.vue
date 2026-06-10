<template>
  <div class="page-container">
    <el-card v-loading="loading">
      <template #header>
        <h1>{{ post?.title }}</h1>
        <div style="margin-top: 12px; color: #909399; font-size: 13px">
          <span>浏览 {{ post?.viewCount || 0 }}</span>
          <span style="margin-left: 16px">{{ formatTime(post?.createdTime) }}</span>
        </div>
      </template>
      <div style="min-height: 200px; white-space: pre-wrap; line-height: 1.8">{{ post?.content }}</div>
    </el-card>

    <!-- 评论区 -->
    <el-card style="margin-top: 20px" header="评论">
      <div v-for="c in comments" :key="c.id" style="padding: 12px 0; border-bottom: 1px solid #eee">
        <p>{{ c.content }}</p>
        <span style="color: #909399; font-size: 12px">{{ formatTime(c.createdTime) }}</span>
      </div>
      <el-empty v-if="comments.length === 0" description="暂无回复，抢个沙发吧" />
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
import { getPost, listComments, saveComment, type ForumPost, type ForumComment } from '@/api/forum'
import { ElMessage } from 'element-plus'
import { formatTime } from '@/utils/format'

const route = useRoute()
const post = ref<ForumPost | null>(null)
const loading = ref(true)
const comments = ref<ForumComment[]>([])
const commentContent = ref('')

async function fetchPost() {
  try {
    const id = Number(route.params.id)
    const [postRes, commentRes] = await Promise.all([getPost(id), listComments(id)])
    post.value = postRes.data
    comments.value = commentRes.data || []
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

async function submitComment() {
  if (!commentContent.value.trim() || !post.value) return
  try {
    await saveComment({ postId: post.value.id, content: commentContent.value })
    ElMessage.success('评论成功')
    commentContent.value = ''
    const res = await listComments(post.value.id)
    comments.value = res.data || []
  } catch {
    // ignore
  }
}

onMounted(fetchPost)
</script>
