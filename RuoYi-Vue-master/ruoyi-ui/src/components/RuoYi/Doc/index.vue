<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'RuoYiDoc',
  data() {
    return {
      url: 'javascript:;'
    }
  },
  methods: {
    goto() {
      this.$message.info('文档功能开发中')
    }
  }
}
</script>