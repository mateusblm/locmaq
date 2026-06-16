package io.github.mateusbm.locmaq.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class RfidTagResponseDTO {
    private Long id;
    private String uid;
    private String descricao;
    private Boolean ativo;
    private Long equipamentoId;
    private String equipamento;
    private String statusEquipamento;
    private LocalDateTime createdAt;
}
