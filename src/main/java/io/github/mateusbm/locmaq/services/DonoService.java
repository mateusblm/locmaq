package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.models.Dono;
import io.github.mateusbm.locmaq.repositories.DonoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonoService {
    @Autowired
    private DonoRepository donoRepository;

    public List<Dono> listarTodosDonos() {
        return donoRepository.findAll();
    }

    public Optional<Dono> buscarPorId(Long id) {
        return donoRepository.findById(id);
    }

    public Dono salvar(Dono dono) {
        return donoRepository.save(dono);
    }

    public Dono editar(Long id, Dono novo) {
        Dono dono = donoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dono não encontrado"));
        dono.setNome(novo.getNome());
        dono.setEndereco(novo.getEndereco());
        dono.setEmail(novo.getEmail());
        dono.setCnpj(novo.getCnpj());
        dono.setTelefone(novo.getTelefone());
        dono.setBanco(novo.getBanco());
        dono.setAgencia(novo.getAgencia());
        dono.setNumeroConta(novo.getNumeroConta());
        return donoRepository.save(dono);
    }

    public void remover(Long id) {
        donoRepository.deleteById(id);
    }
}