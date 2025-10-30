package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.models.Orcamento;
import io.github.mateusbm.locmaq.models.StatusOrcamento;
import io.github.mateusbm.locmaq.models.ContratoLocacao;
import io.github.mateusbm.locmaq.repositories.ContratoLocacaoRepository;
import io.github.mateusbm.locmaq.services.OrcamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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
    public void criar(@RequestBody Orcamento orcamento) {
        ContratoLocacao contrato = contratoRepo.findById(orcamento.getContrato().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato não encontrado."));
        try {
            service.criarOrcamentosDuplos(orcamento, contrato);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ocorreu um erro ao criar o orçamento. Por favor, tente novamente.");
        }
    }

    @PutMapping("/{id}")
    public Orcamento editar(@PathVariable Long id, @RequestBody Orcamento novo) {
        Orcamento o = service.buscarPorId(id);
        if (o.getStatus() != StatusOrcamento.PENDENTE)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Só é possível editar orçamentos pendentes.");
        o.setDiasTrabalhados(novo.getDiasTrabalhados());
        o.setDesconto(novo.getDesconto());
        o.setValorTotal(novo.getValorTotal());
        o.setTaxaLucro(novo.getTaxaLucro());
        try {
            return service.salvar(o);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ocorreu um erro ao editar o orçamento. Por favor, tente novamente.");
        }
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        Orcamento o = service.buscarPorId(id);
        if (o.getStatus() != StatusOrcamento.PENDENTE)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Só é possível excluir orçamentos pendentes.");
        try {
            service.excluir(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ocorreu um erro ao excluir o orçamento. Por favor, tente novamente.");
        }
    }

    @PostMapping("/{id}/aprovar")
    public Orcamento aprovar(@PathVariable Long id, @RequestParam String gestor) {
        try {
            return service.aprovar(id, gestor);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ocorreu um erro ao aprovar o orçamento. Por favor, tente novamente.");
        }
    }

    @PostMapping("/{id}/rejeitar")
    public Orcamento rejeitar(@PathVariable Long id, @RequestParam String gestor) {
        try {
            return service.rejeitar(id, gestor);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ocorreu um erro ao rejeitar o orçamento. Por favor, tente novamente.");
        }
    }
}