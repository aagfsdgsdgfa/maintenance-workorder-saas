<template>
  <div class="app-container">
    <!-- 查询条件 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="报修标题" prop="applyTitle">
        <el-input
          v-model="queryParams.applyTitle"
          placeholder="请输入报修标题"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="客户名称" prop="customerName">
        <el-input
          v-model="queryParams.customerName"
          placeholder="请输入客户名称"
          clearable
          style="width: 160px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 150px">
          <el-option label="待受理" value="0" />
          <el-option label="已受理" value="1" />
          <el-option label="处理中" value="2" />
          <el-option label="已完成" value="3" />
          <el-option label="已评价" value="4" />
          <el-option label="已取消" value="5" />
        </el-select>
      </el-form-item>
      <el-form-item label="申请时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 260px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
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
          v-hasPermi="['zw:repairapply:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zw:repairapply:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-check"
          size="mini"
          :disabled="!hasSelectedPending"
          @click="openAcceptTransferDialog"
          v-hasPermi="['zw:repairapply:accept']"
        >一键受理派发</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleBatchDelete"
          v-hasPermi="['zw:repairapply:remove']"
        >批量删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 报修申请列表 -->
    <el-table v-loading="loading" :data="repairList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="报修标题" align="center" prop="applyTitle" :show-overflow-tooltip="true" min-width="200" />
      <el-table-column label="客户名称" align="center" prop="customerName" :show-overflow-tooltip="true" min-width="140" />
      <el-table-column label="资产名称" align="center" prop="assetName" :show-overflow-tooltip="true" min-width="140" />
      <el-table-column label="故障描述" align="center" prop="faultDesc" :show-overflow-tooltip="true" min-width="180" />
      <el-table-column label="联系人" align="center" prop="contactName" width="100" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" width="130" />
      <el-table-column label="期望上门" align="center" prop="expectedTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.expectedTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ statusFormat(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="关联工单" align="center" prop="orderNo" width="160">
        <template slot-scope="scope">
          <span v-if="scope.row.orderNo" style="color:#409EFF;cursor:pointer" @click="handleViewOrder(scope.row)">{{ scope.row.orderNo }}</span>
          <span v-else style="color:#999">-</span>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zw:repairapply:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zw:repairapply:remove']"
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
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="报修标题" prop="applyTitle">
              <el-input v-model="form.applyTitle" placeholder="请输入报修标题" maxlength="200" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户ID" prop="customerId">
              <el-input-number v-model="form.customerId" :min="1" placeholder="请输入客户ID" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产ID" prop="assetId">
              <el-input-number v-model="form.assetId" :min="1" placeholder="请输入资产ID" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="form.contactName" placeholder="请输入联系人" maxlength="64" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" maxlength="20" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="期望上门" prop="expectedTime">
              <el-date-picker
                v-model="form.expectedTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择期望上门时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="故障描述" prop="faultDesc">
              <el-input v-model="form.faultDesc" type="textarea" placeholder="请详细描述故障情况" maxlength="1000" show-word-limit />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="待受理" value="0" />
                <el-option label="已受理" value="1" />
                <el-option label="处理中" value="2" />
                <el-option label="已完成" value="3" />
                <el-option label="已评价" value="4" />
                <el-option label="已取消" value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="报修详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(detailData.status)">{{ statusFormat(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报修标题" :span="2">{{ detailData.applyTitle }}</el-descriptions-item>
        <el-descriptions-item label="客户名称">{{ detailData.customerName }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ detailData.assetName }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ detailData.contactName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="期望上门时间">{{ parseTime(detailData.expectedTime) }}</el-descriptions-item>
        <el-descriptions-item label="关联工单">{{ detailData.orderNo || '无' }}</el-descriptions-item>
        <el-descriptions-item label="故障描述" :span="2">{{ detailData.faultDesc }}</el-descriptions-item>
        <el-descriptions-item label="故障图片" :span="2">{{ detailData.faultPhotos || '无' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ parseTime(detailData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="创建者">{{ detailData.createBy }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 工程师选择弹窗（一键受理派发） -->
    <el-dialog :title="'选择派发工程师（已选 ' + (repairList.filter(item => ids.includes(item.applyId) && item.status === '0').length) + ' 条待受理记录）'" :visible.sync="engineerDialogVisible" width="600px" append-to-body>
      <el-form :model="engineerQueryParams" ref="engineerQueryForm" size="small" :inline="true" style="margin-bottom: 12px">
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="engineerQueryParams.realName" placeholder="工程师姓名" clearable style="width: 150px" @keyup.enter.native="loadEngineers" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="loadEngineers">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetEngineerQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="engineerLoading" :data="engineerList" border stripe highlight-current-row @current-change="handleEngineerSelect" @row-click="handleEngineerRowClick" style="margin-bottom: 12px">
        <el-table-column label="选择" width="60" align="center">
          <template slot-scope="scope">
            <el-radio v-model="engineerRadioValue" :label="scope.row.engineerId">&nbsp;</el-radio>
          </template>
        </el-table-column>
        <el-table-column label="工号" align="center" prop="employeeNo" width="100" />
        <el-table-column label="工程师姓名" align="center" prop="realName" min-width="100" />
        <el-table-column label="手机号码" align="center" prop="phonenumber" width="120" />
        <el-table-column label="资质等级" align="center" prop="skillLevel" width="80">
          <template slot-scope="scope"><el-tag size="mini">{{ scope.row.skillLevel || '-' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="擅长领域" align="center" prop="specialties" :show-overflow-tooltip="true" min-width="120" />
      </el-table>
      <pagination
        v-show="engineerTotal > 0"
        :total="engineerTotal"
        :page.sync="engineerQueryParams.pageNum"
        :limit.sync="engineerQueryParams.pageSize"
        @pagination="loadEngineers"
      />
      <div slot="footer" class="dialog-footer">
        <el-button :disabled="!selectedEngineer" type="primary" :loading="transferLoading" @click="confirmAcceptTransfer">确认受理并派发（{{ repairList.filter(item => ids.includes(item.applyId) && item.status === '0').length }} 条）</el-button>
        <el-button @click="engineerDialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRepairApply, getRepairApply, addRepairApply, updateRepairApply, delRepairApply, acceptRepairApply, transferRepairApply, acceptAndTransferBatchRepairApply, batchDeleteRepairApply } from "@/api/zw/repairApply"
import { listDispatchableEngineer } from "@/api/zw/engineer"

export default {
  name: "ZwRepairApply",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      repairList: [],
      title: "",
      open: false,
      detailOpen: false,
      dateRange: [],
      detailData: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        applyTitle: undefined,
        customerName: undefined,
        status: undefined
      },
      form: {},
      rules: {
        applyTitle: [
          { required: true, message: "报修标题不能为空", trigger: "blur" }
        ],
        customerId: [
          { required: true, message: "客户ID不能为空", trigger: "blur" }
        ]
      },
      statusOptions: [
        { label: "待受理", value: "0" },
        { label: "已受理", value: "1" },
        { label: "处理中", value: "2" },
        { label: "已完成", value: "3" },
        { label: "已评价", value: "4" },
        { label: "已取消", value: "5" }
      ],
      // 一键受理派发相关
      engineerDialogVisible: false,
      engineerLoading: false,
      engineerList: [],
      engineerTotal: 0,
      selectedEngineer: null,
      engineerRadioValue: null,
      hasSelectedPending: false,
      transferLoading: false,
      engineerQueryParams: {
        pageNum: 1,
        pageSize: 10,
        realName: undefined,
        status: "0"
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      const params = {
        ...this.queryParams
      }
      if (this.dateRange && this.dateRange.length === 2) {
        params.startDate = this.dateRange[0]
        params.endDate = this.dateRange[1]
      }
      listRepairApply(params).then(response => {
        this.repairList = response.rows
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
        applyId: undefined,
        applyTitle: undefined,
        customerId: undefined,
        assetId: undefined,
        faultDesc: undefined,
        contactName: undefined,
        contactPhone: undefined,
        expectedTime: undefined,
        status: "0"
      }
      this.resetForm("form")
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.applyId)
      this.single = selection.length !== 1
      this.multiple = selection.length === 0
      // 只要选中的行中至少有1条是"待受理"状态，就允许一键受理派发
      this.hasSelectedPending = selection.length > 0 && selection.some(item => item.status === '0')
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加报修申请"
    },
    handleUpdate(row) {
      this.reset()
      const applyId = row.applyId || this.ids[0]
      getRepairApply(applyId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改报修申请"
      })
    },
    handleDetail(row) {
      getRepairApply(row.applyId).then(response => {
        this.detailData = response.data
        this.detailOpen = true
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.applyId != undefined) {
            updateRepairApply(this.form).then(() => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addRepairApply(this.form).then(() => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const applyIds = row.applyId || this.ids
      this.$modal.confirm('是否确认删除申请编号为"' + applyIds + '"的数据项？').then(() => {
        return delRepairApply(applyIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    handleBatchDelete() {
      if (this.ids.length === 0) {
        this.$modal.msgWarning("请先选择要删除的报修申请")
        return
      }
      this.$modal.confirm('是否确认删除选中的 ' + this.ids.length + ' 条报修申请？').then(() => {
        return batchDeleteRepairApply(this.ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    handleAccept() {
      const applyId = this.ids[0]
      const row = this.repairList.find(item => item.applyId === applyId)
      if (row && row.status !== '0') {
        this.$modal.msgWarning("只能受理【待受理】状态的报修申请")
        return
      }
      this.$modal.confirm('是否确认受理该报修申请？').then(() => {
        return acceptRepairApply(applyId)
      }).then(() => {
        this.$modal.msgSuccess("受理成功")
        this.getList()
      }).catch(() => {})
    },
    openAcceptTransferDialog() {
      if (this.ids.length === 0) {
        this.$modal.msgWarning("请先选择要受理派发的报修申请")
        return
      }
      // 过滤出状态为"待受理"的选中行
      const pendingRows = this.repairList.filter(item => this.ids.includes(item.applyId) && item.status === '0')
      if (pendingRows.length === 0) {
        this.$modal.msgWarning("选中的记录中没有【待受理】状态的报修申请")
        return
      }
      this.selectedEngineer = null
      this.engineerRadioValue = null
      this.transferLoading = false
      this.engineerQueryParams.pageNum = 1
      this.engineerQueryParams.realName = undefined
      this.engineerDialogVisible = true
      this.loadEngineers()
    },
    loadEngineers() {
      this.engineerLoading = true
      listDispatchableEngineer({ ...this.engineerQueryParams }).then(response => {
        this.engineerList = response.rows
        this.engineerTotal = response.total
        this.engineerLoading = false
      })
    },
    resetEngineerQuery() {
      this.engineerQueryParams.realName = undefined
      this.engineerQueryParams.pageNum = 1
    },
    handleEngineerRowClick(row) {
      this.engineerRadioValue = row.engineerId
      this.selectedEngineer = row
    },
    confirmAcceptTransfer() {
      if (!this.selectedEngineer) {
        this.$modal.msgWarning("请选择工程师")
        return
      }
      // 取所有选中的"待受理"记录的applyId
      const pendingIds = this.repairList
        .filter(item => this.ids.includes(item.applyId) && item.status === '0')
        .map(item => item.applyId)
      if (pendingIds.length === 0) {
        this.$modal.msgWarning("没有待受理的记录")
        return
      }
      this.transferLoading = true
      acceptAndTransferBatchRepairApply(pendingIds, this.selectedEngineer.engineerId).then(res => {
        const successCount = res.data || pendingIds.length
        this.$modal.msgSuccess(`已成功受理派发 ${successCount} 条报修申请`)
        this.engineerDialogVisible = false
        this.getList()
      }).catch(() => {
        this.transferLoading = false
      })
    },
    handleViewOrder(row) {
      if (row.workOrderId) {
        this.$router.push({ path: '/zw/workOrder/detail', query: { workOrderId: row.workOrderId } })
      }
    },
    handleTransfer() {
      const applyId = this.ids[0]
      const row = this.repairList.find(item => item.applyId === applyId)
      if (row && row.status !== '1') {
        this.$modal.msgWarning("只能将【已受理】状态的报修申请转为工单")
        return
      }
      this.$prompt('请输入派单工程师ID（可选）', '转工单', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '工程师ID（留空系统自动分配）',
        inputValue: '',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            const assignedTo = instance.inputValue || null
            transferRepairApply(applyId, assignedTo, '2').then(() => {
              this.$modal.msgSuccess("转工单成功")
              this.getList()
              done()
            }).catch(() => {
              // error already handled in catch
            })
          } else {
            done()
          }
        }
      })
    },
    handleExport() {
      const queryParams = this.queryParams
      this.$modal.confirm("是否确认导出所有报修申请数据项？").then(() => {
        this.download('/zw/repairApply/admin/export', {
          ...queryParams
        }, `报修申请_${this.parseTime(new Date(), '{y}{m}{d}{h}{i}{s}')}.xlsx`)
      }).catch(() => {})
    },
    statusFormat(status) {
      const item = this.statusOptions.find(opt => opt.value === status)
      return item ? item.label : status
    },
    statusTagType(status) {
      const map = {
        '0': 'warning',
        '1': 'primary',
        '2': 'info',
        '3': 'success',
        '4': 'success',
        '5': 'info'
      }
      return map[status] || ''
    }
  }
}
</script>
