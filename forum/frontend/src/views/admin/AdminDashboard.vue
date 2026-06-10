<template>
  <div>
    <h3>仪表盘</h3>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6" v-for="s in stats" :key="s.label">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 32px; font-weight: 700; color: #409eff">{{ s.value }}</div>
            <div style="color: #909399; margin-top: 8px">{{ s.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDashboard } from '@/api/admin'

const stats = ref([
  { label: '用户数', value: 0 },
  { label: '文章数', value: 0 },
  { label: '讨论数', value: 0 },
  { label: '图书数', value: 0 },
])

onMounted(async () => {
  try {
    const res = await getDashboard()
    const d = res.data
    stats.value[0].value = d.userCount
    stats.value[1].value = d.articleCount
    stats.value[2].value = d.postCount
    stats.value[3].value = d.bookCount
  } catch { /* */ }
})
</script>
