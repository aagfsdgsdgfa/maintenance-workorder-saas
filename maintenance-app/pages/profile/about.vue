<template>
  <view class="about-page">
    <!-- Logo区域 -->
    <view class="logo-section">
      <image class="app-logo" src="/static/logo.png" mode="aspectFit" lazy-load></image>
      <text class="app-name">智维云服</text>
      <text class="app-version">v{{ version }}</text>
    </view>

    <!-- 功能介绍 -->
    <view class="intro-section">
      <view class="section-title">产品介绍</view>
      <text class="intro-text">
        智维云服是一款面向现场维保工程师的移动工作台，帮助工程师规范现场动作、强制业务闭环、降低一线人员的案头操作负担。
      </text>
      <text class="intro-text">
        工程师可通过小程序完成接单、签到、巡检、故障上报、完工确认、签名等全链路操作，数据实时同步至后台管理系统。
      </text>
    </view>

    <!-- 功能列表 -->
    <view class="feature-section">
      <view class="section-title">主要功能</view>
      <view class="feature-grid">
        <view class="feature-item">
          <text class="feature-icon">[</text>
          <text class="feature-name">工单管理</text>
        </view>
        <view class="feature-item">
          <text class="feature-icon">?</text>
          <text class="feature-name">巡检任务</text>
        </view>
        <view class="feature-item">
          <text class="feature-icon">@</text>
          <text class="feature-name">GPS打卡</text>
        </view>
        <view class="feature-item">
          <text class="feature-icon">!</text>
          <text class="feature-name">故障上报</text>
        </view>
        <view class="feature-item">
          <text class="feature-icon">#</text>
          <text class="feature-name">AI日报</text>
        </view>
        <view class="feature-item">
          <text class="feature-icon">K</text>
          <text class="feature-name">知识库</text>
        </view>
      </view>
    </view>

    <!-- 联系信息 -->
    <view class="contact-section">
      <view class="section-title">联系我们</view>
      <view class="contact-item">
        <text class="contact-icon">W</text>
        <text class="contact-text">官方网站: www.zhiweiyunfu.com</text>
      </view>
      <view class="contact-item">
        <text class="contact-icon">M</text>
        <text class="contact-text">邮箱: support@zhiweiyunfu.com</text>
      </view>
      <view class="contact-item">
        <text class="contact-icon">P</text>
        <text class="contact-text">客服热线: 400-xxx-xxxx</text>
      </view>
    </view>

    <!-- 底部 -->
    <view class="footer">
      <text class="copyright">© 2026 智维云服 版权所有</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      version: '1.0.0'
    }
  }
}
</script>

<style scoped>
.about-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.logo-section {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  padding: 80rpx 32rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.app-logo {
  width: 160rpx;
  height: 160rpx;
  border-radius: 32rpx;
  background: #fff;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
}

.app-name {
  font-size: 40rpx;
  font-weight: bold;
  color: #fff;
  margin-top: 24rpx;
}

.app-version {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 8rpx;
}

.intro-section,
.feature-section,
.contact-section {
  background: #fff;
  margin: 24rpx;
  border-radius: 16rpx;
  padding: 32rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #303133;
  margin-bottom: 24rpx;
  padding-bottom: 16rpx;
  border-bottom: 1rpx solid #ebeef5;
}

.intro-text {
  font-size: 28rpx;
  color: #606266;
  line-height: 1.6;
  display: block;
  margin-bottom: 16rpx;
}

.feature-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 32rpx;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.feature-icon {
  font-size: 56rpx;
}

.feature-name {
  font-size: 24rpx;
  color: #606266;
  margin-top: 12rpx;
}

.contact-item {
  display: flex;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #ebeef5;
}

.contact-item:last-child {
  border-bottom: none;
}

.contact-icon {
  font-size: 36rpx;
  margin-right: 16rpx;
}

.contact-text {
  font-size: 28rpx;
  color: #606266;
}

.footer {
  padding: 40rpx;
  text-align: center;
}

.copyright {
  font-size: 22rpx;
  color: #c0c4cc;
}
</style>
