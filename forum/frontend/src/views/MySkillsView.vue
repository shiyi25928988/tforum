<template>
  <div class="page-container" style="max-width: 800px">
    <div class="page-header" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px">
      <h2>我的 Skills</h2>
      <el-button type="primary" :icon="Plus" @click="$router.push('/skill/publish')">发布 Skill</el-button>
    </div>

    <el-table v-if="skills.length > 0" :data="skills" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="图标" width="70">
        <template #default="{ row }">
          <el-image v-if="row.iconUrl" :src="row.iconUrl" fit="contain" style="width: 36px; height: 36px" />
          <span v-else style="font-size: 24px">🧩</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" min-width="120" show-overflow-tooltip />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="downloadCount" label="下载" width="70" />
      <el-table-column prop="viewCount" label="浏览" width="70" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="$router.push(`/skill/publish/${row.id}`)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-else description="暂无 Skill" />

    <div v-if="total > pageSize" style="margin-top: 20px; text-align: center">
      <el-pagination
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchSkills"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { listMySkills, deleteSkill } from '@/api/skill'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const skills = ref<any[]>([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

async function fetchSkills() {
  try {
    const res = await listMySkills(pageNum.value, pageSize.value)
    skills.value = res.data.records
    total.value = res.data.total
  } catch { /* */ }
}

async function handleDelete(id: number) {
  await deleteSkill(id)
  ElMessage.success('已删除')
  fetchSkills()
}

onMounted(fetchSkills)
</script>
