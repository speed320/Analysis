<template>
  <div class="register-container">
    <div class="register-card">
      <h1>Регистрация</h1>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="name">Имя:</label>
          <input
            id="name"
            v-model="name"
            type="text"
            placeholder="Введите ваше имя"
            required
          />
        </div>

        <div class="form-group">
          <label for="email">Email:</label>
          <input
            id="email"
            v-model="email"
            type="email"
            placeholder="Введите ваш email"
            required
          />
        </div>

        <div class="form-group">
          <label for="password">Пароль:</label>
          <input
            id="password"
            v-model="password"
            type="password"
            placeholder="Введите пароль"
            required
          />
        </div>

        <div class="form-group">
          <label for="confirmPassword">Подтвердите пароль:</label>
          <input
            id="confirmPassword"
            v-model="confirmPassword"
            type="password"
            placeholder="Подтвердите пароль"
            required
          />
        </div>

        <button type="submit" class="btn-submit">Зарегистрироваться</button>
      </form>

      <p class="register-link">
        Уже есть аккаунт?
        <router-link to="/login">Войдите здесь</router-link>
      </p>

      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>
      <div v-if="successMessage" class="success-message">
        {{ successMessage }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    return {
      name: '',
      email: '',
      password: '',
      confirmPassword: '',
      errorMessage: '',
      successMessage: '',
    }
  },
  methods: {
    async handleRegister() {
      // Валидация
      if (!this.name || !this.email || !this.password || !this.confirmPassword) {
        this.errorMessage = 'Пожалуйста, заполните все поля'
        return
      }

      if (this.password !== this.confirmPassword) {
        this.errorMessage = 'Пароли не совпадают'
        return
      }

      if (this.password.length < 6) {
        this.errorMessage = 'Пароль должен содержать минимум 6 символов'
        return
      }

      const API_URL = import.meta.env.VITE_API_URL;
      try{
        const res = await fetch(`/api/auth/register`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            username: this.name,
            password: this.password,
            email: this.email,
          }),
        })
        if (res.ok) {

          const text = await res.text()
          const data = text ? JSON.parse(text) : null
          if(data){
            localStorage.setItem('token', data.token)
          }

          // Пример успешной регистрации
          this.successMessage = 'Регистрация выполнена успешно! '
          this.errorMessage = ''
          // Перенаправление на страницу входа после успешной регистрации
          setTimeout(() => {
            this.$router.push('/parameters')
          }, 1500)
        } else{
          this.errorMessage = 'Something went wrong'
        }
      } catch (error){

      }



    },
  },
}
</script>

<style scoped lang="scss">
@use "@/assets/constants" as *;
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 400px);
}

.register-card {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
}

h1 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 28px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 5px rgba(102, 126, 234, 0.3);
}

.btn-submit {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, $blue 0%, $blue 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.btn-submit:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-submit:active {
  transform: translateY(0);
}

.register-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.register-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s;
}

.register-link a:hover {
  color: #764ba2;
}

.error-message {
  margin-top: 15px;
  padding: 12px;
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
  font-size: 14px;
}

.success-message {
  margin-top: 15px;
  padding: 12px;
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
  border-radius: 4px;
  font-size: 14px;
}
</style>
