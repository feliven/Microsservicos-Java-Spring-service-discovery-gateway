package br.com.alurafood.pagamentos.http;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.alurafood.pagamentos.dto.PedidoDto;

@FeignClient("pedidos-ms")
public interface PedidoClient {
    @RequestMapping(method = RequestMethod.PUT, value = "/pedidos/{id}/pago")
    void confirmaPagamentoNoPedido(@PathVariable Long id);

    @RequestMapping(method = RequestMethod.PUT, value = "/pedidos/{id}/nao-pago")
    void desconfirmaPagamentoNoPedido(@PathVariable Long id);

    @RequestMapping(method = RequestMethod.GET, value = "/pedidos")
    List<PedidoDto> obterTodosPedidos();

    @RequestMapping(method = RequestMethod.GET, value = "/pedidos/{id}")
    PedidoDto obterPedidoPorId(@PathVariable Long id);
}
