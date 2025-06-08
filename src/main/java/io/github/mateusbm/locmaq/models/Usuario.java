package io.github.mateusbm.locmaq.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@AllArgsConstructor
@Data
@Getter
@Setter
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Getter
    @Column(nullable = false)
    private String senha;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipoUsuario;

    private boolean ativo = true;


    public Usuario(String nome, String senha, TipoUsuario tipoUsuario) {
        this.nome = nome;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
    }

    public Usuario() {
    }

}

