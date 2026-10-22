<template>
  <div class="inspection-list">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-title">巡检报告</div>
      <el-tag type="info" size="default">共 {{ total }} 条记录</el-tag>
    </div>

    <!-- 搜索区 -->
    <div class="search-bar">
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        style="width: 260px"
      />
      <el-button type="primary" @click="onSearch">
        <el-icon><Search /></el-icon>搜索
      </el-button>
      <el-button @click="resetQuery">
        <el-icon><Refresh /></el-icon>重置
      </el-button>
    </div>

    <!-- 表格 -->
    <div class="table-card">
      <el-table
        :data="list"
        v-loading="loading"
        @row-click="goReport"
        highlight-current-row
        stripe
      >
        <el-table-column label="计划时间" width="170">
          <template #default="{ row }">
            <div class="time-cell">
              <el-icon size="12"><Calendar /></el-icon>
              {{ formatTime(row.planTime) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="设备ID" prop="assetId" width="100" align="center" />
        <el-table-column label="任务类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="taskTypeTag(row.taskType)" size="small">{{ taskTypeName(row.taskType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="实际开始" width="170">
          <template #default="{ row }">{{ row.actualStartTime ? formatTime(row.actualStartTime) : '—' }}</template>
        </el-table-column>
        <el-table-column label="实际结束" width="170">
          <template #default="{ row }">{{ row.actualEndTime ? formatTime(row.actualEndTime) : '—' }}</template>
        </el-table-column>
        <el-table-column label="签到地址" prop="checkInAddress" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="taskStatusTag(row.status)" size="small" effect="dark">
              {{ taskStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click.stop="goReport(row)">
              查看报告 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getInspectionList } from '@/api/zw/inspection'

const router = useRouter()
const loading = ref(false)
const dateRange = ref([])
const list = ref([])
const total = ref(0)
const query = ref({ pageNum: 1, pageSize: 10 })

async function loadData() {
  loading.value = true
  try {
    const res = await getInspectionList({
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1],
      pageNum: query.value.pageNum,
      pageSize: query.value.pageSize
    })
    if (res.code === 200) {
      list.value = res.rows || []
      total.value = res.total || 0
    }
  } catch (e) {
    console.error('巡检列表加载失败', e)
  } finally {
    loading.value = false
  }
}

function onSearch() {
  query.value.pageNum = 1
  loadData()
}

function resetQuery() {
  dateRange.value = []
  query.value = { pageNum: 1, pageSize: 10 }
  loadData()
}

function goReport(row) {
  router.push('/inspection/report/' + row.taskId)
}

function taskTypeName(type) {
  return { 'routine': '例行', 'special': '专项', 'fault': '故障' }[type] || type
}

function taskTypeTag(type) {
  return { 'routine': 'primary', 'special': 'warning', 'fault': 'danger' }[type] || 'info'
}

function taskStatusTag(status) {
  return { '0': 'info', '1': 'warning', '2': 'success' }[status] || 'info'
}

function taskStatusName(status) {
  return { '0': '待执行', '1': '执行中', '2': '已完成' }[status] || status
}

function formatTime(t) {
  if (!t) return '-'
  return t.substring ? t.substring(0, 16) : String(t).substring(0, 16)
}

onMounted(() => { loadData() })
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-title::before {
  content: '';
  display: block;
  width: 4px;
  height: 20px;
  background: var(--primary-gradient);
  border-radius: 2px;
}

.search-bar {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 16px 20px;
  display: flex;
  gap: 10px;
  align-items: center;
  box-shadow: var(--shadow-sm);
  margin-bottom: 16px;
}

.table-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.pagination-wrap {
  padding: 16px 20px;
  display: flex;
  justify-content: flex-end;
}

.time-cell {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: var(--text-secondary);
}
</style>
