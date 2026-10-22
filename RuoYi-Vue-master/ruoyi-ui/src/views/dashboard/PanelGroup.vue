<template>
  <el-row :gutter="24" class="panel-group">
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel card-panel--blue" @click="$router.push('/zw/asset')">
        <div class="card-panel-icon-wrap">
          <div class="icon-circle icon-bg-blue"><svg-icon icon-class="peoples" class-name="card-panel-icon" /></div>
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">总资产数</div>
          <count-to :start-val="0" :end-val="summary.assetTotal || 0" :duration="1800" class="card-panel-num" />
          <div class="card-panel-sub">本月新增 {{ summary.monthAssetNew || 0 }}</div>
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel card-panel--green" @click="$router.push('/zw/inspection')">
        <div class="card-panel-icon-wrap">
          <div class="icon-circle icon-bg-green"><svg-icon icon-class="message" class-name="card-panel-icon" /></div>
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">本月巡检</div>
          <count-to :start-val="0" :end-val="summary.monthInspectionCount || 0" :duration="2000" class="card-panel-num" />
          <div class="card-panel-sub">完成率 {{ summary.inspectionCompleteRate || 0 }}%</div>
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel card-panel--purple" @click="$router.push('/zw/workOrder')">
        <div class="card-panel-icon-wrap">
          <div class="icon-circle icon-bg-purple"><svg-icon icon-class="money" class-name="card-panel-icon" /></div>
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">工单完成率</div>
          <count-to :start-val="0" :end-val="summary.workOrderCompleteRate || 0" :duration="2200" class="card-panel-num" suffix="%" />
          <div class="card-panel-sub">本月工单 {{ summary.monthWorkOrderCount || 0 }}</div>
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel card-panel--red" @click="$router.push('/zw/faultRecord')">
        <div class="card-panel-icon-wrap">
          <div class="icon-circle icon-bg-red"><svg-icon icon-class="shopping" class-name="card-panel-icon" /></div>
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">故障率</div>
          <count-to :start-val="0" :end-val="summary.faultRate || 0" :duration="2400" class="card-panel-num" suffix="%" />
          <div class="card-panel-sub">故障资产 {{ summary.assetFault || 0 }}</div>
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script>
import CountTo from 'vue-count-to'
import { getDashboardSummary } from '@/api/zw/dashboard'

export default {
  name: 'PanelGroup',
  components: { CountTo },
  data() {
    return {
      summary: {}
    }
  },
  mounted() {
    this.loadSummary()
  },
  methods: {
    loadSummary() {
      getDashboardSummary().then(res => {
        this.summary = res.data || {}
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.panel-group {
  margin-top: 18px;
  .card-panel-col { margin-bottom: 24px; }
}

.card-panel {
  height: 108px;
  cursor: pointer;
  font-size: 12px;
  position: relative;
  overflow: hidden;
  color: #fff;
  background: linear-gradient(135deg, #1B3A6B 0%, #2155A4 100%);
  box-shadow: 0 4px 20px rgba(27, 58, 107, 0.2);
  border-radius: 12px;
  transition: transform 0.25s ease, box-shadow 0.25s ease;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 30px rgba(27, 58, 107, 0.3);
  }

  &--blue   { background: linear-gradient(135deg, #1B3A6B, #3B82F6); }
  &--green  { background: linear-gradient(135deg, #0EA5E9, #3B82F6); }
  &--purple { background: linear-gradient(135deg, #7C3AED, #A78BFA); }
  &--red    { background: linear-gradient(135deg, #EF4444, #F97316); }
}

.icon-circle {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.icon-bg-blue   { background: rgba(255,255,255,0.15); }
.icon-bg-green  { background: rgba(255,255,255,0.15); }
.icon-bg-purple { background: rgba(255,255,255,0.15); }
.icon-bg-red    { background: rgba(255,255,255,0.15); }

.card-panel-icon-wrap {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
}
.card-panel-icon {
  font-size: 28px;
  color: rgba(255,255,255,0.5);
  display: block;
}

.card-panel-description {
  position: absolute;
  left: 20px;
  top: 50%;
  transform: translateY(-50%);
  .card-panel-text {
    font-size: 13px;
    color: rgba(255,255,255,0.75);
    margin-bottom: 6px;
    letter-spacing: 0.5px;
  }
  .card-panel-num {
    font-size: 26px;
    font-weight: 700;
    color: #fff;
    line-height: 1.2;
    font-family: "DIN Alternate", "Helvetica Neue", Arial, sans-serif;
  }
  .card-panel-sub {
    font-size: 11px;
    color: rgba(255,255,255,0.6);
    margin-top: 4px;
  }
}

@media (max-width: 768px) {
  .card-panel-description { left: 16px; }
  .card-panel-num { font-size: 22px; }
}
</style>
