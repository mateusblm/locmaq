package io.github.mateusbm.locmaq.dto;

import io.github.mateusbm.locmaq.models.Equipamento;
import io.github.mateusbm.locmaq.models.EquipamentoBoletimMedicao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipamentoBoletimMedicaoDTO {
    private Long id;
    private Long equipamentoId;
    private String equipamentoNome;
    private Integer quantidade;
    private String observacao;
    private Double valorMedido;

    public static EquipamentoBoletimMedicaoDTO fromEntity(EquipamentoBoletimMedicao e) {
        EquipamentoBoletimMedicaoDTO dto = new EquipamentoBoletimMedicaoDTO();
        dto.setId(e.getId());
        dto.setEquipamentoId(e.getEquipamento().getId());
        dto.setEquipamentoNome(e.getEquipamento().getNome());
        dto.setQuantidade(e.getQuantidade());
        dto.setObservacao(e.getObservacao());
        dto.setValorMedido(e.getValorMedido());
        return dto;
    }

    public EquipamentoBoletimMedicao toEntity(Equipamento eq){
        EquipamentoBoletimMedicao e = new EquipamentoBoletimMedicao();
        e.setId(this.id);
        e.setEquipamento(eq);
        e.setQuantidade(this.quantidade);
        e.setObservacao(this.observacao);
        e.setValorMedido(this.valorMedido != null ? this.valorMedido : 0.0);
        return e;
    }
}
