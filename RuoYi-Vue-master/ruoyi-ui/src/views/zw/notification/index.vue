<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="用户ID" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable style="width:140px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="通知类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="全部" clearable style="width:130px">
          <el-option label="工单下发" value="1" />
          <el-option label="超时预警" value="2" />
          <el-option label="报修通知" value="3" />
          <el-option label="系统消息" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="已读状态" prop="isRead">
        <el-select v-model="queryParams.isRead" placeholder="全部" clearable style="width:110px">
          <el-option label="未读" value="0" />
          <el-option label="已读" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['zw:notification:add']">新增通知</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="notificationList">
      <el-table-column label="通知ID" align="center" prop="notifId" width="80" />
      <el-table-column label="用户ID" align="center" prop="userId" width="90" />
      <el-table-column label="通知类型" align="center" prop="type" width="100">
        <template slot-scope="scope"><el-tag :type="typeTag(scope.row.type)">{{ typeText(scope.row.type) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="标题" align="center" prop="title" :show-overflow-tooltip="true" min-width="160" />
      <el-table-column label="内容" align="center" prop="content" :show-overflow-tooltip="true" min-width="200" />
      <el-table-column label="关联ID" align="center" prop="relatedId" width="90" />
      <el-table-column label="状态" align="center" prop="isRead" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isRead === '1'" type="success">已读</el-tag>
          <el-tag v-else type="danger">未读</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['zw:notification:query']">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-check" @click="handleRead(scope.row)" v-if="scope.row.isRead === '0'" v-hasPermi="['zw:notification:edit']">标记已读</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['zw:notification:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="550px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户ID" prop="userId">
              <el-input-number v-model="form.userId" :min="1" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="通知类型" prop="type">
              <el-select v-model="form.type" style="width:100%">
                <el-option label="工单下发" value="1" />
                <el-option label="超时预警" value="2" />
                <el-option label="报修通知" value="3" />
                <el-option label="系统消息" value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="关联ID">
          <el-input-number v-model="form.relatedId" :min="1" style="width:100%"/>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入通知标题"/>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="请输入通知内容"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="通知详情" :visible.sync="detailOpen" width="550px" append-to-body>
      <el-descriptions :column="2" border v-if="detailForm.notifId">
        <el-descriptions-item label="通知ID">{{ detailForm.notifId }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detailForm.userId }}</el-descriptions-item>
        <el-descriptions-item label="通知类型"><el-tag :type="typeTag(detailForm.type)">{{ typeText(detailForm.type) }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="关联ID">{{ detailForm.relatedId }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detailForm.isRead === '1' ? '已读' : '未读' }}</el-descriptions-item>
        <el-descriptions-item label="阅读时间">{{ parseTime(detailForm.readTime) }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ detailForm.title }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ detailForm.content }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer"><el-button @click="detailOpen = false">关 闭</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { listNotification, getNotification, addNotification, markRead, delNotification } from "@/api/zw/notification"

export default {
  name: "ZwNotification",
  data() {
    return {
      loading: true, total: 0, notificationList: [], open: false, detailOpen: false, title: "", showSearch: true,
      queryParams: { pageNum: 1, pageSize: 10, userId: undefined, type: undefined, isRead: undefined },
      form: {}, detailForm: {},
      rules: { userId: [{ required: true, message: "用户ID不能为空", trigger: "blur" }], type: [{ required: true, message: "请选择通知类型", trigger: "change" }], title: [{ required: true, message: "标题不能为空", trigger: "blur" }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listNotification(this.queryParams).then(res => { this.notificationList = res.rows; this.total = res.total; this.loading = false }) },
    cancel() { this.open = false; this.reset() },
    reset() { this.form = { userId: undefined, type: undefined, relatedId: undefined, title: undefined, content: undefined }; this.resetForm("form") },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery() },
    handleAdd() { this.reset(); this.open = true; this.title = "新增通知" },
    handleView(row) { getNotification(row.notifId).then(res => { this.detailForm = res.data; this.detailOpen = true }) },
    handleRead(row) { markRead(row.notifId).then(() => { this.$modal.msgSuccess("标记成功"); this.getList() }) },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) { addNotification(this.form).then(() => { this.$modal.msgSuccess("新增成功"); this.open = false; this.getList() }) }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除通知ID为"' + row.notifId + '"的记录？').then(() => delNotification(row.notifId)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功") }).catch(() => {})
    },
    typeText(v) { return { '1': '工单下发', '2': '超时预警', '3': '报修通知', '4': '系统消息' }[v] || '-' },
    typeTag(v) { return { '1': 'primary', '2': 'warning', '3': 'danger', '4': 'info' }[v] || '' }
  }
}
</script>
