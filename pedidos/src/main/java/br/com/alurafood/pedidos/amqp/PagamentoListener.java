package br.com.alurafood.pedidos.amqp;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import br.com.alurafood.pedidos.dto.PagamentoDto;

@Component
public class PagamentoListener {

    @RabbitListener(queues = "pagamento.concluido")
    public void recebeMensagem(PagamentoDto pagamento) {
        System.out.println("""
                Pagamento do pedido id %s recebido!
                Id do pagamento: %s
                Valor: R$ %s
                Número do cartão: final [%s]
                Expiração: %s
                Status: %s
                """.formatted(
                pagamento.getPedidoId(),
                pagamento.getId(),
                pagamento.getValor(),
                pagamento.getNumero().substring(pagamento.getNumero().length() - 4),
                pagamento.getExpiracao(),
                pagamento.getStatus()));
    }
}
