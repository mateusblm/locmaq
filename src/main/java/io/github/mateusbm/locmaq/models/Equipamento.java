package io.github.mateusbm.locmaq.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
public class Equipamento {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(length = 250)
    private String descricao;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal valorLocacao;

    @Column(nullable = false)
    private boolean disponibilidade;

    @ManyToOne // Muitos equipamentos para um cliente
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
}
