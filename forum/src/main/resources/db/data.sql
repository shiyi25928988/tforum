-- =============================================
-- tForum 初始数据
-- =============================================

-- 默认管理员账号 (账号: admin, 密码: admin123)
INSERT IGNORE INTO `system_user` (`id`, `account`, `password`, `username`, `role`, `status`) VALUES (1, 'admin', 'admin123', '管理员', 'admin', 'active');

-- 默认文章分类标签
INSERT IGNORE INTO `article_tag` (`id`, `name`) VALUES (1, 'Java');
INSERT IGNORE INTO `article_tag` (`id`, `name`) VALUES (2, 'Spring Boot');
INSERT IGNORE INTO `article_tag` (`id`, `name`) VALUES (3, 'Vue');
INSERT IGNORE INTO `article_tag` (`id`, `name`) VALUES (4, 'MySQL');
INSERT IGNORE INTO `article_tag` (`id`, `name`) VALUES (5, '前端');
INSERT IGNORE INTO `article_tag` (`id`, `name`) VALUES (6, '后端');
INSERT IGNORE INTO `article_tag` (`id`, `name`) VALUES (7, '开源');
INSERT IGNORE INTO `article_tag` (`id`, `name`) VALUES (8, '技术分享');

-- 默认讨论区分组
INSERT IGNORE INTO `discussion_category` (`id`, `name`, `description`, `sort_order`) VALUES (1, '技术讨论', '技术话题交流', 1);
INSERT IGNORE INTO `discussion_category` (`id`, `name`, `description`, `sort_order`) VALUES (2, '问答求助', '提问与解答', 2);
INSERT IGNORE INTO `discussion_category` (`id`, `name`, `description`, `sort_order`) VALUES (3, '项目合作', '开源项目和合作机会', 3);
INSERT IGNORE INTO `discussion_category` (`id`, `name`, `description`, `sort_order`) VALUES (4, '职场交流', '职业发展与经验分享', 4);
INSERT IGNORE INTO `discussion_category` (`id`, `name`, `description`, `sort_order`) VALUES (5, '灌水闲聊', '轻松话题', 5);
