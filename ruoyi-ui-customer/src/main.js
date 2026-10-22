import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import {
  ArrowDown, ArrowLeft, ArrowRight, Bell, Box, Calendar, Check,
  CircleCheck, Clock, DataAnalysis, Document, Download, FullScreen,
  HomeFilled, InfoFilled, Loading, Lock, Microphone, Odometer, Phone,
  Plus, QuestionFilled, Refresh, Search, Setting, Star, SwitchButton,
  Tickets, Timer, Tools, User, WarnTriangleFilled, WarningFilled
} from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import './styles/customer.css'

const app = createApp(App)

// 按需注册图标（仅注册业务实际使用的 33 个图标，避免打包全量 300+ 图标）
const icons = [
  ArrowDown, ArrowLeft, ArrowRight, Bell, Box, Calendar, Check,
  CircleCheck, Clock, DataAnalysis, Document, Download, FullScreen,
  HomeFilled, InfoFilled, Loading, Lock, Microphone, Odometer, Phone,
  Plus, QuestionFilled, Refresh, Search, Setting, Star, SwitchButton,
  Tickets, Timer, Tools, User, WarnTriangleFilled, WarningFilled
]
icons.forEach(icon => app.component(icon.name, icon))

app.use(ElementPlus)
app.use(createPinia())
app.use(router)

// 使用 queueMicrotask 确保 Vue 调度器在初始导航完成后再挂载 DOM
// 解决 app.use(router) 时 flushJobs 重入导致的错误
queueMicrotask(() => {
  app.mount('#app')
})
