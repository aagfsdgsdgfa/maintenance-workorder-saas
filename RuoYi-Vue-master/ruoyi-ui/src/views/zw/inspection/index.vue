<template>
  <div class="app-container">
    <!-- 查询条件 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="工单ID" prop="workOrderId">
        <el-input
          v-model="queryParams.workOrderId"
          placeholder="请输入工单ID"
          clearable
          style="width: 150px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="资产ID" prop="assetId">
        <el-input
          v-model="queryParams.assetId"
          placeholder="请输入资产ID"
          clearable
          style="width: 150px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="任务类型" prop="taskType">
        <el-select v-model="queryParams.taskType" placeholder="全部" clearable style="width: 120px">
          <el-option label="例行巡检" value="routine" />
          <el-option label="专项检查" value="special" />
          <el-option label="故障排查" value="fault" />
          <el-option label="维保巡检" value="maintenance" />
        </el-select>
      </el-form-item>
      <el-form-item label="任务状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
          <el-option label="待执行" value="0" />
          <el-option label="已到达" value="1" />
          <el-option label="进行中" value="2" />
          <el-option label="已完成" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="分配状态" prop="assignedTo">
        <el-select v-model="queryParams.assignedTo" placeholder="全部" clearable style="width: 120px">
          <el-option label="已派发" value="assigned" />
          <el-option label="未派发" value="unassigned" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['zw:inspection:add']"
        >新增任务</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-user"
          size="mini"
          :disabled="ids.length === 0"
          @click="handleBatchAssign"
          v-hasPermi="['zw:inspection:edit']"
        >批量分配工程师</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 巡检任务列表 -->
    <el-table v-loading="loading" :data="taskList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="任务ID" align="center" prop="taskId" width="80" />
      <el-table-column label="工单" align="center" prop="workOrderTitle" :show-overflow-tooltip="true" min-width="140">
        <template slot-scope="scope"><span>{{ scope.row.workOrderTitle || ('工单#' + scope.row.workOrderId) }}</span></template>
      </el-table-column>
      <el-table-column label="资产" align="center" prop="assetName" :show-overflow-tooltip="true" min-width="140">
        <template slot-scope="scope"><span>{{ scope.row.assetName || ('资产#' + scope.row.assetId) }}</span></template>
      </el-table-column>
      <el-table-column label="任务类型" align="center" prop="taskType" width="110">
        <template slot-scope="scope"><span>{{ formatTaskType(scope.row.taskType) }}</span></template>
      </el-table-column>
      <el-table-column label="计划时间" align="center" prop="planTime" width="150">
        <template slot-scope="scope"><span>{{ parseTime(scope.row.planTime) }}</span></template>
      </el-table-column>
      <el-table-column label="实际开始" align="center" prop="actualStartTime" width="150">
        <template slot-scope="scope"><span>{{ parseTime(scope.row.actualStartTime) }}</span></template>
      </el-table-column>
      <el-table-column label="打卡地址" align="center" prop="checkInAddress" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column label="状态" align="center" prop="status" width="120">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ formatStatus(scope.row) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="150">
        <template slot-scope="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['zw:inspection:query']"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-position"
            @click="handleExecute(scope.row)"
            v-hasPermi="['zw:inspection:edit']"
            v-if="scope.row.status === '0' || scope.row.status === '1'"
          >执行</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleComplete(scope.row)"
            v-hasPermi="['zw:inspection:edit']"
            v-if="scope.row.status === '1' || scope.row.status === '2'"
          >完成</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zw:inspection:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zw:inspection:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增 / 修改 弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="关联工单" prop="workOrderId">
              <el-select v-model="form.workOrderId" placeholder="请选择工单（可选）" clearable style="width: 100%">
                <el-option v-for="item in workOrderOptions" :key="item.workOrderId" :label="item.orderNo + ' - ' + item.title" :value="item.workOrderId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产" prop="assetId">
              <el-select v-model="form.assetId" placeholder="请选择资产" clearable style="width: 100%">
                <el-option v-for="item in assetOptions" :key="item.assetId" :label="item.assetName + '（' + (item.assetCode || '无编号') + '）'" :value="item.assetId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="任务类型" prop="taskType">
              <el-select v-model="form.taskType" placeholder="请选择任务类型" style="width: 100%">
                <el-option label="例行巡检" value="routine" />
                <el-option label="专项检查" value="special" />
                <el-option label="故障排查" value="fault" />
                <el-option label="维保巡检" value="maintenance" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划时间" prop="planTime">
              <el-date-picker
                v-model="form.planTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择计划时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 执行任务弹窗 -->
    <el-dialog title="执行巡检任务" :visible.sync="executeOpen" width="500px" append-to-body>
      <el-form ref="executeForm" :model="executeForm" label-width="100px">
        <el-form-item label="打卡位置">
          <el-input v-model="executeForm.checkInLocation" placeholder="请输入GPS坐标，如：39.9,116.4" />
        </el-form-item>
        <el-form-item label="打卡地址">
          <el-input v-model="executeForm.checkInAddress" placeholder="请输入打卡地址" />
        </el-form-item>
        <el-form-item label="打卡照片URL">
          <el-input v-model="executeForm.checkInPhoto" placeholder="请输入照片URL" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitExecute">确认到达</el-button>
        <el-button @click="executeOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="巡检任务详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border v-if="detailForm.taskId">
        <el-descriptions-item label="任务ID">{{ detailForm.taskId }}</el-descriptions-item>
        <el-descriptions-item label="工单">{{ detailForm.workOrderTitle || ('工单#' + detailForm.workOrderId) }}</el-descriptions-item>
        <el-descriptions-item label="资产">{{ detailForm.assetName || ('资产#' + detailForm.assetId) }}</el-descriptions-item>
        <el-descriptions-item label="任务类型">{{ formatTaskType(detailForm.taskType) }}</el-descriptions-item>
        <el-descriptions-item label="计划时间">{{ parseTime(detailForm.planTime) }}</el-descriptions-item>
        <el-descriptions-item label="实际开始">{{ parseTime(detailForm.actualStartTime) }}</el-descriptions-item>
        <el-descriptions-item label="实际结束">{{ parseTime(detailForm.actualEndTime) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(detailForm.status)">{{ formatStatus(detailForm.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="打卡位置" :span="2">{{ detailForm.checkInLocation }}</el-descriptions-item>
        <el-descriptions-item label="打卡地址" :span="2">{{ detailForm.checkInAddress }}</el-descriptions-item>
        <el-descriptions-item label="打卡照片" :span="2">{{ detailForm.checkInPhoto }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ parseTime(detailForm.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 批量分配工程师弹窗 -->
    <el-dialog title="批量分配工程师" :visible.sync="assignOpen" width="500px" append-to-body>
      <el-form ref="assignForm" :model="assignForm" label-width="100px">
        <el-form-item label="已选任务">
          <span style="color:#666">{{ ids.length }} 个任务</span>
        </el-form-item>
        <el-form-item label="选择工程师" prop="engineerId">
          <el-select v-model="assignForm.engineerId" placeholder="请选择工程师" clearable style="width:100%">
            <el-option
              v-for="item in engineerOptions"
              :key="item.userId"
              :label="item.realName + (item.phonenumber ? ' - ' + item.phonenumber : '')"
              :value="item.userId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="assignOpen = false">取 消</el-button>
        <el-button type="primary" :loading="assignLoading" @click="submitAssign">确认分配</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTask, getTask, addTask, updateTask, delTask, executeTask, completeTask, batchAssign } from "@/api/zw/inspection"
import { listDispatchableEngineer } from "@/api/zw/engineer"
import { listDept } from "@/api/system/dept"
import { listAsset } from "@/api/zw/asset"
import { listWorkOrder } from "@/api/zw/workOrder"

export default {
  name: "ZwInspectionTask",
  data() {
    return {
      loading: true,
      ids: [],
      showSearch: true,
      total: 0,
      taskList: [],
      title: "",
      open: false,
      executeOpen: false,
      detailOpen: false,
      assignOpen: false,
      assignLoading: false,
      engineerOptions: [],
      assignForm: {
        engineerId: undefined
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        workOrderId: undefined,
        assetId: undefined,
        taskType: undefined,
        status: undefined
      },
      form: {},
      executeForm: {
        taskId: undefined,
        checkInLocation: undefined,
        checkInPhoto: undefined,
        checkInAddress: undefined
      },
      detailForm: {},
      rules: {
        assetId: [{ required: true, message: "请选择资产", trigger: "change" }],
        taskType: [{ required: true, message: "请选择任务类型", trigger: "change" }]
      },
      customerOptions: [],
      assetOptions: [],
      workOrderOptions: [],
      formLoading: false,
      taskTypeOptions: [
        { label: "例行巡检", value: "routine" },
        { label: "专项检查", value: "special" },
        { label: "故障排查", value: "fault" },
        { label: "维保巡检", value: "maintenance" }
      ],
      statusOptions: [
        { label: "待执行", value: "0" },
        { label: "已到达", value: "1" },
        { label: "进行中", value: "2" },
        { label: "已完成", value: "3" }
      ]
    }
  },
  created() {
    this.getList()
  },
  watch: {},
  methods: {
    getList() {
      this.loading = true
      listTask(this.queryParams).then(response => {
        this.taskList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        taskId: undefined,
        workOrderId: undefined,
        assetId: undefined,
        taskType: undefined,
        planTime: undefined
      }
      this.resetForm("form")
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.taskId)
    },
    handleAdd() {
      this.reset()
      this.formLoading = true
      this.loadCustomers()
      this.loadWorkOrders()
      this.loadAssets()
      this.open = true
      this.title = "新增巡检任务"
      this.$nextTick(() => { this.formLoading = false })
    },
    handleUpdate(row) {
      this.reset()
      this.formLoading = true
      this.loadCustomers()
      this.loadWorkOrders()
      this.loadAssets()
      getTask(row.taskId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改巡检任务"
        this.$nextTick(() => { this.formLoading = false })
      })
    },
    loadCustomers() {
      listDept({ pageNum: 1, pageSize: 1000 }).then(res => {
        this.customerOptions = (res.rows || []).filter(d => d.parentId !== 0)
      })
    },
    loadAssets() {
      listAsset({ pageNum: 1, pageSize: 500 }).then(res => {
        this.assetOptions = res.rows || []
      })
    },
    loadWorkOrders() {
      listWorkOrder({ pageNum: 1, pageSize: 500, status: '0' }).then(res => {
        this.workOrderOptions = res.rows || []
      })
    },
    handleView(row) {
      getTask(row.taskId).then(response => {
        this.detailForm = response.data
        this.detailOpen = true
      })
    },
    handleExecute(row) {
      this.executeForm = {
        taskId: row.taskId,
        checkInLocation: undefined,
        checkInPhoto: undefined,
        checkInAddress: undefined
      }
      this.executeOpen = true
    },
    submitExecute() {
      executeTask(
        this.executeForm.taskId,
        this.executeForm.checkInLocation,
        this.executeForm.checkInPhoto,
        this.executeForm.checkInAddress
      ).then(() => {
        this.$modal.msgSuccess("打卡成功")
        this.executeOpen = false
        this.getList()
      })
    },
    handleComplete(row) {
      this.$modal.confirm("确认要完成该巡检任务吗？").then(() => {
        return completeTask(row.taskId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("任务已完成")
      }).catch(() => {})
    },
    handleBatchAssign() {
      this.assignForm.engineerId = undefined
      this.assignLoading = false
      listDispatchableEngineer({ pageNum: 1, pageSize: 200 }).then(res => {
        this.engineerOptions = res.rows || []
        this.assignOpen = true
      })
    },
    submitAssign() {
      if (!this.assignForm.engineerId) {
        this.$modal.msgError("请选择工程师")
        return
      }
      this.assignLoading = true
      batchAssign(this.ids, this.assignForm.engineerId).then(() => {
        this.$modal.msgSuccess("分配成功")
        this.assignOpen = false
        this.getList()
      }).catch(() => {
        this.assignLoading = false
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.taskId != undefined) {
            updateTask(this.form).then(() => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addTask(this.form).then(() => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const taskIds = row.taskId || this.ids
      this.$modal.confirm('是否确认删除任务ID为"' + taskIds + '"的数据项？').then(() => {
        return delTask(taskIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    formatTaskType(type) {
      const item = this.taskTypeOptions.find(opt => opt.value === type)
      return item ? item.label : type
    },
    formatStatus(row) {
      const status = typeof row === 'object' ? row.status : row
      const assignedTo = typeof row === 'object' ? row.assignedTo : null
      if (status === '0' && assignedTo) {
        return '已派发待执行'
      }
      if (status === '0' && !assignedTo) {
        return '待执行'
      }
      const item = this.statusOptions.find(opt => opt.value === status)
      return item ? item.label : status
    },
    statusTagType(status) {
      const map = { '0': 'info', '1': 'warning', '2': '', '3': 'success' }
      return map[status] || ''
    }
  }
}
</script>
