import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/',
    component: () => import('@/layout/CustomerLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Workbench',
        component: () => import('@/views/workbench/index.vue')
      },
      {
        path: 'asset/list',
        name: 'AssetList',
        component: () => import('@/views/asset/list.vue')
      },
      {
        path: 'repair/list',
        name: 'RepairList',
        component: () => import('@/views/repair/list.vue')
      },
      {
        path: 'repair/create',
        name: 'RepairCreate',
        component: () => import('@/views/repair/create.vue')
      },
      {
        path: 'repair/detail/:id',
        name: 'RepairDetail',
        component: () => import('@/views/repair/detail.vue')
      },
      {
        path: 'inspection/list',
        name: 'InspectionList',
        component: () => import('@/views/inspection/list.vue')
      },
      {
        path: 'inspection/report/:taskId',
        name: 'InspectionReport',
        component: () => import('@/views/inspection/report.vue')
      },
      {
        path: 'contract/list',
        name: 'ContractList',
        component: () => import('@/views/contract/list.vue')
      },
      {
        path: 'contract/detail/:contractId',
        name: 'ContractDetail',
        component: () => import('@/views/contract/detail.vue')
      },
      {
        path: 'notification/list',
        name: 'NotificationList',
        component: () => import('@/views/notification/index.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue')
      },
      {
        path: 'report/export',
        name: 'ReportExport',
        component: () => import('@/views/report/export.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录则跳转登录页
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('customer_token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
