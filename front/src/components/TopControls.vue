<script>
export default {
  name: "TopControls",
  props: {
    platforms: Array,
    selectedPlatform: String,
  },
  emits: ['update:selectedPlatform']
}
</script>

<template>
<div class="top-controls">
  <div class="class-selector">
    <button
        v-for="cls in platforms"
        :key="cls"
        :class="['class-btn', { active: selectedPlatform === cls}]"
        @click="$emit('update:selectedPlatform', cls)"
    >
      {{ cls }}
    </button>
  </div>

  <div class="year-selector"></div>

</div>
</template>

<style scoped lang="scss">
.controls-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.class-selector {
  display: flex;
  gap: 10px;
}

.class-btn {
  padding: 10px 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  transition: all 0.2s;

  &.active {
    background: #e6efff;
    border-color: #6b9fff;
    color: #2b6bf3;
  }
}


.year-selector select {
  padding: 10px 20px;
  border-radius: 8px;
  border: 1px solid #ccc;
}
</style>