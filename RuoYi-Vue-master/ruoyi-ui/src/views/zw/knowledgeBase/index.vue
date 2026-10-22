<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="标题" prop="kbTitle">
        <el-input v-model="queryParams.kbTitle" placeholder="请输入标题" clearable style="width:150px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="知识分类" prop="kbType">
        <el-select v-model="queryParams.categoryId" placeholder="全部" clearable style="width:150px">
          <el-option v-for="item in categoryOptions" :key="item.categoryId" :label="item.categoryName" :value="item.categoryId" />
        </el-select>
      </el-form-item>
      <el-form-item label="品牌" prop="brand">
        <el-input v-model="queryParams.brand" placeholder="请输入品牌" clearable style="width:120px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="关键词" prop="keywords">
        <el-input v-model="queryParams.keywords" placeholder="请输入关键词" clearable style="width:130px" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['zw:knowledgeBase:add']">新增知识</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-collection" size="mini" @click="openCategoryManager">分类管理</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="kbList">
      <el-table-column label="知识ID" align="center" prop="kbId" width="80" />
      <el-table-column label="标题" align="center" prop="kbTitle" :show-overflow-tooltip="true" min-width="160" />
      <el-table-column label="分类" align="center" prop="categoryName" width="100">
        <template slot-scope="scope"><el-tag>{{ scope.row.categoryName || '未分类' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="品牌" align="center" prop="brand" width="100" />
      <el-table-column label="资产类型" align="center" prop="assetType" width="90">
        <template slot-scope="scope"><el-tag type="info">{{ assetTypeText(scope.row.assetType) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="关键词" align="center" prop="keywords" :show-overflow-tooltip="true" min-width="140" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope"><el-switch v-model="scope.row.status" active-value="0" inactive-value="1" @change="handleStatusChange(scope.row)" v-hasPermi="['zw:knowledgeBase:edit']"/></template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['zw:knowledgeBase:query']">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)" v-hasPermi="['zw:knowledgeBase:edit']">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['zw:knowledgeBase:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="650px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="标题" prop="kbTitle">
              <el-input v-model="form.kbTitle" placeholder="请输入标题"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="知识分类" prop="kbType">
              <el-select v-model="form.categoryId" style="width:100%">
                <el-option v-for="item in categoryOptions" :key="item.categoryId" :label="item.categoryName" :value="item.categoryId" />
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
            <el-form-item label="资产类型">
              <el-select v-model="form.assetType" style="width:100%">
                <el-option label="设备" value="device" />
                <el-option label="软件" value="software" />
                <el-option label="耗材" value="consumable" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="关键词">
          <el-input v-model="form.keywords" placeholder="请输入关键词，逗号分隔"/>
        </el-form-item>
        <el-form-item label="知识内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入知识内容"/>
        </el-form-item>
        <el-form-item label="附件">
          <el-input v-model="form.attachments" type="textarea" :rows="2" placeholder="JSON数组格式，如：[{'name':'xxx.pdf','url':'http://...'}]"/>
        </el-form-item>
        <el-form-item label="状态">
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

    <el-dialog title="知识详情" :visible.sync="detailOpen" width="650px" append-to-body>
      <el-descriptions :column="2" border v-if="detailForm.kbId">
        <el-descriptions-item label="知识ID">{{ detailForm.kbId }}</el-descriptions-item>
        <el-descriptions-item label="知识分类">{{ detailForm.categoryName || '未分类' }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ detailForm.kbTitle }}</el-descriptions-item>
        <el-descriptions-item label="品牌">{{ detailForm.brand }}</el-descriptions-item>
        <el-descriptions-item label="资产类型">{{ assetTypeText(detailForm.assetType) }}</el-descriptions-item>
        <el-descriptions-item label="关键词">{{ detailForm.keywords }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detailForm.status === '0' ? '正常' : '停用' }}</el-descriptions-item>
        <el-descriptions-item label="知识内容" :span="2"><div style="white-space:pre-wrap">{{ detailForm.content }}</div></el-descriptions-item>
        <el-descriptions-item label="附件" :span="2">{{ detailForm.attachments }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer"><el-button @click="detailOpen = false">关 闭</el-button></div>
    </el-dialog>

    <el-dialog title="分类管理" :visible.sync="categoryManagerOpen" width="650px" append-to-body>
      <!-- 列表模式 -->
      <div v-if="categoryFormMode === 'list'">
        <el-row style="margin-bottom:10px">
          <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleCategoryAdd">新增分类</el-button>
        </el-row>
        <el-table :data="categoryList" border size="small">
          <el-table-column prop="categoryName" label="分类名称" width="120"/>
          <el-table-column prop="categoryCode" label="分类编码" width="120"/>
          <el-table-column prop="sort" label="排序" width="80"/>
          <el-table-column prop="status" label="状态" width="80">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === '0' ? 'success' : 'info'">
                {{ scope.row.status === '0' ? '正常' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" show-overflow-tooltip/>
          <el-table-column label="操作" width="150">
            <template slot-scope="scope">
              <el-button size="mini" type="text" icon="el-icon-edit" @click="handleCategoryEdit(scope.row)">编辑</el-button>
              <el-button size="mini" type="text" icon="el-icon-delete" @click="handleCategoryDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- 表单模式 -->
      <el-form v-else ref="categoryForm" :model="categoryForm" :rules="categoryFormRules" label-width="100px" style="margin-top:10px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="categoryForm.categoryName" placeholder="请输入分类名称" maxlength="50"/>
        </el-form-item>
        <el-form-item label="分类编码" prop="categoryCode">
          <el-input v-model="categoryForm.categoryCode" placeholder="请输入分类编码" maxlength="20"/>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="categoryForm.sort" :min="0" controls-position="right"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="categoryForm.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="categoryForm.remark" type="textarea" :rows="2" maxlength="500" placeholder="请输入备注"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitCategoryForm">保存</el-button>
          <el-button @click="cancelCategoryForm">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { listKnowledgeBase, getKnowledgeBase, addKnowledgeBase, updateKnowledgeBase, delKnowledgeBase } from "@/api/zw/knowledgeBase"
import { listAllKbCategory, addKbCategory, updateKbCategory, delKbCategory } from "@/api/zw/kbCategory"

export default {
  name: "ZwKnowledgeBase",
  data() {
    return {
      loading: true, total: 0, kbList: [], open: false, detailOpen: false, title: "", showSearch: true,
      categoryOptions: [],
      queryParams: { pageNum: 1, pageSize: 10, kbTitle: undefined, categoryId: undefined, brand: undefined, assetType: undefined, keywords: undefined, status: undefined },
      form: {}, detailForm: {},
      rules: { kbTitle: [{ required: true, message: "标题不能为空", trigger: "blur" }], categoryId: [{ required: true, message: "请选择知识分类", trigger: "change" }] },
      categoryManagerOpen: false,
      categoryFormMode: 'list',
      categoryList: [],
      categoryForm: { categoryId: null, categoryName: '', categoryCode: '', sort: 0, status: '0', remark: '' },
      categoryFormRules: {
        categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
        categoryCode: [{ required: true, message: '请输入分类编码', trigger: 'blur' }]
      }
    }
  },
  created() { this.getList(); this.loadCategoryOptions() },
  methods: {
    loadCategoryOptions() {
      listAllKbCategory().then(res => { this.categoryOptions = res.data || [] })
    },
    getList() { this.loading = true; listKnowledgeBase(this.queryParams).then(res => { this.kbList = res.rows; this.total = res.total; this.loading = false }) },
    cancel() { this.open = false; this.reset() },
    reset() { this.form = { kbTitle: undefined, categoryId: undefined, brand: undefined, assetType: undefined, keywords: undefined, content: undefined, attachments: undefined, status: '0' }; this.resetForm("form") },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery() },
    handleAdd() { this.reset(); this.open = true; this.title = "新增知识" },
    handleEdit(row) { getKnowledgeBase(row.kbId).then(res => { this.form = res.data; this.open = true; this.title = "编辑知识" }) },
    handleView(row) { getKnowledgeBase(row.kbId).then(res => { this.detailForm = res.data; this.detailOpen = true }) },
    handleStatusChange(row) { updateKnowledgeBase({ kbId: row.kbId, status: row.status }).then(() => this.$modal.msgSuccess("状态修改成功")) },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const action = this.form.kbId ? updateKnowledgeBase(this.form) : addKnowledgeBase(this.form)
          action.then(() => { this.$modal.msgSuccess(this.form.kbId ? "修改成功" : "新增成功"); this.open = false; this.getList() })
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除知识ID为"' + row.kbId + '"的记录？').then(() => delKnowledgeBase(row.kbId)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功") }).catch(() => {})
    },
    assetTypeText(v) { return { device: '设备', software: '软件', consumable: '耗材', other: '其他' }[v] || '-' },
    openCategoryManager() { this.categoryManagerOpen = true; this.categoryFormMode = 'list'; this.loadCategoryList() },
    loadCategoryList() { listAllKbCategory().then(res => { this.categoryList = res.data || [] }) },
    handleCategoryAdd() { this.categoryForm = { categoryId: null, categoryName: '', categoryCode: '', sort: 0, status: '0', remark: '' }; this.categoryFormMode = 'add' },
    handleCategoryEdit(row) { this.categoryForm = { ...row }; this.categoryFormMode = 'edit' },
    submitCategoryForm() {
      this.$refs.categoryForm.validate(valid => {
        if (!valid) return
        const action = this.categoryForm.categoryId ? updateKbCategory(this.categoryForm) : addKbCategory(this.categoryForm)
        action.then(() => { this.$modal.msgSuccess('保存成功'); this.categoryFormMode = 'list'; this.loadCategoryList() })
      })
    },
    handleCategoryDelete(row) {
      this.$modal.confirm('确认删除分类"' + row.categoryName + '"吗？').then(() => {
        delKbCategory(row.categoryId).then(() => { this.$modal.msgSuccess('删除成功'); this.loadCategoryList() })
      }).catch(() => {})
    },
    handleCategoryStatusChange(row) {
      const status = row.status === '0' ? '1' : '0'
      updateKbCategory({ categoryId: row.categoryId, status }).then(() => { row.status = status; this.$modal.msgSuccess('状态修改成功') })
    },
    cancelCategoryForm() { this.categoryFormMode = 'list' }
  }
}
</script>
