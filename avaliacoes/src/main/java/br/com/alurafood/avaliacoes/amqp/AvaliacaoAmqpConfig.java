package br.com.alurafood.avaliacoes.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AvaliacaoAmqpConfig {

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public Queue filaDetalhesAvaliacao() {
        return QueueBuilder.durable("pagamentos.detalhes-avaliacao").deadLetterExchange("pagamentos.dlx").build();
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return ExchangeBuilder.fanoutExchange("pagamentos.ex").build();
    }

    @Bean
    public Queue dlqDetalhesAvaliacao() {
        return QueueBuilder.durable("pagamentos.detalhes-avaliacao-dlq").build();
    }

    @Bean
    public FanoutExchange deadLetterExchange() {
        return ExchangeBuilder.fanoutExchange("pagamentos.dlx").build();
    }

    @Bean
    public Binding bindPagamentoPedido() {
        return BindingBuilder.bind(filaDetalhesAvaliacao()).to(fanoutExchange());
    }

    @Bean
    public Binding bindDlxPagamentoPedido() {
        return BindingBuilder.bind(dlqDetalhesAvaliacao()).to(deadLetterExchange());
    }

}