<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 1. Инициализируем переменную сразу (чтобы при первой загрузке состояние было верным)
const isAuthenticated = ref(!!localStorage.getItem('token'));

// Функция для обновления статуса
const updateAuthStatus = () => {
  isAuthenticated.value = !!localStorage.getItem('token');
};

// Функция, которая слушает изменения из других вкладок или компонентов
const handleStorageChange = (event) => {
  // Нас интересует только изменение ключа 'token'
  if (event.key === 'token') {
    isAuthenticated.value = !!event.newValue;
  }
};

onMounted(() => {
  // 2. Дополнительно проверяем при монтировании
  updateAuthStatus();

  // 3. Подписываемся на событие изменения localStorage в браузере
  window.addEventListener('storage', handleStorageChange);

  // Крошечный лайфхак для отслеживания изменений внутри этой же вкладки:
  // Переопределяем стандартные вызовы, чтобы шапка знала о входе мгновенно
  const originalSetItem = localStorage.setItem;
  localStorage.setItem = function (key, value) {
    originalSetItem.apply(this, arguments);
    if (key === 'token') {
      isAuthenticated.value = true;
    }
  };
});

// Не забываем очищать за собой слушатели событий
onBeforeUnmount(() => {
  window.removeEventListener('storage', handleStorageChange);
});

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
      color: $orange;
      transition: 0.2s;
    }
  }
}
</style>