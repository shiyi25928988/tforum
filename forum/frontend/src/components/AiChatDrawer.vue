<template>
  <Teleport v-if="userStore.isLoggedIn" to="body">
    <div v-if="isOpen" class="ai-overlay" @click.self="isOpen = false">
      <div class="ai-chat" @click.stop>
        <div class="ai-chat-header">
          AI 助手
          <el-button size="small" text @click="handleClear">清除</el-button>
          <span class="ai-close" @click="isOpen = false">&times;</span>
        </div>
        <div class="ai-chat-msgs" ref="msgContainer">
          <div v-if="messages.length === 0 && !streaming" class="ai-empty">有什么可以帮你的？</div>
          <div v-for="(msg, i) in messages" :key="i" class="ai-msg" :class="msg.role">
            <div v-html="renderMsg(msg.content)" />
          </div>
          <div v-if="streaming" class="ai-msg asst"><span v-html="renderMsg(streamContent)" /><i class="blink">|</i></div>
        </div>
        <div class="ai-chat-foot">
          <input
            v-model="input"
            class="ai-input"
            placeholder="输入问题，Enter 发送"
            :disabled="streaming"
            @keydown.enter.prevent="handleSend"
          />
          <el-button type="primary" size="small" :disabled="!input.trim() || streaming" @click="handleSend">发送</el-button>
        </div>
      </div>
    </div>
    <div class="ai-fab" @click="isOpen = !isOpen" ref="fabRef">
      <svg viewBox="0 0 100 100" class="ai-fab-svg">
        <!-- 圆角三角形 -->
        <path d="M50 10 C55 10 58 13 60 16 L92 72 C95 78 92 84 86 84 L14 84 C8 84 5 78 8 72 L40 16 C42 13 45 10 50 10 Z" fill="#409eff" />
        <!-- 眼睛 - 眼白 -->
        <ellipse cx="50" cy="48" rx="21.6" ry="16.8" fill="white" />
        <!-- 瞳孔 -->
        <circle cx="50" cy="48" r="8.4" fill="#1a1a2e" ref="pupilRef" />
        <!-- 眼睛高光 -->
        <circle cx="46" cy="44" r="3" fill="white" />
      </svg>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, nextTick, computed, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isOpen = ref(false)
const input = ref('')
const messages = ref<{ role: string; content: string }[]>([])
const streaming = ref(false)
const streamContent = ref('')
const msgContainer = ref<HTMLElement | null>(null)
const pupilRef = ref<SVGCircleElement | null>(null)
const fabRef = ref<HTMLElement | null>(null)
const baseConvId = computed(() => 'user-' + (userStore.user?.id || 0))
const convId = ref(baseConvId.value)

function renderMsg(t: string) { return t.replace(/\n/g, '<br>') }

async function handleSend() {
  if (!input.value.trim() || streaming.value) return
  const userMsg = input.value.trim()
  messages.value.push({ role: 'user', content: userMsg })
  input.value = ''
  streaming.value = true; streamContent.value = ''
  await nextTick(); scrollBottom()

  const token = localStorage.getItem('tforum_token') || ''
  try {
    const res = await fetch(`${import.meta.env.VITE_API_BASE_URL || ''}/api/v1/ai/chat`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'token': token },
      body: JSON.stringify({ conversationId: convId.value, message: userMsg }),
    })
    const reader = res.body?.getReader(); if (!reader) throw 0
    const dec = new TextDecoder(); let buf = '', le = false
    while (true) {
      const { done, value } = await reader.read(); if (done) break
      buf += dec.decode(value, { stream: true })
      const ls = buf.split('\n'); buf = ls.pop() || ''
      for (const l of ls) {
        if (!l.startsWith('data:')) continue
        const t = l.substring(5)
        if (t === '' && le) { streamContent.value += '\n'; le = false }
        else if (t === '') { le = true }
        else { if (le) { streamContent.value += ' '; le = false }; streamContent.value += t }
      }
      scrollBottom()
    }
  } catch { streamContent.value += '\n[失败]' }
  finally {
    if (streamContent.value) messages.value.push({ role: 'assistant', content: streamContent.value })
    streamContent.value = ''; streaming.value = false
  }
}
function handleClear() { messages.value = []; convId.value = baseConvId.value + '-' + Date.now() }
function scrollBottom() { nextTick(() => { if (msgContainer.value) msgContainer.value.scrollTop = msgContainer.value.scrollHeight }) }

function handleMouseMove(e: MouseEvent) {
  if (!fabRef.value || !pupilRef.value) return
  const fabRect = fabRef.value.getBoundingClientRect()
  const fabCenterX = fabRect.left + fabRect.width / 2
  const fabCenterY = fabRect.top + fabRect.height / 2
  const dx = e.clientX - fabCenterX
  const dy = e.clientY - fabCenterY
  // 限制瞳孔最大偏移量
  const maxOffset = 5
  const dist = Math.sqrt(dx * dx + dy * dy)
  const scale = Math.min(dist, 200) / 200 * maxOffset
  const angle = Math.atan2(dy, dx)
  const px = Math.cos(angle) * scale
  const py = Math.sin(angle) * scale
  pupilRef.value.setAttribute('cx', String(50 + px))
  pupilRef.value.setAttribute('cy', String(48 + py))
}

onMounted(() => {
  window.addEventListener('mousemove', handleMouseMove)
})

onUnmounted(() => {
  window.removeEventListener('mousemove', handleMouseMove)
})
</script>

<style>
.ai-overlay{position:fixed;inset:0;z-index:99999;background:rgba(0,0,0,.15)}
.ai-chat{position:fixed;right:20px;bottom:80px;width:400px;height:520px;background:#fff;border-radius:12px;box-shadow:0 8px 32px rgba(0,0,0,.2);display:flex;flex-direction:column;overflow:hidden}
.ai-chat-header{display:flex;align-items:center;gap:8px;padding:10px 14px;border-bottom:1px solid #eee;font-weight:600;font-size:14px}
.ai-close{margin-left:auto;cursor:pointer;font-size:20px;color:#999}
.ai-chat-msgs{flex:1;overflow-y:auto;padding:12px 14px}
.ai-empty{text-align:center;color:#bbb;padding-top:60px;font-size:13px}
.ai-msg{margin-bottom:12px}
.ai-msg.user{text-align:right}
.ai-msg.user>div{display:inline-block;background:#409eff;color:#fff;border-radius:12px 12px 4px 12px;padding:6px 12px;max-width:85%;text-align:left}
.ai-msg.asst>div{display:inline-block;background:#f0f2f5;border-radius:12px 12px 12px 4px;padding:6px 12px;max-width:100%;line-height:1.6}
.blink{animation:bl 1s step-end infinite;color:#409eff;font-weight:700}@keyframes bl{50%{opacity:0}}
.ai-chat-foot{display:flex;gap:8px;padding:10px 14px;border-top:1px solid #eee;align-items:center}
.ai-input{flex:1;border:1px solid #dcdfe6;border-radius:6px;padding:6px 10px;font-size:13px;outline:none;font-family:inherit}
.ai-input:focus{border-color:#409eff}
.ai-fab{position:fixed;right:16px;bottom:20px;width:56px;height:56px;cursor:pointer;z-index:99998;filter:drop-shadow(0 4px 12px rgba(64,158,255,.4));user-select:none;transition:transform .2s}
.ai-fab:hover{transform:scale(1.1)}
.ai-fab-svg{width:100%;height:100%;display:block}
</style>
