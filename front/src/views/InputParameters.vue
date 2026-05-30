<script>
import { computed, reactive, ref, watch, onMounted } from 'vue'
import TopControls from '@/components/TopControls.vue'
import defaultData from '@/dafaultPayloadData.json'
import ParametersForm from "@/components/ParametersForm.vue";
import PredictionForm from "@/components/PredictionForm.vue";

export default {
  name: "InputParameters",
  components:{
    PredictionForm,
    ParametersForm,
    TopControls,
  },
  setup() {
    class PlatformStats {
      constructor(id, month, costs, sales, isNew = false) {
        this.id = id || null;
        this.changed = isNew;
        this.month = month;
        this.costs = costs != null ? Number(costs).toFixed(2) : "0.00";
        this.sales = sales != null ? Number(sales).toFixed(2) : "0.00";
      }
    }

    class PlatformsAnalytics {
      constructor() {
        this.data = {
          VK: [],
          TV: [],
          NEWSPAPER: [],
          PROMOTIONAL: [],
        };
      }

      clearData() {
        Object.keys(this.data).forEach(platform => {
          this.data[platform] = [];
        });
      }
      addStats(platform, id, month, costs, sales, isNew = false) {
        if (this.data[platform]) {
          this.data[platform].push(
              new PlatformStats(id, month, costs, sales, isNew)
          );
        }
      }

      async delStats(platform, targetItem) {
        if (!this.data[platform]) return;

        if (targetItem.id) {
          try {
            const response = await fetch(`/api/marketing/all/data/${targetItem.id}`, {
              method: 'DELETE',
              headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
                'Content-Type': 'application/json'
              }
            });

            if (!response.ok) {
              throw new Error(`Ошибка сервера: ${response.status}`);
            }
            console.log(`Запись ${targetItem.id} удалена с сервера`);
          } catch (error) {
            console.error('Не удалось удалить запись на сервере:', error);
            alert('Ошибка при удалении строки с сервера.');
            return;
          }
        }

        this.data[platform] = this.data[platform].filter(item => item !== targetItem);
      }

      getPlatforms() {
        return Object.keys(this.data);
      }

      loadJSON(json) {
        if (!json || !json.data) return;
        json.data.forEach(item => {
          this.addStats(
              item.platform,
              item.id || null,
              item.month,
              item.costs,
              item.sales,
              false
          );
        });
      }
    }

    const platformIdMap = reactive({});
    const platformNameMap = reactive({});
    const analytics = reactive(new PlatformsAnalytics());

    const fetchPlatforms = async () => {
      try {
        const platfornResponse = await fetch('/api/marketing/platforms');
        if (!platfornResponse.ok) {
          throw new Error(`Ошибка загрузки платформ: ${platfornResponse.status}`);
        }
        const platformsList = await platfornResponse.json();

        Object.keys(platformIdMap).forEach(key => delete platformIdMap[key]);
        platformsList.forEach(item => {
          platformIdMap[item.platformName] = item.id;
          platformNameMap[item.id] = item.platformName;
        });

        const dataResponse = await fetch('/api/marketing/all/data', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
          }
        });
        if (!dataResponse.ok) {
          throw new Error(`Ошибка данных: ${dataResponse.status}`);
        }
        const savedData = await dataResponse.json();
        const dataArray = Array.isArray(savedData) ? savedData : (savedData.data || []);

        analytics.clearData();

        dataArray.forEach(item => {
          analytics.addStats(
              item.platformName,
              item.id,
              item.reportingMonth,
              item.costs,
              item.salesVolume,
              false
          );
        });

      } catch (error) {
        console.error('Не удалось загрузить данные с сервера, откат к локальному JSON:', error);
        const fallback = { VK: 1, TV: 2, NEWSPAPER: 3, PROMOTIONAL: 4 };
        Object.assign(platformIdMap, fallback);
        Object.assign(platformNameMap, { 1: "VK", 2: "TV", 3: "NEWSPAPER", 4: "PROMOTIONAL" });
        analytics.clearData();
        analytics.loadJSON(exampleData);
      }
    };

    onMounted(() => {
      fetchPlatforms();
    });

    watch(
        analytics,
        (newValue) => {
          console.log('analytics changed:', newValue)
        },
        { deep: true }
    );

    const platforms = analytics.getPlatforms();
    const selectedPlatform = ref(platforms[0]);

    const totalChangedCount = computed(() => {
      let count = 0;
      platforms.forEach(platform => {
        const platformData = analytics.data[platform] || [];
        count += platformData.filter(item => item.changed).length;
      });
      return count;
    });

    // ОБНОВЛЕННЫЙ МЕТОД СОХРАНЕНИЯ
    const saveAllChanges = async () => {
      if (totalChangedCount.value === 0) {
        console.log('Нет изменённых строк для сохранения');
        return;
      }

      const payload = [];
      const serverRowsToDelete = [];

      // 1. Собираем информацию об измененных строках
      platforms.forEach(platform => {
        const platformData = analytics.data[platform] || [];
        const changedRows = platformData.filter(item => item.changed);

        changedRows.forEach(item => {
          // Если у строки есть ID, отправляем её в список на удаление
          if (item.id) {
            serverRowsToDelete.push(item.id);
          }

          // В любом случае подготавливаем новые данные для POST-запроса
          payload.push({
            platformId: platformIdMap[platform] || 0,
            reportingMonth: item.month,
            costs: Number(item.costs) || 0,
            salesVolume: Number(item.sales) || 0
          });
        });
      });

      try {
        const token = localStorage.getItem('token');

        // 2. СНАЧАЛА УДАЛЯЕМ СТАРЫЕ ЗАПИСИ С СЕРВЕРА
        if (serverRowsToDelete.length > 0) {
          console.log(`Удаление устаревших строк с сервера перед перезаписью. Количество: ${serverRowsToDelete.length}`);

          // Выполняем параллельное удаление всех старых версий строк
          const deletePromises = serverRowsToDelete.map(id =>
              fetch(`/api/marketing/all/data/${id}`, {
                method: 'DELETE',
                headers: {
                  'Authorization': `Bearer ${token}`,
                  'Content-Type': 'application/json'
                }
              }).then(res => {
                if (!res.ok) throw new Error(`Не удалось удалить строку с ID ${id}`);
              })
          );

          await Promise.all(deletePromises);
          console.log('Все перезаписываемые строки успешно удалены с сервера.');
        }

        // 3. ДОБАВЛЯЕМ ОБНОВЛЕННЫЕ СТРОКИ КАК НОВЫЕ
        const response = await fetch('/api/marketing/data', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify(payload),
        });

        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }

        console.log('Новые модифицированные данные успешно сохранены на сервере');
        alert('Данные успешно сохранены!');

        // Перезапрашиваем актуальное состояние с сервера (новые ID и сброс changed флагов)
        await fetchPlatforms();

      } catch (error) {
        console.error('Ошибка в процессе перезаписи данных:', error);
        alert('Не удалось корректно обновить данные на сервере. Пожалуйста, обновите страницу.');
      }
    };

    return {
      analytics,
      platforms,
      selectedPlatform,
      totalChangedCount,
      saveAllChanges,
      platformIdMap,
    }
  }
}
</script>

<template>
  <div class="container">
    <div class="input-parameters">
      <div class="input-parameters__header">
        <span>Платформы:</span>
        <TopControls
            :platforms="platforms"
            v-model:selectedPlatform="selectedPlatform"
        />
      </div>

      <div class="main-card">
        <div class="parameters-grid">
          <ParametersForm
              :analytics="analytics"
              :selectedPlatform="selectedPlatform"
          />
          <div class="footer">
            <span v-if="totalChangedCount > 0" class="changed-count">
              Изменено: {{ totalChangedCount }}
            </span>
            <button class="btn-submit" @click="saveAllChanges" :disabled="totalChangedCount === 0">
              {{ totalChangedCount > 0 ? 'Сохранить' : 'Нет изменений' }}
            </button>
          </div>
        </div>
        <div class="parameters-grid">
          <PredictionForm
              :analytics="analytics"
              :selectedPlatform="selectedPlatform"
          />
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped lang="scss">
@use "@/assets/constants" as *;
.main-card{
  display: flex;
  //flex-direction: column;
  gap: 20px;

}
.parameters-grid{
  border-radius: 15px;
  background: white;
  border: 2px solid $light-gray;
  padding: 40px;
}
.input-parameters{
  &__header{
    display: flex;
    gap: 20px;
    font-size: 28px;
    align-items: center;
    margin-bottom: 20px;
  }
}
.footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 40px;
  padding-right: 40px;
  gap: 20px;
}

.changed-count {
  font-size: 14px;
  color: #ffa940;
  font-weight: 500;
}

.btn-submit {
  background: #f27b00;
  color: white;
  border: none;
  padding: 12px 60px;
  border-radius: 20px;
  font-size: 18px;
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

</style>