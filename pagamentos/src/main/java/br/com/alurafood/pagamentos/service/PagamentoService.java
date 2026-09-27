package br.com.alurafood.pagamentos.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.alurafood.pagamentos.dto.PagamentoDto;
import br.com.alurafood.pagamentos.dto.PagamentoGetDto;
import br.com.alurafood.pagamentos.http.PedidoClient;
import br.com.alurafood.pagamentos.model.Pagamento;
import br.com.alurafood.pagamentos.model.Status;
import br.com.alurafood.pagamentos.repository.PagamentoRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PedidoClient pedidoClient;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<PagamentoGetDto> obterTodosOsPagamentos(Pageable page) {
        var pagamentoDtoPage = pagamentoRepository.findAll(page).map(p -> modelMapper.map(p, PagamentoGetDto.class));

        var pedidos = pedidoClient.obterTodosPedidos();

        pagamentoDtoPage.forEach(pgto -> pgto
                .setPedido(pedidos.stream().filter(pedido -> pedido.getId() == pgto.getPedidoId()).findFirst().get()));

        return pagamentoDtoPage;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public PagamentoGetDto obterPagamentoPorId(Long id) {
        var pagamento = pagamentoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());

        var pedido = pedidoClient.obterPedidoPorId(id);

        var pagamentoDto = modelMapper.map(pagamento, PagamentoGetDto.class);
        pagamentoDto.setPedido(pedido);
        return pagamentoDto;
    }

    @Transactional
    public PagamentoDto criarPagamento(PagamentoDto dto) {
        var pagamento = modelMapper.map(dto, Pagamento.class);
        pagamento.setStatus(Status.CRIADO);
        pagamentoRepository.save(pagamento);

        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    @Transactional
    public PagamentoDto atualizarPagamento(Long id, PagamentoDto dto) {
        var pagamento = modelMapper.map(dto, Pagamento.class);
        pagamento.setId(id);

        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    @Transactional
    public void excluirPagamento(Long id) {
        pagamentoRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @CircuitBreaker(name = "atualizaPedido", fallbackMethod = "pagamentoAutorizadoComIntegracaoPendente")
    public void confirmarPagamento(Long id) {
        Optional<Pagamento> pagamentoOptional = pagamentoRepository.findById(id);

        if (!pagamentoOptional.isPresent()) {
            throw new EntityNotFoundException();
        }

        var pagamento = pagamentoOptional.get();
        pedidoClient.confirmaPagamentoNoPedido(pagamento.getPedidoId()); // tenta a integração primeiro
        pagamento.setStatus(Status.CONFIRMADO);
        pagamentoRepository.save(pagamento); // só persiste se a chamada deu certo
    }

    @Transactional
    public void pagamentoAutorizadoComIntegracaoPendente(Long id, Exception e) {
        Optional<Pagamento> pagamentoOptional = pagamentoRepository.findById(id);

        if (!pagamentoOptional.isPresent()) {
            throw new EntityNotFoundException();
        }

        var pagamento = pagamentoOptional.get();
        pagamento.setStatus(Status.CONFIRMADO_SEM_INTEGRACAO);
        pagamentoRepository.save(pagamento);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @CircuitBreaker(name = "atualizaPedido", fallbackMethod = "")
    public void desconfirmarPagamento(Long id) {
        Optional<Pagamento> pagamentoOptional = pagamentoRepository.findById(id);

        if (!pagamentoOptional.isPresent()) {
            throw new EntityNotFoundException();
        }

        var pagamento = pagamentoOptional.get();
        pedidoClient.desconfirmaPagamentoNoPedido(pagamento.getPedidoId()); // tenta a integração primeiro
        pagamento.setStatus(Status.CRIADO);
        pagamentoRepository.save(pagamento); // só persiste se a chamada deu certo
    }
}
