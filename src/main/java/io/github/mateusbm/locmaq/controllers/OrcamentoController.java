package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.models.Orcamento;
import io.github.mateusbm.locmaq.models.StatusOrcamento;
import io.github.mateusbm.locmaq.models.ContratoLocacao;
import io.github.mateusbm.locmaq.repositories.ContratoLocacaoRepository;
import io.github.mateusbm.locmaq.services.OrcamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orcamentos")
public class OrcamentoController {
    private final OrcamentoService service;
    private final ContratoLocacaoRepository contratoRepo;

    public OrcamentoController(OrcamentoService service, ContratoLocacaoRepository contratoRepo) {
        this.service = service;
        this.contratoRepo = contratoRepo;
    }

    @GetMapping
    public List<Orcamento> listar() {
        return service.listar();
    }

    @PostMapping
    public Orcamento criar(@RequestBody Orcamento orcamento) {
        ContratoLocacao contrato = contratoRepo.findById(orcamento.getContrato().getId()).orElseThrow();
        orcamento.setContrato(contrato);
        orcamento.setStatus(StatusOrcamento.PENDENTE);
        orcamento.setDataCriacao(LocalDateTime.now());
        orcamento.setAprovadoPor(null);
        return service.salvar(orcamento);
    }

    @PutMapping("/{id}")
    public Orcamento editar(@PathVariable Long id, @RequestBody Orcamento novo) {
        Orcamento o = service.buscarPorId(id);
        if (o.getStatus() != StatusOrcamento.PENDENTE)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Só é possível editar orçamentos pendentes.");
        o.setDiasTrabalhados(novo.getDiasTrabalhados());
        o.setDesconto(novo.getDesconto());
        o.setValorTotal(novo.getValorTotal());
        return service.salvar(o);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        Orcamento o = service.buscarPorId(id);
        if (o.getStatus() != StatusOrcamento.PENDENTE)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Só é possível excluir orçamentos pendentes.");
        service.excluir(id);
    }

    @PostMapping("/{id}/aprovar")
    public Orcamento aprovar(@PathVariable Long id, @RequestParam String gestor) {
        return service.aprovar(id, gestor);
    }

    @PostMapping("/{id}/rejeitar")
    public Orcamento rejeitar(@PathVariable Long id, @RequestParam String gestor) {
        return service.rejeitar(id, gestor);
    }
}