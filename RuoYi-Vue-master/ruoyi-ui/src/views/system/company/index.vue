<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="公司名称" prop="deptName">
        <el-input
          v-model="queryParams.deptName"
          placeholder="请输入公司名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="公司编码" prop="companyCode">
        <el-input
          v-model="queryParams.companyCode"
          placeholder="请输入公司编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="公司状态" clearable>
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:company:add']"
        >新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="companyList" row-key="deptId" @expand-change="handleExpandChange">
      <el-table-column type="expand" width="50">
        <template slot-scope="scope">
          <div class="company-users-section" v-if="scope.row._usersLoaded">
            <div class="section-header">
              <i class="el-icon-user"></i>
              <span>公司下用户（共 {{ scope.row.users ? scope.row.users.length : 0 }} 人）</span>
            </div>
            <el-table v-if="scope.row.users && scope.row.users.length > 0" :data="scope.row.users" size="mini" border style="width: 100%">
              <el-table-column label="用户名称" prop="userName" width="140" />
              <el-table-column label="昵称" prop="nickName" width="130" />
              <el-table-column label="手机号码" prop="phonenumber" width="130" />
              <el-table-column label="状态" prop="status" width="80">
                <template slot-scope="userScope">
                  <dict-tag :options="dict.type.sys_normal_disable" :value="userScope.row.status"/>
                </template>
              </el-table-column>
              <el-table-column label="创建时间" prop="createTime" width="160">
                <template slot-scope="userScope">
                  <span>{{ parseTime(userScope.row.createTime) }}</span>
                </template>
              </el-table-column>
            </el-table>
            <div v-else class="empty-users">暂无关联用户</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="公司编码" prop="companyCode" width="150" />
      <el-table-column label="公司名称" prop="deptName" width="220" show-overflow-tooltip />
      <el-table-column label="联系人" prop="leader" width="120" />
      <el-table-column label="联系电话" prop="phone" width="130" />
      <el-table-column label="邮箱" prop="email" width="180" show-overflow-tooltip />
      <el-table-column label="状态" prop="status" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:company:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:company:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改公司对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="公司名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入公司名称" />
        </el-form-item>
        <el-form-item label="公司编码" prop="companyCode">
          <el-input v-model="form.companyCode" placeholder="请输入公司编码" maxlength="50" />
        </el-form-item>
        <el-form-item label="联系人" prop="leader">
          <el-input v-model="form.leader" placeholder="请输入联系人" maxlength="50" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
        </el-form-item>
        <el-form-item label="公司状态">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.sys_normal_disable"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
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
import { listCompany, getCompany, addCompany, updateCompany, delCompany } from "@/api/system/company"
import { listUser } from "@/api/system/user"

export default {
  name: "Company",
  dicts: ['sys_normal_disable'],
  data() {
    return {
      loading: true,
      showSearch: true,
      companyList: [],
      total: 0,
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deptName: undefined,
        companyCode: undefined,
        status: undefined
      },
      form: {},
      rules: {
        deptName: [
          { required: true, message: "公司名称不能为空", trigger: "blur" }
        ],
        companyCode: [
          { required: true, message: "公司编码不能为空", trigger: "blur" },
          { pattern: /^[a-zA-Z0-9_-]+$/, message: "公司编码只能包含字母、数字、下划线和连字符", trigger: "blur" }
        ],
        email: [
          { type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] }
        ],
        phone: [
          { pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输入正确的手机号码", trigger: "blur" }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listCompany(this.queryParams).then(response => {
        this.companyList = response.rows.map(item => {
          item.users = []
          item._usersLoaded = false
          item._usersLoading = false
          return item
        })
        this.total = response.total
        this.loading = false
      })
    },
    handleExpandChange(row, expandedRows) {
      if (expandedRows.includes(row)) {
        this.getCompanyUsers(row)
      }
    },
    getCompanyUsers(company) {
      if (company._usersLoaded || company._usersLoading) return
      company._usersLoading = true
      listUser({ deptId: company.deptId, pageNum: 1, pageSize: 100 }).then(response => {
        company.users = response.rows
        company._usersLoaded = true
        company._usersLoading = false
      }).catch(() => {
        company._usersLoading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        deptId: undefined,
        deptName: undefined,
        companyCode: undefined,
        leader: undefined,
        phone: undefined,
        email: undefined,
        orderNum: 100,
        status: "0"
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
      this.title = "添加公司"
    },
    handleUpdate(row) {
      this.reset()
      const deptId = row.deptId
      getCompany(deptId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改公司"
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.deptId != undefined) {
            updateCompany(this.form).then(() => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addCompany(this.form).then(() => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const deptIds = row.deptId
      this.$modal.confirm('是否确认删除公司"' + row.deptName + '"？').then(() => {
        return delCompany(deptIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.company-users-section {
  padding: 12px 20px;
  background: #f5f7fa;
  border-radius: 4px;
}
.section-header {
  margin-bottom: 10px;
  font-size: 13px;
  color: #606266;
}
.section-header i {
  margin-right: 4px;
}
.empty-users {
  text-align: center;
  color: #909399;
  padding: 10px 0;
  font-size: 13px;
}
</style>
