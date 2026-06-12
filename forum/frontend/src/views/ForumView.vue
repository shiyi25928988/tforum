<template>
  <div class="page-container">
    <div class="page-header" style="display: flex; justify-content: space-between; align-items: center">
      <h2>讨论区</h2>
      <el-button v-if="userStore.isLoggedIn" type="primary" :icon="Plus" @click="showTopicDialog = true">发起话题</el-button>
    </div>

    <el-row :gutter="20">
      <!-- 左侧分类导航 -->
      <el-col :span="4">
        <el-card shadow="never" class="category-card">
          <el-menu :default-active="activeCategory" @select="handleCategorySelect">
            <el-menu-item index="">
              <el-icon><ChatLineSquare /></el-icon>
              <span>全部话题</span>
            </el-menu-item>
            <el-menu-item
              v-for="cat in categories"
              :key="cat.id"
              :index="String(cat.id)"
            >
              <el-icon><Folder /></el-icon>
              <span>{{ cat.name }}</span>
              <el-tag size="small" class="cat-count">{{ cat.topicCount || 0 }}</el-tag>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <!-- 中间话题列表 -->
      <el-col :span="20">
        <div v-if="posts.length > 0" class="card-list">
          <el-card
            v-for="item in posts"
            :key="item.id"
            class="topic-card"
            shadow="hover"
            @click="$router.push(`/forum/post/${item.id}`)"
          >
            <div class="topic-body">
              <el-avatar :size="40" class="topic-avatar">
                {{ (authorNames[item.authorId] || '?').slice(0, 2) }}
              </el-avatar>
              <div class="topic-content">
                <div class="topic-title">
                  <span class="title-text">{{ item.title }}</span>
                  <el-tag v-if="item.categoryId" size="small" type="info" class="tag-ml">
                    {{ getCategoryName(item.categoryId) }}
                  </el-tag>
                </div>
                <p class="topic-preview">{{ item.content?.substring(0, 150) }}</p>
                <div class="topic-meta">
                  <span>{{ authorNames[item.authorId] || '用户' + item.authorId }}</span>
                  <span>{{ item.viewCount || 0 }} 浏览</span>
                  <span>{{ item.commentCount || 0 }} 回答</span>
                  <span>{{ formatTime(item.createdTime) }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </div>
        <el-empty v-else description="暂无话题，快来发起第一个吧！" />

        <div v-if="total > pageSize" style="margin-top: 20px; text-align: center">
          <el-pagination
            v-model:current-page="pageNum"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="fetchTopics"
          />
        </div>
      </el-col>

    </el-row>

    <!-- 发起话题弹窗 -->
    <el-dialog v-model="showTopicDialog" title="发起新话题" width="600px">
      <el-form :model="topicForm" label-width="80px">
        <el-form-item label="所属分组">
          <el-select v-model="topicForm.categoryId" placeholder="选择分组" clearable>
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="topicForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="topicForm.content" type="textarea" :rows="8" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showTopicDialog = false">取消</el-button>
        <el-button type="primary" @click="handleTopic" :loading="posting">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { listPosts, savePost, type ForumPost } from '@/api/forum'
import { listDiscussionCategories, type DiscussionCategory } from '@/api/forum'
import { getUserById } from '@/api/user'
import { ElMessage } from 'element-plus'
import { Plus, ChatLineSquare, Folder } from '@element-plus/icons-vue'
import { formatTime } from '@/utils/format'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const posts = ref<ForumPost[]>([])
const categories = ref<DiscussionCategory[]>([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const activeCategory = ref('')

const showTopicDialog = ref(false)
const posting = ref(false)
const topicForm = ref({ title: '', content: '', categoryId: undefined as number | undefined })
const authorNames = ref<Record<number, string>>({})

async function fetchCategories() {
  try {
    const res = await listDiscussionCategories()
    categories.value = res.data || []
  } catch {
    // ignore
  }
}

async function fetchTopics() {
  try {
    const catId = activeCategory.value ? Number(activeCategory.value) : undefined
    const res = await listPosts(pageNum.value, pageSize.value, catId)
    posts.value = res.data.records
    total.value = res.data.total
    loadAuthorNames(res.data.records.map((p: ForumPost) => p.authorId))
  } catch {
    // ignore
  }
}

function handleCategorySelect(index: string) {
  activeCategory.value = index
  pageNum.value = 1
  fetchTopics()
}

function getCategoryName(id: number): string {
  return categories.value.find(c => c.id === id)?.name || ''
}

async function loadAuthorNames(authorIds: number[]) {
  const ids = [...new Set(authorIds)].filter(id => id && !authorNames.value[id])
  for (const id of ids) {
    try {
      const res = await getUserById(id)
      authorNames.value[id] = res.data?.username || ('用户' + id)
    } catch { authorNames.value[id] = '用户' + id }
  }
}

async function handleTopic() {
  if (!topicForm.value.title.trim()) {
    ElMessage.warning('请输入标题')
    return
  }
  posting.value = true
  try {
    await savePost(topicForm.value)
    ElMessage.success('话题发布成功')
    showTopicDialog.value = false
    topicForm.value = { title: '', content: '', categoryId: undefined }
    fetchTopics()
  } catch {
    // ignore
  } finally {
    posting.value = false
  }
}

onMounted(() => {
  fetchCategories()
  fetchTopics()
})
</script>

<style scoped>
.category-card {
  position: sticky;
  top: 20px;
}
.cat-count {
  margin-left: auto;
  font-size: 11px;
}
.topic-card {
  cursor: pointer;
  transition: box-shadow 0.3s;
}
.topic-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.topic-body {
  display: flex;
  gap: 14px;
}
.topic-avatar {
  flex-shrink: 0;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  font-weight: 600;
}
.topic-content {
  flex: 1;
  min-width: 0;
}
.topic-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}
.title-text {
  font-size: 15px;
  font-weight: 600;
}
.tag-ml {
  flex-shrink: 0;
}
.topic-preview {
  color: #909399;
  margin-bottom: 8px;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.topic-meta {
  display: flex;
  gap: 16px;
  color: #909399;
  font-size: 12px;
  align-items: center;
}
</style>
