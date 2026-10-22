<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="资产ID" prop="assetId">
        <el-input v-model="queryParams.assetId" placeholder="请输入资产ID" clearable style="width: 140px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="工单ID" prop="workOrderId">
        <el-input v-model="queryParams.workOrderId" placeholder="请输入工单ID" clearable style="width: 140px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="故障类型" prop="faultType">
        <el-select v-model="queryParams.faultType" placeholder="全部" clearable style="width: 130px">
          <el-option label="硬件故障" value="hardware" />
          <el-option label="软件故障" value="software" />
          <el-option label="网络故障" value="network" />
          <el-option label="其他故障" value="other" />
        </el-select>
      </el-form-item>
      <el-form-item label="故障级别" prop="faultLevel">
        <el-select v-model="queryParams.faultLevel" placeholder="全部" clearable style="width: 110px">
          <el-option label="轻微" value="1" />
          <el-option label="一般" value="2" />
          <el-option label="严重" value="3" />
          <el-option label="紧急" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="faultStatus">
        <el-select v-model="queryParams.faultStatus" placeholder="全部" clearable style="width: 120px">
          <el-option label="已上报" value="0" />
          <el-option label="处理中" value="1" />
          <el-option label="已解决" value="2" />
          <el-option label="已验收" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['zw:fault:add']">新增故障</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="faultList">
      <el-table-column label="故障ID" align="center" prop="faultId" width="80" />
      <el-table-column label="客户名称" align="center" prop="customerName" width="120" :show-overflow-tooltip="true" />
      <el-table-column label="资产名称" align="center" prop="assetName" width="120" :show-overflow-tooltip="true" />
      <el-table-column label="关联工单" align="center" prop="orderNo" width="120">
        <template slot-scope="scope">
          <el-link v-if="scope.row.orderNo" type="primary" :underline="false" @click="$router.push('/zw/workOrder/detail?workOrderId=' + scope.row.workOrderId)">{{ scope.row.orderNo }}</el-link>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="故障类型" align="center" prop="faultType" width="100">
        <template slot-scope="scope">
          <el-tag :type="faultTypeTag(scope.row.faultType)">{{ faultTypeText(scope.row.faultType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="故障级别" align="center" prop="faultLevel" width="90">
        <template slot-scope="scope">
          <el-tag :type="faultLevelTag(scope.row.faultLevel)">{{ faultLevelText(scope.row.faultLevel) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="faultStatus" width="90">
        <template slot-scope="scope">
          <el-tag :type="faultStatusTag(scope.row.faultStatus)">{{ faultStatusText(scope.row.faultStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="故障描述" align="center" prop="faultDesc" :show-overflow-tooltip="true" min-width="160" />
      <el-table-column label="故障时间" align="center" prop="faultTime" width="160">
        <template slot-scope="scope"><span>{{ parseTime(scope.row.faultTime) }}</span></template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['zw:fault:query']">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)" v-hasPermi="['zw:fault:edit']">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['zw:fault:remove']">删除</el-button>
          <el-button size="mini" type="text" icon="el-icon-document" @click="handleToWorkOrder(scope.row)" v-hasPermi="['zw:fault:edit']" :disabled="!!scope.row.workOrderId">转工单</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="资产ID" prop="assetId">
              <el-input-number v-model="form.assetId" :min="1" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户ID" prop="customerId">
              <el-input-number v-model="form.customerId" :min="1" style="width:100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="故障类型" prop="faultType">
              <el-select v-model="form.faultType" style="width:100%">
                <el-option label="硬件故障" value="hardware" />
                <el-option label="软件故障" value="software" />
                <el-option label="网络故障" value="network" />
                <el-option label="其他故障" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="故障级别" prop="faultLevel">
              <el-select v-model="form.faultLevel" style="width:100%">
                <el-option label="轻微" value="1" />
                <el-option label="一般" value="2" />
                <el-option label="严重" value="3" />
                <el-option label="紧急" value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="故障描述" prop="faultDesc">
          <el-input v-model="form.faultDesc" type="textarea" :rows="3" placeholder="请输入故障描述" @input="handleFaultDescInput"/>
        </el-form-item>
        <el-form-item label="知识库推荐" v-if="kbSuggestions.length > 0">
          <div class="kb-suggestions">
            <el-tag v-for="item in kbSuggestions" :key="item.kbId" class="kb-tag" @click="handleKbSelect(item)">{{ item.kbTitle }}</el-tag>
          </div>
        </el-form-item>
        <el-form-item label="故障时间" prop="faultTime">
          <el-date-picker v-model="form.faultTime" type="datetime" placeholder="选择时间" value-format="yyyy-MM-dd HH:mm:ss" style="width:100%"/>
        </el-form-item>
        <el-form-item label="解决方案" prop="resolution">
          <el-input v-model="form.resolution" type="textarea" :rows="3" placeholder="请输入解决方案"/>
        </el-form-item>
        <el-divider content-position="left">同时生成工单</el-divider>
        <el-form-item label="同步派发">
          <el-checkbox v-model="createWithWorkOrder">同时创建工单并派发工程师</el-checkbox>
        </el-form-item>
        <el-form-item v-if="createWithWorkOrder" label="选择工程师" prop="assignedTo">
          <el-select v-model="selectedEngineerForFault" placeholder="请选择派发工程师" clearable style="width:100%">
            <el-option v-for="item in engineerOptions" :key="item.userId" :label="item.realName + '（' + (item.specialties || '通用') + '）'" :value="item.userId" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="故障详情" :visible.sync="detailOpen" width="600px" append-to-body>
      <el-descriptions :column="2" border v-if="detailForm.faultId">
        <el-descriptions-item label="故障ID">{{ detailForm.faultId }}</el-descriptions-item>
        <el-descriptions-item label="客户名称">{{ detailForm.customerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ detailForm.assetName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="关联工单">
          <el-link v-if="detailForm.orderNo" type="primary" :underline="false" @click="$router.push('/zw/workOrder/detail?workOrderId=' + detailForm.workOrderId)">{{ detailForm.orderNo }}</el-link>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="故障类型">
          <el-tag :type="faultTypeTag(detailForm.faultType)">{{ faultTypeText(detailForm.faultType) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="故障级别">
          <el-tag :type="faultLevelTag(detailForm.faultLevel)">{{ faultLevelText(detailForm.faultLevel) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="faultStatusTag(detailForm.faultStatus)">{{ faultStatusText(detailForm.faultStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="故障时间">{{ parseTime(detailForm.faultTime) }}</el-descriptions-item>
        <el-descriptions-item label="解决时间">{{ parseTime(detailForm.resolveTime) }}</el-descriptions-item>
        <el-descriptions-item label="故障描述" :span="2">{{ detailForm.faultDesc }}</el-descriptions-item>
        <el-descriptions-item label="解决方案" :span="2">{{ detailForm.resolution }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer"><el-button @click="detailOpen = false">关 闭</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { listFaultRecord, getFaultRecord, addFaultRecord, updateFaultRecord, delFaultRecord, toWorkOrder, addFaultRecordWithWorkOrder } from "@/api/zw/faultRecord"
import { listDispatchableEngineer } from "@/api/zw/engineer"
import { suggestKnowledgeBase } from "@/api/zw/knowledgeBase"

export default {
  name: "ZwFaultRecord",
  data() {
    return {
      loading: true,
      total: 0,
      faultList: [],
      open: false,
      detailOpen: false,
      title: "",
      showSearch: true,
      queryParams: { pageNum: 1, pageSize: 10, customerId: undefined, assetId: undefined, workOrderId: undefined, faultType: undefined, faultLevel: undefined, faultStatus: undefined },
      form: {},
      detailForm: {},
      kbSuggestions: [],
      createWithWorkOrder: false,
      engineerOptions: [],
      selectedEngineerForFault: null,
      rules: {
        assetId: [{ required: true, message: "请选择资产", trigger: "change" }],
        customerId: [{ required: true, message: "请选择客户", trigger: "change" }],
        faultType: [{ required: true, message: "请选择故障类型", trigger: "change" }],
        faultLevel: [{ required: true, message: "请选择故障级别", trigger: "change" }],
        faultDesc: [{ required: true, message: "故障描述不能为空", trigger: "blur" }]
      }
    }
  },
  created() { this.getList() },
  methods: {
    getList() {
      this.loading = true
      listFaultRecord(this.queryParams).then(res => { this.faultList = res.rows; this.total = res.total; this.loading = false })
    },
    cancel() { this.open = false; this.reset() },
    reset() {
      this.form = { customerId: undefined, assetId: undefined, workOrderId: undefined, faultType: undefined, faultLevel: undefined, faultDesc: undefined, faultTime: undefined, resolution: undefined }
      this.createWithWorkOrder = false
      this.selectedEngineerForFault = null
      this.resetForm("form")
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery() },
    handleAdd() { this.reset(); this.loadEngineers(); this.open = true; this.title = "新增故障" },
    handleEdit(row) { getFaultRecord(row.faultId).then(res => { this.form = res.data; this.open = true; this.title = "编辑故障" }) },
    handleView(row) { getFaultRecord(row.faultId).then(res => { this.detailForm = res.data; this.detailOpen = true }) },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) return
        if (this.form.faultId) {
          // 编辑模式
          updateFaultRecord(this.form).then(() => {
            this.$modal.msgSuccess("修改成功")
            this.open = false
            this.getList()
          })
        } else if (this.createWithWorkOrder && this.selectedEngineerForFault) {
          // 新增 + 同时生成工单
          addFaultRecordWithWorkOrder(this.form, this.selectedEngineerForFault).then(res => {
            this.$modal.msgSuccess("故障上报成功" + (res.data ? "，已创建工单（ID：" + res.data + "）" : ""))
            this.open = false
            this.getList()
          })
        } else {
          // 新增普通故障
          addFaultRecord(this.form).then(() => {
            this.$modal.msgSuccess("新增成功")
            this.open = false
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除故障ID为"' + row.faultId + '"的记录？').then(() => delFaultRecord(row.faultId)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功") }).catch(() => {})
    },
    handleToWorkOrder(row) {
      if (row.workOrderId) {
        this.$modal.msgWarning("该故障已关联工单")
        return
      }
      this.$modal.confirm('是否将此故障转为工单？').then(() => {
        toWorkOrder(row.faultId).then(res => {
          this.$modal.msgSuccess("转工单成功，工单ID：" + res.data)
          this.getList()
        })
      }).catch(() => {})
    },
    loadEngineers() {
      listDispatchableEngineer().then(res => {
        this.engineerOptions = res.data || []
      })
    },
    faultTypeText(v) { return { hardware: '硬件故障', software: '软件故障', network: '网络故障', other: '其他故障' }[v] || '-' },
    faultTypeTag(v) { return { hardware: 'danger', software: 'warning', network: 'info', other: '' }[v] || '' },
    faultLevelText(v) { return { '1': '轻微', '2': '一般', '3': '严重', '4': '紧急' }[v] || '-' },
    faultLevelTag(v) { return { '1': 'info', '2': '', '3': 'warning', '4': 'danger' }[v] || '' },
    faultStatusText(v) { return { '0': '已上报', '1': '处理中', '2': '已解决', '3': '已验收' }[v] || '-' },
    faultStatusTag(v) { return { '0': 'info', '1': 'warning', '2': 'success', '3': 'success' }[v] || '' },
    handleFaultDescInput() {
      if (!this.form.faultDesc || this.form.faultDesc.length < 2) {
        this.kbSuggestions = []
        return
      }
      suggestKnowledgeBase(this.form.faultDesc).then(res => {
        this.kbSuggestions = res.data || []
      })
    },
    handleKbSelect(item) {
      if (this.form.resolution) {
        this.form.resolution += '\n' + item.content
      } else {
        this.form.resolution = item.content
      }
    }
  }
}
</script>

<style scoped>
.kb-suggestions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.kb-tag {
  cursor: pointer;
}
</style>
