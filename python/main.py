from handler_json import Handler
from handler_rabbit import RabbitMQHandler
import traceback


if __name__ == "__main__":
    # Путь к модели
    MODEL_PATH = "model/sales_model_pickle.pkl"

    handler = Handler(model_path=MODEL_PATH)

    # Создаем RabbitMQ обработчик
    rabbit_handler = RabbitMQHandler(handler)

    try:
        # Подключаемся
        rabbit_handler.connect()

        # Запускаем прослушивание
        rabbit_handler.start_consuming()

    except Exception as e:
        traceback.print_exc()
    finally:
        rabbit_handler.close()