package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.models.Cliente;
import io.github.mateusbm.locmaq.models.Dono;
import io.github.mateusbm.locmaq.models.Equipamento;
import io.github.mateusbm.locmaq.dto.EquipamentoDTO;
import io.github.mateusbm.locmaq.repositories.ClienteRepository;
import io.github.mateusbm.locmaq.repositories.DonoRepository;
import io.github.mateusbm.locmaq.repositories.EquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipamentoService {

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DonoRepository donoRepository;

    @Autowired
    private ActionLogService actionLogService;

    private String getUsuarioAutenticado() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public void cadastrarEquipamento(EquipamentoDTO dto) {
        if (equipamentoRepository.existsByNome(dto.getNome())) {
            throw new RuntimeException("Já existe um equipamento cadastrado com este nome.");
        }
        Optional<Cliente> clienteOpt = clienteRepository.findById(dto.getClienteId());
        Optional<Dono> donoOpt = donoRepository.findById(dto.getDonoId());

        if (clienteOpt.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado");
        }
        if (donoOpt.isEmpty()) {
            throw new RuntimeException("Dono não encontrado");
        }

        Equipamento equipamento = new Equipamento();
        equipamento.setNome(dto.getNome());
        equipamento.setDescricao(dto.getDescricao());
        equipamento.setValorLocacao(dto.getValorLocacao());
        equipamento.setDisponibilidade(dto.isDisponibilidade());
        equipamento.setCliente(clienteOpt.get());
        equipamento.setDono(donoOpt.get());

        Equipamento saved = equipamentoRepository.save(equipamento);
        actionLogService.logAction("Cadastro de equipamento",
                getUsuarioAutenticado(),
                "Equipamento ID: " + saved.getId() + ", Nome: " + saved.getNome());
    }

    public List<Equipamento> listarEquipamentos() {
        return equipamentoRepository.findAll();
    }

    public Optional<Equipamento> buscarPorId(Long id) {
        return equipamentoRepository.findById(id);
    }

    public Equipamento editarEquipamento(Long id, EquipamentoDTO dto) {
        // Verifica se já existe outro equipamento com o mesmo nome
        Optional<Equipamento> existente = equipamentoRepository.findByNome(dto.getNome());
        if (existente.isPresent() && !existente.get().getId().equals(id)) {
            throw new RuntimeException("Já existe um equipamento cadastrado com este nome.");
        }
        Equipamento equipamento = equipamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado"));
        equipamento.setNome(dto.getNome());
        equipamento.setDescricao(dto.getDescricao());
        equipamento.setValorLocacao(dto.getValorLocacao());
        equipamento.setDisponibilidade(dto.isDisponibilidade());

        if (dto.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
            equipamento.setCliente(cliente);
        }

        if (dto.getDonoId() != null) {
            Dono dono = donoRepository.findById(dto.getDonoId())
                    .orElseThrow(() -> new RuntimeException("Dono não encontrado"));
            equipamento.setDono(dono);
        }
        Equipamento saved = equipamentoRepository.save(equipamento);
        actionLogService.logAction("Edição de equipamento",
                getUsuarioAutenticado(),
                "Equipamento ID: " + saved.getId() + ", Nome: " + saved.getNome());
        return saved;
    }

    public void removerEquipamento(Long id) {
        equipamentoRepository.deleteById(id);
        actionLogService.logAction("Remoção de equipamento", getUsuarioAutenticado(), "Equipamento ID: " + id);
    }

    public boolean verificarDisponibilidade(Long id) {
        Equipamento equipamento = equipamentoRepository.findById(id).orElse(null);
        return equipamento != null && equipamento.isDisponibilidade();
    }
}
