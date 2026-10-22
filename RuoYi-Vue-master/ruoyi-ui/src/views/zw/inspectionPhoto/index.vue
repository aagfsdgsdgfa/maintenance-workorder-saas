<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="任务ID" prop="taskId">
        <el-input v-model="queryParams.taskId" placeholder="请输入任务ID" clearable style="width:140px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="工单ID" prop="workOrderId">
        <el-input v-model="queryParams.workOrderId" placeholder="请输入工单ID" clearable style="width:140px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="照片类型" prop="photoType">
        <el-select v-model="queryParams.photoType" placeholder="全部" clearable style="width:110px">
          <el-option label="现场" value="1" />
          <el-option label="设备" value="2" />
          <el-option label="故障" value="3" />
          <el-option label="签收" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['zw:inspectionPhoto:add']">新增照片</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="photoList" style="width: 100%">
      <el-table-column label="照片ID" align="center" prop="photoId" />
      <el-table-column label="关联工单" align="center" prop="orderNo" width="140">
        <template slot-scope="scope">
          <el-link v-if="scope.row.orderNo" type="primary" :underline="false" @click="$router.push('/zw/workOrder/detail?workOrderId=' + scope.row.workOrderId)">{{ scope.row.orderNo }}</el-link>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="照片类型" align="center" prop="photoType">
        <template slot-scope="scope"><el-tag>{{ photoTypeText(scope.row.photoType) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="照片" align="center" prop="photoUrl">
        <template slot-scope="scope">
          <el-image v-if="scope.row.photoUrl" :src="getPhotoUrl(scope.row.photoUrl)" style="width:40px;height:40px" fit="cover" :preview-src-list="[getPhotoUrl(scope.row.photoUrl)]"/>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="位置" align="center">
        <template slot-scope="scope">{{ scope.row.latitude }}, {{ scope.row.longitude }}</template>
      </el-table-column>
      <el-table-column label="拍摄时间" align="center" prop="shootTime">
        <template slot-scope="scope"><span>{{ parseTime(scope.row.shootTime) }}</span></template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['zw:inspectionPhoto:query']">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['zw:inspectionPhoto:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="任务ID" prop="taskId">
              <el-input-number v-model="form.taskId" :min="1" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工单ID">
              <el-input-number v-model="form.workOrderId" :min="1" style="width:100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="照片类型" prop="photoType">
              <el-select v-model="form.photoType" style="width:100%">
                <el-option label="现场" value="1" />
                <el-option label="设备" value="2" />
                <el-option label="故障" value="3" />
                <el-option label="签收" value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="拍摄时间">
              <el-date-picker v-model="form.shootTime" type="datetime" placeholder="选择时间" value-format="yyyy-MM-dd HH:mm:ss" style="width:100%"/>
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
        <el-form-item label="照片" prop="photoUrl">
          <image-upload v-model="form.photoUrl" :limit="1" :isShowTip="false"/>
        </el-form-item>
        <el-form-item label="水印信息">
          <el-input v-model="form.watermarkInfo" placeholder="请输入水印信息"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="照片详情" :visible.sync="detailOpen" width="600px" append-to-body>
      <el-descriptions :column="2" border v-if="detailForm.photoId">
        <el-descriptions-item label="照片ID">{{ detailForm.photoId }}</el-descriptions-item>
        <el-descriptions-item label="任务ID">{{ detailForm.taskId }}</el-descriptions-item>
        <el-descriptions-item label="关联工单">
          <el-link v-if="detailForm.orderNo" type="primary" :underline="false" @click="$router.push('/zw/workOrder/detail?id=' + detailForm.workOrderId)">{{ detailForm.orderNo }}</el-link>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="照片类型">{{ photoTypeText(detailForm.photoType) }}</el-descriptions-item>
        <el-descriptions-item label="纬度">{{ detailForm.latitude }}</el-descriptions-item>
        <el-descriptions-item label="经度">{{ detailForm.longitude }}</el-descriptions-item>
        <el-descriptions-item label="拍摄时间">{{ parseTime(detailForm.shootTime) }}</el-descriptions-item>
        <el-descriptions-item label="水印信息">{{ detailForm.watermarkInfo }}</el-descriptions-item>
        <el-descriptions-item label="照片" :span="2">
          <el-image v-if="detailForm.photoUrl" :src="getPhotoUrl(detailForm.photoUrl)" style="width:80px;height:80px" fit="cover" :preview-src-list="[getPhotoUrl(detailForm.photoUrl)]"/>
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer"><el-button @click="detailOpen = false">关 闭</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { listInspectionPhoto, getInspectionPhoto, addInspectionPhoto, delInspectionPhoto } from "@/api/zw/inspectionPhoto"
import ImageUpload from "@/components/ImageUpload"

export default {
  name: "ZwInspectionPhoto",
  components: { ImageUpload },
  data() {
    return {
      loading: true, total: 0, photoList: [], open: false, detailOpen: false, title: "", showSearch: true,
      queryParams: { pageNum: 1, pageSize: 10, taskId: undefined, workOrderId: undefined, photoType: undefined },
      form: {}, detailForm: {},
      rules: { taskId: [{ required: true, message: "任务ID不能为空", trigger: "blur" }], photoType: [{ required: true, message: "请选择照片类型", trigger: "change" }], photoUrl: [{ required: true, message: "照片URL不能为空", trigger: "blur" }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listInspectionPhoto(this.queryParams).then(res => { this.photoList = res.rows; this.total = res.total; this.loading = false }) },
    cancel() { this.open = false; this.reset() },
    reset() { this.form = { taskId: undefined, workOrderId: undefined, photoType: undefined, photoUrl: undefined, latitude: undefined, longitude: undefined, shootTime: undefined, watermarkInfo: undefined }; this.resetForm("form") },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery() },
    handleAdd() { this.reset(); this.open = true; this.title = "新增照片" },
    handleView(row) { getInspectionPhoto(row.photoId).then(res => { this.detailForm = res.data; this.detailOpen = true }) },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) { addInspectionPhoto(this.form).then(() => { this.$modal.msgSuccess("新增成功"); this.open = false; this.getList() }) }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除照片ID为"' + row.photoId + '"的记录？').then(() => delInspectionPhoto(row.photoId)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功") }).catch(() => {})
    },
    photoTypeText(v) { return { '1': '现场', '2': '设备', '3': '故障', '4': '签收' }[v] || '-' },
    getPhotoUrl(url) {
      if (!url) return ''
      if (url.startsWith('http://') || url.startsWith('https://')) return url
      return window.location.origin + url
    }
  }
}
</script>

<style scoped>
.el-table {
  width: 100%;
}
</style>
