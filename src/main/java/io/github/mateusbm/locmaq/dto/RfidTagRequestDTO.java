package io.github.mateusbm.locmaq.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RfidTagRequestDTO {
    private String uid;
    private String descricao;
    private Boolean ativo;
    private Long equipamentoId;
}
