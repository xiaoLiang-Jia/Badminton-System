import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/user/Register.vue')
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        redirect: '/home'
      },
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/user/Home.vue')
      },
      {
        path: '/courts',
        name: 'Courts',
        component: () => import('@/views/user/Courts.vue')
      },
      {
        path: '/courts/:id',
        name: 'CourtDetail',
        component: () => import('@/views/user/CourtDetail.vue')
      },
      {
        path: '/courses',
        name: 'Courses',
        component: () => import('@/views/user/Courses.vue')
      },
      {
        path: '/courses/:id',
        name: 'CourseDetail',
        component: () => import('@/views/user/CourseDetail.vue')
      },
      {
        path: '/forum',
        name: 'Forum',
        component: () => import('@/views/user/Forum.vue')
      },
      {
        path: '/forum/:id',
        name: 'ForumDetail',
        component: () => import('@/views/user/ForumDetail.vue')
      },
      {
        path: '/user/profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/user/bookings',
        name: 'MyBookings',
        component: () => import('@/views/user/MyBookings.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/user/courses',
        name: 'MyCourses',
        component: () => import('@/views/user/MyCourses.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/user/students',
        name: 'MyStudents',
        component: () => import('@/views/user/MyStudents.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/user/balance',
        name: 'Balance',
        component: () => import('@/views/user/Balance.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/messages',
        name: 'Messages',
        component: () => import('@/views/user/Messages.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/chat/:userId',
        name: 'Chat',
        component: () => import('@/views/user/Chat.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: '/admin/dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: '/admin/courts',
        name: 'AdminCourts',
        component: () => import('@/views/admin/Courts.vue')
      },
      {
        path: '/admin/users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue')
      },
      {
        path: '/admin/courses',
        name: 'AdminCourses',
        component: () => import('@/views/admin/Courses.vue')
      },
      {
        path: '/admin/forum',
        name: 'AdminForum',
        component: () => import('@/views/admin/Forum.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresAdmin) {
    // 需要先确保用户已登录
    if (!userStore.user || userStore.user.role !== 3) {
      next({ name: 'Home' })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
