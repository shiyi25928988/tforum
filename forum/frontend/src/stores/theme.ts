import { ref, watch } from 'vue'

export type Theme = 'light' | 'dark' | 'macaron'

const THEME_KEY = 'tforum_theme'

const saved = (localStorage.getItem(THEME_KEY) || 'light') as Theme
const current = ref<Theme>(saved)

// 应用主题到 html 元素
function applyTheme(theme: Theme) {
  const root = document.documentElement
  root.classList.remove('dark', 'macaron')
  if (theme === 'dark') {
    root.classList.add('dark')
  } else if (theme === 'macaron') {
    root.classList.add('macaron')
  }
}

applyTheme(current.value)

watch(current, (val) => {
  localStorage.setItem(THEME_KEY, val)
  applyTheme(val)
})

export function useThemeStore() {
  function setTheme(theme: Theme) {
    current.value = theme
  }

  return { current, setTheme }
}
