package com.example.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String OVERCONSUMPTION_QUEUE = "overconsumption_queue";
    public static final String OVERCONSUMPTION_EXCHANGE = "overconsumption.exchange";
    public static final String OVERCONSUMPTION_ROUTING_KEY = "overconsumption";
    public static final String CHAT_REQUEST_QUEUE = "chat_request_queue";
    public static final String CHAT_RESPONSE_QUEUE = "chat_response_queue";
    @Bean public Queue overconsumptionQueue() { return new Queue(OVERCONSUMPTION_QUEUE, true); }
    @Bean public Queue chatRequestQueue() { return new Queue(CHAT_REQUEST_QUEUE, true); }
    @Bean public Queue chatResponseQueue() { return new Queue(CHAT_RESPONSE_QUEUE, true); }
    @Bean public TopicExchange overconsumptionExchange() { return new TopicExchange(OVERCONSUMPTION_EXCHANGE); }
    @Bean
    public Binding overconsumptionBinding(Queue overconsumptionQueue, TopicExchange overconsumptionExchange) {
        return BindingBuilder.bind(overconsumptionQueue)
                .to(overconsumptionExchange)
                .with(OVERCONSUMPTION_ROUTING_KEY);
    }
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}