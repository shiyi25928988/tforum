# tForum

> 一个集成了 AI 能力的现代化社区论坛平台

## 功能模块

### 前台
| 模块 | 说明 |
|------|------|
| 首页 | 文章列表 + 搜索 + 热门标签，支持 Markdown 发布 |
| 讨论区 | 分类话题讨论，支持回复 |
| 图书角 | PDF 图书上传与分享，SHA-256 文件哈希去重 |
| Markdown 编辑器 | 全屏编辑器，支持语法高亮、图片上传、AI 写作辅助 |
| AI 助手 | 右侧悬浮 AI 对话抽屉，支持流式 SSE 聊天 |
| 用户系统 | 注册 / 登录 / 个人中心，密码 Base64 编码传输 |
| 全局搜索 | Lucene + IK 分词全文模糊搜索 |

### 后台管理
| 模块 | 说明 |
|------|------|
| 仪表盘 | 用户 / 文章 / 讨论 / 图书数量统计 |
| 用户管理 | 列表 + 禁用 / 启用 |
| 内容管理 | 文章、讨论、图书的查看与删除 |

## 技术栈

### 后端
| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 21 | 运行环境 |
| Spring Boot | 3.5.7 | 基础框架 |
| MyBatis-Plus | 3.5.14 | ORM |
| Sa-Token | 1.44.0 | 认证鉴权 |
| Spring AI | 1.0.3 | AI 对话 / RAG / MCP |
| Milvus | 2.6 | 向量数据库 |
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
└── docker/          # Docker Compose 编排（MySQL、Redis、MinIO、Milvus 等）
```

## 快速开始

### 环境要求
- JDK 21
- Maven 3.9+
- Node.js 18+
- MySQL 8.0
- Redis
- MinIO（可选，文件上传需要）
- Milvus（可选，AI RAG 需要）

### 1. 初始化数据库

```sql
CREATE DATABASE tforum DEFAULT CHARACTER SET utf8mb4;
```

启动后 `db/schema.sql` 和 `db/data.sql` 会自动执行建表与初始数据（默认管理员 admin/admin123）。

### 2. 启动后端

```bash
cd tforum
mvn clean compile -pl forum -am
cd forum
mvn spring-boot:run
```

默认端口：`8081`

### 3. 启动前端

```bash
cd forum/frontend
npm install
npm run dev
```

默认端口：`3000`，API 请求代理到 `localhost:8081`

### 4. 访问

- 前台：`http://localhost:3000`
- 后台：`http://localhost:3000/admin`
- Swagger：`http://localhost:8081/swagger-ui.html`

### 默认账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | 管理员 |

## API 概览

```
/api/v1/user/*          用户（注册、登录、信息）
/api/v1/article/*       文章（CRUD、搜索、点赞、标签）
/api/v1/forum/post/*    讨论帖（CRUD、列表）
/api/v1/forum/comment/* 评论
/api/v1/markdown/*      Markdown 文档
/api/v1/book/*          图书（上传、下载）
/api/v1/oss/*           文件管理
/api/v1/search          全局搜索
/api/v1/ai/*            AI 对话（简单、流式、RAG、MCP）
/api/v1/vector/*        向量存储与检索
/api/v1/milvus/*        Milvus 集合管理
/api/v1/admin/*         管理后台
```
