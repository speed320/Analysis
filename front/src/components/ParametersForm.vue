<script>
import { ref, computed } from "vue";

export default {
  props: {
    analytics: Object,
    selectedPlatform: String,
  },
  setup(props) {
    const monthsRu = [
      "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
      "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    ];

    const currentYear = new Date().getFullYear();
    const years = Array.from({ length: (currentYear + 2) - 2020 + 1 }, (_, i) => 2020 + i);

    // Вычисляемое свойство для получения данных текущей платформы
    const allRows = computed(() => {
      return props.analytics.data[props.selectedPlatform] || [];
    });

    const getMonthValue = (dateStr) => new Date(dateStr).getMonth();
    const getYearValue = (dateStr) => new Date(dateStr).getFullYear();

    const updateDate = (item, type, value) => {
      const date = new Date(item.month);
      if (type === 'month') date.setMonth(parseInt(value));
      if (type === 'year') date.setFullYear(parseInt(value));
      const y = date.getFullYear();
      const m = String(date.getMonth() + 1).padStart(2, '0');
      item.month = `${y}-${m}-01`;
      item.changed = true; // Явно устанавливаем флаг (хотя Proxy должен это сделать)
    };

    const addRow = () => {
      const newMonth = new Date().toISOString().slice(0, 7) + '-01';
      // Последний аргумент true взводит флаг changed = true при создании
      props.analytics.addStats(props.selectedPlatform, null, newMonth, "0.00", "0.00", true);
    };

    const deleteRow = (item) => {
      // Передаем весь объект item, чтобы фильтрация шла по ссылке на объект, а не по месяцу
      props.analytics.delStats(props.selectedPlatform, item);
    };
    const formatDecimal = (item, field) => {
      if (item[field] === null || item[field] === '' || item[field] === undefined) return;
      const value = Number(String(item[field]).replace(',', '.'));
      if (!Number.isNaN(value)) {
        item[field] = value.toFixed(2);
        item.changed = true; // Помечаем как изменённое (хотя Proxy должен это сделать)
      }
    };

    return {
      allRows,
      monthsRu,
      years,
      addRow,
      deleteRow,
      formatDecimal,
      getMonthValue,
      getYearValue,
      updateDate,
    };
  }
}
</script>

<template>
  <div class="stats-editor">

    <div class="table-header">
      <div class="col">Месяц</div>
      <div class="col">Расходы</div>
      <div class="col">Объем продаж</div>
      <div class="col-actions"></div>
    </div>

    <div v-for="(item, index) in allRows"
         :key="item.id ? `db-${item.id}` : `local-${index}-${item.month}`"
         class="row"
         :class="{ 'row-changed': item.changed }">
      <div class="capsule-container month-picker">
        <select :value="getMonthValue(item.month)" @change="updateDate(item, 'month', $event.target.value)">
          <option v-for="(m, i) in monthsRu" :key="i" :value="i">{{ m }}</option>
        </select>
        <select :value="getYearValue(item.month)" @change="updateDate(item, 'year', $event.target.value)">
          <option v-for="y in years" :key="y" :value="y">{{ y }}</option>
        </select>
      </div>

      <div class="capsule-container input-wrapper">
        <input type="text" inputmode="decimal" v-model="item.costs" @blur="formatDecimal(item, 'costs')">
        <span class="currency">₽</span>
      </div>

      <div class="capsule-container input-wrapper">
        <input type="text" inputmode="decimal" v-model="item.sales" @blur="formatDecimal(item, 'sales')">
        <span class="currency">₽</span>
      </div>

      <button class="btn-delete" @click="deleteRow(item)">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2M10 11v6M14 11v6"/></svg>
      </button>
    </div>

    <div class="row add-row-line">
      <button class="btn-plus-capsule" @click="addRow">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#909399" stroke-width="3"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
      </button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.stats-editor {
  padding: 10px;

  .table-header, .row {
    display: grid;
    // Распределение колонок: Месяц(180), Расходы(240), Продажи(240), Удалить(40)
    grid-template-columns: 180px 240px 240px 40px;
    gap: 20px;
    align-items: center;
    margin-bottom: 15px;
  }

  .table-header .col {
    font-size: 20px;
    color: #333;
    font-weight: 400;
    padding-left: 5px;
  }

  // Стиль для изменённых строк
  .row-changed {
    .capsule-container {
      background-color: #fff8e6;
      border-color: #ffc069;
    }
  }

  // Общий стиль для всех капсул
  .capsule-container {
    border: 1px solid #e0e0e0;
    border-radius: 14px;
    height: 42px;
    display: flex;
    align-items: center;
    padding: 0 15px;
    background: white;
    transition: background-color 0.2s, border-color 0.2s;
  }

  .month-picker select {
    border: none;
    outline: none;
    background: transparent;
    font-size: 15px;
    flex: 1;
    cursor: pointer;
    &:first-child { text-align: left; }
    &:last-child { text-align: right; }
  }

  .input-wrapper {
    input {
      border: none;
      outline: none;
      width: 100%;
      font-size: 15px;
      background: transparent;
    }
    .currency {
      margin-left: 8px;
      color: #333;
    }
  }

  // Кнопка "+" в виде капсулы под месяцем
  .btn-plus-capsule {
    grid-column: 1; // Только в первой колонке
    border: 1px solid #e0e0e0;
    border-radius: 14px;
    height: 42px;
    background: white;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    transition: background 0.2s;
    &:hover { background: #f9f9f9; }
  }

  .btn-delete {
    border: none;
    background: none;
    color: #333;
    cursor: pointer;
    opacity: 0.6;
    transition: opacity 0.2s;
    &:hover { opacity: 1; color: #ff4d4f; }
  }
}
</style>
