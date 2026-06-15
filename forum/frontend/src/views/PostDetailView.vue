<template>
  <div class="page-container">
    <el-card v-loading="loading">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: flex-start">
          <h1 style="margin: 0">{{ post?.title }}</h1>
          <el-popconfirm
            v-if="canDelete"
            title="确定删除该话题？"
            @confirm="handleDelete"
          >
            <template #reference>
              <el-button type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </div>
        <div style="margin-top: 12px; color: #909399; font-size: 13px">
          <span>浏览 {{ post?.viewCount || 0 }}</span>
          <span style="margin-left: 16px">{{ formatTime(post?.createdTime) }}</span>
        </div>
      </template>
      <div style="min-height: 200px; white-space: pre-wrap; line-height: 1.8">{{ post?.content }}</div>
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
      <el-empty v-if="comments.length === 0" description="暂无回复，抢个沙发吧" />
      <div style="margin-top: 16px; display: flex; gap: 8px">
        <el-input v-model="commentContent" :placeholder="replyTarget ? '回复 #' + replyTarget + '：' : '写下你的评论...'" />
        <el-button v-if="replyTarget" size="small" @click="cancelReply">取消</el-button>
        <el-button type="primary" @click="submitComment">发表</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPost, listComments, saveComment, deletePost, type ForumPost, type ForumComment } from '@/api/forum'
import { getUserById } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { formatTime } from '@/utils/format'
import CommentItem from '@/components/CommentItem.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const post = ref<ForumPost | null>(null)
const loading = ref(true)
const comments = ref<ForumComment[]>([])
const commentContent = ref('')
const replyTarget = ref<number | null>(null)
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

const canDelete = computed(() => {
  if (!post.value || !userStore.user) return false
  return post.value.authorId === userStore.user.id || userStore.user.role === 'admin'
})

async function handleDelete() {
  if (!post.value) return
  try {
    await deletePost(post.value.id)
    ElMessage.success('已删除')
    router.push('/forum')
  } catch { /* */ }
}

async function fetchPost() {
  try {
    const id = Number(route.params.id)
    const [postRes, commentRes] = await Promise.all([getPost(id), listComments(id, 'post')])
    post.value = postRes.data
    comments.value = commentRes.data || []
    loadCommentAuthorNames(comments.value)
  } catch {
    // ignore
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

async function submitComment() {
  if (!commentContent.value.trim() || !post.value) return
  try {
    await saveComment({ postId: post.value.id, content: commentContent.value, commentType: 'post', replyTo: replyTarget.value ?? undefined })
    ElMessage.success('评论成功')
    commentContent.value = ''
    replyTarget.value = null
    const res = await listComments(post.value.id, 'post')
    comments.value = res.data || []
    loadCommentAuthorNames(comments.value)
  } catch {
    // ignore
  }
}

onMounted(fetchPost)
</script>
