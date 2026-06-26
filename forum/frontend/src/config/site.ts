/**
 * 站点配置 — 所有可定制化的文案集中管理
 *
 * 修改这里的值即可全局生效，无需逐个文件查找替换。
 * 未来可以从后端接口或 .env 文件加载，实现动态配置。
 */

export const siteConfig = {
  /** 站点名称，显示在标题栏、Logo、登录/注册页 */
  name: 'tForum',

  /** 站点副标题 / 一句话描述 */
  subtitle: '开发者社区',

  /** Logo 图标文字（显示在登录/注册页的 logo 方块中） */
  logoIcon: 'F',

  /** 浏览器标签页标题 */
  htmlTitle: 'tForum',

  /** 登录页欢迎语 */
  loginTitle: 'tForum',

  /** 注册页欢迎语 */
  registerTitle: '加入 tForum',

  /** 注册页描述 */
  registerDesc: '注册账号，开始交流',
} as const
