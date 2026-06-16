package io.github.mateusbm.locmaq.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class RfidLeituraAdminDTO {
    private Long id;
    private String uid;
    private String origem;
    private String tipo;
    private String movimento;
    private Boolean autorizado;
    private String mensagem;
    private LocalDateTime dataHora;
    private String equipamento;
    private String statusAtual;
}
