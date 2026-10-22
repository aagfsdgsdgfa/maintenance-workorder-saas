<template>
  <div class="app-container">
    <el-button icon="el-icon-arrow-left" size="mini" @click="goBack">返回</el-button>

    <el-card class="mt15" shadow="never">
      <div slot="header">
        <span class="card-title">合同概要</span>
        <el-tag :type="statusTag(detail.status)" size="small" style="float:right">{{ statusName(detail.status) }}</el-tag>
      </div>
      <el-descriptions :column="3" border size="small">
        <el-descriptions-item label="合同编号">{{ detail.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="合同名称">{{ detail.contractName }}</el-descriptions-item>
        <el-descriptions-item label="所属客户">{{ customerName }}</el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ parseTime(detail.startDate) }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ parseTime(detail.endDate) }}</el-descriptions-item>
        <el-descriptions-item label="SLA响应">{{ detail.slaResponseHours }} 小时</el-descriptions-item>
        <el-descriptions-item label="SLA到场">{{ detail.slaArrivalHours }} 小时</el-descriptions-item>
        <el-descriptions-item label="含巡检次数">{{ detail.includedInspections }} 次</el-descriptions-item>
        <el-descriptions-item label="含维修次数">{{ detail.includedRepairs }} 次</el-descriptions-item>
        <el-descriptions-item label="免费配件额度">¥{{ detail.freePartsQuota }}</el-descriptions-item>
        <el-descriptions-item label="已用配件金额">¥{{ detail.usedPartsAmount }}</el-descriptions-item>
        <el-descriptions-item label="折扣率">{{ detail.discountRate }}%</el-descriptions-item>
        <el-descriptions-item label="已用巡检">{{ detail.usedInspections }} 次</el-descriptions-item>
        <el-descriptions-item label="已用维修">{{ detail.usedRepairs }} 次</el-descriptions-item>
        <el-descriptions-item label="附件" :span="2">
          <a v-if="detail.attachmentUrl" :href="detail.attachmentUrl" target="_blank">{{ detail.attachmentUrl }}</a>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(detail.createTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script>
import { getContract } from "@/api/zw/contract"
import { companyTreeSelect } from "@/api/system/user"
import { parseTime } from "@/utils/ruoyi"

export default {
  name: "ContractDetail",
  data() {
    return {
      loading: false,
      contractId: null,
      detail: {},
      customerOptions: []
    }
  },
  computed: {
    customerName() {
      const c = this.customerOptions.find(x => x.deptId === this.detail.customerId)
      return c ? c.deptName : this.detail.customerId || '-'
    }
  },
  created() {
    this.contractId = this.$route.params.contractId
    companyTreeSelect().then(res => { this.customerOptions = res.data || [] })
    if (this.contractId) this.getDetail()
  },
  methods: {
    parseTime,
    goBack() { this.$router.go(-1) },
    getDetail() {
      this.loading = true
      getContract(this.contractId).then(res => {
        this.detail = res.data || {}
        this.loading = false
      })
    },
    statusName(status) {
      return { '0': '生效中', '1': '已到期', '2': '已终止' }[status] || '-'
    },
    statusTag(status) {
      return { '0': 'success', '1': 'warning', '2': 'info' }[status] || ''
    }
  }
}
</script>

<style scoped>
.mt15 { margin-top: 15px; }
.card-title { font-weight: 600; font-size: 15px; }
</style>
