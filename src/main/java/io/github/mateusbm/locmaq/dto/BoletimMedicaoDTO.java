package io.github.mateusbm.locmaq.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.mateusbm.locmaq.models.BoletimMedicao;
import io.github.mateusbm.locmaq.models.EquipamentoBoletimMedicao;
import io.github.mateusbm.locmaq.models.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoletimMedicaoDTO {
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dataInicio;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dataFim;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dataMedicao;

    private Long planejadorId;
    private String planejadorNome;
    private String situacao;
    private List<EquipamentoBoletimMedicaoDTO> equipamentos;
    private Boolean assinado;

    public static BoletimMedicaoDTO fromEntity(BoletimMedicao b) {
        BoletimMedicaoDTO dto = new BoletimMedicaoDTO();
        dto.setId(b.getId());
        dto.setDataInicio(b.getDataInicio());
        dto.setDataFim(b.getDataFim());
        dto.setSituacao(b.getSituacao());
        dto.setAssinado(b.getAssinado());
        dto.setDataMedicao(b.getDataMedicao());
        if (b.getPlanejador() != null) {
            dto.setPlanejadorId(b.getPlanejador().getId());
            dto.setPlanejadorNome(b.getPlanejador().getNome());
        }
        if (b.getEquipamentos() != null) {
            dto.setEquipamentos(b.getEquipamentos().stream().map(EquipamentoBoletimMedicaoDTO::fromEntity).toList());
        }
        return dto;
    }

    public BoletimMedicao toEntity(Usuario planejador, List<EquipamentoBoletimMedicao> equipamentos) {
        BoletimMedicao b = new BoletimMedicao();
        b.setId(this.id);
        b.setDataInicio(this.dataInicio);
        b.setDataFim(this.dataFim);
        b.setSituacao(this.situacao);
        b.setPlanejador(planejador);
        b.setEquipamentos(equipamentos != null ? equipamentos : new ArrayList<>());
        b.setAssinado(this.assinado != null ? this.assinado : false);
        b.setDataMedicao(this.dataMedicao != null ? this.dataMedicao : LocalDate.now());
        return b;
    }
}