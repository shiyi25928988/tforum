import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

import App from './App.vue'
import router from './router'
import './styles/main.css'
import './styles/themes.css'

import { siteConfig } from './config/site'

// 设置浏览器标签页标题
document.title = siteConfig.htmlTitle

// ========================================
// md-editor-v3 本地化配置：将所有 CDN 资源替换为本地路径
// 内网环境无法访问 unpkg.com，必须打包到前端
// ========================================
import { config } from 'md-editor-v3'

const LOCAL_LIBS = '/libs'

config({
  editorExtensions: {
    highlight: {
      js: `${LOCAL_LIBS}/highlight.js/highlight.min.js`,
      css: {
        a11y:      { light: `${LOCAL_LIBS}/highlight.js/styles/a11y-light.min.css`,      dark: `${LOCAL_LIBS}/highlight.js/styles/a11y-dark.min.css` },
        atom:      { light: `${LOCAL_LIBS}/highlight.js/styles/atom-one-light.min.css`,   dark: `${LOCAL_LIBS}/highlight.js/styles/atom-one-dark.min.css` },
        github:    { light: `${LOCAL_LIBS}/highlight.js/styles/github.min.css`,            dark: `${LOCAL_LIBS}/highlight.js/styles/github-dark.min.css` },
        gradient:  { light: `${LOCAL_LIBS}/highlight.js/styles/gradient-light.min.css`,    dark: `${LOCAL_LIBS}/highlight.js/styles/gradient-dark.min.css` },
        kimbie:    { light: `${LOCAL_LIBS}/highlight.js/styles/kimbie-light.min.css`,      dark: `${LOCAL_LIBS}/highlight.js/styles/kimbie-dark.min.css` },
        paraiso:   { light: `${LOCAL_LIBS}/highlight.js/styles/paraiso-light.min.css`,     dark: `${LOCAL_LIBS}/highlight.js/styles/paraiso-dark.min.css` },
        qtcreator: { light: `${LOCAL_LIBS}/highlight.js/styles/qtcreator-light.min.css`,   dark: `${LOCAL_LIBS}/highlight.js/styles/qtcreator-dark.min.css` },
        stackoverflow: { light: `${LOCAL_LIBS}/highlight.js/styles/stackoverflow-light.min.css`, dark: `${LOCAL_LIBS}/highlight.js/styles/stackoverflow-dark.min.css` },
      },
    },
    prettier: {
      standaloneJs: `${LOCAL_LIBS}/prettier/standalone.js`,
      parserMarkdownJs: `${LOCAL_LIBS}/prettier/markdown.js`,
    },
    cropper: {
      js: `${LOCAL_LIBS}/cropperjs/cropper.min.js`,
      css: `${LOCAL_LIBS}/cropperjs/cropper.min.css`,
    },
    screenfull: {
      js: `${LOCAL_LIBS}/screenfull/screenfull.js`,
    },
    mermaid: {
      js: `${LOCAL_LIBS}/mermaid/mermaid.min.js`,
    },
    katex: {
      js: `${LOCAL_LIBS}/katex/katex.min.js`,
      css: `${LOCAL_LIBS}/katex/katex.min.css`,
    },
    echarts: {
      js: `${LOCAL_LIBS}/echarts/echarts.min.js`,
    },
  },
})

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn as any })

app.mount('#app')
