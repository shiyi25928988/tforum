<div align="center">

<img alt="Java" src="https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white">
<img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.5.7-6DB33F?logo=springboot&logoColor=white">
<img alt="Vue" src="https://img.shields.io/badge/Vue-3.5.13-4FC08D?logo=vuedotjs&logoColor=white">
<img alt="License" src="https://img.shields.io/badge/License-MIT-blue?logo=opensourceinitiative&logoColor=white">
<img alt="Docker" src="https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker&logoColor=white">

</div>

# tForum

> 一个集成了 AI 能力的现代化开发者社区论坛平台 —— 文章、讨论、图书角、Skills、全文检索、RAG 向量问答、第三方应用 SSO 集成，开箱即用的 Docker 全栈部署。

<p align="center">
🇨🇳 <a href="./README.md">简体中文</a> | 🇺🇸 <a href="./README.en.md">English</a>
</p>

---

## 目录

- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [快速开始](#快速开始)
- [API 概览](#api-概览)
- [第三方应用集成](#第三方应用集成)
- [参与贡献](#参与贡献)
- [开源许可](#开源许可)

---

## 功能特性

### 前台

| 模块 | 说明 |
|------|------|
| 首页 | 文章列表 + 搜索 + 热门标签，支持 Markdown 发布 |
| 讨论区 | 分类话题讨论，支持回复 |
| 图书角 | PDF 图书上传与分享，SHA-256 文件哈希去重 |
| Skills | 技能分享，图标 / 附件 / Git 链接 |
| Markdown 编辑器 | 全屏编辑器，支持语法高亮、图片上传、AI 写作辅助 |
| AI 助手 | 右侧悬浮 AI 对话抽屉，支持流式 SSE 聊天、RAG 知识库问答 |
| 用户系统 | 注册 / 登录 / 个人中心，Sa-Token 鉴权 |
| 全局搜索 | Lucene + IK 分词全文模糊搜索 |
| 动态导航栏 | 管理员可配置栏目显隐、新增外部链接栏目 |
| MinIO 文件代理 | 所有文件下载经后端转发，客户端无需直连 MinIO |

### 后台管理

| 模块 | 说明 |
|------|------|
| 仪表盘 | 用户 / 文章 / 讨论 / 图书数量统计 |
| 用户管理 | 列表 + 禁用 / 启用 |
| 内容管理 | 文章、讨论、图书、Skills 的查看与删除 |
| 导航栏管理 | 配置栏目显隐 / 排序，新增自定义栏目（支持外部链接） |
| 向量库管理 | 文章向量化入库 / 删除，Milvus 集合管理 |
| 标签 / 讨论分组 | 增删改 |

### 第三方应用集成

| 能力 | 说明 |
|------|------|
| Token 占位符 | 导航栏外部链接支持 `{token}` 占位符，用户点击时自动携带当前 token 跳转 |
| Token 校验接口 | `GET /api/v1/user/verifyToken?token=xxx` 供第三方应用验证 token 并获取用户信息，实现 SSO |

> [!NOTE]
> 第三方应用 SSO 集成的完整流程与代码示例详见 [第三方应用集成方案](docs/第三方应用集成方案.md)。

---

## 技术栈

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 21 | 运行环境 |
| Spring Boot | 3.5.7 | 基础框架 |
| Spring Cloud | 2025.0.0 | 微服务 |
| MyBatis-Plus | 3.5.14 | ORM |
| Sa-Token | 1.44.0 | 认证鉴权 |
| Spring AI | 1.0.3 | AI 对话 / RAG / MCP |
| Milvus | 2.6.2 | 向量数据库 |
| Apache Lucene | 10.3.2 | 全文检索 |
| MinIO | 8.6.0 | 对象存储 |
| IK Analyzer | 5.1.0 | 中文分词 |

### 前端

| 技术 | 用途 |
|------|------|
| Vue 3 + Composition API | UI 框架 |
| TypeScript | 类型安全 |
| Vite | 构建工具 |
| Element Plus | UI 组件库 |
| Pinia | 状态管理 |
| Vue Router | 路由 |
| md-editor-v3 | Markdown 编辑器 |
| marked | Markdown 渲染 |
| Axios | HTTP 请求 |
| ECharts | 图表 |
| Mermaid | 流程图渲染 |

---

## 项目结构

```
tforum/
├── common/          # 公共模块（AI 服务、工具类、注解、HTTP 封装）
├── forum/           # 主应用（Controller、Service、Table、前端）
│   ├── src/main/java/.../controller/
│   │   ├── ai/          # AI 对话
│   │   ├── admin/       # 管理后台
│   │   ├── article/     # 文章标签
│   │   ├── book/        # 图书角
│   │   ├── discussion/  # 讨论区分组
│   │   ├── markdown/    # Markdown 文档
│   │   ├── oss/         # 文件管理
│   │   ├── search/      # 全局搜索
│   │   └── user/        # 用户管理
│   └── frontend/        # Vue 3 前端工程
├── markdown/        # Markdown 文档模块（Entity、解析工具）
├── mcp/             # MCP 服务（RAG、SSH、邮件工具）
├── oss/             # 对象存储模块（MinIO 封装）
├── search/          # 搜索模块（Lucene 索引与查询）
├── user/            # 用户模块（实体、转换器、认证配置）
├── docs/            # 文档（第三方应用集成方案）
├── docker/          # Docker Compose 编排（Milvus 等）
└── docker-compose-all.yml  # 全栈一键部署
```

---

## 快速开始

### 方式一：Docker 一键部署（推荐）

> [!IMPORTANT]
> 部署前需先打包后端 jar 包。

```bash
# 1. 打包后端
cd forum && mvn package -DskipTests && cd ..

# 2. 启动全部服务（MySQL + Redis + MinIO + Milvus + 前后端）
docker-compose -f docker-compose-all.yml up -d

# 3. 查看日志
docker-compose -f docker-compose-all.yml logs -f
```

<details>
<summary>📁 离线镜像导出/导入</summary>

```bash
# 在线环境导出镜像
save_images.bat          # Windows
# 或手动 docker save -o <name>.tar <image>

# 离线环境导入镜像
cd images && ./load_image.sh   # macOS / Linux
```

</details>

启动后访问 `http://localhost` 即可使用。

### 方式二：本地开发

<details>
<summary>🔧 展开本地开发步骤</summary>

#### 环境要求

- JDK 21
- Maven 3.9+
- Node.js 18+
- MySQL 8.0
- Redis
- MinIO（可选，文件上传需要）
- Milvus（可选，AI RAG 需要）

#### 1. 初始化数据库

```sql
CREATE DATABASE tforum DEFAULT CHARACTER SET utf8mb4;
```

启动后 `db/schema.sql` 和 `db/data.sql` 会自动执行建表与初始数据。

#### 2. 启动后端

```bash
mvn clean compile -pl forum -am
cd forum
mvn spring-boot:run
```

默认端口：`8081`

#### 3. 启动前端

```bash
cd forum/frontend
npm install
npm run dev
```

默认端口：`3000`，API 请求代理到 `localhost:8081`

</details>

### 访问地址

| 地址 | 说明 |
|------|------|
| `http://localhost` | 前台页面（Docker 部署） |
| `http://localhost/admin` | 管理后台 |
| `http://localhost:8081/swagger-ui.html` | Swagger API 文档 |
| `http://localhost:9001` | MinIO 控制台（minioadmin / minioadmin） |

### 默认账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | 管理员 |

> [!WARNING]
> 生产环境请务必修改默认管理员密码。

---

## API 概览

| 路径 | 说明 |
|------|------|
| `/api/v1/user/*` | 用户（注册、登录、信息、token 校验） |
| `/api/v1/article/*` | 文章（CRUD、搜索、点赞、标签） |
| `/api/v1/forum/post/*` | 讨论帖（CRUD、列表） |
| `/api/v1/forum/comment/*` | 评论 |
| `/api/v1/markdown/*` | Markdown 文档 |
| `/api/v1/book/*` | 图书（上传、下载） |
| `/api/v1/skill/*` | Skills（发布、下载） |
| `/api/v1/nav/*` | 导航栏（公开列表） |
| `/api/v1/oss/*` | 文件管理（上传、代理下载） |
| `/api/v1/search` | 全局搜索 |
| `/api/v1/ai/*` | AI 对话（简单、流式、RAG、MCP） |
| `/api/v1/vector/*` | 向量存储与检索 |
| `/api/v1/milvus/*` | Milvus 集合管理 |
| `/api/v1/admin/*` | 管理后台（含导航栏管理） |

---

## 第三方应用集成

tForum 支持通过导航栏外部链接 + Token 占位符 + Token 校验接口实现与第三方应用的单点登录（SSO）。

**流程**：管理员配置外部链接 `https://app.com/sso?token={token}` → 用户点击时自动替换为真实 token 跳转 → 第三方应用调 `GET /api/v1/user/verifyToken?token=xxx` 校验并获取用户信息。

完整教程详见 **[第三方应用集成方案](docs/第三方应用集成方案.md)**。

---

## 参与贡献

1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/your-feature`
3. 提交更改：`git commit -m 'feat: add your feature'`
4. 推送分支：`git push origin feature/your-feature`
5. 提交 Pull Request

---

## 开源许可

本项目基于 [MIT License](LICENSE) 开源。

Copyright (c) 2026 shiyi
