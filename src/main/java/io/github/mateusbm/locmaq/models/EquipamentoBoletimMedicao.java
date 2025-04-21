package io.github.mateusbm.locmaq.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class EquipamentoBoletimMedicao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "boletim_medicao_id", nullable = false)
    private BoletimMedicao boletimMedicao;

    @ManyToOne
    private Equipamento equipamento;

    private Integer quantidade;
    private String observacao;

    @Column(nullable = false)
    private Double valorMedido;


}