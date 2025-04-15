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

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING) // Certifique de que o enum seja mapeado como STRING
    @Column(nullable = false)
    private TipoUsuario tipo;

    public Usuario(String nome, String senha, TipoUsuario tipo) {
        this.nome = nome;
        this.senha = senha;
        this.tipo = tipo;
    }

    public TipoUsuario getTipoUsuario() {
        return tipo;
    }

    public Usuario() {
    }

    public String getSenha() {
        return senha;
    }
}

