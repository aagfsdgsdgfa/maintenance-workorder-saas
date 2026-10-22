<template>
  <div class="app-container">
    <!-- 搜索条件 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="巡检点名称" prop="pointName">
        <el-input
          v-model="queryParams.pointName"
          placeholder="请输入巡检点名称"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="资产ID" prop="assetId">
        <el-input-number
          v-model="queryParams.assetId"
          :min="1"
          placeholder="资产ID"
          style="width: 160px"
          controls-position="right"
        />
      </el-form-item>
      <el-form-item label="是否必检" prop="isRequired">
        <el-select v-model="queryParams.isRequired" placeholder="全部" clearable style="width: 120px">
          <el-option label="是" value="Y" />
          <el-option label="否" value="N" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
          <el-option label="启用" value="0" />
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
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['zw:inspectionpoint:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['zw:inspectionpoint:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 巡检点列表 -->
    <el-table v-loading="loading" :data="pointList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="巡检点ID" align="center" prop="pointId" width="100" />
      <el-table-column label="巡检点名称" align="center" prop="pointName" :show-overflow-tooltip="true" min-width="180" />
      <el-table-column label="所属资产" align="center" prop="assetId" width="100" />
      <el-table-column label="检查内容" align="center" prop="checkContent" :show-overflow-tooltip="true" min-width="160" />
      <el-table-column label="检查方法" align="center" prop="checkMethod" :show-overflow-tooltip="true" min-width="140" />
      <el-table-column label="检查标准" align="center" prop="checkStandard" :show-overflow-tooltip="true" min-width="160" />
      <el-table-column label="是否必检" align="center" prop="isRequired" width="90">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isRequired === 'Y' ? 'danger' : 'info'" size="small">
            {{ scope.row.isRequired === 'Y' ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sortOrder" width="70" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            active-text="启用"
            inactive-text="停用"
            @change="handleToggle(scope.row)"
            v-hasPermi="['zw:inspectionpoint:edit']"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zw:inspectionpoint:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zw:inspectionpoint:remove']"
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
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="巡检点名称" prop="pointName">
              <el-input v-model="form.pointName" placeholder="请输入巡检点名称" maxlength="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属资产ID" prop="assetId">
              <el-input-number v-model="form.assetId" :min="1" placeholder="请输入资产ID" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="是否必检" prop="isRequired">
              <el-radio-group v-model="form.isRequired">
                <el-radio label="Y">是</el-radio>
                <el-radio label="N">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" :max="999" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="检查内容" prop="checkContent">
              <el-input v-model="form.checkContent" type="textarea" placeholder="请输入检查内容" maxlength="500" show-word-limit />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="检查方法" prop="checkMethod">
              <el-input v-model="form.checkMethod" type="textarea" placeholder="请输入检查方法" maxlength="500" show-word-limit />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="检查标准" prop="checkStandard">
              <el-input v-model="form.checkStandard" type="textarea" placeholder="请输入检查标准" maxlength="500" show-word-limit />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">启用</el-radio>
                <el-radio label="1">停用</el-radio>
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
  </div>
</template>

<script>
import { listInspectionPoint, getInspectionPoint, addInspectionPoint, updateInspectionPoint, delInspectionPoint, toggleInspectionPoint } from "@/api/zw/inspectionPoint"

export default {
  name: "ZwInspectionPoint",
  data() {
    return {
      loading: true,
      ids: [],
      multiple: true,
      showSearch: true,
      total: 0,
      pointList: [],
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        pointName: undefined,
        assetId: undefined,
        isRequired: undefined,
        status: undefined
      },
      form: {},
      rules: {
        pointName: [
          { required: true, message: "巡检点名称不能为空", trigger: "blur" }
        ],
        assetId: [
          { required: true, message: "所属资产ID不能为空", trigger: "blur" }
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
      listInspectionPoint(this.queryParams).then(response => {
        this.pointList = response.rows
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
        pointId: undefined,
        pointName: undefined,
        assetId: undefined,
        checkContent: undefined,
        checkMethod: undefined,
        checkStandard: undefined,
        isRequired: 'Y',
        sortOrder: 0,
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
      this.queryParams.assetId = undefined
      this.queryParams.isRequired = undefined
      this.queryParams.status = undefined
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.pointId)
      this.multiple = selection.length === 0
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加巡检点"
    },
    handleUpdate(row) {
      this.reset()
      const pointId = row.pointId || this.ids[0]
      getInspectionPoint(pointId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改巡检点"
      })
    },
    handleToggle(row) {
      toggleInspectionPoint(row.pointId).then(() => {
        this.$modal.msgSuccess("状态修改成功")
      }).catch(() => {
        // revert
        row.status = row.status === '0' ? '1' : '0'
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.pointId != undefined) {
            updateInspectionPoint(this.form).then(() => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addInspectionPoint(this.form).then(() => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const pointIds = row.pointId || this.ids
      this.$modal.confirm('是否确认删除选中的巡检点数据项？').then(() => {
        return delInspectionPoint(pointIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    }
  }
}
</script>
