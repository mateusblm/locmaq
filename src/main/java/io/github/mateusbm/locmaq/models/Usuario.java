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
public class Usuario {

    @Id
    @Column
    @GeneratedValue
    private Long id;

    @Column
    private String nome;

    @Column
    private String senha;

    private enum tipo {PLANEJADOR, LOGISTICA, GESTOR}



}
