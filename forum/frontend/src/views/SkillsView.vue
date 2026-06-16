<template>
  <div class="page-container">
    <div class="page-header" style="display: flex; justify-content: space-between; align-items: center">
      <h2><el-icon><MagicStick /></el-icon> Skills</h2>
      <el-button v-if="userStore.isLoggedIn" type="primary" :icon="Plus" @click="$router.push('/skill/publish')">发布 Skill</el-button>
    </div>

    <!-- 搜索栏 -->
    <div style="margin-bottom: 20px; display: flex; gap: 12px; flex-wrap: wrap">
      <el-input
        v-model="keyword"
        placeholder="搜索 Skill 名称、描述..."
        clearable
        style="width: 320px"
        @keyup.enter="search"
      >
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-select v-model="selectedCategory" placeholder="分类" clearable style="width: 160px" @change="search">
        <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
      </el-select>
      <el-button @click="search">搜索</el-button>
    </div>

    <!-- Skills 卡片网格 -->
    <div v-loading="loading">
      <el-empty v-if="skills.length === 0" description="暂无 Skill" />
      <el-row v-else :gutter="20">
        <el-col v-for="skill in skills" :key="skill.id" :xs="24" :sm="12" :md="8" :lg="6" style="margin-bottom: 20px">
          <el-card shadow="hover" class="skill-card" @click="showDetail(skill)">
            <div class="skill-card-icon">
              <el-image v-if="skill.iconUrl" :src="skill.iconUrl" fit="contain" style="width: 48px; height: 48px" />
              <span v-else class="skill-icon-placeholder">🧩</span>
            </div>
            <div class="skill-card-name">{{ skill.name }}</div>
            <div class="skill-card-desc">{{ skill.description || '暂无描述' }}</div>
            <div class="skill-card-meta">
              <el-tag v-if="skill.category" size="small" type="info">{{ skill.category }}</el-tag>
              <span>⬇ {{ skill.downloadCount || 0 }}</span>
              <span>👁 {{ skill.viewCount || 0 }}</span>
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
        @current-change="fetchSkills"
      />
    </div>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="detailSkill?.name" width="700px" @close="detailSkill = null">
      <div v-if="detailSkill">
        <!-- 图标 + 基本信息 -->
        <div style="display: flex; gap: 16px; align-items: flex-start; margin-bottom: 20px">
          <el-image v-if="detailSkill.iconUrl" :src="detailSkill.iconUrl" fit="contain" style="width: 72px; height: 72px; border-radius: 12px; flex-shrink: 0" />
          <span v-else style="font-size: 48px; flex-shrink: 0">🧩</span>
          <div style="flex: 1">
            <!-- 描述 -->
            <p style="margin: 0 0 12px; color: #303133; font-size: 14px; line-height: 1.6">{{ detailSkill.description || '暂无描述' }}</p>
            <!-- 元信息 -->
            <div style="display: flex; flex-wrap: wrap; gap: 10px; align-items: center">
              <span style="color: #909399; font-size: 13px">
                <span style="margin-right: 4px">📂</span>分类：
                <el-tag v-if="detailSkill.category" size="small" type="primary">{{ detailSkill.category }}</el-tag>
                <span v-else style="color: #c0c4cc">未分类</span>
              </span>
              <span style="color: #909399; font-size: 13px">
                <span style="margin-right: 4px">🏷️</span>标签：
                <template v-if="tagList.length">
                  <el-tag v-for="t in tagList" :key="t" size="small" type="info" style="margin-right: 4px">{{ t }}</el-tag>
                </template>
                <span v-else style="color: #c0c4cc">无</span>
              </span>
            </div>
            <!-- 统计 -->
            <div style="margin-top: 8px; display: flex; gap: 16px; color: #909399; font-size: 12px">
              <span>⬇ 下载 {{ detailSkill.downloadCount || 0 }}</span>
              <span>👁 浏览 {{ detailSkill.viewCount || 0 }}</span>
            </div>
          </div>
        </div>
        <!-- 内容 -->
        <div class="skill-detail-content markdown-body" v-html="renderedContent" />
        <!-- Git 地址 -->
        <div v-if="detailSkill.gitUrl" style="margin-top: 12px; display: flex; align-items: center; gap: 8px">
          <span style="color: #909399; font-size: 13px">🔗 Git 仓库：</span>
          <el-link :href="detailSkill.gitUrl" target="_blank" type="primary">{{ detailSkill.gitUrl }}</el-link>
        </div>
        <!-- 附件 -->
        <div v-if="detailSkill.attachmentUrl" style="margin-top: 8px; display: flex; align-items: center; gap: 8px">
          <span style="color: #909399; font-size: 13px">📎 附件：</span>
          <el-link :href="detailSkill.attachmentUrl" target="_blank" type="primary">
            {{ detailSkill.attachmentUrl.split('/').pop() || '下载' }}
          </el-link>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="detailSkill?.attachmentUrl" type="primary" @click="handleDownload(detailSkill!)">下载附件</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { listSkills, downloadSkill, type Skill } from '@/api/skill'
import { MagicStick, Plus, Search } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'

const userStore = useUserStore()
const skills = ref<Skill[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)
const keyword = ref('')
const selectedCategory = ref('')
const categories = ref(['编程', '设计', '写作', '效率', 'AI', '数据分析', '其他'])

const detailVisible = ref(false)
const detailSkill = ref<Skill | null>(null)

const tagList = computed(() => {
  if (!detailSkill.value?.tags) return []
  return detailSkill.value.tags.split(',').map(t => t.trim()).filter(Boolean)
})

const renderedContent = computed(() => {
  if (!detailSkill.value?.content) return ''
  return marked.parse(detailSkill.value.content) as string
})

async function fetchSkills() {
  loading.value = true
  try {
    const res = await listSkills(pageNum.value, pageSize.value, selectedCategory.value || undefined, keyword.value || undefined)
    skills.value = res.data.records
    total.value = res.data.total
  } catch { /* */ }
  finally { loading.value = false }
}

function search() {
  pageNum.value = 1
  fetchSkills()
}

function showDetail(skill: Skill) {
  detailSkill.value = skill
  detailVisible.value = true
}

async function handleDownload(skill: Skill) {
  try {
    const res = await downloadSkill(skill.id)
    ElMessage.success('下载成功')
    // 复制内容到剪贴板
    const content = res.data || ''
    navigator.clipboard.writeText(content).then(() => {
      ElMessage.success('内容已复制到剪贴板')
    })
    fetchSkills()
  } catch { /* */ }
}

onMounted(fetchSkills)
</script>

<style scoped>
.skill-card {
  cursor: pointer;
  text-align: center;
  transition: box-shadow 0.3s;
  padding: 8px;
}
.skill-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
.skill-card-icon {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
}
.skill-icon-placeholder {
  font-size: 40px;
}
.skill-card-name {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.skill-card-desc {
  font-size: 12px;
  color: #909399;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.skill-card-meta {
  display: flex;
  justify-content: center;
  gap: 12px;
  font-size: 12px;
  color: #c0c4cc;
  align-items: center;
}

/* 详情弹窗 md 内容样式 */
.skill-detail-content {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  max-height: 400px;
  overflow-y: auto;
  line-height: 1.8;
  font-size: 14px;
}
.skill-detail-content :deep(h1) { font-size: 1.5em; margin: 0.5em 0; }
.skill-detail-content :deep(h2) { font-size: 1.3em; margin: 0.5em 0; }
.skill-detail-content :deep(h3) { font-size: 1.15em; margin: 0.4em 0; }
.skill-detail-content :deep(p) { margin: 0.4em 0; }
.skill-detail-content :deep(ul), .skill-detail-content :deep(ol) { padding-left: 1.5em; margin: 0.4em 0; }
.skill-detail-content :deep(blockquote) {
  border-left: 3px solid #409eff;
  padding-left: 12px;
  color: #606266;
  margin: 0.4em 0;
}
.skill-detail-content :deep(code) {
  background: #e8eaed;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: monospace;
  font-size: 0.9em;
}
.skill-detail-content :deep(pre) {
  background: #e8eaed;
  padding: 12px;
  border-radius: 6px;
  overflow-x: auto;
}
.skill-detail-content :deep(pre code) {
  background: none;
  padding: 0;
}
.skill-detail-content :deep(table) {
  border-collapse: collapse;
  width: 100%;
}
.skill-detail-content :deep(th), .skill-detail-content :deep(td) {
  border: 1px solid #dcdfe6;
  padding: 6px 12px;
  text-align: left;
}
.skill-detail-content :deep(th) { background: #f0f2f5; }
.skill-detail-content :deep(img) { max-width: 100%; }
.skill-detail-content :deep(a) { color: #409eff; }
</style>
