<template>
  <div>
    <h3>Skills 管理</h3>
    <el-table :data="skills" style="margin-top: 16px" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="图标" width="70">
        <template #default="{ row }">
          <el-image v-if="row.iconUrl" :src="row.iconUrl" fit="contain" style="width: 32px; height: 32px" />
          <span v-else style="font-size: 20px">🧩</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" min-width="120" show-overflow-tooltip />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="authorId" label="作者ID" width="80" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="downloadCount" label="下载" width="70" />
      <el-table-column prop="viewCount" label="浏览" width="70" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { adminListSkills, adminDeleteSkill } from '@/api/admin'
import { ElMessage } from 'element-plus'

const skills = ref<any[]>([])

async function fetch() {
  try { const res = await adminListSkills(); skills.value = res.data || [] } catch { /* */ }
}

async function handleDelete(id: number) {
  await adminDeleteSkill(id)
  ElMessage.success('已删除')
  fetch()
}

onMounted(fetch)
</script>
