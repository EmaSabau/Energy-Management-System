package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String USER_EXCHANGE = "user.exchange";
    public static final String USER_SERVICE_SYNC_QUEUE = "user_service_sync_queue";

    @Bean public TopicExchange userExchange() { return new TopicExchange(USER_EXCHANGE); }
    @Bean public Queue userServiceQueue() { return new Queue(USER_SERVICE_SYNC_QUEUE, true); }

    @Bean
    public Binding userServiceBinding(Queue userServiceQueue, TopicExchange userExchange) {
        // 🚀 REPARAT: User Service ascultă DOAR mesajele de la Auth (Register)
        return BindingBuilder.bind(userServiceQueue).to(userExchange).with("auth.user.#");
    }

    @Bean public Jackson2JsonMessageConverter jsonMessageConverter() { return new Jackson2JsonMessageConverter(); }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter()); // 🚀 FIX: Rezolvă eroarea SimpleMessageConverter
        return template;
    }
}