<script>
import InfoCard from "@/components/InfoCard.vue";

export default {
  components: {
    InfoCard
  },
  data() {
    return {
      dataImage: 'src/assets/svg/data-accounting-icon.svg',
      analyzeImage: 'src/assets/svg/analyze-icon.svg',
      PredictionImage: 'src/assets/svg/prediction-icon.svg',
      visualisationImage: 'src/assets/svg/visualization-icon.svg',
    }
  },
  methods:{
    handleStart(){
      const token = localStorage.getItem('token');
      if (token) this.$router.push('/parameters');
      else this.$router.push('/login');
    }
  }
}
</script>

<template>
  <div class="container">
    <span class="landing__title">Платформа маркетингового анализа и прогнозирования продаж</span>
    <span class="landing__subtitle">Автоматический анализ зависимости рекламных затрат и обьемов продаж</span>
    <div class="landing-grid">
      <InfoCard
          :image="dataImage"
          title="Учёт данных"
          text_content="Удобный ввод и надежное хранение рекламных затрат (VK, ТВ, Газеты, Промо) и данных продаж"
      />
      <InfoCard
          :image="analyzeImage"
          title="Анализ"
          text_content="Автоматический расчет коэффициента корреляции и построение регрессионных моделей прогнозирования"
      />
      <InfoCard
          :image="PredictionImage"
          title="Предсказание"
          text_content="Показывает предполагаемый объем продаж по заданным затратам на рекламу"
      />
      <InfoCard
          :image="visualisationImage"
          title="Визуализация"
          text_content="Представление результатов анализа в виде диаграмм рассеяния, доходов и расходов."
      />
    </div>

      <a @click.prevent="handleStart" class="landing__start-btn">Начать работу</a>

  </div>


</template>

<style scoped lang="scss">
@use "@/assets/constants" as *;

.landing{
  &__title{
    display: block;
    font-size: 48px;
    font-weight: 600;
    text-align: center;
    margin-bottom: 20px;
  }
  &__subtitle{
    display: block;
    font-size: 26px;
    text-align: center;
    color: $graphite;
  }

  &__start-btn{
    width: fit-content;
    display: block;
    padding: 16px 74px;
    background: $blue;
    border-radius: 15px;
    color: white;
    margin: 0 auto;
    box-shadow: 0px 2px 6px rgba(0,0,0,0.25);

  }
}
.landing-grid {
  display: grid;
  grid-template-columns: repeat(2, max-content);
  justify-content: center;
  column-gap: 130px;
  width: 100%;
  margin-bottom: 60px;
}

</style>