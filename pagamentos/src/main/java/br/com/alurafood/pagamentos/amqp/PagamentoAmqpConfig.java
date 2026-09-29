package br.com.alurafood.pagamentos.amqp;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PagamentoAmqpConfig {

    @Bean
    public Queue criaFila() {
        return new Queue("pagamento.concluido", true);
    }

    @Bean
    public Queue criaFilaComBuilder() {
        return QueueBuilder.durable("pagamento.concluido2").build();
    }

    @Bean
    public RabbitAdmin criaRabbitAdmin(ConnectionFactory conn) {
        return new RabbitAdmin(conn);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> inicializaRabbitAdmin(RabbitAdmin admin) {
        return evento -> admin.initialize();
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    };

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory conn,
            JacksonJsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(conn);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
