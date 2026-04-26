package ps.emall.catalog.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ps.emall.catalog.event.OutgoingEventConstant;
import ps.emall.catalog.job.OutgoingJobConstant;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitProducerConfig {

    @Bean
    public TopicExchange emallEventsExchange() {
        return new TopicExchange(
                OutgoingEventConstant.EXCHANGE_NAME,
                true,
                false
        );
    }

    @Bean
    public TopicExchange emallJobsExchange() {
        return new TopicExchange(
                OutgoingJobConstant.EXCHANGE_NAME,
                true,
                false
        );
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter jacksonJsonMessageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonJsonMessageConverter);
        return rabbitTemplate;
    }
}