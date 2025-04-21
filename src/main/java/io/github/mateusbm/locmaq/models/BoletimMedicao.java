package io.github.mateusbm.locmaq.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
public class BoletimMedicao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String periodo;

    @ManyToOne
    private Usuario planejador;

    private String situacao;

    @Column(nullable = false)
    private LocalDate dataMedicao;

    @OneToMany(mappedBy = "boletimMedicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EquipamentoBoletimMedicao> equipamentos = new ArrayList<>();

    private Boolean assinado = false;
    private String observacoes;

    public BoletimMedicao() {

    }
}