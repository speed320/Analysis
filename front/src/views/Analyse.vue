
<script setup>
import { ref, computed, onBeforeUnmount } from 'vue';
import { Chart, registerables } from 'chart.js';
import { ScatterChart, LineChart } from 'vue-chart-3';

Chart.register(...registerables);

// Состояния интерфейса
const rawData = ref(null);
const isLoading = ref(false);
const loadingMessage = ref('');
const errorMessage = ref('');

// Базовые URL для запросов аналитики
const ANALYZE_URL = '/api/marketing/analyze';
const RESULTS_URL = '/api/results/';

let pollingInterval = null;

// Метод для получения токена из localStorage (как в InputParameters)
const getAuthHeader = () => {
  const token = localStorage.getItem('token');
  return token ? `Bearer ${token}` : '';
};

// Функция запуска долгого анализа
const startAnalysis = async () => {
  // Инициализируем состояние загрузки
  isLoading.value = true;
  rawData.value = null;
  errorMessage.value = '';
  loadingMessage.value = 'Инициализация анализа...';

  try {
    const tokenHeader = getAuthHeader();
    if (!tokenHeader) {
      throw new Error('Токен авторизации отсутствует. Пожалуйста, войдите в систему.');
    }

    // 1. POST запрос на запуск процесса анализа
    const response = await fetch(ANALYZE_URL, {
      method: 'POST',
      headers: {
        'accept': '*/*',
        'Authorization': tokenHeader,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({}) // Пустое тело, аналогично ключу -d '' в curl
    });

    if (!response.ok) {
      throw new Error(`Ошибка запуска анализа: Сервер вернул статус ${response.status}`);
    }

    const data = await response.json();
    const requestId = data.requestId;

    if (!requestId) {
      throw new Error('Сервер не передал уникальный requestId для отслеживания задачи.');
    }

    // 2. Если requestId успешно получен, переходим к циклическому опросу (Polling)
    loadingMessage.value = 'Расчет метрик на сервере...';
    startPolling(requestId);

  } catch (error) {
    isLoading.value = false;
    errorMessage.value = error.message;
  }
};

// Функция циклического опроса статуса готовности (Long Polling)
const startPolling = (requestId) => {
  if (pollingInterval) clearInterval(pollingInterval);

  pollingInterval = setInterval(async () => {
    try {
      const response = await fetch(`${RESULTS_URL}${requestId}`, {
        method: 'GET',
        headers: {
          'accept': '*/*',
          'Authorization': getAuthHeader()
        }
      });

      if (!response.ok) {
        throw new Error(`Ошибка чтения результатов: Статус ${response.status}`);
      }

      const data = await response.json();

      // Ждем строго статус COMPLETED
      if (data.status === 'COMPLETED') {
        clearInterval(pollingInterval);
        rawData.value = data; // Записываем реактивный объект данных
        isLoading.value = false;
      } else {
        // Меняем сообщение на статус, который отдал сервер (например, PENDING или PROCESSING)
        loadingMessage.value = `Статус обработки: ${data.status || 'В очереди'}...`;
      }

    } catch (error) {
      clearInterval(pollingInterval);
      isLoading.value = false;
      errorMessage.value = `Сбой обработки задачи: ${error.message}`;
    }
  }, 2000); // Интервал опроса сервера — каждые 2 секунды
};

// Предотвращение утечки памяти при переключении роутов Vue
onBeforeUnmount(() => {
  if (pollingInterval) clearInterval(pollingInterval);
});

// Безопасное вычисление корневой ветки данных
const analysis = computed(() => rawData.value?.resultData?.all_data_analysis);

// Форматирование данных под Диаграмму рассеяния
const scatterChartData = computed(() => {
  if (!analysis.value) return null;
  const points = analysis.value.visualization_data.scatter_plot.map(point => ({
    x: point[0],
    y: point[1]
  }));
  return {
    datasets: [{
      label: 'Затраты / Объемы (на одного)',
      data: points,
      backgroundColor: '#f27b00', // Оранжевый бренд-цвет как на кнопках
    }]
  };
});

const scatterOptions = {
  responsive: true,
  scales: {
    x: { title: { display: true, text: 'Объемы из БД (на одного)' } },
    y: { title: { display: true, text: 'Затраты из БД' } }
  }
};

// Форматирование данных под График затрат
const costsChartData = computed(() => {
  if (!analysis.value) return null;
  const [labels, data] = analysis.value.visualization_data.time_series_costs;
  return {
    labels: labels,
    datasets: [{
      label: 'Затраты',
      data: data,
      borderColor: '#3b82f6',
      backgroundColor: 'rgba(59, 130, 246, 0.1)',
      fill: true,
      tension: 0.2
    }]
  };
});

// Форматирование данных под График объемов
const salesChartData = computed(() => {
  if (!analysis.value) return null;
  const [labels, data] = analysis.value.visualization_data.time_series_sales;
  return {
    labels: labels,
    datasets: [{
      label: 'Объем',
      data: data,
      borderColor: '#10b981',
      backgroundColor: 'rgba(16, 185, 129, 0.1)',
      fill: true,
      tension: 0.2
    }]
  };
});
</script>

<template>
  <div class="container">
    <div class="analysis-page">

      <div class="top-bar">
        <h2>Маркетинговый анализ данных</h2>
        <button
            @click="startAnalysis"
            :disabled="isLoading"
            class="btn-submit"
        >
          {{ isLoading ? 'Анализ...' : 'Провести анализ' }}
        </button>
      </div>

      <div v-if="isLoading" class="loading-status">
        <div class="spinner"></div>
        <p>{{ loadingMessage }}</p>
      </div>

      <div v-if="errorMessage" class="error-status">
        <strong>Внимание:</strong> {{ errorMessage }}
      </div>

      <div v-if="rawData" class="results-layout">
        <div class="correlation-badge">
          Коэффициент корреляции: <strong>{{ analysis.correlation_coefficient }}</strong>
        </div>

        <div class="charts-grid">
          <div class="chart-card large">
            <h3>Диаграмма рассеяния</h3>
            <p class="subtitle">Тут от даты ничего не зависит</p>
            <ScatterChart :chartData="scatterChartData" :options="scatterOptions" />
          </div>

          <div class="chart-card">
            <h3>График затрат</h3>
            <LineChart :chartData="costsChartData" />
          </div>

          <div class="chart-card">
            <h3>График объемов</h3>
            <LineChart :chartData="salesChartData" />
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped lang="scss">
@use "@/assets/constants" as *;

.analysis-page {
  padding: 20px;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;

  h2 {
    color: #1f2937;
    font-weight: 600;
  }
}

.btn-submit {
  background: #f27b00;
  color: white;
  border: none;
  padding: 12px 40px;
  border-radius: 20px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: transform 0.1s, background 0.2s;

  &:hover:not(:disabled) {
    background: #e67500;
  }

  &:active:not(:disabled) {
    transform: scale(0.98);
  }

  &:disabled {
    background: #d9d9d9;
    cursor: not-allowed;
    opacity: 0.6;
  }
}

.loading-status {
  text-align: center;
  padding: 50px;
  color: #4b5563;

  .spinner {
    border: 4px solid #f3f4f6;
    border-top: 4px solid #f27b00;
    border-radius: 50%;
    width: 45px;
    height: 45px;
    animation: spin 1s linear infinite;
    margin: 0 auto 20px;
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-status {
  background-color: #fef2f2;
  color: #dc2626;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #fee2e2;
  margin-bottom: 25px;
}

.correlation-badge {
  background: white;
  border: 2px solid #e5e7eb;
  padding: 12px 25px;
  border-radius: 15px;
  display: inline-block;
  margin-bottom: 25px;
  font-size: 16px;
  color: #374151;

  strong {
    color: #f27b00;
    font-size: 18px;
  }
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 25px;

  @media (max-width: 992px) {
    grid-template-columns: 1fr;
  }
}

.chart-card {
  border-radius: 15px;
  background: white;
  border: 2px solid #f0f0f0;
  padding: 30px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.02);

  h3 {
    margin-top: 0;
    margin-bottom: 5px;
    color: #1f2937;
  }

  .subtitle {
    font-size: 13px;
    color: #8c8c8c;
    margin-bottom: 20px;
  }

  &.large {
    grid-column: span 2;
    @media (max-width: 992px) {
      grid-column: span 1;
    }
  }
}
</style>
