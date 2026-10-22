<template>
  <div class="app-container">
    <!-- 查询条件 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="合同名称" prop="contractName">
        <el-input v-model="queryParams.contractName" placeholder="请输入合同名称" clearable style="width:160px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="合同编号" prop="contractNo">
        <el-input v-model="queryParams.contractNo" placeholder="请输入合同编号" clearable style="width:160px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="客户名称" prop="customerId">
        <el-select v-model="queryParams.customerId" placeholder="请选择客户" clearable filterable style="width:160px">
          <el-option v-for="c in customerOptions" :key="c.id" :label="c.label" :value="c.id"/>
        </el-select>
      </el-form-item>
      <el-form-item label="合同状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px">
          <el-option label="生效中" value="0"/>
          <el-option label="已到期" value="1"/>
          <el-option label="已终止" value="2"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['zw:contract:add']">新增合同</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-upload
          :show-file-list="false"
          :before-upload="handleOcrUpload"
          accept="image/*"
          action="#"
          style="display:inline-block;margin-right:8px"
        >
          <el-button size="mini" plain icon="el-icon-camera" type="success">OCR扫描</el-button>
        </el-upload>
      </el-col>
      <el-col :span="1.5">
        <el-upload
          :show-file-list="false"
          :before-upload="handleExcelUpload"
          accept=".xlsx,.xls"
          action="#"
          style="display:inline-block"
        >
          <el-button size="mini" plain icon="el-icon-upload2" type="warning">Excel导入</el-button>
        </el-upload>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>

    <el-table v-loading="loading" :data="contractList">
      <el-table-column label="合同ID" align="center" prop="contractId" width="80"/>
      <el-table-column label="合同编号" align="center" prop="contractNo" width="140"/>
      <el-table-column label="合同名称" align="center" prop="contractName" :show-overflow-tooltip="true" min-width="160"/>
      <el-table-column label="客户" align="center" prop="customerId" width="120">
        <template slot-scope="scope">{{ getCustomerName(scope.row.customerId) }}</template>
      </el-table-column>
      <el-table-column label="合同期间" width="200">
        <template slot-scope="scope">{{ parseTime(scope.row.startDate) }} ~ {{ parseTime(scope.row.endDate) }}</template>
      </el-table-column>
      <el-table-column label="含巡检次数" align="center" prop="includedInspections" width="100"/>
      <el-table-column label="含维修次数" align="center" prop="includedRepairs" width="100"/>
<el-table-column label="状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <el-tag :type="statusTag(scope.row.status)">{{ statusName(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="图片" align="center" width="80">
        <template slot-scope="scope">
          <el-image v-if="scope.row.imageUrl" :src="scope.row.imageUrl" fit="cover" style="width:40px;height:40px;border-radius:4px" :preview-src-list="[scope.row.imageUrl]"/>
          <span v-else style="color:#c0c4cc">-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['zw:contract:query']">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)" v-hasPermi="['zw:contract:edit']">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['zw:contract:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <!-- OCR预览弹窗 -->
    <el-dialog title="OCR扫描识别" :visible.sync="ocrOpen" width="900px" append-to-body>
      <el-alert v-if="ocrError" :title="ocrError" type="error" show-icon :closable="false" style="margin-bottom:12px"/>
      <template v-if="!ocrError && ocrForm._imageBase64">
        <el-row :gutter="16">
          <el-col :span="10">
            <div style="border:1px solid #ddd;border-radius:6px;overflow:hidden;text-align:center;background:#f5f5f5">
              <img :src="ocrForm._imageBase64" style="max-width:100%;max-height:400px;display:block;margin:0 auto"/>
              <div style="padding:8px;font-size:12px;color:#999">原始扫描图片</div>
            </div>
          </el-col>
          <el-col :span="14">
            <div style="margin-bottom:8px;font-weight:600;color:#333">自动识别结果（可编辑）</div>
            <el-form ref="ocrFormRef" :model="ocrForm" label-width="110px" size="small">
              <el-form-item label="合同名称"><el-input v-model="ocrForm.contractName" placeholder="OCR自动识别，可修改"/></el-form-item>
              <el-form-item label="合同编号"><el-input v-model="ocrForm.contractNo" placeholder="OCR自动识别，可修改"/></el-form-item>
              <el-row :gutter="8">
                <el-col :span="12"><el-form-item label="开始日期"><el-date-picker v-model="ocrForm.startDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/></el-form-item></el-col>
                <el-col :span="12"><el-form-item label="结束日期"><el-date-picker v-model="ocrForm.endDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/></el-form-item></el-col>
              </el-row>
              <el-row :gutter="8">
                <el-col :span="12"><el-form-item label="SLA响应(h)"><el-input-number v-model="ocrForm.slaResponseHours" :min="0" style="width:100%"/></el-form-item></el-col>
                <el-col :span="12"><el-form-item label="SLA到场(h)"><el-input-number v-model="ocrForm.slaArrivalHours" :min="0" style="width:100%"/></el-form-item></el-col>
              </el-row>
              <el-row :gutter="8">
                <el-col :span="8"><el-form-item label="含巡检次数"><el-input-number v-model="ocrForm.includedInspections" :min="0" style="width:100%"/></el-form-item></el-col>
                <el-col :span="8"><el-form-item label="含维修次数"><el-input-number v-model="ocrForm.includedRepairs" :min="0" style="width:100%"/></el-form-item></el-col>
                <el-col :span="8"><el-form-item label="免费配件额度"><el-input-number v-model="ocrForm.freePartsQuota" :min="0" style="width:100%"/></el-form-item></el-col>
              </el-row>
              <el-form-item label="客户">
                <el-select v-model="ocrForm.customerId" placeholder="请选择客户" filterable style="width:100%">
                  <el-option v-for="c in customerOptions" :key="c.id" :label="c.label" :value="c.id"/>
                </el-select>
              </el-form-item>
            </el-form>
            <div style="margin-top:10px">
              <div style="font-weight:600;color:#333;margin-bottom:4px">OCR原始文本</div>
              <div style="background:#f8f8f8;border:1px solid #e8e8e8;border-radius:4px;padding:8px;max-height:120px;overflow:auto;font-size:12px;color:#555;white-space:pre-wrap;word-break:break-all">{{ ocrForm._rawText || '（无）' }}</div>
            </div>
          </el-col>
        </el-row>
      </template>
      <div slot="footer">
        <el-button type="primary" @click="submitOcr" v-if="!ocrError">确认并填入表单</el-button>
        <el-button @click="ocrOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- Excel导入预览弹窗 -->
    <el-dialog title="Excel导入预览" :visible.sync="excelOpen" width="900px" append-to-body>
      <el-alert v-if="excelError" :title="excelError" type="error" show-icon :closable="false" style="margin-bottom:12px"/>
      <template v-if="!excelError">
        <div style="margin-bottom:10px;font-weight:600;color:#333">识别到 {{ (excelForm._preview || []).length }} 条数据（第1条将填入表单）</div>
        <el-table :data="excelForm._preview || []" border size="small" max-height="320" style="width:100%">
          <el-table-column prop="contractName" label="合同名称" min-width="160" show-overflow-tooltip/>
          <el-table-column prop="contractNo" label="合同编号" width="130" show-overflow-tooltip/>
          <el-table-column prop="customerName" label="客户" width="120" show-overflow-tooltip/>
          <el-table-column prop="startDate" label="开始日期" width="110"/>
          <el-table-column prop="endDate" label="结束日期" width="110"/>
          <el-table-column prop="slaResponseHours" label="响应(h)" width="80" align="center"/>
          <el-table-column prop="slaArrivalHours" label="到场(h)" width="80" align="center"/>
          <el-table-column prop="includedInspections" label="巡检次数" width="80" align="center"/>
          <el-table-column prop="includedRepairs" label="维修次数" width="80" align="center"/>
          <el-table-column prop="freePartsQuota" label="配件额度" width="90" align="center"/>
        </el-table>
        <div style="margin-top:14px;font-weight:600;color:#333">填写到表单</div>
        <el-form ref="excelFormRef" :model="excelForm" label-width="110px" size="small" style="margin-top:8px">
          <el-row :gutter="8">
            <el-col :span="12"><el-form-item label="合同名称"><el-input v-model="excelForm.contractName"/></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="合同编号"><el-input v-model="excelForm.contractNo"/></el-form-item></el-col>
          </el-row>
          <el-row :gutter="8">
            <el-col :span="12"><el-form-item label="开始日期"><el-date-picker v-model="excelForm.startDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="结束日期"><el-date-picker v-model="excelForm.endDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/></el-form-item></el-col>
          </el-row>
          <el-row :gutter="8">
            <el-col :span="8"><el-form-item label="SLA响应(h)"><el-input-number v-model="excelForm.slaResponseHours" :min="0" style="width:100%"/></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="SLA到场(h)"><el-input-number v-model="excelForm.slaArrivalHours" :min="0" style="width:100%"/></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="含巡检次数"><el-input-number v-model="excelForm.includedInspections" :min="0" style="width:100%"/></el-form-item></el-col>
          </el-row>
          <el-row :gutter="8">
            <el-col :span="8"><el-form-item label="含维修次数"><el-input-number v-model="excelForm.includedRepairs" :min="0" style="width:100%"/></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="免费配件额度"><el-input-number v-model="excelForm.freePartsQuota" :min="0" style="width:100%"/></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="状态">
              <el-select v-model="excelForm.status" style="width:100%">
                <el-option label="生效" value="0"/>
                <el-option label="到期" value="1"/>
                <el-option label="终止" value="2"/>
              </el-select>
            </el-form-item></el-col>
          </el-row>
          <el-form-item label="客户">
            <el-select v-model="excelForm.customerId" placeholder="请选择客户" filterable style="width:100%">
              <el-option v-for="c in customerOptions" :key="c.id" :label="c.label" :value="c.id"/>
            </el-select>
          </el-form-item>
        </el-form>
      </template>
      <div slot="footer">
        <el-button type="primary" @click="submitExcel" v-if="!excelError">确认并填入表单</el-button>
        <el-button @click="excelOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="合同编号" prop="contractNo">
              <el-input v-model="form.contractNo" placeholder="请输入合同编号"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同名称" prop="contractName">
              <el-input v-model="form.contractName" placeholder="请输入合同名称"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="所属客户" prop="customerId">
          <el-select v-model="form.customerId" placeholder="请选择客户" filterable style="width:100%">
            <el-option v-for="c in customerOptions" :key="c.id" :label="c.label" :value="c.id"/>
          </el-select>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="合同开始日期" prop="startDate">
              <el-date-picker v-model="form.startDate" type="date" value-format="yyyy-MM-dd" placeholder="请选择" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同结束日期" prop="endDate">
              <el-date-picker v-model="form.endDate" type="date" value-format="yyyy-MM-dd" placeholder="请选择" style="width:100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="SLA响应(h)" prop="slaResponseHours">
              <el-input-number v-model="form.slaResponseHours" :min="0" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="SLA到场(h)" prop="slaArrivalHours">
              <el-input-number v-model="form.slaArrivalHours" :min="0" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="折扣率(%)">
              <el-input-number v-model="form.discountRate" :min="0" :max="100" style="width:100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="含巡检次数" prop="includedInspections">
              <el-input-number v-model="form.includedInspections" :min="0" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="含维修次数" prop="includedRepairs">
              <el-input-number v-model="form.includedRepairs" :min="0" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="免费配件额度">
              <el-input-number v-model="form.freePartsQuota" :min="0" style="width:100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="合同图片">
              <el-upload action="" :show-file-list="false" :before-upload="handleImageUpload" accept="image/*">
                <img v-if="form.imageUrl" :src="form.imageUrl" style="max-height:80px;border-radius:4px"/>
                <i v-else class="el-icon-plus" style="font-size:24px;color:#999;padding:8px;border:1px dashed #ddd;border-radius:4px;display:inline-block"/>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同附件">
              <el-upload action="" :show-file-list="false" :before-upload="handleAttachmentUpload" accept="image/*,.pdf,.doc,.docx">
                <img v-if="form.attachmentUrl" :src="form.attachmentUrl" style="max-height:80px;border-radius:4px"/>
                <i v-else class="el-icon-plus" style="font-size:24px;color:#999;padding:8px;border:1px dashed #ddd;border-radius:4px;display:inline-block"/>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio label="0">生效</el-radio>
                <el-radio label="1">到期</el-radio>
                <el-radio label="2">终止</el-radio>
              </el-radio-group>
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
    <el-dialog title="合同详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border v-if="detailForm.contractId">
        <el-descriptions-item label="合同ID">{{ detailForm.contractId }}</el-descriptions-item>
        <el-descriptions-item label="合同编号">{{ detailForm.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="合同名称" :span="2">{{ detailForm.contractName }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ getCustomerName(detailForm.customerId) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTag(detailForm.status)">{{ statusName(detailForm.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ parseTime(detailForm.startDate) }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ parseTime(detailForm.endDate) }}</el-descriptions-item>
        <el-descriptions-item label="SLA响应时效">{{ detailForm.slaResponseHours }} 小时</el-descriptions-item>
        <el-descriptions-item label="SLA到场时效">{{ detailForm.slaArrivalHours }} 小时</el-descriptions-item>
        <el-descriptions-item label="含巡检次数">{{ detailForm.includedInspections }} 次</el-descriptions-item>
        <el-descriptions-item label="含维修次数">{{ detailForm.includedRepairs }} 次</el-descriptions-item>
        <el-descriptions-item label="已用巡检">{{ detailForm.usedInspections }} 次</el-descriptions-item>
        <el-descriptions-item label="已用维修">{{ detailForm.usedRepairs }} 次</el-descriptions-item>
        <el-descriptions-item label="免费配件额度">¥{{ detailForm.freePartsQuota }}</el-descriptions-item>
        <el-descriptions-item label="已用配件金额">¥{{ detailForm.usedPartsAmount }}</el-descriptions-item>
        <el-descriptions-item label="折扣率">{{ detailForm.discountRate }}%</el-descriptions-item>
        <el-descriptions-item label="合同附件" :span="2">
          <a v-if="detailForm.attachmentUrl" :href="detailForm.attachmentUrl" target="_blank">{{ detailForm.attachmentUrl }}</a>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="合同图片" :span="2">
          <el-image v-if="detailForm.imageUrl" :src="detailForm.imageUrl" fit="contain" style="height:120px" :preview-src-list="[detailForm.imageUrl]" title="点击放大"/>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(detailForm.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ detailForm.createBy }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer"><el-button @click="detailOpen = false">关 闭</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { listContract, getContract, addContract, updateContract, delContract, ocrContractImage, parseContractExcel } from "@/api/zw/contract"
import request from "@/utils/request"
import { companyTreeSelect } from "@/api/system/user"
import { parseTime } from "@/utils/ruoyi"

export default {
  name: "ZwContract",
  data() {
    return {
      loading: true,
      total: 0,
      contractList: [],
      showSearch: true,
      open: false,
      detailOpen: false,
      ocrOpen: false,
      excelOpen: false,
      title: "",
      customerOptions: [],
      queryParams: {
        pageNum: 1, pageSize: 10,
        contractName: undefined, contractNo: undefined, customerId: undefined, status: undefined
      },
      form: {},
      detailForm: {},
      ocrForm: {},
      excelForm: {},
      ocrError: null,
      excelError: null,
      rules: {
        contractNo: [{ required: true, message: "合同编号不能为空", trigger: "blur" }],
        contractName: [{ required: true, message: "合同名称不能为空", trigger: "blur" }],
        customerId: [{ required: true, message: "请选择客户", trigger: "change" }],
        startDate: [{ required: true, message: "请选择开始日期", trigger: "change" }],
        endDate: [{ required: true, message: "请选择结束日期", trigger: "change" }]
      }
    }
  },
  created() {
    this.getList()
    this.getCustomerOptions()
  },
  methods: {
    parseTime,
getList() {
      this.loading = true
      listContract(this.queryParams).then(res => {
        this.contractList = res.rows || []
        this.total = res.total || 0
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    getCustomerOptions() {
      companyTreeSelect().then(res => {
        this.customerOptions = res.data || []
      })
    },
    getCustomerName(customerId) {
      if (!customerId) return '-'
      const c = this.customerOptions.find(x => String(x.id) === String(customerId))
      return c ? c.label : customerId
    },
    cancel() { this.open = false; this.reset() },
    reset() {
      this.form = {
        contractNo: undefined, contractName: undefined, customerId: undefined,
        startDate: undefined, endDate: undefined, slaResponseHours: undefined,
        slaArrivalHours: undefined, includedInspections: undefined, includedRepairs: undefined,
        freePartsQuota: undefined, extraVisitFee: undefined, discountRate: undefined,
        attachmentUrl: undefined, imageUrl: undefined, status: '0'
      }
      this.resetForm("form")
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery() },
    handleAdd() { this.reset(); this.open = true; this.title = "新增合同" },
    handleEdit(row) {
      getContract(row.contractId).then(res => {
        this.form = res.data
        this.open = true
        this.title = "编辑合同"
      })
    },
    handleView(row) {
      getContract(row.contractId).then(res => {
        this.detailForm = res.data || {}
        this.detailOpen = true
      })
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除合同"' + row.contractName + '"？').then(() => {
        return delContract(row.contractId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const action = this.form.contractId ? updateContract(this.form) : addContract(this.form)
          action.then(() => {
            this.$modal.msgSuccess(this.form.contractId ? "修改成功" : "新增成功")
            this.open = false
            this.getList()
          })
        }
      })
    },
    // OCR图片扫描
    handleOcrUpload(file) {
      const formData = new FormData()
      formData.append('file', file)
      ocrContractImage(formData).then(res => {
        if (res.code === 200 && res.data) {
          this.ocrForm = { ...res.data, status: res.data.status || '0' }
          this.ocrError = null
        } else {
          this.ocrError = res.msg || "OCR识别失败，请手动填写"
          this.ocrForm = {}
        }
        this.ocrOpen = true
      }).catch(() => {
        this.ocrError = "OCR服务调用失败，请手动填写"
        this.ocrForm = {}
        this.ocrOpen = true
      })
      return false
    },
    submitOcr() {
      this.form = { ...this.ocrForm }
      this.ocrOpen = false
      this.open = true
      this.title = "新增合同（OCR识别）"
    },
    // Excel导入
    handleExcelUpload(file) {
      const formData = new FormData()
      formData.append('file', file)
      parseContractExcel(formData).then(res => {
        if (res.code === 200 && res.data) {
          this.excelForm = { ...res.data, status: res.data.status || '0' }
          this.excelError = null
        } else {
          this.excelError = res.msg || "Excel解析失败，请手动填写"
          this.excelForm = {}
        }
        this.excelOpen = true
      }).catch(() => {
        this.excelError = "Excel解析服务调用失败，请手动填写"
        this.excelForm = {}
        this.excelOpen = true
      })
      return false
    },
    submitExcel() {
      this.form = { ...this.excelForm }
      this.excelOpen = false
      this.open = true
      this.title = "新增合同（Excel导入）"
    },
    handleImageUpload(file) {
      const formData = new FormData()
      formData.append('file', file)
      request.post('/common/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      }).then(res => {
        if (res.code === 200 && res.url) {
          this.form.imageUrl = window.location.origin + res.url
        } else {
          this.$modal.msgError(res.msg || '图片上传失败')
        }
      }).catch(() => {
        this.$modal.msgError('图片上传失败')
      })
      return false
    },
    handleAttachmentUpload(file) {
      const formData = new FormData()
      formData.append('file', file)
      request.post('/common/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      }).then(res => {
        if (res.code === 200 && res.url) {
          this.form.attachmentUrl = window.location.origin + res.url
        } else {
          this.$modal.msgError(res.msg || '附件上传失败')
        }
      }).catch(() => {
        this.$modal.msgError('附件上传失败')
      })
      return false
    },
    statusName(status) {
      if (status === '0' || status === 'active') return '生效中'
      if (status === '1' || status === 'expired') return '已到期'
      if (status === '2' || status === 'terminated') return '已终止'
      return '-'
    },
    statusTag(status) {
      if (status === '0' || status === 'active') return 'success'
      if (status === '1' || status === 'expired') return 'warning'
      if (status === '2' || status === 'terminated') return 'info'
      return ''
    }
  }
}
</script>
