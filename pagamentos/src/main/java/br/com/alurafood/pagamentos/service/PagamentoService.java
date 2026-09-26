package br.com.alurafood.pagamentos.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.alurafood.pagamentos.dto.PagamentoDto;
import br.com.alurafood.pagamentos.model.Pagamento;
import br.com.alurafood.pagamentos.model.Status;
import br.com.alurafood.pagamentos.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ModelMapper modelMapper;

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

    public PagamentoDto criarPagamento(PagamentoDto dto) {
        var pagamento = modelMapper.map(dto, Pagamento.class);
        pagamento.setStatus(Status.CRIADO);
        pagamentoRepository.save(pagamento);

        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    public PagamentoDto atualizarPagamento(Long id, PagamentoDto dto) {
        var pagamento = modelMapper.map(dto, Pagamento.class);
        pagamento.setId(id);
        pagamentoRepository.save(pagamento);

        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    public void excluirPagamento(Long id) {
        pagamentoRepository.deleteById(id);
    }
}
