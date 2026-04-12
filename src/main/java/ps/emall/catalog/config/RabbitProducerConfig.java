package ps.emall.catalog.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
// 1. Import the new Jackson 3 converter
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// 2. Import JsonMapper
import ps.emall.catalog.event.OutgoingEventRoutingKey;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitProducerConfig {

    public static final String CATALOG_EVENTS_EXCHANGE = "catalog.events";

    @Bean
    public TopicExchange catalogEventsExchange() {
        return new TopicExchange(CATALOG_EVENTS_EXCHANGE, true, false);
    }

    // 3. Update the return type and inject your custom JsonMapper
    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter(JsonMapper jsonMapper) {
        // By passing jsonMapper here, RabbitMQ will respect the DateTimeFeature
        // rules we configured in JacksonConfig!
        return new JacksonJsonMessageConverter(jsonMapper);
    }

    // 4. Update the parameter type here as well
    @Bean
    public RabbitTemplate rabbitTemplate(
            org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter jacksonJsonMessageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonJsonMessageConverter);
        return rabbitTemplate;
    }

    @Bean
    public TopicExchange emallEventsExchange() {
        return new TopicExchange(OutgoingEventRoutingKey.EXCHANGE_NAME, true, false);
    }
}