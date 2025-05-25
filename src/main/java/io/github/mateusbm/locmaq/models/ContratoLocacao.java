package io.github.mateusbm.locmaq.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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

    @Column(name = "status_pagamento", nullable = false)
    private String statusPagamento = "PENDENTE";

    public ContratoLocacao() {

    }
}