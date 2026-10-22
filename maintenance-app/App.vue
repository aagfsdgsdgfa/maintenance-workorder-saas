<script>
export default {
  onLaunch: function() {},
  onShow: function() {},
  onHide: function() {}
}
</script>

<style>
/* 全局公共样式 */
@import './static/common.css';
/* uni-icons 图标字体 */
@import '@dcloudio/uni-ui/lib/uni-icons/uniicons.css';

/* ================================================
   全局安全区适配（iPhone X+ 及 Android 刘海屏）
   ================================================ */

/* 页面底部预留 Home Indicator 空间 */
page {
  padding-bottom: env(safe-area-inset-bottom);
}

/* 顶部导航栏预留状态栏空间（navigationStyle=custom 页面使用） */
.uni-navbar--border,
.uni-navigation-bar {
  padding-top: var(--status-bar-height, 0);
}

/* ================================================
   全局基础重置
   ================================================ */
page {
  background-color: #f8fafc;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC',
    'Helvetica Neue', Helvetica, 'Microsoft YaHei', sans-serif;
  font-size: 28rpx;
  color: #1f2937;
  line-height: 1.5;
}

view, text, button, input, image, scroll-view, swiper, swiper-item {
  box-sizing: border-box;
}

button {
  margin: 0;
  padding: 0;
  background: transparent;
  border: none;
  line-height: normal;
}

button::after {
  border: none;
}
</style>