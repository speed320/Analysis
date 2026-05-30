<script>
export default {
  name: 'Login',
  data() {
    return {
      login: '',
      password: '',
      errorMessage: '',
      successMessage: '',
    }
  },
  methods: {
    async handleLogin() {
      // Валидация
      if (!this.login || !this.password) {
        this.errorMessage = 'Пожалуйста, заполните все поля'
        return
      }

      // Здесь вы можете добавить логику отправки данных на сервер
      // Например, через fetch или axios
      try {
        const res = await fetch(`/api/auth/login`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            username: this.login,
            password: this.password,
          }),
        })
        console.log('Попытка входа с:', {
          login: this.login,
          password: this.password,
        })

        if (res.ok) {
          const text = await res.text()
          const data = text ? JSON.parse(text) : null
          if (data) {
            localStorage.setItem('token', data.token)
          }
          this.successMessage = 'Вход выполнен успешно!'
          this.errorMessage = ''

          setTimeout(() => {
            this.$router.push('/parameters')
          }, 1500)

        } else{
          this.errorMessage = 'Неверный логин или пароль'
        }

      } catch (error) {
        // 3. Handle network/connection errors (e.g., server is offline)
        console.error('Network error or request failed:', error);
      }
      // Перенаправление на страницу параметров после успешного входа

    }
  }
}
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <h1>Вход</h1>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="login">Email:</label>
          <input
            id="login"
            v-model="login"
            type="text"
            placeholder="Введите ваш логин"
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

        <button type="submit" class="btn-submit">Войти</button>
      </form>

      <p class="login-link">
        Нет аккаунта?
        <router-link to="/register">Зарегистрируйтесь здесь</router-link>
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

<style scoped lang="scss">
@use "@/assets/constants" as *;
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 400px);
}

.login-card {
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

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.login-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s;
}

.login-link a:hover {
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
