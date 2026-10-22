<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="分类名称" prop="categoryName">
        <el-input v-model="queryParams.categoryName" placeholder="请输入分类名称" clearable style="width:150px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="分类编码" prop="categoryCode">
        <el-input v-model="queryParams.categoryCode" placeholder="请输入分类编码" clearable style="width:150px" @keyup.enter.native="handleQuery"/>
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['zw:kbCategory:add']">新增分类</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="categoryList">
      <el-table-column label="分类ID" align="center" prop="categoryId" width="80" />
      <el-table-column label="分类名称" align="center" prop="categoryName" :show-overflow-tooltip="true" min-width="120" />
      <el-table-column label="分类编码" align="center" prop="categoryCode" width="140" />
      <el-table-column label="排序" align="center" prop="sort" width="80" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.status" active-value="0" inactive-value="1" @change="handleStatusChange(scope.row)" v-hasPermi="['zw:kbCategory:edit']"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" min-width="160" />
      <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)" v-hasPermi="['zw:kbCategory:edit']">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['zw:kbCategory:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称"/>
        </el-form-item>
        <el-form-item label="分类编码" prop="categoryCode">
          <el-input v-model="form.categoryCode" placeholder="请输入分类编码（唯一）"/>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" controls-position="right" style="width:100%"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注"/>
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
import { listKbCategory, getKbCategory, addKbCategory, updateKbCategory, delKbCategory } from "@/api/zw/kbCategory"

export default {
  name: "ZwKbCategory",
  data() {
    return {
      loading: true,
      total: 0,
      categoryList: [],
      open: false,
      title: "",
      showSearch: true,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        categoryName: undefined,
        categoryCode: undefined,
        status: undefined
      },
      form: {},
      rules: {
        categoryName: [{ required: true, message: "分类名称不能为空", trigger: "blur" }],
        categoryCode: [{ required: true, message: "分类编码不能为空", trigger: "blur" }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listKbCategory(this.queryParams).then(res => {
        this.categoryList = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = { categoryName: undefined, categoryCode: undefined, sort: 0, status: '0', remark: undefined }
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
      this.title = "新增分类"
    },
    handleEdit(row) {
      getKbCategory(row.categoryId).then(res => {
        this.form = res.data
        this.open = true
        this.title = "编辑分类"
      })
    },
    handleStatusChange(row) {
      updateKbCategory({ categoryId: row.categoryId, status: row.status }).then(() => {
        this.$modal.msgSuccess("状态修改成功")
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const action = this.form.categoryId ? updateKbCategory(this.form) : addKbCategory(this.form)
          action.then(() => {
            this.$modal.msgSuccess(this.form.categoryId ? "修改成功" : "新增成功")
            this.open = false
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除分类"' + row.categoryName + '"？').then(() => {
        return delKbCategory(row.categoryId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    }
  }
}
</script>
