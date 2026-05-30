<script>
import { ref, computed, onBeforeUnmount } from 'vue';

export default {
  props: {
    analytics: Object,
    selectedPlatform: String,
    // Передаем карту ID платформ из родителя, чтобы узнать platformId по имени (например, VK -> 1)
    platformIdMap: {
      type: Object,
      default: () => ({ VK: 1, TV: 2, NEWSPAPER: 3, PROMOTIONAL: 4 })
    }
  },
  setup(props) {
    // Состояние формы предсказания
    const plannedCosts = ref("10.00");
    const predictionResult = ref(null);
    const loading = ref(false);
    const loadingStatus = ref(""); // Для отображения текста статуса (PROCESSING...)

    let pollInterval = null; // Хранилище для таймера

    // Очистка таймера при уничтожении компонента, чтобы не было утечек памяти
    onBeforeUnmount(() => {
      clearInterval(pollInterval);
    });

    const formatDecimal = () => {
      if (!plannedCosts.value) return;
      const value = Number(String(plannedCosts.value).replace(',', '.'));
      if (!Number.isNaN(value)) {
        plannedCosts.value = value.toFixed(2);
      }
    };

    // Основная функция запуска предсказания
    const startPrediction = async () => {
      if (loading.value) return;

      const currentPlatformName = props.selectedPlatform;
      // Получаем ID платформы из мапы (если её нет в props, возьмет дефолтный ID)
      const platformId = props.platformIdMap[currentPlatformName] || 1;

      loading.value = true;
      loadingStatus.value = "Инициализация...";
      predictionResult.value = null;

      try {
        // Шаг 1: Создаем запрос на предсказание
        const response = await fetch('/api/marketing/predict', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          },
          body: JSON.stringify({
            platformId: Number(platformId),
            costs: Number(plannedCosts.value) || 0
          })
        });

        if (!response.ok) throw new Error(`Ошибка инициализации: ${response.status}`);

        const predictData = await response.json();
        const requestId = predictData.requestId;

        if (!requestId) throw new Error("Не получен requestId от сервера");

        // Шаг 2: Запускаем polling (опрос раз в секунду)
        loadingStatus.value = "Расчет модели...";

        pollInterval = setInterval(async () => {
          await checkPredictionStatus(requestId);
        }, 1000);

      } catch (error) {
        console.error(error);
        alert(error.message || "Произошла ошибка при отправке запроса");
        loading.value = false;
      }
    };

    // Функция проверки статуса на бэкенде
    const checkPredictionStatus = async (requestId) => {
      try {
        const response = await fetch(`/api/results/${requestId}`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
          }
        });

        if (!response.ok) throw new Error(`Ошибка опроса: ${response.status}`);

        const result = await response.json();

        if (result.status === 'COMPLETED') {
          clearInterval(pollInterval);
          predictionResult.value = result.resultData;
          loading.value = false;
        } else if (result.status === 'ERROR') {
          clearInterval(pollInterval);
          throw new Error("Бэкенд вернул статус ошибки при расчете.");
        } else {
          // Если PROCESSING — ничего не делаем, ждем следующую секунду
          loadingStatus.value = "Вычисления на сервере...";
        }

      } catch (error) {
        console.error(error);
        clearInterval(pollInterval);
        alert(error.message || "Ошибка при получении результатов расчета");
        loading.value = false;
      }
    };

    return {
      plannedCosts,
      predictionResult,
      loading,
      loadingStatus,
      formatDecimal,
      startPrediction
    };
  }
}
</script>

<template>
  <div class="prediction-container">
    <h3 class="title">Прогноз объема продаж для платформы: <span class="platform-badge">{{ selectedPlatform }}</span></h3>

    <div class="prediction-form">
      <div class="input-block">
        <label>Планируемые расходы:</label>
        <div class="capsule-container input-wrapper">
          <input
              type="text"
              inputmode="decimal"
              v-model="plannedCosts"
              @blur="formatDecimal"
              :disabled="loading"
          >
          <span class="currency">₽</span>
        </div>
      </div>

      <div class="actions-block">
        <button
            class="btn-predict"
            @click="startPrediction"
            :disabled="loading || !plannedCosts"
        >
          <span v-if="!loading">Рассчитать прогноз</span>
          <div v-else class="loader-wrapper">
            <div class="spinner"></div>
            <span class="status-text">{{ loadingStatus }}</span>
          </div>
        </button>
      </div>
    </div>

    <div v-if="predictionResult" class="result-card">
      <h4>Результат анализа модели:</h4>
      <div class="result-grid">
        <div class="result-item">
          <span class="label">Платформа:</span>
          <span class="value">{{ predictionResult.platform }}</span>
        </div>
        <div class="result-item">
          <span class="label">Бюджет расходов:</span>
          <span class="value">{{ Number(predictionResult.planned_costs).toLocaleString() }} ₽</span>
        </div>
        <div class="result-item result-item-predict highlight">
          <span class="label">Прогноз продаж:</span>
          <span class="value">{{ Number(predictionResult.predicted_sales).toLocaleString() }} ₽</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use "@/assets/constants" as *; // Используем ваши переменные, если есть

.prediction-container {
  padding: 10px;

  .title {
    font-size: 18px;
    margin-bottom: 25px;
    color: #333;

    .platform-badge {
      background: #e6f7ff;
      color: #1890ff;
      padding: 2px 10px;
      border-radius: 10px;
      font-weight: 600;
    }
  }
}

.prediction-form {
  display: flex;
  flex-direction: column;
  //align-items: flex-end;
  gap: 30px;
  margin-bottom: 30px;
}

.input-block {
  display: flex;
  flex-direction: column;
  gap: 10px;

  label {
    font-size: 14px;
    color: #666;
  }
}

.capsule-container {
  border: 1px solid #e0e0e0;
  border-radius: 14px;
  height: 42px;
  display: flex;
  align-items: center;
  padding: 0 15px;
  background: white;
  width: 240px;

  input {
    border: none;
    outline: none;
    width: 100%;
    font-size: 15px;
    background: transparent;
    &:disabled {
      color: #999;
    }
  }
  .currency {
    margin-left: 8px;
    color: #333;
  }
}

.btn-predict {
  background: #1890ff;
  color: white;
  border: none;
  height: 44px;
  padding: 0 40px;
  border-radius: 20px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;

  &:hover:not(:disabled) {
    background: #40a9ff;
  }

  &:disabled {
    background: #f5f5f5;
    border: 1px solid #d9d9d9;
    color: rgba(0, 0, 0, 0.25);
    cursor: not-allowed;
  }
}

/* Стили крутилки (CSS Spinner) */
.loader-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(242, 123, 0, 0.2);
  border-top-color: #f27b00; /* Оранжевый акцент под ваш стиль */
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.status-text {
  font-size: 14px;
  color: #666;
}

/* Карточка результатов */
.result-card {
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 15px;
  padding: 20px;
  animation: fadeIn 0.3s ease-in-out;

  h4 {
    margin-top: 0;
    margin-bottom: 15px;
    color: #389e0d;
    font-size: 16px;
  }
}

.result-grid {
  display: flex;
  row-gap: 20px;
  column-gap: 40px;
  flex-wrap: wrap;
}

.result-item {
  display: flex;
  flex-direction: column;
  gap: 5px;

  .label {
    font-size: 13px;
    color: #8c8c8c;
  }
  .value {
    font-size: 16px;
    font-weight: 500;
    color: #262626;
  }

  &.highlight {
    .value {
      color: #389e0d;
      font-weight: 700;
      font-size: 18px;
    }
  }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>