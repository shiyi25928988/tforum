<template>
  <div class="page-container" style="max-width: 600px">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <h2>个人中心</h2>
          <el-button :icon="Close" text size="small" style="font-size: 18px; color: #909399" @click="$router.push('/')" />
        </div>
      </template>
      <el-form :model="form" label-width="80px">
        <el-form-item label="头像">
          <div style="display: flex; align-items: center; gap: 16px">
            <img :src="avatarSrc" class="avatar-img" />
            <el-button size="small" @click="randomAvatar">随机生成</el-button>
          </div>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="form.username" @input="onUsernameChange" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdate" :loading="saving">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { updateUserInfo } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { Close } from '@element-plus/icons-vue'
import multiavatar from '@multiavatar/multiavatar'

const userStore = useUserStore()
const saving = ref(false)
const avatarSeed = ref('')
const avatarSrc = ref('')

const form = ref({ avatar: '', username: '', phone: '', email: '' })

function genAvatar(seed: string) {
  avatarSeed.value = seed
  avatarSrc.value = 'data:image/svg+xml,' + encodeURIComponent(multiavatar(seed))
  form.value.avatar = 'mva:' + seed
}

function randomAvatar() {
  genAvatar(Math.random().toString(36).slice(2, 10))
}

function onUsernameChange() {
  if (form.value.username) genAvatar(form.value.username)
}

async function handleUpdate() {
  saving.value = true
  try {
    await updateUserInfo(form.value)
    ElMessage.success('更新成功')
    if (userStore.user) {
      Object.assign(userStore.user, form.value)
      localStorage.setItem('tforum_user', JSON.stringify(userStore.user))
    }
  } catch { /* ignore */ }
  finally { saving.value = false }
}

onMounted(() => {
  if (userStore.user) {
    const u = userStore.user
    form.value = {
      avatar: u.avatar || '',
      username: u.username || '',
      phone: u.phone || '',
      email: u.email || '',
    }
    // mva: 前缀表示 multiavatar 种子，否则是 URL
    if (u.avatar?.startsWith('mva:')) {
      avatarSrc.value = 'data:image/svg+xml,' + encodeURIComponent(multiavatar(u.avatar.substring(4)))
    } else if (u.avatar) {
      avatarSrc.value = u.avatar
    } else {
      genAvatar(u.username || 'user')
    }
  }
})
</script>

<style scoped>
.avatar-img {
  width: 80px; height: 80px;
  border-radius: 50%;
  border: 2px solid #e4e7ed;
  object-fit: cover;
}
</style>
