import pika
import json
from typing import Dict, Any
from os import getenv
import time


class RabbitMQHandler:
    def __init__(self, handler_instance):
        self.handler = handler_instance

        # Настройки подключения
        self.rabbitmq_host = getenv('RABBITMQ_HOST', 'rabbitmq')
        self.rabbitmq_port = int(getenv('RABBITMQ_PORT', 5672))
        self.rabbitmq_user = getenv('RABBITMQ_USER', 'guest')
        self.rabbitmq_password = getenv('RABBITMQ_PASS', 'guest')

        # Настройки для повторных попыток
        self.max_retries = 30  # Максимальное количество попыток
        self.retry_delay = 2  # Задержка между попытками в секундах

        # Настройки exchanges
        self.input_exchange = 'marketing.exchange'
        self.output_exchange = 'results.exchange'

        # Routing keys для входящих сообщений
        self.input_routing_keys = ['train.key', 'predict.key', 'analysis.key']

        # Маппинг входных routing keys к выходным
        self.output_key_mapping = {
            'train.key': None,
            'predict.key': 'predict.result.key',
            'analysis.key': 'analysis.result.key'
        }

        # Очереди для каждого routing key
        self.queues = {
            'train.key': 'train_queue',
            'predict.key': 'predict_queue',
            'analysis.key': 'analysis_queue'
        }

        self.connection = None
        self.channel = None

    def connect(self):
        # установка соединения с RabbitMQ с повторными попытками
        for attempt in range(1, self.max_retries + 1):
            try:
                # попытка подключения
                credentials = pika.PlainCredentials(self.rabbitmq_user, self.rabbitmq_password)
                parameters = pika.ConnectionParameters(
                    host=self.rabbitmq_host,
                    port=self.rabbitmq_port,
                    credentials=credentials,
                    heartbeat=600,
                    blocked_connection_timeout=300
                )

                self.connection = pika.BlockingConnection(parameters)
                self.channel = self.connection.channel()

                # Создаем exchanges
                self.channel.exchange_declare(
                    exchange=self.input_exchange,
                    exchange_type='direct',
                    durable=True
                )

                self.channel.exchange_declare(
                    exchange=self.output_exchange,
                    exchange_type='direct',
                    durable=True
                )

                # Создаем очереди и привязываем их
                for routing_key, queue_name in self.queues.items():
                    self.channel.queue_declare(
                        queue=queue_name,
                        durable=True,
                        exclusive=False,
                        auto_delete=False
                    )

                    self.channel.queue_bind(
                        exchange=self.input_exchange,
                        queue=queue_name,
                        routing_key=routing_key
                    )
                return True

            except pika.exceptions.AMQPConnectionError as e:
                # при неудавшейся попытке
                if attempt < self.max_retries:
                    # подождать
                    time.sleep(self.retry_delay)
                else:
                    # попытки исчерпаны
                    raise
            except Exception as e:
                raise

    def process_message(self, channel, method, properties, body):
        input_data = None
       # Обработка входящего сообщения
        try:
            # Парсим JSON
            input_data = body.decode('utf-8')
            routing_key = method.routing_key

            # передаем JSON обработчику
            result = self.handler.process_request(input_data)

            # Отправляем результат обратно
            output_routing_key = self.output_key_mapping.get(routing_key)
            if output_routing_key:
                self.send_response(result, output_routing_key)

            # Подтверждаем обработку
            channel.basic_ack(delivery_tag=method.delivery_tag)

        except Exception:
            error_response = {
                "request_id": input_data.get('request_id') if input_data else None,
                "action": "ERROR",
                "status": "ERROR"
            }
            self.send_response(error_response, 'analysis.result.key')
            channel.basic_ack(delivery_tag=method.delivery_tag)

    def send_response(self, response_data: Dict[str, Any], routing_key: str):
        # Отправка ответа в output exchange
        try:
            response_json = json.dumps(response_data, ensure_ascii=False, default=str)

            self.channel.basic_publish(
                exchange=self.output_exchange,
                routing_key=routing_key,
                body=response_json.encode('utf-8'),
                properties=pika.BasicProperties(
                    delivery_mode=2,
                    content_type='application/json'
                )
            )

        except Exception:
            pass

    def start_consuming(self):
        # Запуск прослушивания очередей
        self.channel.basic_qos(prefetch_count=1) # пока Consumer не обработает одно сообщение, следующее к нему не поступит

        for routing_key, queue_name in self.queues.items():
            self.channel.basic_consume(
                queue=queue_name,
                on_message_callback=self.process_message,
                auto_ack=False
            )
        # надо будет убрать, это для прерывания с клавы
        try:
            self.channel.start_consuming()
        except KeyboardInterrupt:
            print("\n👋 Завершение работы...")
            self.close()

    def close(self):
        # закрытие соединения
        if self.connection and self.connection.is_open:
            self.connection.close()
