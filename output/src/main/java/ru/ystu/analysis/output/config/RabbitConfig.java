package ru.ystu.analysis.output.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.queue.results}")
    private String queueName;

    @Value("${rabbitmq.exchange.results}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key.analysis-res}")
    private String analysisRoutingKey;

    @Value("${rabbitmq.routing-key.predict-res}")
    private String predictRoutingKey;

    @Bean
    public Queue resultsQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public DirectExchange resultsExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding analysisBinding(Queue resultsQueue, DirectExchange resultsExchange) {
        return BindingBuilder.bind(resultsQueue)
                .to(resultsExchange)
                .with(analysisRoutingKey);
    }

    @Bean
    public Binding predictBinding(Queue resultsQueue, DirectExchange resultsExchange) {
        return BindingBuilder.bind(resultsQueue)
                .to(resultsExchange)
                .with(predictRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}