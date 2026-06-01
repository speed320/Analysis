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
      item.changed = true;
    };

    const addRow = () => {
      const newMonth = new Date().toISOString().slice(0, 7) + '-01';
      props.analytics.addStats(props.selectedPlatform, null, newMonth, "0.00", "0.00", true);
    };

    const deleteRow = (item) => {
      props.analytics.delStats(props.selectedPlatform, item);
    };

    // Отмечаем как измененное при любом вводе с клавиатуры
    const onInput = (item) => {
      item.changed = true;
    };

    const formatDecimal = (item, field) => {
      if (item[field] === null || item[field] === '' || item[field] === undefined) return;

      // Заменяем запятую на точку для парсинга
      let value = String(item[field]).replace(',', '.');

      // Убираем все символы, кроме цифр и точки
      value = value.replace(/[^0-9.]/g, '');

      const numValue = Number(value);
      if (!Number.isNaN(numValue) && value.trim() !== '') {
        item[field] = numValue.toFixed(2);
      } else {
        item[field] = "0.00"; // Фолбэк, если ввели полную абракадабру
      }
      item.changed = true;
    };

    // Локальная валидация для подсветки конкретных строк с ошибками
    const getRowErrors = (item) => {
      const errors = [];
      const rows = allRows.value;

      // 1. Проверка на дубликат месяца
      const duplicateCount = rows.filter(r => r.month === item.month).length;
      if (duplicateCount > 1) {
        errors.push("Такой месяц уже добавлен");
      }

      // 2. Валидация чисел
      const cost = Number(String(item.costs).replace(',', '.'));
      const sales = Number(String(item.sales).replace(',', '.'));

      if (isNaN(cost) || cost < 0 || String(item.costs).trim() === '') {
        errors.push("Некорректное значение расходов");
      }
      if (isNaN(sales) || sales < 0 || String(item.sales).trim() === '') {
        errors.push("Некорректный объем продаж");
      }

      return errors;
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
      onInput,
      getRowErrors,
    };
  }
}
</script>

<template>
  <div class="stats-editor">
    <div class="table-header">
      <div class="col">Дата</div>
      <div class="col">Расходы</div>
      <div class="col">Объем продаж</div>
      <div class="col-actions"></div>
    </div>

    <div v-for="(item, index) in allRows"
         :key="item.id ? `db-${item.id}` : `local-${index}-${item.month}`"
         class="row-wrapper">

      <div class="row"
           :class="{
             'row-changed': item.changed,
             'row-error': getRowErrors(item).length > 0
           }">

        <div class="capsule-container month-picker">
          <select :value="getMonthValue(item.month)" @change="updateDate(item, 'month', $event.target.value)">
            <option v-for="(m, i) in monthsRu" :key="i" :value="i">{{ m }}</option>
          </select>
          <select :value="getYearValue(item.month)" @change="updateDate(item, 'year', $event.target.value)">
            <option v-for="y in years" :key="y" :value="y">{{ y }}</option>
          </select>
        </div>

        <div class="capsule-container input-wrapper">
          <input
              type="text"
              inputmode="decimal"
              v-model="item.costs"
              @input="onInput(item)"
              @blur="formatDecimal(item, 'costs')">
          <span class="currency">₽</span>
        </div>

        <div class="capsule-container input-wrapper">
          <input
              type="text"
              inputmode="decimal"
              v-model="item.sales"
              @input="onInput(item)"
              @blur="formatDecimal(item, 'sales')">
          <span class="currency">₽</span>
        </div>

        <button class="btn-delete" @click="deleteRow(item)" title="Удалить строку">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2M10 11v6M14 11v6"/></svg>
        </button>
      </div>

      <div v-if="getRowErrors(item).length > 0" class="error-messages">
        <span v-for="(err, i) in getRowErrors(item)" :key="i">{{ err }}</span>
      </div>
    </div>

    <div class="row add-row-line">
      <button class="btn-plus-capsule" @click="addRow" title="Добавить месяц">
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
    grid-template-columns: 180px 240px 240px 40px;
    gap: 20px;
    align-items: center;
  }

  .table-header {
    margin-bottom: 15px;
  }

  .table-header .col {
    font-size: 20px;
    color: #333;
    font-weight: 400;
    padding-left: 5px;
  }

  .row-wrapper {
    margin-bottom: 15px;
  }

  .row-changed {
    .capsule-container {
      background-color: #fff8e6;
      border-color: #ffc069;
    }
  }

  /* Стили для строки с ошибкой */
  .row-error {
    .capsule-container {
      background-color: #fff1f0;
      border-color: #ff4d4f;
    }
  }

  .error-messages {
    display: flex;
    flex-direction: column;
    gap: 2px;
    margin-top: 4px;
    padding-left: 10px;
    font-size: 13px;
    color: #ff4d4f;

    span::before {
      content: '• ';
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

  .btn-plus-capsule {
    grid-column: 1;
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