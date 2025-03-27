package io.github.mateusbm.locmaq.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor

public class BoletimMedicao {

    @Id
    @Column
    @GeneratedValue
    private Long id;

    @Column
    private ContratoLocacao contratoLocacao;

    @Column
    private Date dataMedicao;

    @Column
    private double valorMedido;

    @Column
    private String observacoes;
}
