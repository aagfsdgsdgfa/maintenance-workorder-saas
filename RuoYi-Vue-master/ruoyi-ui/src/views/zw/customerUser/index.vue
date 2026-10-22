<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="客户ID" prop="customerId">
        <el-input v-model="queryParams.customerId" placeholder="请输入客户ID" clearable style="width:140px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="姓名" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入姓名" clearable style="width:140px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable style="width:140px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['zw:customerUser:add']">新增联系人</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="customerUserList">
      <el-table-column label="联系人ID" align="center" prop="custUserId" width="100" />
      <el-table-column label="客户ID" align="center" prop="customerId" width="100" />
      <el-table-column label="姓名" align="center" prop="userName" width="120" />
      <el-table-column label="手机号" align="center" prop="phone" width="130" />
      <el-table-column label="邮箱" align="center" prop="email" :show-overflow-tooltip="true" min-width="160" />
      <el-table-column label="职位" align="center" prop="position" width="120" />
      <el-table-column label="主联系人" align="center" prop="isPrimary" width="90">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isPrimary === 'Y'" type="success">是</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['zw:customerUser:query']">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)" v-hasPermi="['zw:customerUser:edit']">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['zw:customerUser:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="550px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户ID" prop="customerId">
              <el-input-number v-model="form.customerId" :min="1" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入姓名"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="职位">
          <el-input v-model="form.position" placeholder="请输入职位"/>
        </el-form-item>
        <el-form-item label="主联系人">
          <el-radio-group v-model="form.isPrimary">
            <el-radio label="Y">是</el-radio>
            <el-radio label="N">否</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="联系人详情" :visible.sync="detailOpen" width="500px" append-to-body>
      <el-descriptions :column="2" border v-if="detailForm.custUserId">
        <el-descriptions-item label="联系人ID">{{ detailForm.custUserId }}</el-descriptions-item>
        <el-descriptions-item label="客户ID">{{ detailForm.customerId }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailForm.userName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailForm.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detailForm.email }}</el-descriptions-item>
        <el-descriptions-item label="职位">{{ detailForm.position }}</el-descriptions-item>
        <el-descriptions-item label="主联系人">{{ detailForm.isPrimary === 'Y' ? '是' : '否' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer"><el-button @click="detailOpen = false">关 闭</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { listCustomerUser, getCustomerUser, addCustomerUser, updateCustomerUser, delCustomerUser } from "@/api/zw/customerUser"

export default {
  name: "ZwCustomerUser",
  data() {
    return {
      loading: true, total: 0, customerUserList: [], open: false, detailOpen: false, title: "", showSearch: true,
      queryParams: { pageNum: 1, pageSize: 10, customerId: undefined, userName: undefined, phone: undefined },
      form: {}, detailForm: {},
      rules: { customerId: [{ required: true, message: "客户ID不能为空", trigger: "blur" }], userName: [{ required: true, message: "姓名不能为空", trigger: "blur" }], phone: [{ required: true, message: "手机号不能为空", trigger: "blur" }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listCustomerUser(this.queryParams).then(res => { this.customerUserList = res.rows; this.total = res.total; this.loading = false }) },
    cancel() { this.open = false; this.reset() },
    reset() { this.form = { customerId: undefined, userName: undefined, phone: undefined, email: undefined, position: undefined, isPrimary: 'N' }; this.resetForm("form") },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery() },
    handleAdd() { this.reset(); this.open = true; this.title = "新增联系人" },
    handleEdit(row) { getCustomerUser(row.custUserId).then(res => { this.form = res.data; this.open = true; this.title = "编辑联系人" }) },
    handleView(row) { getCustomerUser(row.custUserId).then(res => { this.detailForm = res.data; this.detailOpen = true }) },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const action = this.form.custUserId ? updateCustomerUser(this.form) : addCustomerUser(this.form)
          action.then(() => { this.$modal.msgSuccess(this.form.custUserId ? "修改成功" : "新增成功"); this.open = false; this.getList() })
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除联系人ID为"' + row.custUserId + '"的记录？').then(() => delCustomerUser(row.custUserId)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功") }).catch(() => {})
    }
  }
}
</script>
