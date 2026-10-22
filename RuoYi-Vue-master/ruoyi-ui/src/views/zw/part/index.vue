<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="配件名称" prop="partName">
        <el-input v-model="queryParams.partName" placeholder="请输入配件名称" clearable style="width:150px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="配件类别" prop="partType">
        <el-select v-model="queryParams.partType" placeholder="全部" clearable style="width:130px">
          <el-option v-for="t in partTypeOptions" :key="t" :label="t" :value="t" />
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['zw:part:add']">新增配件</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="partList">
      <el-table-column label="配件ID" align="center" prop="partId" width="80" />
      <el-table-column label="配件名称" align="center" prop="partName" min-width="120" />
      <el-table-column label="配件编码" align="center" prop="partCode" width="120" />
      <el-table-column label="类别" align="center" prop="partType" width="100" />
      <el-table-column label="规格型号" align="center" prop="specModel" min-width="140" :show-overflow-tooltip="true" />
      <el-table-column label="单位" align="center" prop="unit" width="60" />
      <el-table-column label="参考单价" align="center" prop="unitPrice" width="100">
        <template slot-scope="scope">¥{{ scope.row.unitPrice }}</template>
      </el-table-column>
      <el-table-column label="品牌" align="center" prop="brand" width="90" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope"><el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" min-width="120" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)" v-hasPermi="['zw:part:edit']">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['zw:part:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="配件名称" prop="partName">
              <el-input v-model="form.partName" placeholder="请输入配件名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="配件编码" prop="partCode">
              <el-input v-model="form.partCode" placeholder="如 PWR-001" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="配件类别" prop="partType">
              <el-select v-model="form.partType" placeholder="请选择" style="width:100%">
                <el-option v-for="t in partTypeOptions" :key="t" :label="t" :value="t" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌" prop="brand">
              <el-input v-model="form.brand" placeholder="如 Dell、华为" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="规格型号" prop="specModel">
              <el-input v-model="form.specModel" placeholder="请输入规格型号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="form.unit" placeholder="如 个、块、根" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="参考单价" prop="unitPrice">
              <el-input-number v-model="form.unitPrice" :min="0" :precision="2" placeholder="元" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPart, getPart, addPart, updatePart, delPart } from "@/api/zw/part"

export default {
  name: "ZwPart",
  data() {
    return {
      loading: true,
      total: 0,
      partList: [],
      open: false,
      title: "",
      showSearch: true,
      partTypeOptions: ['电源类', '散热类', '存储类', '内存类', '网络类', '计算类', '核心配件', '其他'],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        partName: undefined,
        partType: undefined,
        status: undefined
      },
      form: {},
      rules: {
        partName: [{ required: true, message: "配件名称不能为空", trigger: "blur" }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listPart(this.queryParams).then(res => {
        this.partList = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        partName: undefined,
        partCode: undefined,
        partType: undefined,
        specModel: undefined,
        unit: '个',
        unitPrice: 0,
        brand: undefined,
        remark: undefined,
        status: '0'
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
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "新增配件"
    },
    handleEdit(row) {
      getPart(row.partId).then(res => {
        this.form = res.data
        this.open = true
        this.title = "编辑配件"
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.partId) {
            updatePart(this.form).then(() => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addPart(this.form).then(() => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除配件"' + row.partName + '"？').then(() => {
        return delPart(row.partId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    }
  }
}
</script>
