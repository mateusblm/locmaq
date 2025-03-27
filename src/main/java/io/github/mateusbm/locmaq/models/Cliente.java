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
public class Cliente {

    @Id
    @Column
    @GeneratedValue
    private Long id;

    @Column
    private String nome;

    @Column
    private String cnpj;

    @Column
    private String endereco;

    @Column
    private String email;

    @Column
    private String telefone;
}
