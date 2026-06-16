package io.github.mateusbm.locmaq.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RfidLeituraResponseDTO {
    private Boolean autorizado;
    private String mensagem;
    private String uid;
    private String equipamento;
    private String movimento;
    private String statusAtual;
}
