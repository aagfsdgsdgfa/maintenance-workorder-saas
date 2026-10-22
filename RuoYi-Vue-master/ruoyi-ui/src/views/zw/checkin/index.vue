<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="工单ID" prop="workOrderId">
        <el-input v-model="queryParams.workOrderId" placeholder="请输入工单ID" clearable style="width: 150px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="工程师ID" prop="engineerId">
        <el-input v-model="queryParams.engineerId" placeholder="请输入工程师ID" clearable style="width: 150px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="打卡类型" prop="checkType">
        <el-select v-model="queryParams.checkType" placeholder="全部" clearable style="width: 120px">
          <el-option label="到达打卡" value="1" />
          <el-option label="离开打卡" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['zw:checkin:add']">新增打卡</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="checkinList">
      <el-table-column label="打卡ID" align="center" prop="checkId" width="80" />
      <el-table-column label="工单编号" align="center" prop="orderNo" width="130">
        <template slot-scope="scope">
          <el-link v-if="scope.row.orderNo" type="primary" :underline="false" @click="$router.push('/zw/workOrder/detail?workOrderId=' + scope.row.workOrderId)">{{ scope.row.orderNo }}</el-link>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="工程师" align="center" prop="engineerName" width="100" />
      <el-table-column label="打卡类型" align="center" prop="checkType" width="110">
        <template slot-scope="scope">
          <el-tag :type="scope.row.checkType === '1' ? 'success' : 'primary'">
            {{ scope.row.checkType === '1' ? '到达打卡' : '离开打卡' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="位置" align="center" width="180">
        <template slot-scope="scope">
          {{ scope.row.latitude }}, {{ scope.row.longitude }}
        </template>
      </el-table-column>
      <el-table-column label="打卡地址" align="center" prop="address" :show-overflow-tooltip="true" min-width="160" />
      <el-table-column label="照片" align="center" prop="photoUrl" width="100">
        <template slot-scope="scope">
          <el-image v-if="scope.row.photoUrl" :src="scope.row.photoUrl" style="width:40px;height:40px" fit="cover" :preview-src-list="[scope.row.photoUrl]" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="打卡时间" align="center" prop="checkTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.checkTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="120" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['zw:checkin:query']">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['zw:checkin:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <!-- 新增弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="550px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="工单ID" prop="workOrderId">
              <el-input-number v-model="form.workOrderId" :min="1" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工程师ID" prop="engineerId">
              <el-input-number v-model="form.engineerId" :min="1" style="width:100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="打卡类型" prop="checkType">
              <el-select v-model="form.checkType" style="width:100%">
                <el-option label="到达打卡" value="1" />
                <el-option label="离开打卡" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="纬度">
              <el-input-number v-model="form.latitude" :precision="7" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="经度">
              <el-input-number v-model="form.longitude" :precision="7" style="width:100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="打卡地址">
          <el-input v-model="form.address" placeholder="请输入打卡地址"/>
        </el-form-item>
        <el-form-item label="照片URL">
          <el-input v-model="form.photoUrl" placeholder="请输入照片URL"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="打卡详情" :visible.sync="detailOpen" width="550px" append-to-body>
      <el-descriptions :column="2" border v-if="detailForm.checkId">
        <el-descriptions-item label="打卡ID">{{ detailForm.checkId }}</el-descriptions-item>
        <el-descriptions-item label="工单编号">
          <el-link v-if="detailForm.orderNo" type="primary" :underline="false" @click="$router.push('/zw/workOrder/detail?id=' + detailForm.workOrderId)">{{ detailForm.orderNo }}</el-link>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="工程师">{{ detailForm.engineerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="打卡类型">
          <el-tag :type="detailForm.checkType === '1' ? 'success' : 'primary'">{{ detailForm.checkType === '1' ? '到达打卡' : '离开打卡' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="纬度">{{ detailForm.latitude }}</el-descriptions-item>
        <el-descriptions-item label="经度">{{ detailForm.longitude }}</el-descriptions-item>
        <el-descriptions-item label="打卡地址" :span="2">{{ detailForm.address }}</el-descriptions-item>
        <el-descriptions-item label="照片" :span="2">
          <el-image v-if="detailForm.photoUrl" :src="detailForm.photoUrl" style="width:80px;height:80px" fit="cover" :preview-src-list="[detailForm.photoUrl]"/>
        </el-descriptions-item>
        <el-descriptions-item label="打卡时间" :span="2">{{ parseTime(detailForm.checkTime) }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer"><el-button @click="detailOpen = false">关 闭</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { listCheckin, getCheckin, addCheckin, delCheckin } from "@/api/zw/checkin"

export default {
  name: "ZwCheckIn",
  data() {
    return {
      loading: true,
      total: 0,
      checkinList: [],
      open: false,
      detailOpen: false,
      title: "",
      showSearch: true,
      queryParams: { pageNum: 1, pageSize: 10, workOrderId: undefined, engineerId: undefined, checkType: undefined },
      form: {},
      detailForm: {},
      rules: {
        workOrderId: [{ required: true, message: "工单ID不能为空", trigger: "blur" }],
        engineerId: [{ required: true, message: "工程师ID不能为空", trigger: "blur" }],
        checkType: [{ required: true, message: "请选择打卡类型", trigger: "change" }]
      }
    }
  },
  created() { this.getList() },
  methods: {
    getList() {
      this.loading = true
      listCheckin(this.queryParams).then(res => { this.checkinList = res.rows; this.total = res.total; this.loading = false })
    },
    cancel() { this.open = false; this.reset() },
    reset() { this.form = { workOrderId: undefined, engineerId: undefined, checkType: undefined, latitude: undefined, longitude: undefined, address: undefined, photoUrl: undefined }; this.resetForm("form") },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery() },
    handleAdd() { this.reset(); this.open = true; this.title = "新增打卡" },
    handleView(row) { getCheckin(row.checkId).then(res => { this.detailForm = res.data; this.detailOpen = true }) },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          addCheckin(this.form).then(() => { this.$modal.msgSuccess("新增成功"); this.open = false; this.getList() })
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除打卡ID为"' + row.checkId + '"的记录？').then(() => delCheckin(row.checkId)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功") }).catch(() => {})
    }
  }
}
</script>