package io.github.mateusbm.locmaq.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RfidLeituraRequestDTO {
    private String uid;
    private String origem;
    private String tipo;
}
