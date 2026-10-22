import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: 路由配置项
 *
 * hidden: true                     // 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 * alwaysShow: true                 // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 *                                  // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 *                                  // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 *                                  // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * redirect: noRedirect             // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'               // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * query: '{"id": 1, "name": "ry"}' // 访问路由的默认传递参数
 * roles: ['admin', 'common']       // 访问路由的角色权限
 * permissions: ['a:a:a', 'b:b:b']  // 访问路由的菜单权限
 * meta : {
    noCache: true                   // 如果设置为true，则不会被 <keep-alive> 缓存(默认 false)
    title: 'title'                  // 设置该路由在侧边栏和面包屑中展示的名字
    icon: 'svg-name'                // 设置该路由的图标，对应路径src/assets/icons/svg
    breadcrumb: false               // 如果设置为false，则不会在breadcrumb面包屑中显示
    activeMenu: '/system/user'      // 当路由设置了该属性，则会高亮相对应的侧边栏。
  }
 */

// 公共路由
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/register'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401'),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: 'index',
    children: [
      {
        path: 'index',
        component: () => import('@/views/index'),
        name: 'Index',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/lock',
    component: () => import('@/views/lock'),
    hidden: true,
    meta: { title: '锁定屏幕' }
  },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    redirect: 'noredirect',
    children: [
      {
        path: 'profile',
        component: () => import('@/views/system/user/profile/index'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  }
]

// 动态路由，基于用户权限动态去加载
export const dynamicRoutes = [
  {
    path: '/system/user-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:user:edit'],
    children: [
      {
        path: 'role/:userId(\\d+)',
        component: () => import('@/views/system/user/authRole'),
        name: 'AuthRole',
        meta: { title: '分配角色', activeMenu: '/system/user' }
      }
    ]
  },
  {
    path: '/system/role-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:role:edit'],
    children: [
      {
        path: 'user/:roleId(\\d+)',
        component: () => import('@/views/system/role/authUser'),
        name: 'AuthUser',
        meta: { title: '分配用户', activeMenu: '/system/role' }
      }
    ]
  },
  {
    path: '/system/dict-data',
    component: Layout,
    hidden: true,
    permissions: ['system:dict:list'],
    children: [
      {
        path: 'index/:dictId(\\d+)',
        component: () => import('@/views/system/dict/data'),
        name: 'Data',
        meta: { title: '字典数据', activeMenu: '/system/dict' }
      }
    ]
  },
  {
    path: '/monitor/job-log',
    component: Layout,
    hidden: true,
    permissions: ['monitor:job:list'],
    children: [
      {
        path: 'index/:jobId(\\d+)',
        component: () => import('@/views/monitor/job/log'),
        name: 'JobLog',
        meta: { title: '调度日志', activeMenu: '/monitor/job' }
      }
    ]
  },
  {
    path: '/tool/gen-edit',
    component: Layout,
    hidden: true,
    permissions: ['tool:gen:edit'],
    children: [
      {
        path: 'index/:tableId(\\d+)',
        component: () => import('@/views/tool/gen/editTable'),
        name: 'GenEdit',
        meta: { title: '修改生成配置', activeMenu: '/tool/gen' }
      }
    ]
  },
  {
    path: '/zw/workOrder',
    component: Layout,
    hidden: true,
    permissions: ['zw:workOrder:list'],
    children: [
      {
        path: '',
        component: () => import('@/views/zw/workOrder/index'),
        name: 'WorkOrderManage',
        meta: { title: '工单管理', activeMenu: '/zw/workOrder' }
      },
      {
        path: 'detail',
        component: () => import('@/views/zw/workOrder/detail'),
        name: 'WorkOrderDetail',
        meta: { title: '工单详情', activeMenu: '/zw/workOrder' }
      }
    ]
  },
  {
    path: '/zw/contract',
    component: Layout,
    hidden: true,
    permissions: ['zw:contract:query'],
    children: [
      {
        path: 'detail/:contractId(\\d+)',
        component: () => import('@/views/zw/contract/detail'),
        name: 'ContractDetail',
        meta: { title: '合同详情', activeMenu: '/zw/contract' }
      }
    ]
  },
  {
    path: '/zw/contract-list',
    component: Layout,
    hidden: true,
    permissions: ['zw:contract:list'],
    children: [
      {
        path: '',
        component: () => import('@/views/zw/contract/index'),
        name: 'ContractList',
        meta: { title: '合同管理', activeMenu: '/zw/contract-list' }
      }
    ]
  },
  {
    path: '/zw/asset',
    component: Layout,
    hidden: true,
    permissions: ['zw:asset:list'],
    children: [
      {
        path: '',
        component: () => import('@/views/zw/asset/index'),
        name: 'AssetManage',
        meta: { title: '资产管理', activeMenu: '/zw/asset' }
      }
    ]
  },
  {
    path: '/zw/inspection',
    component: Layout,
    hidden: true,
    permissions: ['zw:inspection:list'],
    children: [
      {
        path: '',
        component: () => import('@/views/zw/inspection/index'),
        name: 'InspectionManage',
        meta: { title: '巡检任务', activeMenu: '/zw/inspection' }
      }
    ]
  },
  {
    path: '/zw/repairApply',
    component: Layout,
    hidden: true,
    permissions: ['zw:repairApply:list'],
    children: [
      {
        path: '',
        component: () => import('@/views/zw/repairApply/index'),
        name: 'RepairApplyManage',
        meta: { title: '报修申请', activeMenu: '/zw/repairApply' }
      }
    ]
  },
  {
    path: '/zw/notification',
    component: Layout,
    hidden: true,
    permissions: ['zw:notification:list'],
    children: [
      {
        path: '',
        component: () => import('@/views/zw/notification/index'),
        name: 'NotificationManage',
        meta: { title: '发送通知', activeMenu: '/zw/notification' }
      }
    ]
  },
  ]

// 防止连续点击多次路由报错
let routerPush = Router.prototype.push
let routerReplace = Router.prototype.replace
// push
Router.prototype.push = function push(location) {
  return routerPush.call(this, location).catch(err => err)
}
// replace
Router.prototype.replace = function push(location) {
  return routerReplace.call(this, location).catch(err => err)
}

export default new Router({
  mode: 'history', // 去掉url中的#
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})
