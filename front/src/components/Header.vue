<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// Реактивная переменная, которая хранит наличие токена
const isAuthenticated = ref(false);

// Проверяем токен при загрузке компонента
onMounted(() => {
  isAuthenticated.value = !!localStorage.getItem('token');
});
console.log(isAuthenticated)
// Функция для выхода из системы
const handleLogout = () => {
  localStorage.removeItem('token');
  isAuthenticated.value = false;
  router.push('/login');
};
</script>

<template>
  <header class="header">
    <div class="container">
      <div class="header__content">
        <div class="header__logo">
          <img src="@/assets/svg/logo_ystu.svg" alt="Лого"/>
          <span class="header__logo-text">Рейтинг</span>
        </div>

        <nav class="header__nav-links">
          <router-link to="/parameters" class="header__nav-link">Ввод параметров</router-link>
          <router-link to="/analysis" class="header__nav-link">Анализ и визуализация</router-link>
        </nav>

        <div class="header__auth">
          <button
              v-if="isAuthenticated"
              @click="handleLogout"
              class="header__logout-btn"
          >
            Выйти
          </button>

          <router-link
              v-else
              to="/login"
              class="header__nav-link"
          >
            Войти
          </router-link>
        </div>
      </div>
    </div>
  </header>
</template>

<style lang="scss" scoped>
@use "@/assets/constants" as *;
.header {
  background-color: $blue;
  height: 100px;
  border-radius: 0 0 20px 20px ;
  &__content{
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 100%;
  }

  &__logo{
    display: flex;
    align-items: center;

    &-text{
      margin-left: 30px;
      font-size: 24px;
      color: $white;
    }
  }

  &__nav-links{
    display: flex;
    gap: 40px;
  }

  &__nav-link{
    font-family: 'Inter', sans-serif;
    text-decoration: none;
    cursor: pointer;
    font-size: 20px;
    color: $white;
    font-weight: 500;
    &:hover {
      opacity: 0.8;
      transition: 0.2s;
    }
    &.router-link-active{
      color: $orange;
    }
  }

  &__auth{
    font-size: 24px;
    color: $white;
    display: flex;
    align-items: center;
  }

  // Добавили стили для новой кнопки выхода, чтобы она выглядела аккуратно
  &__logout-btn {
    font-family: 'Inter', sans-serif;
    background: none;
    border: none;
    cursor: pointer;
    font-size: 20px;
    color: $white;
    font-weight: 500;
    padding: 0;

    &:hover {
      color: $orange; /* Или opacity: 0.8 в зависимости от твоего дизайна */
      transition: 0.2s;
    }
  }
}
</style>