<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="配置名称" prop="configName">
        <el-input v-model="queryParams.configName" placeholder="请输入配置名称" clearable style="width:150px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="资产类型" prop="assetType">
        <el-select v-model="queryParams.assetType" placeholder="全部" clearable style="width:130px">
          <el-option label="设备" value="device" />
          <el-option label="软件" value="software" />
          <el-option label="耗材" value="consumable" />
          <el-option label="其他" value="other" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:100px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['zw:formConfig:add']">新增配置</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="configList">
      <el-table-column label="配置ID" align="center" prop="configId" width="80" />
      <el-table-column label="配置名称" align="center" prop="configName" :show-overflow-tooltip="true" min-width="140" />
      <el-table-column label="资产类型" align="center" prop="assetType" width="100">
        <template slot-scope="scope">
          <el-tag>{{ assetTypeText(scope.row.assetType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="品牌" align="center" prop="brand" width="120" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.status" active-value="0" inactive-value="1" @change="handleStatusChange(scope.row)" v-hasPermi="['zw:formConfig:edit']"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['zw:formConfig:query']">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)" v-hasPermi="['zw:formConfig:edit']">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['zw:formConfig:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="650px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="配置名称" prop="configName">
              <el-input v-model="form.configName" placeholder="请输入配置名称"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产类型" prop="assetType">
              <el-select v-model="form.assetType" style="width:100%">
                <el-option label="设备" value="device" />
                <el-option label="软件" value="software" />
                <el-option label="耗材" value="consumable" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="品牌">
              <el-input v-model="form.brand" placeholder="请输入品牌"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="表单Schema">
          <el-input v-model="form.formSchema" type="textarea" :rows="6" placeholder='JSON格式，如：{"fields":[{"name":"power_status","label":"电源状态","type":"select","options":["正常","异常"]}]}'/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="配置详情" :visible.sync="detailOpen" width="600px" append-to-body>
      <el-descriptions :column="2" border v-if="detailForm.configId">
        <el-descriptions-item label="配置ID">{{ detailForm.configId }}</el-descriptions-item>
        <el-descriptions-item label="配置名称">{{ detailForm.configName }}</el-descriptions-item>
        <el-descriptions-item label="资产类型">{{ assetTypeText(detailForm.assetType) }}</el-descriptions-item>
        <el-descriptions-item label="品牌">{{ detailForm.brand }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detailForm.status === '0' ? '正常' : '停用' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(detailForm.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="表单Schema" :span="2"><pre style="margin:0;white-space:pre-wrap">{{ detailForm.formSchema }}</pre></el-descriptions-item>
      </el-descriptions>
      <div slot="footer"><el-button @click="detailOpen = false">关 闭</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { listFormConfig, getFormConfig, addFormConfig, updateFormConfig, delFormConfig } from "@/api/zw/formConfig"

export default {
  name: "ZwFormConfig",
  data() {
    return {
      loading: true, total: 0, configList: [], open: false, detailOpen: false, title: "", showSearch: true,
      queryParams: { pageNum: 1, pageSize: 10, configName: undefined, assetType: undefined, status: undefined },
      form: {}, detailForm: {},
      rules: { configName: [{ required: true, message: "配置名称不能为空", trigger: "blur" }], assetType: [{ required: true, message: "请选择资产类型", trigger: "change" }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listFormConfig(this.queryParams).then(res => { this.configList = res.rows; this.total = res.total; this.loading = false }) },
    cancel() { this.open = false; this.reset() },
    reset() { this.form = { configName: undefined, assetType: undefined, brand: undefined, formSchema: undefined, status: '0' }; this.resetForm("form") },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery() },
    handleAdd() { this.reset(); this.open = true; this.title = "新增配置" },
    handleEdit(row) { getFormConfig(row.configId).then(res => { this.form = res.data; this.open = true; this.title = "编辑配置" }) },
    handleView(row) { getFormConfig(row.configId).then(res => { this.detailForm = res.data; this.detailOpen = true }) },
    handleStatusChange(row) { updateFormConfig({ configId: row.configId, status: row.status }).then(() => this.$modal.msgSuccess("状态修改成功")) },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const action = this.form.configId ? updateFormConfig(this.form) : addFormConfig(this.form)
          action.then(() => { this.$modal.msgSuccess(this.form.configId ? "修改成功" : "新增成功"); this.open = false; this.getList() })
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除配置ID为"' + row.configId + '"的记录？').then(() => delFormConfig(row.configId)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功") }).catch(() => {})
    },
    assetTypeText(v) { return { device: '设备', software: '软件', consumable: '耗材', other: '其他' }[v] || '-' }
  }
}
</script>
