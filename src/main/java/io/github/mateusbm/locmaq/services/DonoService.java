package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.models.Dono;
import io.github.mateusbm.locmaq.dto.DonoBuscaDTO;
import io.github.mateusbm.locmaq.repositories.DonoRepository;
import io.github.mateusbm.locmaq.repositories.EquipamentoRepository;
import io.github.mateusbm.locmaq.utils.ValidadorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DonoService {
    @Autowired
    private DonoRepository donoRepository;
    @Autowired
    private ActionLogService actionLogService;

    @Autowired
    private EquipamentoRepository equipamentoRepository; 


    private String getUsuarioAutenticado() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public List<Dono> listarTodosDonos() {
        return donoRepository.findAll();
    }

    public Optional<Dono> buscarPorId(Long id) {
        return donoRepository.findById(id);
    }

    public Dono salvar(Dono dono) {
        String doc = dono.getCnpj().replaceAll("\\D", "");
        if (doc.length() == 11) {
            if (!ValidadorUtil.isCpfValido(doc)) {
                throw new IllegalArgumentException("CPF inválido");
            }
        } else if (doc.length() == 14) {
            if (!ValidadorUtil.isCnpjValido(doc)) {
                throw new IllegalArgumentException("CNPJ inválido");
            }
        } else {
            throw new IllegalArgumentException("CPF/CNPJ deve ter 11 ou 14 dígitos");
        }
        if (!ValidadorUtil.isAgenciaValida(dono.getAgencia())) {
            throw new IllegalArgumentException("Agência inválida");
        }
        if (!ValidadorUtil.isNumeroContaValido(dono.getNumeroConta())) {
            throw new IllegalArgumentException("Número da conta inválido");
        }
        Dono saved = donoRepository.save(dono);
        actionLogService.logAction("Cadastro de proprietário", getUsuarioAutenticado(),
                "Proprietário ID: " + saved.getId() + ", Nome: " + saved.getNome());
        return saved;
    }

    public Dono editar(Long id, Dono novo) {
        String doc = novo.getCnpj().replaceAll("\\D", "");
        if (doc.length() == 11) {
            if (!ValidadorUtil.isCpfValido(doc)) {
                throw new IllegalArgumentException("CPF inválido");
            }
        } else if (doc.length() == 14) {
            if (!ValidadorUtil.isCnpjValido(doc)) {
                throw new IllegalArgumentException("CNPJ inválido");
            }
        } else {
            throw new IllegalArgumentException("CPF/CNPJ deve ter 11 ou 14 dígitos");
        }
        if (!ValidadorUtil.isAgenciaValida(novo.getAgencia())) {
            throw new IllegalArgumentException("Agência inválida");
        }
        if (!ValidadorUtil.isNumeroContaValido(novo.getNumeroConta())) {
            throw new IllegalArgumentException("Número da conta inválido");
        }

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
        Dono saved = donoRepository.save(dono);
        actionLogService.logAction("Edição de proprietário", getUsuarioAutenticado(),
                "Proprietário ID: " + saved.getId() + ", Nome: " + saved.getNome());
        return saved;
    }

    public void remover(Long id) {
        long count = equipamentoRepository.countByDonoId(id);
        if (count > 0) {
            throw new IllegalArgumentException("Não é possível remover o proprietário: existem equipamentos vinculados a ele.");
        }
        donoRepository.deleteById(id);
        actionLogService.logAction("Remoção de proprietário", getUsuarioAutenticado(),
                "Proprietário ID: " + id);
    }

    public List<DonoBuscaDTO> buscarPorNomeDTO(String nome) {
        return donoRepository.findAll().stream()
            .filter(d -> d.getNome() != null && d.getNome().toLowerCase().contains(nome.toLowerCase()))
            .map(d -> new DonoBuscaDTO(d.getId(), d.getNome(), d.getEmail()))
            .collect(Collectors.toList());
    }
}
