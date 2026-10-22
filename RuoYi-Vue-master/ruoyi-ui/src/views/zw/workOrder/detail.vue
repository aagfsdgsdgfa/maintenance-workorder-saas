<template>
  <div class="app-container">
    <el-button icon="el-icon-arrow-left" size="mini" @click="goBack">返回</el-button>
    <el-button type="warning" icon="el-icon-document" size="mini"
      :loading="exportLoading" @click="handleExportWord" v-hasPermi="['zw:workOrder:export']">导出Word</el-button>

    <!-- 工单概要卡片 -->
    <el-card class="box-card mt15" shadow="never">
      <div slot="header">
        <span class="card-title">工单概要</span>
        <el-tag :type="statusTagType(detail.status)" size="small" style="float: right;">{{ statusNameMap[detail.status] }}</el-tag>
      </div>
      <el-descriptions :column="3" border size="small">
        <el-descriptions-item label="工单编号">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="工单标题">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="工单类型">{{ orderTypeNameMap[detail.orderType] }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ detail.customerName }}</el-descriptions-item>
        <el-descriptions-item label="资产">{{ detail.assetName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="priorityTagType(detail.priority)" size="small">{{ priorityNameMap[detail.priority] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="工程师">{{ detail.assignedToName || '待分配' }}</el-descriptions-item>
        <el-descriptions-item label="SLA截止时间">
          <span :style="{ color: isSlaOverdue ? '#F56C6C' : 'inherit', fontWeight: isSlaOverdue ? '600' : '400' }">
            {{ parseTime(detail.slaDeadline) }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(detail.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ detail.contactName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detail.contactPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="上门地址" :span="2">{{ detail.contactAddress || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="detail.description" class="wo-desc mt10">
        <span class="label">工单描述：</span>
        <span class="value">{{ detail.description }}</span>
      </div>
    </el-card>

    <!-- Tab页签 -->
    <el-card class="mt15" shadow="never">
      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="info">
          <el-descriptions :column="2" border size="small" class="mt10">
            <el-descriptions-item label="工单编号">{{ detail.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="工单类型">{{ orderTypeNameMap[detail.orderType] }}</el-descriptions-item>
            <el-descriptions-item label="工单标题">{{ detail.title }}</el-descriptions-item>
            <el-descriptions-item label="优先级">
              <el-tag :type="priorityTagType(detail.priority)" size="small">{{ priorityNameMap[detail.priority] }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="客户">{{ detail.customerName }}</el-descriptions-item>
            <el-descriptions-item label="关联资产">{{ detail.assetName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="分配工程师">{{ detail.assignedToName || '待分配' }}</el-descriptions-item>
            <el-descriptions-item label="派单人">{{ detail.assignedByName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="SLA截止时间">{{ parseTime(detail.slaDeadline) }}</el-descriptions-item>
            <el-descriptions-item label="完成时间">{{ parseTime(detail.completeTime) || '-' }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="statusTagType(detail.status)" size="small">{{ statusNameMap[detail.status] }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建人">{{ detail.createBy }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ parseTime(detail.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ parseTime(detail.updateTime) }}</el-descriptions-item>
            <el-descriptions-item label="工单描述" :span="2">{{ detail.description || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <!-- 巡检任务列表 -->
        <el-tab-pane label="巡检任务列表" name="inspection">
          <el-table v-loading="loading" :data="detail.inspectionTasks" size="small" class="mt10">
            <el-table-column label="任务类型" align="center" prop="taskType" />
            <el-table-column label="计划时间" align="center" prop="planTime" width="170">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.planTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="实际开始时间" align="center" prop="actualStartTime" width="170">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.actualStartTime) || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="实际结束时间" align="center" prop="actualEndTime" width="170">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.actualEndTime) || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="巡检结果" align="center" prop="result" :show-overflow-tooltip="true" />
            <el-table-column label="状态" align="center" prop="status" width="100">
              <template slot-scope="scope">
                <el-tag :type="taskStatusTagType(scope.row.status)" size="small">{{ scope.row.statusName }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loading && (!detail.inspectionTasks || detail.inspectionTasks.length === 0)" description="暂无巡检任务数据" />
        </el-tab-pane>

        <!-- 打卡记录 -->
        <el-tab-pane label="打卡记录" name="checkin">
          <el-table v-loading="loading" :data="detail.checkInRecords" size="small" class="mt10">
            <el-table-column label="打卡类型" align="center" prop="checkInType">
              <template slot-scope="scope">
                {{ checkInTypeNameMap[scope.row.checkInType] || scope.row.checkInType }}
              </template>
            </el-table-column>
            <el-table-column label="打卡位置" align="center" prop="location" :show-overflow-tooltip="true" />
            <el-table-column label="打卡时间" align="center" prop="checkInTime" width="170">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.checkInTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="打卡照片" align="center" prop="photoUrl" width="120">
              <template slot-scope="scope">
                <el-image
                  v-if="scope.row.photoUrl"
                  :src="scope.row.photoUrl"
                  style="width: 60px; height: 60px;"
                  fit="cover"
                  :preview-src-list="[scope.row.photoUrl]"
                />
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
          </el-table>
          <el-empty v-if="!loading && (!detail.checkInRecords || detail.checkInRecords.length === 0)" description="暂无打卡记录数据" />
        </el-tab-pane>

        <!-- 故障记录 -->
        <el-tab-pane label="故障记录" name="fault">
          <el-table v-loading="loading" :data="detail.faultRecords" size="small" class="mt10">
            <el-table-column label="故障类型" align="center" prop="faultType" />
            <el-table-column label="故障描述" align="center" prop="faultDesc" :show-overflow-tooltip="true" />
            <el-table-column label="故障级别" align="center" prop="faultLevel" width="100">
              <template slot-scope="scope">
                <el-tag :type="faultLevelTagType(scope.row.faultLevel)" size="small">{{ scope.row.faultLevelName }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="处理措施" align="center" prop="solution" :show-overflow-tooltip="true" />
            <el-table-column label="处理人" align="center" prop="handleBy" />
            <el-table-column label="处理时间" align="center" prop="handleTime" width="170">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.handleTime) || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" align="center" prop="status" width="100">
              <template slot-scope="scope">
                <el-tag size="small">{{ scope.row.status === '0' ? '未处理' : '已处理' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loading && (!detail.faultRecords || detail.faultRecords.length === 0)" description="暂无故障记录数据" />
        </el-tab-pane>

        <!-- 满意度评价 -->
        <el-tab-pane label="满意度评价" name="satisfaction">
          <div v-if="detail.satisfaction" class="sat-wrap mt10">
            <el-row :gutter="20">
              <el-col :span="4">
                <div class="sat-score-box">
                  <div class="score-num">{{ detail.satisfaction.score }}</div>
                  <div class="score-label">综合评分</div>
                  <el-rate v-model="detail.satisfaction.score" disabled text-color="#ff9900" />
                </div>
              </el-col>
              <el-col :span="20">
                <el-descriptions :column="2" border size="small">
                  <el-descriptions-item label="评价人">{{ detail.satisfaction.evaluatorName || '-' }}</el-descriptions-item>
                  <el-descriptions-item label="评价时间">{{ parseTime(detail.satisfaction.createTime) }}</el-descriptions-item>
                  <el-descriptions-item label="响应速度评分">{{ detail.satisfaction.responseScore }}分</el-descriptions-item>
                  <el-descriptions-item label="服务质量评分">{{ detail.satisfaction.serviceScore }}分</el-descriptions-item>
                  <el-descriptions-item label="专业程度评分">{{ detail.satisfaction.professionalScore }}分</el-descriptions-item>
                  <el-descriptions-item label="总体评价" :span="2">{{ detail.satisfaction.comment || '-' }}</el-descriptions-item>
                </el-descriptions>
              </el-col>
            </el-row>
          </div>
          <el-empty v-else description="暂无满意度评价数据" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { getWorkOrder, exportWorkOrderWord } from "@/api/zw/workOrder"

export default {
  name: "ZwWorkOrderDetail",
  data() {
    return {
      loading: true,
      exportLoading: false,
      workOrderId: null,
      activeTab: "info",
      detail: {
        inspectionTasks: [],
        checkInRecords: [],
        faultRecords: [],
        satisfaction: null
      },
      // 工单类型映射
      orderTypeNameMap: {
        '1': '巡检',
        '2': '维修',
        '3': '送货',
        '4': '混合'
      },
      // 状态名称映射
      statusNameMap: {
        '0': '待接单',
        '1': '处理中',
        '2': '待确认',
        '3': '已完成',
        '4': '已取消'
      },
      // 优先级名称映射
      priorityNameMap: {
        '1': '低',
        '2': '中',
        '3': '高',
        '4': '紧急'
      },
      // 打卡类型映射
      checkInTypeNameMap: {
        'arrival': '到达打卡',
        'leave': '离开打卡',
        'normal': '正常打卡'
      }
    }
  },
  computed: {
    isSlaOverdue() {
      if (!this.detail.slaDeadline || this.detail.status === '3' || this.detail.status === '4') {
        return false
      }
      return new Date(this.detail.slaDeadline) < new Date()
    }
  },
  created() {
    this.workOrderId = this.$route.query.workOrderId
    if (this.workOrderId) {
      this.getDetail()
    }
  },
  methods: {
    /** 获取工单详情 */
    getDetail() {
      this.loading = true
      getWorkOrder(this.workOrderId).then(response => {
        this.detail = response.data || {}
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    /** 返回列表 */
    goBack() {
      this.$router.push({ path: "/zw/workOrder/index" })
    },
    /** 导出工单Word */
    handleExportWord() {
      this.$modal.confirm('确认导出该工单的Word文档吗？').then(() => {
        this.exportLoading = true
        exportWorkOrderWord(this.workOrderId).then(blob => {
          const pad = n => n < 10 ? '0' + n : '' + n
          const d = new Date()
          const dateStr = `${d.getFullYear()}${pad(d.getMonth() + 1)}${pad(d.getDate())}`
          const fileName = `维保工单_${this.detail.orderNo}_${dateStr}.docx`
          const url = window.URL.createObjectURL(new Blob([blob], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' }))
          const link = document.createElement('a')
          link.href = url
          link.download = fileName
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          window.URL.revokeObjectURL(url)
        }).catch(() => {
          this.$modal.msgError('导出失败，请重试')
        }).finally(() => {
          this.exportLoading = false
        })
      }).catch(() => {})
    },
    /** 状态标签颜色 */
    statusTagType(status) {
      const map = {
        '0': 'info',
        '1': 'warning',
        '2': 'primary',
        '3': 'success',
        '4': 'info'
      }
      return map[status] || ''
    },
    /** 优先级标签颜色 */
    priorityTagType(priority) {
      const map = {
        '1': 'info',
        '2': 'primary',
        '3': 'warning',
        '4': 'danger'
      }
      return map[priority] || ''
    },
    /** 巡检任务状态标签颜色 */
    taskStatusTagType(status) {
      const map = {
        '0': 'info',
        '1': 'primary',
        '2': 'warning',
        '3': 'success'
      }
      return map[status] || ''
    },
    /** 故障级别标签颜色 */
    faultLevelTagType(level) {
      const map = {
        '1': 'info',
        '2': 'warning',
        '3': 'danger'
      }
      return map[level] || ''
    }
  }
}
</script>

<style scoped>
.box-card {
  border: 0;
}
.card-title {
  font-size: 15px;
  font-weight: 600;
}
.mt10 {
  margin-top: 10px;
}
.mt15 {
  margin-top: 15px;
}
.wo-desc {
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
  line-height: 1.6;
}
.wo-desc .label {
  font-weight: 600;
  color: #606266;
}
.wo-desc .value {
  color: #303133;
}
.sat-wrap {
  padding: 10px;
}
.sat-score-box {
  text-align: center;
  padding: 20px 10px;
  background: #f5f7fa;
  border-radius: 8px;
}
.score-num {
  font-size: 42px;
  color: #409EFF;
  font-weight: 700;
  line-height: 1.2;
}
.score-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
}
</style>
