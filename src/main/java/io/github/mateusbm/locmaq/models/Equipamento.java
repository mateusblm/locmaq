package io.github.mateusbm.locmaq.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
public class Equipamento {

    @Id
    @Column
    @GeneratedValue
    private Long id;

    @Column
    private String nome;

    @Column
    private String descricao;

    @Column
    private double valorLocacao;

    @Column
    private boolean disponibilidade;
}
