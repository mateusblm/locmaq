package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.models.Orcamento;
import io.github.mateusbm.locmaq.models.StatusOrcamento;
import io.github.mateusbm.locmaq.repositories.OrcamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrcamentoService {
    private final OrcamentoRepository repository;

    public OrcamentoService(OrcamentoRepository repository) {
        this.repository = repository;
    }

    public Orcamento salvar(Orcamento orcamento) {
        return repository.save(orcamento);
    }

    public List<Orcamento> listar() {
        return repository.findAll();
    }

    public Orcamento buscarPorId(Long id) {
        return repository.findById(id).orElseThrow();
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
}