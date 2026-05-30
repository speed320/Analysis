<script setup>

</script>

<template>
  <footer class="footer">
    <div class="container">
      <div class="footer__content">
        <div class="footer__title">
          <img src="@/assets/svg/logo_ystu_full.svg" alt="Лого"/>
        </div>

        <div class="footer__copyright">
          <p class="footer__copyright-text">
            © 1944-2026, ФГБОУ ВО «ЯГТУ»
          </p>
        </div>

        <nav class="footer__links">
          <ul class="footer__link-list">
            <li class="footer__link-item">
              <a href="index.html" class="footer__link-item-link">IT ПОДДЕРЖКА</a>
            </li>
          </ul>
        </nav>


      </div>
    </div>
  </footer>
</template>

<style lang="scss" scoped>
@use "@/assets/constants" as *;
.footer{
    margin-top: auto;
    margin-bottom: 20px;

    &__content{
      background-color: $blue;
      border-radius: 30px;

      padding: 30px 50px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    &__copyright{
      align-self: flex-end;
      color: $white;
      opacity: 0.8;
    }

    &__link {
      &-list {
        display: flex;
        flex-direction: column;
        gap: 16px;
      }

      &-item {
        &-link {
          color: $white;
          font-family: 'Involve', sans-serif;
          font-size: 16px;
          font-weight: 500;

        }
      }
    }
  }
</style>