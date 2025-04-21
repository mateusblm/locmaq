package io.github.mateusbm.locmaq.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
public class ContratoLocacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Usuario usuarioLogistica;

    @ManyToOne
    private Cliente cliente;

    @ManyToOne
    private Equipamento equipamento;

    @Column(nullable = false)
    private Double valorTotal;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    public ContratoLocacao() {

    }
}
