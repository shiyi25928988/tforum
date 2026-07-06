-- =============================================
-- tForum 数据库初始化脚本
-- =============================================

-- 用户表
CREATE TABLE IF NOT EXISTS `system_user` (
    `id` BIGINT AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(255) DEFAULT NULL COMMENT '用户名',
    `account` VARCHAR(255) NOT NULL COMMENT '账号',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `email` VARCHAR(255) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `role` VARCHAR(50) DEFAULT NULL COMMENT '角色',
    `permission` VARCHAR(50) COMMENT '权限信息',
    `status` VARCHAR(20) DEFAULT NULL COMMENT '状态',
    PRIMARY KEY (id),
    UNIQUE KEY uk_account (account)
) ENGINE=InnoDB COMMENT='用户表';

-- 头像表
CREATE TABLE IF NOT EXISTS `avatar` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `svg` TEXT COMMENT 'SVG content of the avatar',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='用户头像表';

-- =============================================
-- 文章模块
-- =============================================

-- 文章表
CREATE TABLE IF NOT EXISTS `article` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title` VARCHAR(255) NOT NULL COMMENT '文章标题',
    `content` TEXT COMMENT '文章内容',
    `summary` VARCHAR(500) DEFAULT NULL COMMENT '摘要',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `author_id` BIGINT DEFAULT NULL COMMENT '作者ID',
    `status` INT DEFAULT 1 COMMENT '状态: 0=草稿, 1=已发布',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `like_count` INT DEFAULT 0 COMMENT '点赞次数',
    `comment_count` INT DEFAULT 0 COMMENT '评论次数',
    `is_pinned` INT DEFAULT 0 COMMENT '是否置顶: 0=否, 1=是',
    `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签，逗号分隔',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `creator_id` BIGINT DEFAULT NULL COMMENT '创建者ID',
    `updater_id` BIGINT DEFAULT NULL COMMENT '更新者ID',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除: 0=未删除, 1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB COMMENT='文章表';

-- 文章标签表
CREATE TABLE IF NOT EXISTS `article_tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB COMMENT='文章标签表';

-- =============================================
-- 论坛模块
-- =============================================

-- 讨论区分组表
CREATE TABLE IF NOT EXISTS `discussion_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` VARCHAR(100) NOT NULL COMMENT '分组名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '分组描述',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `topic_count` INT DEFAULT 0 COMMENT '话题数量',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='讨论区分组表';

-- 图书角
CREATE TABLE IF NOT EXISTS `book` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title` VARCHAR(255) NOT NULL COMMENT '书名',
    `author` VARCHAR(200) DEFAULT NULL COMMENT '作者',
    `description` VARCHAR(1000) DEFAULT NULL COMMENT '简介',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面URL',
    `file_url` VARCHAR(500) NOT NULL COMMENT 'PDF文件URL',
    `file_size` BIGINT DEFAULT 0 COMMENT '文件大小(字节)',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `uploader_id` BIGINT DEFAULT NULL COMMENT '上传者ID',
    `download_count` INT DEFAULT 0 COMMENT '下载次数',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `file_hash` CHAR(64) DEFAULT NULL COMMENT 'SHA-256文件哈希，防重复',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `creator_id` BIGINT DEFAULT NULL COMMENT '创建者ID',
    `updater_id` BIGINT DEFAULT NULL COMMENT '更新者ID',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_file_hash` (`file_hash`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_uploader_id` (`uploader_id`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB COMMENT='图书角表';

-- 论坛帖子表
CREATE TABLE IF NOT EXISTS `forum_post` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title` VARCHAR(255) NOT NULL COMMENT '帖子标题',
    `content` TEXT COMMENT '帖子内容',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `author_id` BIGINT DEFAULT NULL COMMENT '作者ID',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `comment_count` INT DEFAULT 0 COMMENT '评论次数',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `creator_id` BIGINT DEFAULT NULL COMMENT '创建者ID',
    `updater_id` BIGINT DEFAULT NULL COMMENT '更新者ID',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除: 0=未删除, 1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB COMMENT='论坛帖子表';

-- 论坛评论表
CREATE TABLE IF NOT EXISTS `forum_comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `post_id` BIGINT NOT NULL COMMENT '帖子ID',
    `content` TEXT COMMENT '评论内容',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID，用于回复',
    `author_id` BIGINT DEFAULT NULL COMMENT '作者ID',
    `comment_type` VARCHAR(20) DEFAULT 'post' COMMENT '评论类型: article=文章评论, post=帖子评论',
    `reply_to` BIGINT DEFAULT NULL COMMENT '回复目标评论ID',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `creator_id` BIGINT DEFAULT NULL COMMENT '创建者ID',
    `updater_id` BIGINT DEFAULT NULL COMMENT '更新者ID',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除: 0=未删除, 1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_post_id` (`post_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_author_id` (`author_id`)
) ENGINE=InnoDB COMMENT='论坛评论表';

-- =============================================
-- Markdown 文档模块
-- =============================================

CREATE TABLE IF NOT EXISTS `markdown_doc` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title` VARCHAR(255) NOT NULL COMMENT '文档标题',
    `content` TEXT COMMENT '完整的 Markdown 文本内容',
    `author_id` BIGINT DEFAULT NULL COMMENT '作者ID',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签，逗号分隔',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `creator_id` BIGINT DEFAULT NULL COMMENT '创建者ID',
    `updater_id` BIGINT DEFAULT NULL COMMENT '更新者ID',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除: 0=未删除, 1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB COMMENT='Markdown 文档表';

-- =============================================
-- 搜索模块
-- =============================================

CREATE TABLE IF NOT EXISTS `search_frequency` (
    `term` VARCHAR(255) PRIMARY KEY COMMENT '搜索词',
    `frequency` BIGINT DEFAULT 0 COMMENT '搜索频率',
    `last_access_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后访问时间'
) ENGINE=InnoDB COMMENT='搜索频率统计表';

-- =============================================
-- Skills 模块
-- =============================================

CREATE TABLE IF NOT EXISTS `skill` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` VARCHAR(255) NOT NULL COMMENT 'Skill名称',
    `description` VARCHAR(1000) DEFAULT NULL COMMENT '描述',
    `content` TEXT COMMENT 'Skill内容（代码/提示词/配置等）',
    `icon_url` VARCHAR(500) DEFAULT NULL COMMENT '图标URL',
    `category` VARCHAR(100) DEFAULT NULL COMMENT '分类',
    `author_id` BIGINT DEFAULT NULL COMMENT '作者ID',
    `download_count` INT DEFAULT 0 COMMENT '下载次数',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `status` INT DEFAULT 1 COMMENT '状态: 0=草稿, 1=已发布',
    `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签，逗号分隔',
    `attachment_url` VARCHAR(500) DEFAULT NULL COMMENT '附件URL（zip等）',
    `git_url` VARCHAR(500) DEFAULT NULL COMMENT 'Git仓库地址',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `creator_id` BIGINT DEFAULT NULL COMMENT '创建者ID',
    `updater_id` BIGINT DEFAULT NULL COMMENT '更新者ID',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB COMMENT='Skills表';

CREATE TABLE IF NOT EXISTS SPRING_AI_CHAT_MEMORY (
    `conversation_id` VARCHAR(36) NOT NULL,
    `content` TEXT NOT NULL,
    `type` ENUM('USER', 'ASSISTANT', 'SYSTEM', 'TOOL') NOT NULL,
    `timestamp` TIMESTAMP NOT NULL,
    INDEX `SPRING_AI_CHAT_MEMORY_CONVERSATION_ID_TIMESTAMP_IDX` (`conversation_id`, `timestamp`)
);

-- =============================================
-- 导航栏配置表
-- =============================================

CREATE TABLE IF NOT EXISTS `nav_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` VARCHAR(100) NOT NULL COMMENT '栏目名称',
    `url` VARCHAR(500) NOT NULL COMMENT '链接URL（内部路由或外部完整链接）',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '图标（可选）',
    `type` VARCHAR(20) DEFAULT 'internal' COMMENT '类型: internal=内部路由, external=外部链接',
    `is_visible` TINYINT(1) DEFAULT 1 COMMENT '是否显示: 0=隐藏, 1=显示',
    `sort_order` INT DEFAULT 0 COMMENT '排序（越小越靠前）',
    `is_system` TINYINT(1) DEFAULT 0 COMMENT '是否系统内置: 0=自定义, 1=系统内置',
    PRIMARY KEY (`id`),
    KEY `idx_visible` (`is_visible`),
    KEY `idx_sort` (`sort_order`)
) ENGINE=InnoDB COMMENT='导航栏配置表';

-- =============================================
-- 文章向量记录表
-- =============================================

CREATE TABLE IF NOT EXISTS `article_vector_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `milvus_id` BIGINT DEFAULT NULL COMMENT 'Milvus中的向量ID',
    `created_time` DATETIME NOT NULL COMMENT '存入时间',
    PRIMARY KEY (`id`),
    KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB COMMENT='文章向量记录表（一篇文章可能对应多条记录）';
