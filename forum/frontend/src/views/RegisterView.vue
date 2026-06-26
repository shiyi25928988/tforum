<template>
  <div class="auth-container">
    <div class="auth-bg"></div>
    <el-card class="auth-card" shadow="always">
      <div class="auth-logo">
        <span class="logo-icon">{{ siteConfig.logoIcon }}</span>
        <h1>{{ siteConfig.registerTitle }}</h1>
        <p class="subtitle">{{ siteConfig.registerDesc }}</p>
      </div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        size="large"
        @submit.prevent="handleRegister"
      >
        <el-form-item prop="account">
          <el-input
            v-model="form.account"
            placeholder="请设置账号"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请设置用户名"
            :prefix-icon="EditPen"
            clearable
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请设置密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleRegister"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            native-type="submit"
            :loading="loading"
            round
            class="submit-btn"
          >
            {{ loading ? '注册中...' : '注 册' }}
          </el-button>
        </el-form-item>
      </el-form>
      <p class="auth-footer">
        已有账号？<el-link type="primary" underline="hover" @click="$router.push('/login')">立即登录</el-link>
      </p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/user'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { encode } from '@/utils/crypto'
import { User, EditPen, Lock } from '@element-plus/icons-vue'
import { siteConfig } from '@/config/site'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  account: '',
  username: '',
  password: '',
  confirmPassword: '',
})

const validateConfirmPassword = (_rule: any, value: string, callback: Function) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  account: [
    { required: true, message: '请设置账号', trigger: 'blur' },
    { min: 2, max: 32, message: '账号长度为 2-32 位', trigger: 'blur' },
  ],
  username: [
    { required: true, message: '请设置用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度为 2-20 位', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请设置密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度为 6-32 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await register({
      account: form.account,
      username: form.username,
      password: encode(form.password),
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}
.auth-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0.06;
}
.auth-card {
  width: 420px;
  padding: 8px 16px;
  border-radius: 12px;
}
.auth-logo {
  text-align: center;
  margin-bottom: 28px;
}
.logo-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 12px;
}
.auth-logo h1 {
  font-size: 24px;
  color: #303133;
  margin: 0 0 4px;
}
.subtitle {
  font-size: 13px;
  color: #909399;
  margin: 0;
}
.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  letter-spacing: 4px;
}
.auth-footer {
  text-align: center;
  color: #909399;
  font-size: 14px;
}
</style>
