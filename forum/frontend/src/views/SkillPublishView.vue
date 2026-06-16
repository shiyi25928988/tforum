<template>
  <div class="page-container" style="max-width: 800px">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <h2>{{ isEdit ? '编辑 Skill' : '发布 Skill' }}</h2>
          <el-button :icon="Close" text size="small" style="font-size: 18px; color: #909399" @click="$router.push('/skills')" />
        </div>
      </template>
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" placeholder="Skill 名称" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" placeholder="选择分类" clearable style="width: 100%">
            <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="简短描述这个 Skill 的功能" />
        </el-form-item>
        <el-form-item label="图标">
          <div v-if="iconPreview" style="display: flex; align-items: center; gap: 12px">
            <el-image :src="iconPreview" fit="contain" style="width: 64px; height: 64px; border-radius: 8px; border: 1px solid #eee" />
            <div>
              <el-button size="small" @click="triggerIconInput">更换</el-button>
              <el-button size="small" type="danger" @click="removeIcon">移除</el-button>
            </div>
          </div>
          <div v-else style="width: 64px; height: 64px; border: 2px dashed #d9d9d9; border-radius: 8px; display: flex; align-items: center; justify-content: center; cursor: pointer; color: #909399; font-size: 12px" @click="triggerIconInput">
            + 图标
          </div>
          <input ref="iconInputRef" type="file" accept="image/*" style="display: none" @change="handleIconChange" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder="多个标签用逗号分隔，如：Python, 自动化" />
        </el-form-item>
        <el-form-item label="内容" required>
          <md-editor
            v-model="form.content"
            :toolbars="skillToolbars"
            :preview="true"
            style="height: 300px"
          />
        </el-form-item>
        <el-form-item label="附件">
          <div v-if="attachmentName" style="display: flex; align-items: center; gap: 8px">
            <span style="color: #409eff">{{ attachmentName }}</span>
            <el-button size="small" type="danger" @click="removeAttachment">移除</el-button>
          </div>
          <div v-else style="display: flex; align-items: center; gap: 8px">
            <el-button size="small" @click="triggerAttachmentInput">选择文件</el-button>
            <span style="color: #909399; font-size: 12px">支持 .zip 格式</span>
          </div>
          <input ref="attachmentInputRef" type="file" accept=".zip" style="display: none" @change="handleAttachmentChange" />
        </el-form-item>
        <el-form-item label="Git地址">
          <el-input v-model="form.gitUrl" placeholder="Git 仓库地址，如 https://github.com/xxx/yyy" />
        </el-form-item>
        <el-form-item>
          <el-radio-group v-model="form.status">
            <el-radio-button :value="1">发布</el-radio-button>
            <el-radio-button :value="0">草稿</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">{{ isEdit ? '更新' : '提交' }}</el-button>
          <el-button @click="$router.push('/skills')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { saveSkill, getSkill } from '@/api/skill'
import { ElMessage } from 'element-plus'
import { Close } from '@element-plus/icons-vue'
import { MdEditor, type ToolbarNames } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const saving = ref(false)

const categories = ['编程', '设计', '写作', '效率', 'AI', '数据分析', '其他']

// 简单 md 工具栏
const skillToolbars: ToolbarNames[] = [
  'bold', 'italic', 'strikeThrough', '-',
  'title', 'quote', 'unorderedList', 'orderedList', '-',
  'codeRow', 'code', 'link', 'image', 'table', '-',
  'preview',
]

const form = ref({
  name: '',
  description: '',
  content: '',
  category: '',
  tags: '',
  status: 1,
  gitUrl: '',
})

const iconFile = ref<File | null>(null)
const iconPreview = ref('')
const iconInputRef = ref<HTMLInputElement>()
const attachmentFile = ref<File | null>(null)
const attachmentName = ref('')
const attachmentInputRef = ref<HTMLInputElement>()

function triggerIconInput() {
  iconInputRef.value?.click()
}

function handleIconChange(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  iconFile.value = file
  const reader = new FileReader()
  reader.onload = (ev) => { iconPreview.value = ev.target?.result as string }
  reader.readAsDataURL(file)
  input.value = ''
}

function removeIcon() {
  iconFile.value = null
  iconPreview.value = ''
}

function triggerAttachmentInput() {
  attachmentInputRef.value?.click()
}

function handleAttachmentChange(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  attachmentFile.value = file
  attachmentName.value = file.name
  input.value = ''
}

function removeAttachment() {
  attachmentFile.value = null
  attachmentName.value = ''
}

async function fetchSkill() {
  if (!isEdit.value) return
  try {
    const res = await getSkill(Number(route.params.id))
    const data = res.data
    form.value = {
      name: data.name,
      description: data.description || '',
      content: data.content || '',
      category: data.category || '',
      tags: data.tags || '',
      status: data.status,
      gitUrl: data.gitUrl || '',
    }
    if (data.iconUrl) {
      iconPreview.value = data.iconUrl
    }
    if (data.attachmentUrl) {
      attachmentName.value = data.attachmentUrl.split('/').pop() || '已上传'
    }
  } catch { /* */ }
}

async function handleSave() {
  if (!form.value.name.trim()) { ElMessage.warning('请输入名称'); return }
  if (!form.value.content.trim()) { ElMessage.warning('请输入内容'); return }
  saving.value = true
  try {
    const fd = new FormData()
    if (isEdit.value) fd.append('id', route.params.id as string)
    fd.append('name', form.value.name)
    fd.append('description', form.value.description)
    fd.append('content', form.value.content)
    fd.append('category', form.value.category)
    fd.append('tags', form.value.tags)
    fd.append('status', String(form.value.status))
    if (iconFile.value) fd.append('iconFile', iconFile.value)
    if (attachmentFile.value) fd.append('attachmentFile', attachmentFile.value)
    fd.append('gitUrl', form.value.gitUrl)
    await saveSkill(fd)
    ElMessage.success(isEdit.value ? '更新成功' : '发布成功')
    router.push('/skills')
  } catch { /* */ }
  finally { saving.value = false }
}

onMounted(fetchSkill)
</script>
