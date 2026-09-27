package br.com.alurafood.pagamentos.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDto {
    private Long id;
    private List<ItemDoPedidoDto> itens;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
final class ItemDoPedidoDto {
    private Long id;
    private Integer quantidade;
    private String descricao;

}