import json
import pandas as pd
from typing import Dict, List, Any
from scipy.stats import pearsonr
import pickle


class Handler:
    def __init__(self, model_path):
        self.df = None
        self.model_path = model_path
        self.model = None  # Это будет Pipeline
        if model_path:
            self.load_model()

    def load_model(self):
        try:
            with open(self.model_path, 'rb') as f:
                self.model = pickle.load(f)
                print(f"Модель Pipeline загружена из {self.model_path}")
        except Exception as e:
            print(f"Ошибка загрузки модели: {e}")
            raise

    def save_model(self, path=None):
        """Сохраняет модель (Pipeline)"""
        save_path = path or self.model_path
        if not save_path:
            raise ValueError("Не указан путь для сохранения модели")

        if self.model is None:
            raise ValueError("Нет модели для сохранения")

        with open(save_path, 'wb') as f:
            pickle.dump(self.model, f)
        print(f"Модель сохранена в {save_path}")

    def detect_request_type(self, json_data: dict) -> str:
        """Определяет тип запроса по содержимому JSON"""
        action = json_data.get('action', '')

        if action == 'FULL_ANALYSIS':
            return 'analysis'
        elif action == 'PREDICT':
            return 'predict'
        elif action == 'TRAIN':
            return 'train'
        elif 'data' in json_data:
            return 'analysis'
        elif 'platform' in json_data and 'planned_costs' in json_data:
            return 'predict'
        else:
            raise ValueError(f"Не удалось определить тип запроса. Действие: {action}")

    # МЕТОДЫ ДЛЯ АНАЛИЗА
    def load_analysis_data(self, json_data: dict) -> pd.DataFrame:
        """Загружает и парсит JSON данные"""
        if isinstance(json_data, str):
            json_data = json.loads(json_data)

        self.df = pd.DataFrame(json_data['data'])

        # Преобразуем даты
        self.df['month'] = pd.to_datetime(self.df['month'])
        self.df = self.df.sort_values('month')

        self.metadata = {
            'request_id': json_data.get('request_id'),
            'user_id': json_data.get('user_id'),
            'action': json_data.get('action'),
            'platforms': self.df['platform'].unique().tolist(),
            'n_records': len(self.df)
        }

        print(f"✓ Загружено {self.metadata['n_records']} записей")
        print(f"✓ Платформы: {', '.join(self.metadata['platforms'])}")

        return self.df

    def calculate_correlation(self, data: pd.DataFrame) -> float:
        """Рассчитывает корреляцию Пирсона для переданных данных"""
        if len(data) < 2:
            return None

        corr, _ = pearsonr(data['costs'], data['sales'])
        return round(corr, 4)

    def prepare_scatter_data(self, data: pd.DataFrame) -> List[List[float]]:
        """Данные для диаграммы рассеяния (затраты → продажи)"""
        data = data.sort_values(['costs'])
        return [
            data['costs'].tolist(),
            data['sales'].tolist()
        ]

    def prepare_time_series_costs(self, data: pd.DataFrame) -> List[List]:
        """Временной ряд для затрат"""
        data = data.sort_values(['month'])
        return [
            data['month'].dt.strftime('%Y-%m-%d').tolist(),
            data['costs'].tolist()
        ]

    def prepare_time_series_sales(self, data: pd.DataFrame) -> List[List]:
        """Временной ряд для продаж"""
        data = data.sort_values(['month'])
        return [
            data['month'].dt.strftime('%Y-%m-%d').tolist(),
            data['sales'].tolist()
        ]

    def analyze_platform(self, platform_name: str) -> Dict[str, Any]:
        """Анализирует данные для конкретной платформы"""
        platform_data = self.df[self.df['platform'] == platform_name]

        return {
            "platform": platform_name,
            "correlation_coefficient": self.calculate_correlation(platform_data),
            "visualization_data": {
                "scatter_plot": self.prepare_scatter_data(platform_data),
                "time_series_costs": self.prepare_time_series_costs(platform_data),
                "time_series_sales": self.prepare_time_series_sales(platform_data)
            }
        }

    def analyze_all_data(self) -> Dict[str, Any]:
        """Анализирует все данные без привязки к платформе"""
        return {
            "scope": "all_platforms",
            "correlation_coefficient": self.calculate_correlation(self.df),
            "visualization_data": {
                "scatter_plot": self.prepare_scatter_data(self.df),
                "time_series_costs": self.prepare_time_series_costs(self.df),
                "time_series_sales": self.prepare_time_series_sales(self.df)
            }
        }

    def generate_analysis_response(self, json_input: dict) -> dict:
        """Генерирует полный ответ"""
        self.load_analysis_data(json_input)

        platforms_analysis = []
        for platform in self.metadata['platforms']:
            platforms_analysis.append(self.analyze_platform(platform))

        all_data_analysis = self.analyze_all_data()

        response = {
            "request_id": self.metadata['request_id'],
            "user_id": self.metadata['user_id'],
            "action": "ANALYSIS",
            "status": "SUCCESS",
            "data": {
                "platforms_analysis": platforms_analysis,
                "all_data_analysis": all_data_analysis
            }
        }
        return response

    # МЕТОДЫ ДЛЯ ПРЕДСКАЗАНИЯ
    def load_and_predict(self, new_data: List[Dict]) -> List[float]:
        """
        Делает предсказание используя Pipeline
        """
        if self.model is None:
            raise ValueError("Модель не загружена")

        # Преобразуем данные в DataFrame
        df_new = pd.DataFrame(new_data)

        # Pipeline сам обработает one-hot encoding и scaling
        predictions = self.model.predict(df_new)
        return predictions

    def process_predict_request(self, json_data: dict) -> dict:
        """Обрабатывает запрос на предсказание"""
        if self.model is None:
            raise ValueError("Модель не загружена. Невозможно сделать предсказание.")

        # Подготавливаем данные для предсказания
        predict_data = [{
            'platform': json_data['platform'],
            'costs': json_data['planned_costs']
        }]

        # Делаем предсказание
        predictions = self.load_and_predict(predict_data)
        predicted_sales = round(float(predictions[0]), 2)

        response = {
            "request_id": json_data.get('request_id'),
            "action": "PREDICTION",
            "status": "SUCCESS",
            "predicted_sales": predicted_sales,
            "platform": json_data['platform'],
            "planned_costs": json_data['planned_costs'],
        }
        return response

    # МЕТОДЫ ДЛЯ ДООБУЧЕНИЯ
    def train_model(self, train_data: pd.DataFrame, target: pd.Series):
        # Дообучает модель SGDRegressor используя partial_fit
        # Извлекаем регрессор из пайплайна
        regressor = self.model.named_steps['regressor']

        # Получаем предобработанные признаки через пайплайн
        # Для partial_fit нужно применять предобработку вручную
        preprocessor = self.model.named_steps['preprocessor']

        # Предобрабатываем данные (one-hot encoding + scaling)
        X_processed = preprocessor.transform(train_data)

        # Получаем значения целевой переменной
        y_values = target.values

        # Используем partial_fit для дообучения
        regressor.partial_fit(X_processed, y_values)
        print(f"Модель дообучена на {len(train_data)} примерах")

        # Сохраняем обновленную модель
        if self.model_path:
            self.save_model()

    def process_train_request(self, json_data: dict):
        """
        Обрабатывает запрос на дообучение модели
        """
        # Загружаем данные из запроса
        train_df = pd.DataFrame(json_data['data'])

        # Преобразуем даты (на всякий случай, хотя они не используются для обучения)
        if 'month' in train_df.columns:
            train_df['month'] = pd.to_datetime(train_df['month'])

        # Извлекаем признаки (platform, costs) и целевую переменную (sales)
        features_df = train_df[['platform', 'costs']]
        target = train_df['sales']

        # Дообучаем модель
        self.train_model(features_df, target)
        return 0

    def process_request(self, json_input):
        """Основной метод обработки запросов"""
        if isinstance(json_input, str):
            json_input = json.loads(json_input)

        try:
            request_type = self.detect_request_type(json_input)

            if request_type == 'analysis':
                response = self.generate_analysis_response(json_input)
            elif request_type == 'predict':
                response = self.process_predict_request(json_input)
            elif request_type == 'train':
                response = self.process_train_request(json_input)
            else:
                raise ValueError(f"Неподдерживаемый тип запроса: {request_type}")
        except Exception as e:
            print(e)
            response = {
                "request_id": json_input.get('request_id') if isinstance(json_input, dict) else None,
                "user_id": json_input.get('user_id') if isinstance(json_input, dict) else None,
                "action": "ERROR",
                "status": "ERROR"
            }
        return response