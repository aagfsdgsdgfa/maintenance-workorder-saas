<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="工单ID" prop="workOrderId">
        <el-input v-model="queryParams.workOrderId" placeholder="请输入工单ID" clearable style="width: 140px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="客户ID" prop="customerId">
        <el-input v-model="queryParams.customerId" placeholder="请输入客户ID" clearable style="width: 140px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['zw:satisfaction:add']">新增评价</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="satisfactionList">
      <el-table-column label="评价ID" align="center" prop="evalId" width="80" />
      <el-table-column label="工单编号" align="center" prop="orderNo" width="130">
        <template slot-scope="scope">
          <el-link v-if="scope.row.orderNo" type="primary" :underline="false" @click="$router.push('/zw/workOrder/detail?workOrderId=' + scope.row.workOrderId)">{{ scope.row.orderNo }}</el-link>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="客户名称" align="center" prop="customerName" width="130" :show-overflow-tooltip="true" />
      <el-table-column label="响应评分" align="center" prop="responseScore" width="100">
        <template slot-scope="scope">
          <el-rate v-model="scope.row.responseScore" disabled text-color="#ff9900" score-template="{value}"/>
        </template>
      </el-table-column>
      <el-table-column label="技术评分" align="center" prop="techScore" width="100">
        <template slot-scope="scope">
          <el-rate v-model="scope.row.techScore" disabled text-color="#ff9900" score-template="{value}"/>
        </template>
      </el-table-column>
      <el-table-column label="综合评分" align="center" prop="overallScore" width="100">
        <template slot-scope="scope">
          <el-rate v-model="scope.row.overallScore" disabled text-color="#ff9900" score-template="{value}"/>
        </template>
      </el-table-column>
      <el-table-column label="评价内容" align="center" prop="comment" :show-overflow-tooltip="true" min-width="160" />
      <el-table-column label="评价时间" align="center" prop="evalTime" width="160">
        <template slot-scope="scope"><span>{{ parseTime(scope.row.evalTime) }}</span></template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="140" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['zw:satisfaction:query']">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['zw:satisfaction:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <!-- 新增弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="工单ID" prop="workOrderId">
              <el-input-number v-model="form.workOrderId" :min="1" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户ID" prop="customerId">
              <el-input-number v-model="form.customerId" :min="1" style="width:100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="响应速度" prop="responseScore">
          <el-rate v-model="form.responseScore" :max="5" show-text style="line-height:36px"/>
        </el-form-item>
        <el-form-item label="技术水平" prop="techScore">
          <el-rate v-model="form.techScore" :max="5" show-text style="line-height:36px"/>
        </el-form-item>
        <el-form-item label="综合评分" prop="overallScore">
          <el-rate v-model="form.overallScore" :max="5" show-text style="line-height:36px"/>
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="form.comment" type="textarea" :rows="3" placeholder="请输入评价内容"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="评价详情" :visible.sync="detailOpen" width="500px" append-to-body>
      <el-descriptions :column="2" border v-if="detailForm.evalId">
        <el-descriptions-item label="评价ID">{{ detailForm.evalId }}</el-descriptions-item>
        <el-descriptions-item label="工单编号">
          <el-link v-if="detailForm.orderNo" type="primary" :underline="false" @click="$router.push('/zw/workOrder/detail?id=' + detailForm.workOrderId)">{{ detailForm.orderNo }}</el-link>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="客户名称">{{ detailForm.customerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="评价时间">{{ parseTime(detailForm.evalTime) }}</el-descriptions-item>
        <el-descriptions-item label="响应评分"><el-rate v-model="detailForm.responseScore" disabled style="display:inline-block"/></el-descriptions-item>
        <el-descriptions-item label="技术评分"><el-rate v-model="detailForm.techScore" disabled style="display:inline-block"/></el-descriptions-item>
        <el-descriptions-item label="综合评分"><el-rate v-model="detailForm.overallScore" disabled style="display:inline-block"/></el-descriptions-item>
        <el-descriptions-item label="评价内容" :span="2">{{ detailForm.comment }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer"><el-button @click="detailOpen = false">关 闭</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { listSatisfaction, getSatisfaction, addSatisfaction, delSatisfaction } from "@/api/zw/satisfaction"

export default {
  name: "ZwSatisfaction",
  data() {
    return {
      loading: true,
      total: 0,
      satisfactionList: [],
      open: false,
      detailOpen: false,
      title: "",
      showSearch: true,
      queryParams: { pageNum: 1, pageSize: 10, workOrderId: undefined, customerId: undefined },
      form: { workOrderId: undefined, customerId: undefined, responseScore: 5, techScore: 5, overallScore: 5, comment: undefined },
      detailForm: {},
      rules: {
        workOrderId: [{ required: true, message: "工单ID不能为空", trigger: "blur" }],
        responseScore: [{ required: true, message: "请评分", trigger: "change" }],
        overallScore: [{ required: true, message: "请评分", trigger: "change" }]
      }
    }
  },
  created() { this.getList() },
  methods: {
    getList() {
      this.loading = true
      listSatisfaction(this.queryParams).then(res => { this.satisfactionList = res.rows; this.total = res.total; this.loading = false })
    },
    cancel() { this.open = false; this.reset() },
    reset() { this.form = { workOrderId: undefined, customerId: undefined, responseScore: 5, techScore: 5, overallScore: 5, comment: undefined }; this.resetForm("form") },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery() },
    handleAdd() { this.reset(); this.open = true; this.title = "新增评价" },
    handleView(row) { getSatisfaction(row.evalId).then(res => { this.detailForm = res.data; this.detailOpen = true }) },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          addSatisfaction(this.form).then(() => { this.$modal.msgSuccess("新增成功"); this.open = false; this.getList() })
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除评价ID为"' + row.evalId + '"的记录？').then(() => delSatisfaction(row.evalId)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功") }).catch(() => {})
    }
  }
}
</script>
