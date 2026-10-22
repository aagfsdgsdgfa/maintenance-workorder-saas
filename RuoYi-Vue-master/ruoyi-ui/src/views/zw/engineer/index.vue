<template>
  <div class="app-container">
    <!-- 查询条件 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="工程师姓名" prop="realName">
        <el-input v-model="queryParams.realName" placeholder="请输入工程师姓名" clearable style="width: 160px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="手机号码" prop="phonenumber">
        <el-input v-model="queryParams.phonenumber" placeholder="请输入手机号码" clearable style="width: 140px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="工号" prop="employeeNo">
        <el-input v-model="queryParams.employeeNo" placeholder="请输入工号" clearable style="width: 140px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['zw:engineer:add']">新增工程师</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['zw:engineer:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['zw:engineer:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['zw:engineer:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 工程师列表 -->
    <el-table v-loading="loading" :data="engineerList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="工号" align="center" prop="employeeNo" width="120" :show-overflow-tooltip="true" />
      <el-table-column label="工程师姓名" align="center" prop="realName" min-width="100" :show-overflow-tooltip="true" />
      <el-table-column label="手机号码" align="center" prop="phonenumber" width="120" />
      <el-table-column label="部门" align="center" prop="deptName" width="140" :show-overflow-tooltip="true" />
      <el-table-column label="资质等级" align="center" prop="skillLevel" width="90">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.skillLevel" :type="skillTagType(scope.row.skillLevel)">{{ scope.row.skillLevel }}</el-tag>
          <span v-else style="color:#999">-</span>
        </template>
      </el-table-column>
      <el-table-column label="擅长领域" align="center" prop="specialties" width="140" :show-overflow-tooltip="true"><template slot-scope="scope"><span>{{ scope.row.specialties || '-' }}</span></template></el-table-column>
      <el-table-column label="总接单" align="center" prop="totalOrders" width="80" />
      <el-table-column label="完工数" align="center" prop="completedOrders" width="80" />
      <el-table-column label="评分" align="center" prop="ratingAvg" width="80">
        <template slot-scope="scope"><span v-if="scope.row.ratingAvg > 0" style="color:#E6A23C;font-weight:600">★ {{ scope.row.ratingAvg.toFixed(1) }}</span><span v-else style="color:#999">-</span></template>
      </el-table-column>
      <el-table-column label="今日待处理" align="center" prop="pendingToday" width="100">
        <template slot-scope="scope"><el-badge :value="scope.row.pendingToday" :max="99" :hidden="!scope.row.pendingToday" /></template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope"><el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="入职日期" align="center" prop="joinDate" width="110">
        <template slot-scope="scope"><span>{{ parseTime(scope.row.joinDate, '{y}-{m}-{d}') }}</span></template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="260" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-s-custom" @click="handleDispatch(scope.row)" style="color:#67C23A" v-hasPermi="['zw:engineer:dispatch']">派发工单</el-button>
          <el-button size="mini" type="text" icon="el-icon-time" @click="handleDispatchHistory(scope.row)">派发记录</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['zw:engineer:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" style="color:#F56C6C" v-hasPermi="['zw:engineer:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <!-- 新增 / 修改 弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="650px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="关联用户" prop="userId">
              <el-select v-model="form.userId" placeholder="请选择关联的系统用户" filterable style="width:100%" clearable @change="onUserChange">
                <el-option v-for="u in userOptions" :key="u.userId" :label="u.userName + ' / ' + u.nickName" :value="u.userId">
                  <span style="float:left">{{ u.userName }}</span>
                  <span style="float:right;color:#999;font-size:12px">{{ u.nickName }}</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工程师姓名">
              <el-input v-model="form.realName" placeholder="选择用户后自动填充，可修改" maxlength="30" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="工号" prop="employeeNo">
              <el-input v-model="form.employeeNo" placeholder="请输入工号" maxlength="32" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资质等级" prop="skillLevel">
              <el-select v-model="form.skillLevel" placeholder="请选择资质等级" style="width:100%">
                <el-option label="初级" value="初级" />
                <el-option label="中级" value="中级" />
                <el-option label="高级" value="高级" />
                <el-option label="专家" value="专家" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="入职日期" prop="joinDate">
              <el-date-picker v-model="form.joinDate" type="date" value-format="yyyy-MM-dd" placeholder="请选择入职日期" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="擅长领域" prop="specialties">
          <el-input v-model="form.specialties" placeholder="请输入擅长领域，多个用逗号分隔" maxlength="255" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="工程师详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="工号">{{ detailData.employeeNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="工程师姓名">{{ detailData.realName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号码">{{ detailData.phonenumber || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detailData.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ detailData.deptName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="资质等级">{{ detailData.skillLevel || '-' }}</el-descriptions-item>
        <el-descriptions-item label="擅长领域" :span="2">{{ detailData.specialties || '-' }}</el-descriptions-item>
        <el-descriptions-item label="入职日期">{{ parseTime(detailData.joinDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag :type="detailData.status === '0' ? 'success' : 'danger'" size="small">{{ detailData.status === '0' ? '正常' : '停用' }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="总接单">{{ detailData.totalOrders || 0 }}</el-descriptions-item>
        <el-descriptions-item label="完工数">{{ detailData.completedOrders || 0 }}</el-descriptions-item>
        <el-descriptions-item label="评分均分"><span v-if="detailData.ratingAvg > 0" style="color:#E6A23C">★ {{ detailData.ratingAvg.toFixed(1) }}</span><span v-else>-</span></el-descriptions-item>
        <el-descriptions-item label="今日待处理">{{ detailData.pendingToday || 0 }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer"><el-button @click="detailOpen = false">关 闭</el-button></div>
    </el-dialog>

    <!-- 派发工单弹窗 -->
    <el-dialog title="派发工单" :visible.sync="dispatchOpen" width="600px" append-to-body>
      <el-form :model="dispatchForm" ref="dispatchForm" label-width="100px">
        <el-form-item label="目标工程师"><span style="font-weight:600;color:#409EFF">{{ dispatchForm.engineerName }}</span></el-form-item>
        <el-form-item label="选择工单" prop="workOrderId">
          <el-select v-model="dispatchForm.workOrderId" placeholder="请选择待派发工单（仅显示待接单状态）" filterable style="width:100%" clearable>
            <el-option v-for="wo in pendingWorkOrders" :key="wo.workOrderId" :label="wo.orderNo + ' - ' + wo.title" :value="wo.workOrderId">
              <span style="float:left">{{ wo.orderNo }}</span>
              <span style="float:right;color:#999;font-size:12px">{{ wo.title }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="工单详情" v-if="dispatchForm.workOrderId" style="margin-top:10px">
          <div v-if="selectedWorkOrder" class="dispatch-wo-info">
            <div><b>客户：</b>{{ selectedWorkOrder.customerName || '-' }}</div>
            <div><b>优先级：</b><el-tag size="mini" :type="priorityTagType(selectedWorkOrder.priority)">{{ priorityNameMap[selectedWorkOrder.priority] }}</el-tag></div>
            <div><b>描述：</b>{{ selectedWorkOrder.description || '-' }}</div>
            <div><b>SLA截止：</b>{{ parseTime(selectedWorkOrder.slaDeadline) || '-' }}</div>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" :loading="dispatchLoading" @click="submitDispatch">确认派发</el-button>
        <el-button @click="dispatchOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 派发记录弹窗 -->
    <el-dialog title="派发记录" :visible.sync="historyOpen" width="800px" append-to-body>
      <el-table v-loading="historyLoading" :data="historyList" max-height="400">
        <el-table-column label="工单编号" align="center" prop="orderNo" width="180" :show-overflow-tooltip="true" />
        <el-table-column label="工单标题" align="center" prop="title" min-width="200" :show-overflow-tooltip="true" />
        <el-table-column label="客户" align="center" prop="customerName" width="150" :show-overflow-tooltip="true" />
        <el-table-column label="状态" align="center" prop="status" width="90">
          <template slot-scope="scope"><el-tag :type="statusTagType(scope.row.status)">{{ statusNameMap[scope.row.status] }}</el-tag></template>
        </el-table-column>
        <el-table-column label="派发时间" align="center" prop="createTime" width="160">
          <template slot-scope="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
        </el-table-column>
      </el-table>
      <pagination v-show="historyTotal > 0" :total="historyTotal" :page.sync="historyQuery.pageNum" :limit.sync="historyQuery.pageSize" @pagination="getDispatchHistory"/>
      <div slot="footer"><el-button @click="historyOpen = false">关 闭</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { listEngineer, getEngineer, addEngineer, updateEngineer, delEngineer, listDispatchableEngineer, dispatchWorkOrder, dispatchHistory } from "@/api/zw/engineer"
import { listAllUsers } from "@/api/system/user"
import { listWorkOrder } from "@/api/zw/workOrder"

export default {
  name: "ZwEngineer",
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true,
      total: 0, engineerList: [], title: "", open: false,
      form: { userId: undefined, realName: '', employeeNo: undefined, skillLevel: undefined, specialties: undefined, joinDate: undefined },
      rules: { userId: [{ required: true, message: "请选择关联用户", trigger: "change" }], skillLevel: [{ required: true, message: "请选择资质等级", trigger: "change" }] },
      userOptions: [],
      queryParams: { pageNum: 1, pageSize: 10, realName: undefined, phonenumber: undefined, employeeNo: undefined, status: undefined },
      detailOpen: false, detailData: {},
      dispatchOpen: false, dispatchLoading: false, dispatchForm: { engineerId: undefined, engineerName: "", workOrderId: undefined },
      pendingWorkOrders: [],
      historyOpen: false, historyLoading: false, historyList: [], historyTotal: 0,
      historyQuery: { pageNum: 1, pageSize: 10 },
      priorityNameMap: { 1: '低', 2: '中', 3: '高', 4: '紧急' },
      statusNameMap: { 0: '待接单', 1: '处理中', 2: '待确认', 3: '已完成', 4: '已取消' }
    }
  },
  computed: {
    selectedWorkOrder() {
      if (!this.dispatchForm.workOrderId) return null
      return this.pendingWorkOrders.find(wo => wo.workOrderId === this.dispatchForm.workOrderId) || null
    }
  },
  created() { this.getList(); this.getUserOptions() },
  methods: {
    getList() {
      this.loading = true
      listEngineer(this.queryParams).then(res => { this.engineerList = res.rows || []; this.total = res.total || 0; this.loading = false }).catch(() => { this.loading = false })
    },
    getUserOptions() {
      listAllUsers().then(res => {
        this.userOptions = (res.data || []).map(u => ({ userId: u.userId, userName: u.userName, nickName: u.nickName || u.userName }))
      })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.$refs.queryForm.resetFields(); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.engineerId); this.single = selection.length !== 1; this.multiple = !selection.length },
    handleAdd() { this.reset(); this.title = "新增工程师"; this.open = true },
    handleUpdate(row) {
      this.reset()
      const engineerId = row ? row.engineerId : this.ids[0]
      getEngineer(engineerId).then(res => {
        const data = res.data || {}
        this.form = { userId: data.userId, realName: data.realName || '', employeeNo: data.employeeNo, skillLevel: data.skillLevel, specialties: data.specialties, joinDate: data.joinDate ? this.parseTime(data.joinDate, '{y}-{m}-{d}') : null }
        this.title = "修改工程师"; this.open = true
      })
    },
    handleDetail(row) { getEngineer(row.engineerId).then(res => { this.detailData = res.data || {}; this.detailOpen = true }) },
    handleDelete(row) {
      const engineerIds = row ? [row.engineerId] : this.ids
      this.$modal.confirm('确认删除选中的工程师吗？删除后工程师扩展信息将移除。').then(() => delEngineer(engineerIds.join(','))).then(() => { this.getList(); this.$modal.msgSuccess("删除成功") }).catch(() => {})
    },
    handleExport() { this.download('/zw/engineer/export', { ...this.queryParams }, `工程师管理_${new Date().getTime()}.xlsx`) },
    onUserChange(userId) {
      if (!userId) { this.form.realName = ''; return }
      const user = this.userOptions.find(u => u.userId === userId)
      if (user && user.nickName) { this.form.realName = user.nickName }
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const data = { ...this.form }
        if (data.engineerId) {
          updateEngineer(data).then(() => { this.$modal.msgSuccess("修改成功"); this.open = false; this.getList() })
        } else {
          addEngineer(data).then(() => { this.$modal.msgSuccess("新增成功"); this.open = false; this.getList() }).catch(() => { this.$modal.msgError("新增失败") })
        }
      })
    },
    cancel() { this.open = false; this.reset() },
    reset() { this.form = { userId: undefined, realName: '', employeeNo: undefined, skillLevel: undefined, specialties: undefined, joinDate: undefined }; if (this.$refs.form) this.$refs.form.resetFields() },
    handleDispatch(row) {
      this.dispatchForm = { engineerId: row.engineerId, engineerName: row.realName, workOrderId: undefined }
      this.dispatchOpen = true
      listWorkOrder({ status: '0', pageNum: 1, pageSize: 200 }).then(res => { this.pendingWorkOrders = res.rows || [] })
    },
    submitDispatch() {
      if (!this.dispatchForm.workOrderId) { this.$modal.msgError("请选择要派发的工单"); return }
      this.dispatchLoading = true
      dispatchWorkOrder(this.dispatchForm.workOrderId, this.dispatchForm.engineerId).then(res => { this.$modal.msgSuccess("派发成功，系统已向工程师发送通知"); this.dispatchOpen = false; this.getList() }).catch(() => {}).finally(() => { this.dispatchLoading = false })
    },
    handleDispatchHistory(row) { this.historyOpen = true; this.historyQuery = { pageNum: 1, pageSize: 10 }; this.getDispatchHistory(row.engineerId) },
    getDispatchHistory(engineerId) {
      this.historyLoading = true
      dispatchHistory(engineerId, this.historyQuery).then(res => { this.historyList = res.rows || []; this.historyTotal = res.total || 0; this.historyLoading = false }).catch(() => { this.historyLoading = false })
    },
    skillTagType(level) { const map = { '初级': 'info', '中级': 'warning', '高级': 'success', '专家': 'danger' }; return map[level] || 'info' },
    priorityTagType(p) { const map = { 1: '', 2: 'success', 3: 'warning', 4: 'danger' }; return map[p] || '' },
    statusTagType(s) { const map = { 0: 'warning', 1: 'primary', 2: 'info', 3: 'success', 4: 'danger' }; return map[s] || '' }
  }
}
</script>

<style scoped>
.dispatch-wo-info { background: #f5f7fa; border: 1px solid #e4e7ed; border-radius: 4px; padding: 12px 16px; font-size: 13px; line-height: 1.8; }
.dispatch-wo-info div { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
