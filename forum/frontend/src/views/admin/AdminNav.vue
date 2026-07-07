<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center">
      <h3>导航栏管理</h3>
      <el-button type="primary" size="small" @click="openAddDialog">新增栏目</el-button>
    </div>

    <el-table :data="items" style="margin-top: 16px" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="栏目名称" min-width="120" />
      <el-table-column prop="url" label="链接" min-width="200">
        <template #default="{ row }">
          <el-tag v-if="row.type === 'internal'" size="small" type="info">内部路由</el-tag>
          <el-tag v-else size="small" type="success">外部链接</el-tag>
          <span style="margin-left: 8px; font-size: 13px; color: var(--text-secondary)">{{ row.url }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-switch
            :model-value="row.isVisible === 1"
            :disabled="row.isSystem === 1 && row.sortOrder === 1"
            @change="(val: boolean) => handleToggleVisible(row.id!, val)"
          />
        </template>
      </el-table-column>
      <el-table-column label="类型" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.isSystem === 1" size="small" type="warning">系统内置</el-tag>
          <el-tag v-else size="small">自定义</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-popconfirm
            v-if="row.isSystem !== 1"
            title="确定删除该栏目？"
            @confirm="handleDelete(row.id!)"
          >
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑栏目' : '新增栏目'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="栏目名称">
          <el-input v-model="form.name" :disabled="form.isSystem === 1" />
        </el-form-item>
        <el-form-item label="链接URL">
          <el-input v-model="form.url" :disabled="form.isSystem === 1" />
          <div v-if="form.type === 'external'" style="font-size: 12px; color: var(--el-text-color-secondary); line-height: 1.6; margin-top: 4px">
            外部链接可使用占位符 <code>{token}</code>，用户点击跳转时会自动替换为当前登录 token。<br />
            示例：<code>https://other-app.com/sso?token={token}</code>
          </div>
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="form.type" :disabled="form.isSystem === 1">
            <el-radio value="internal">内部路由</el-radio>
            <el-radio value="external">外部链接</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="可选，emoji 或 icon class" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="1" :max="999" />
        </el-form-item>
        <el-form-item label="可见">
          <el-switch v-model="form.isVisibleBool" :disabled="form.isSystem === 1 && form.sortOrder === 1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { adminListNavItems, adminSaveNavItem, adminToggleNavVisible, adminDeleteNavItem, type NavItem } from '@/api/admin'
import { ElMessage } from 'element-plus'

const items = ref<NavItem[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)

const form = reactive<NavItem & { isVisibleBool: boolean }>({
  name: '',
  url: '',
  type: 'internal',
  icon: '',
  sortOrder: 99,
  isVisible: 1,
  isSystem: 0,
  isVisibleBool: true,
})

async function fetch() {
  try {
    const r = await adminListNavItems()
    items.value = (r as any).data || []
  } catch { /* */ }
}

function openAddDialog() {
  isEdit.value = false
  form.id = undefined
  form.name = ''
  form.url = ''
  form.type = 'external'
  form.icon = ''
  form.sortOrder = 99
  form.isVisible = 1
  form.isVisibleBool = true
  form.isSystem = 0
  dialogVisible.value = true
}

function openEditDialog(row: NavItem) {
  isEdit.value = true
  form.id = row.id
  form.name = row.name
  form.url = row.url
  form.type = row.type || 'internal'
  form.icon = row.icon || ''
  form.sortOrder = row.sortOrder || 99
  form.isVisible = row.isVisible
  form.isVisibleBool = row.isVisible === 1
  form.isSystem = row.isSystem
  dialogVisible.value = true
}

async function handleSave() {
  try {
    await adminSaveNavItem({
      id: form.id,
      name: form.name,
      url: form.url,
      type: form.type,
      icon: form.icon || undefined,
      sortOrder: form.sortOrder,
      isVisible: form.isVisibleBool ? 1 : 0,
    })
    ElMessage.success(isEdit.value ? '已更新' : '已添加')
    dialogVisible.value = false
    fetch()
  } catch { /* */ }
}

async function handleToggleVisible(id: number, val: boolean) {
  try {
    await adminToggleNavVisible(id)
    fetch()
  } catch { /* */ }
}

async function handleDelete(id: number) {
  await adminDeleteNavItem(id)
  ElMessage.success('已删除')
  fetch()
}

onMounted(fetch)
</script>
