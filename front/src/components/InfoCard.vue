<script>
export default {
  props: {
    image: [String, Object],
    title: String,
    text_content: String,
  },
}
</script>

<template>
  <div class="info-card">
    <div class="info-card__pin">
      <img :src="image"/>
    </div>
    <div class="info-card__content">
      <span class="info-card__title">{{title}}</span>
      <span class="info-card__text">{{text_content}}</span>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use "@/assets/constants" as *;

.info-card{
  max-width: 400px;
  width: 100%;
  flex-direction: column;
  align-items: center;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-8px);
  }
  &__pin{
    background: $white;
    border-radius: 50px;
    width: fit-content;
    padding: 10px;
    margin: 0 auto;
    transform: translateY(50%);
    box-shadow: 0px 2px 6px rgba(0,0,0,0.25);
  }
  &__content{
    display: flex;
    align-items: center;
    flex-direction: column;
    padding: 60px 20px 40px 20px;
    gap: 14px;
    background: $white;
    border-radius: 15px;
  }

  &__title{
    text-transform: uppercase;
    font-weight: 600;
    font-size: 24px;
  }
  &__text{
    font-size: 20px;
    text-align: center;
  }
}
</style>