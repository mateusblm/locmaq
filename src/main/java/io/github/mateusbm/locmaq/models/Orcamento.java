package io.github.mateusbm.locmaq.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orcamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contrato_id", nullable = false)
    private ContratoLocacao contrato;

    private int diasTrabalhados;
    private double desconto;

    @Column(name = "valor_total", nullable = false)
    private double valorTotal;

    @Enumerated(EnumType.STRING)
    private StatusOrcamento status;

    private LocalDateTime dataCriacao;

    private String aprovadoPor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoOrcamento tipoOrcamento; // CLIENTE ou DONO

    private double taxaLucro; // Usado para CLIENTE


    public Double calcularValorCliente(ContratoLocacao contratoLocacao) {
        Double valorContrato = contrato.getValorTotal();
        double valorBase = (valorContrato != null) ? valorContrato : 0.0;
        return valorBase * diasTrabalhados;
    }

    public Double calcularValorDono(ContratoLocacao contratoLocacao) {
        Double valorCliente = calcularValorCliente(contratoLocacao);
        return valorCliente - desconto - (valorCliente * taxaLucro / 100.0);
    }
}