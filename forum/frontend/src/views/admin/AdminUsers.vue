<template>
  <div>
    <h3>用户管理</h3>
    <el-table :data="users" style="margin-top: 16px" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="account" label="账号" width="150" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'disabled' ? 'danger' : 'success'" size="small">
            {{ row.status === 'disabled' ? '已禁用' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-popconfirm
            :title="row.status === 'disabled' ? '确定启用该用户？' : '确定禁用该用户？'"
            @confirm="handleToggle(row.id)"
          >
            <template #reference>
              <el-button :type="row.status === 'disabled' ? 'success' : 'warning'" size="small">
                {{ row.status === 'disabled' ? '启用' : '禁用' }}
              </el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { listUsers, toggleUserStatus } from '@/api/admin'
import { ElMessage } from 'element-plus'

const users = ref<any[]>([])

async function fetch() {
  try { const res = await listUsers(); users.value = res.data || [] } catch { /* */ }
}

async function handleToggle(id: number) {
  await toggleUserStatus(id)
  ElMessage.success('操作成功')
  fetch()
}

onMounted(fetch)
</script>
