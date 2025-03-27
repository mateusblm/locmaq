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
public class ContratoLocacao {

    @Id
    @Column
    @GeneratedValue
    private Long id;

    @Column
    private Cliente cliente;

    @Column
    private Equipamento equipamento;

    @Column
    private Date dataInicio;

    @Column
    private Date dataFim;

    @Column
    private Double valorTotal;

    private enum status {
        ATIVO,
        INATIVO,
        CONCLUIDO
    }
}
