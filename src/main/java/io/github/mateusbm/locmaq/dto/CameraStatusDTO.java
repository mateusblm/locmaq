package io.github.mateusbm.locmaq.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CameraStatusDTO {
    private boolean ativo;
    private String mensagem;
    private String output;
}
