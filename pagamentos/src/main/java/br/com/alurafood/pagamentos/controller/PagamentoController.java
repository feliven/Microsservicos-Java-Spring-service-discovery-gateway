package br.com.alurafood.pagamentos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alurafood.pagamentos.dto.PagamentoDto;
import br.com.alurafood.pagamentos.service.PagamentoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService service;

    @GetMapping
    public ResponseEntity<Page<PagamentoDto>> getAll(Pageable page) {
        var pagamentos = service.obterTodosOsPagamentos(page);
        return ResponseEntity.ok(pagamentos);
    }

    @GetMapping("{id}")
    public ResponseEntity<PagamentoDto> getById(@PathVariable @NotNull Long id) {
        var dto = service.obterPagamentoPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<PagamentoDto> postPagamento(@RequestBody @Valid PagamentoDto dto,
            UriComponentsBuilder uriBuilder) {
        var pagamento = service.criarPagamento(dto);

        var uri = uriBuilder.path("/pagamentos/{id}").buildAndExpand(pagamento.getId()).toUri();

        return ResponseEntity.created(uri).body(pagamento);
    }

    @PutMapping("{id}")
    public ResponseEntity<PagamentoDto> putPagamento(@RequestBody @Valid PagamentoDto dto,
            @PathVariable @NotNull Long id) {
        var pagamentoAtualizado = service.atualizarPagamento(id, dto);
        return ResponseEntity.ok(pagamentoAtualizado);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deletePagamento(@PathVariable @NotNull Long id) {
        service.excluirPagamento(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}/confirmar")
    public void confirmarPagamento(@PathVariable @NotNull Long id) {
        service.confirmarPagamento(id);
    }
}
