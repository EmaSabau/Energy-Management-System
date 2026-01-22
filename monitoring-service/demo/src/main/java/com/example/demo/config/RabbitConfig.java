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

    public static final String USER_EXCHANGE = "user.exchange";
    public static final String DEVICE_EXCHANGE = "device.exchange";
    public static final String OVERCONSUMPTION_EXCHANGE = "overconsumption.exchange";
    public static final String MONITORING_USER_SYNC_QUEUE = "monitoring_user_sync_queue";
    public static final String MONITORING_DEVICE_SYNC_QUEUE = "monitoring_device_sync_queue";
    public static final String DEVICE_DATA_QUEUE = "device_data_queue";

    public static final String OVERCONSUMPTION_ROUTING_KEY = "overconsumption";
    @Bean public Queue monitoringUserQueue() { return new Queue(MONITORING_USER_SYNC_QUEUE, true); }
    @Bean public Queue monitoringDeviceQueue() { return new Queue(MONITORING_DEVICE_SYNC_QUEUE, true); }
    @Bean public Queue deviceDataQueue() { return new Queue(DEVICE_DATA_QUEUE, true); }
    @Bean public TopicExchange userExchange() { return new TopicExchange(USER_EXCHANGE); }
    @Bean public TopicExchange deviceExchange() { return new TopicExchange(DEVICE_EXCHANGE); }
    @Bean public TopicExchange overconsumptionExchange() { return new TopicExchange(OVERCONSUMPTION_EXCHANGE); }

    @Bean
    public Binding userMonitoringBinding(Queue monitoringUserQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(monitoringUserQueue).to(userExchange).with("#.user.#");
    }

    @Bean
    public Binding deviceMonBinding(Queue monitoringDeviceQueue, TopicExchange deviceExchange) {
        return BindingBuilder.bind(monitoringDeviceQueue).to(deviceExchange).with("device.#");
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