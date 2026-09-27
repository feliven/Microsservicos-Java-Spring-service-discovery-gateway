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
import br.com.alurafood.pagamentos.http.PedidoClient;
import br.com.alurafood.pagamentos.model.Pagamento;
import br.com.alurafood.pagamentos.model.Status;
import br.com.alurafood.pagamentos.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional(readOnly = true)
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PedidoClient pedidoClient;

    public Page<PagamentoDto> obterTodosOsPagamentos(Pageable page) {
        return pagamentoRepository.findAll(page).map(p -> modelMapper.map(p, PagamentoDto.class));
    }

    // private List<PagamentoDto> obterTodosSemPageable() {
    // return pagamentoRepository.findAll().stream()
    // .map(p -> modelMapper.map(p, PagamentoDto.class)).toList();
    // }

    public PagamentoDto obterPagamentoPorId(Long id) {
        var pagamento = pagamentoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());

        return modelMapper.map(pagamento, PagamentoDto.class);
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
        pagamentoRepository.save(pagamento);

        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    @Transactional
    public void excluirPagamento(Long id) {
        pagamentoRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void confirmarPagamento(Long id) {
        Optional<Pagamento> pagamentoOptional = pagamentoRepository.findById(id);

        if (!pagamentoOptional.isPresent()) {
            throw new EntityNotFoundException();
        }

        var pagamento = pagamentoOptional.get();
        pagamento.setStatus(Status.CONFIRMADO);
        pagamentoRepository.save(pagamento);
        pedidoClient.atualizaPagamento(pagamento.getPedidoId());
    }
}
