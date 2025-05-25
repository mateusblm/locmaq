package io.github.mateusbm.locmaq.models;

import io.github.mateusbm.locmaq.models.ContratoLocacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orcamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contrato_id", nullable = false)
    private ContratoLocacao contrato;

    private int diasTrabalhados;
    private double desconto;
    private double valorTotal;

    @Enumerated(EnumType.STRING)
    private StatusOrcamento status;

    private LocalDateTime dataCriacao;

    private String aprovadoPor;

}