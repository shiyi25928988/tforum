import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('@/components/Layout.vue'),
      redirect: '/home',
      children: [
        { path: '/home', name: 'home', component: () => import('@/views/HomeView.vue') },
        { path: '/article/:id', name: 'articleDetail', component: () => import('@/views/ArticleDetailView.vue') },
        { path: '/forum', name: 'forum', component: () => import('@/views/ForumView.vue') },
        { path: '/books', name: 'books', component: () => import('@/views/BooksView.vue') },
        { path: '/forum/post/:id', name: 'postDetail', component: () => import('@/views/PostDetailView.vue') },
        { path: '/user/profile', name: 'profile', component: () => import('@/views/ProfileView.vue') },
        { path: '/user/articles', name: 'myArticles', component: () => import('@/views/MyArticlesView.vue') },
        { path: '/user/skills', name: 'mySkills', component: () => import('@/views/MySkillsView.vue') },
        { path: '/skills', name: 'skills', component: () => import('@/views/SkillsView.vue') },
        { path: '/skill/:id', name: 'skillDetail', component: () => import('@/views/SkillsView.vue') },
      ],
    },
    { path: '/article/edit/:id?', name: 'articleEdit', component: () => import('@/views/ArticleEditView.vue') },
    { path: '/skill/publish/:id?', name: 'skillPublish', component: () => import('@/views/SkillPublishView.vue') },
    // 管理后台
    { path: '/admin', component: () => import('@/views/admin/AdminLayout.vue'), redirect: '/admin/dashboard',
      children: [
        { path: 'dashboard', component: () => import('@/views/admin/AdminDashboard.vue') },
        { path: 'users', component: () => import('@/views/admin/AdminUsers.vue') },
        { path: 'articles', component: () => import('@/views/admin/AdminArticles.vue') },
        { path: 'posts', component: () => import('@/views/admin/AdminPosts.vue') },
        { path: 'discussion-categories', component: () => import('@/views/admin/AdminDiscussionCategories.vue') },
        { path: 'books', component: () => import('@/views/admin/AdminBooks.vue') },
        { path: 'tags', component: () => import('@/views/admin/AdminTags.vue') },
        { path: 'milvus', component: () => import('@/views/admin/AdminMilvus.vue') },
        { path: 'skills', component: () => import('@/views/admin/AdminSkills.vue') },
      ],
    },
    { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue') },
    { path: '/register', name: 'register', component: () => import('@/views/RegisterView.vue') },
  ],
})

export default router
