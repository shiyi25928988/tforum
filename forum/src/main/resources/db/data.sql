-- =============================================
-- tForum 初始数据
-- =============================================

-- 默认管理员账号 (账号: admin, 密码: admin123)
INSERT IGNORE INTO `system_user` (`id`, `account`, `password`, `username`, `role`, `status`) VALUES (1, 'admin', 'admin123', '管理员', 'admin', 'active');

-- 默认文章分类标签
INSERT IGNORE INTO `article_tag` (`id`, `name`) VALUES (1, 'AI');
INSERT IGNORE INTO `article_tag` (`id`, `name`) VALUES (2, '技术分享');
INSERT IGNORE INTO `article_tag` (`id`, `name`) VALUES (3, '云计算');


-- 默认导航栏栏目（系统内置，首页始终可见）
INSERT IGNORE INTO `nav_item` (`id`, `name`, `url`, `type`, `is_visible`, `sort_order`, `is_system`) VALUES (1, '首页', '/', 'internal', 1, 1, 1);
INSERT IGNORE INTO `nav_item` (`id`, `name`, `url`, `type`, `is_visible`, `sort_order`, `is_system`) VALUES (2, '讨论区', '/forum', 'internal', 1, 2, 1);
INSERT IGNORE INTO `nav_item` (`id`, `name`, `url`, `type`, `is_visible`, `sort_order`, `is_system`) VALUES (3, 'Skills', '/skills', 'internal', 1, 3, 1);
INSERT IGNORE INTO `nav_item` (`id`, `name`, `url`, `type`, `is_visible`, `sort_order`, `is_system`) VALUES (4, '图书角', '/books', 'internal', 1, 4, 1);

-- 默认讨论区分组
INSERT IGNORE INTO `discussion_category` (`id`, `name`, `description`, `sort_order`) VALUES (1, '技术讨论', '技术话题交流', 1);
INSERT IGNORE INTO `discussion_category` (`id`, `name`, `description`, `sort_order`) VALUES (2, '问答求助', '提问与解答', 2);
INSERT IGNORE INTO `discussion_category` (`id`, `name`, `description`, `sort_order`) VALUES (3, '项目合作', '开源项目和合作机会', 3);
INSERT IGNORE INTO `discussion_category` (`id`, `name`, `description`, `sort_order`) VALUES (4, '职场交流', '职业发展与经验分享', 4);
INSERT IGNORE INTO `discussion_category` (`id`, `name`, `description`, `sort_order`) VALUES (5, '灌水闲聊', '轻松话题', 5);
