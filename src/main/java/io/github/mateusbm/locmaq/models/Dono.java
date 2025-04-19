package io.github.mateusbm.locmaq.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dono {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    private String endereco;
    private String email;
    private String cnpj;
    private String telefone;
    private String banco;
    private String agencia;
    private String numeroConta;

    // RELACIONAMENTO: UM DONO PARA MUITOS EQUIPAMENTOS
    @OneToMany(mappedBy = "dono")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Equipamento> equipamentos;
}