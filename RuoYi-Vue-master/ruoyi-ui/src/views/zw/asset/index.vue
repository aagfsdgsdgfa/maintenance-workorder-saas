<template>
  <div class="app-container">
    <!-- 查询条件 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="资产名称" prop="assetName">
        <el-input
          v-model="queryParams.assetName"
          placeholder="请输入资产名称"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="资产编码" prop="assetCode">
        <el-input
          v-model="queryParams.assetCode"
          placeholder="请输入资产编码"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="客户" prop="customerName">
        <el-input
          v-model="queryParams.customerName"
          placeholder="请输入客户名称"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="资产状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 160px">
          <el-option label="正常" value="0" />
          <el-option label="故障" value="1" />
          <el-option label="维修中" value="2" />
          <el-option label="已报废" value="3" />
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
          v-hasPermi="['zw:asset:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zw:asset:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 资产列表 -->
    <el-table v-loading="loading" :data="assetList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="资产编号" align="center" prop="assetId" width="80" />
      <el-table-column label="资产编码" align="center" prop="assetCode" :show-overflow-tooltip="true" width="160" />
      <el-table-column label="资产名称" align="center" prop="assetName" :show-overflow-tooltip="true" min-width="180" />
      <el-table-column label="资产类型" align="center" prop="assetType" width="100">
        <template slot-scope="scope">
          <span>{{ formatAssetType(scope.row.assetType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="品牌" align="center" prop="brand" :show-overflow-tooltip="true" width="120" />
      <el-table-column label="型号" align="center" prop="model" :show-overflow-tooltip="true" width="140" />
      <el-table-column label="客户" align="center" prop="customerName" :show-overflow-tooltip="true" width="150" />
      <el-table-column label="安装位置" align="center" prop="location" :show-overflow-tooltip="true" width="150" />
      <el-table-column label="健康度" align="center" prop="healthScore" width="80">
        <template slot-scope="scope">
          <span :style="{ color: healthScoreColor(scope.row.healthScore) }">{{ scope.row.healthScore }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ formatStatus(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="140" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zw:asset:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zw:asset:remove']"
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
          <el-col :span="12">
            <el-form-item label="资产编码" prop="assetCode">
              <el-input v-model="form.assetCode" placeholder="请输入资产编码/SN" maxlength="64" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产名称" prop="assetName">
              <el-input v-model="form.assetName" placeholder="请输入资产名称" maxlength="200" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="资产类型" prop="assetType">
              <el-select v-model="form.assetType" placeholder="请选择资产类型" style="width: 100%">
                <el-option label="设备" value="device" />
                <el-option label="耗材" value="consumable" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户ID" prop="customerId">
              <el-input-number v-model="form.customerId" :min="1" placeholder="请输入客户ID" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="品牌" prop="brand">
              <el-input v-model="form.brand" placeholder="请输入品牌" maxlength="64" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="型号" prop="model">
              <el-input v-model="form.model" placeholder="请输入型号" maxlength="64" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="购买日期" prop="purchaseDate">
              <el-date-picker
                v-model="form.purchaseDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择购买日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="保修截止" prop="warrantyEndDate">
              <el-date-picker
                v-model="form.warrantyEndDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择保修截止日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="安装位置" prop="location">
              <el-input v-model="form.location" placeholder="请输入安装位置" maxlength="255" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="二维码编号" prop="qrCode">
              <el-input v-model="form.qrCode" placeholder="请输入二维码编号" maxlength="64" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="正常" value="0" />
                <el-option label="故障" value="1" />
                <el-option label="维修中" value="2" />
                <el-option label="已报废" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="健康度评分" prop="healthScore">
              <el-input-number v-model="form.healthScore" :min="0" :max="100" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-col :span="24">
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" maxlength="500" show-word-limit />
          </el-form-item>
        </el-col>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAsset, getAsset, addAsset, updateAsset, delAsset, exportAsset } from "@/api/zw/asset"

export default {
  name: "ZwAsset",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 资产表格数据
      assetList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        assetName: undefined,
        assetCode: undefined,
        customerName: undefined,
        status: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        assetName: [
          { required: true, message: "资产名称不能为空", trigger: "blur" },
          { max: 200, message: "资产名称最大长度为200个字符", trigger: "blur" }
        ],
        assetCode: [
          { required: true, message: "资产编码不能为空", trigger: "blur" },
          { max: 64, message: "资产编码最大长度为64个字符", trigger: "blur" }
        ]
      },
      // 资产类型映射
      assetTypeOptions: [
        { label: "设备", value: "device" },
        { label: "耗材", value: "consumable" },
        { label: "其他", value: "other" }
      ],
      // 状态映射
      statusOptions: [
        { label: "正常", value: "0" },
        { label: "故障", value: "1" },
        { label: "维修中", value: "2" },
        { label: "已报废", value: "3" }
      ]
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询资产列表 */
    getList() {
      this.loading = true
      listAsset(this.queryParams).then(response => {
        this.assetList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        assetId: undefined,
        assetCode: undefined,
        assetName: undefined,
        assetType: "device",
        brand: undefined,
        model: undefined,
        customerId: undefined,
        isThirdParty: "0",
        purchaseDate: undefined,
        warrantyEndDate: undefined,
        location: undefined,
        qrCode: undefined,
        status: "0",
        healthScore: 100,
        inspectionConfig: undefined,
        remark: undefined
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.assetId)
      this.single = selection.length !== 1
      this.multiple = selection.length === 0
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加资产"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const assetId = row.assetId || this.ids[0]
      getAsset(assetId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改资产"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.assetId != undefined) {
            updateAsset(this.form).then(() => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addAsset(this.form).then(() => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const assetIds = row.assetId || this.ids
      this.$modal.confirm('是否确认删除资产编号为"' + assetIds + '"的数据项？').then(function() {
        return delAsset(assetIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮 */
    handleExport() {
      const queryParams = this.queryParams
      this.$modal.confirm("是否确认导出所有资产数据项？").then(() => {
        exportAsset(queryParams).then(response => {
          const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
          const url = window.URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = '资产数据_' + this.parseTime(new Date(), '{y}{m}{d}{h}{i}{s}') + '.xlsx'
          link.click()
          window.URL.revokeObjectURL(url)
          this.$modal.msgSuccess("导出成功")
        })
      }).catch(() => {})
    },
    /** 资产类型格式化 */
    formatAssetType(assetType) {
      const item = this.assetTypeOptions.find(opt => opt.value === assetType)
      return item ? item.label : assetType
    },
    /** 状态格式化 */
    formatStatus(status) {
      const item = this.statusOptions.find(opt => opt.value === status)
      return item ? item.label : status
    },
    /** 状态标签颜色 */
    statusTagType(status) {
      const map = {
        '0': '',
        '1': 'danger',
        '2': 'warning',
        '3': 'info'
      }
      return map[status] || ''
    },
    /** 健康度颜色 */
    healthScoreColor(score) {
      if (score >= 80) return '#67C23A'
      if (score >= 60) return '#E6A23C'
      return '#F56C6C'
    }
  }
}
</script>
