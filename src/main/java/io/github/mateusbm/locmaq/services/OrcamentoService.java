package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.models.*;
import io.github.mateusbm.locmaq.repositories.OrcamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrcamentoService {
    private final OrcamentoRepository repository;

    public OrcamentoService(OrcamentoRepository repository) {
        this.repository = repository;
    }

    public Orcamento salvar(Orcamento orcamento) {
        validarDesconto(orcamento);
        return repository.save(orcamento);
    }

    public List<Orcamento> listar() {
        return repository.findAll();
    }

    public Orcamento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento não encontrado."));
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

    public Orcamento aprovar(Long id, String gestor) {
        Orcamento o = buscarPorId(id);
        o.setStatus(StatusOrcamento.APROVADO);
        o.setAprovadoPor(gestor);
        return repository.save(o);
    }

    public Orcamento rejeitar(Long id, String gestor) {
        Orcamento o = buscarPorId(id);
        o.setStatus(StatusOrcamento.REJEITADO);
        o.setAprovadoPor(gestor);
        return repository.save(o);
    }

    // Cria dois orçamentos: DONO e CLIENTE
    public void criarOrcamentosDuplos(Orcamento orcamento, ContratoLocacao contrato) {
        int dias = orcamento.getDiasTrabalhados();
        double desconto = orcamento.getDesconto();
        double taxaLucro = orcamento.getTaxaLucro();
        double valorDiaria = contrato.getValorTotal();

        double valorCliente = valorDiaria * dias;
        if (desconto > valorCliente - (valorCliente * taxaLucro / 100.0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O desconto não pode ser maior que o valor total do aluguel menos a taxa de lucro (R$ " +
                            String.format("%.2f", valorCliente - (valorCliente * taxaLucro / 100.0)) + ").");
        }

        Orcamento cliente = new Orcamento();
        cliente.setContrato(contrato);
        cliente.setDiasTrabalhados(dias);
        cliente.setDesconto(0);
        cliente.setValorTotal(valorCliente);
        cliente.setStatus(StatusOrcamento.PENDENTE);
        cliente.setDataCriacao(LocalDateTime.now());
        cliente.setAprovadoPor(null);
        cliente.setTipoOrcamento(TipoOrcamento.CLIENTE);
        cliente.setTaxaLucro(taxaLucro);

        double valorDono = valorCliente - desconto - (valorCliente * taxaLucro / 100.0);
        Orcamento dono = new Orcamento();
        dono.setContrato(contrato);
        dono.setDiasTrabalhados(dias);
        dono.setDesconto(desconto);
        dono.setValorTotal(valorDono);
        dono.setStatus(StatusOrcamento.PENDENTE);
        dono.setDataCriacao(LocalDateTime.now());
        dono.setAprovadoPor(null);
        dono.setTipoOrcamento(TipoOrcamento.DONO);
        dono.setTaxaLucro(0);

        repository.save(dono);
        repository.save(cliente);
    }

    private void validarDesconto(Orcamento orcamento) {
        double taxaLucro = orcamento.getTaxaLucro();
        double valorCliente = orcamento.getContrato().getValorTotal() * orcamento.getDiasTrabalhados();
        if (orcamento.getDesconto() > valorCliente - (valorCliente * taxaLucro / 100.0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O desconto não pode ser maior que o valor total do aluguel menos a taxa de lucro (R$ " +
                            String.format("%.2f", valorCliente - (valorCliente * taxaLucro / 100.0)) + ").");
        }
    }
}