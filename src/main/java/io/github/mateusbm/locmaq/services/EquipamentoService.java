package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.models.Cliente;
import io.github.mateusbm.locmaq.models.Dono;
import io.github.mateusbm.locmaq.models.Equipamento;
import io.github.mateusbm.locmaq.repositories.ClienteRepository;
import io.github.mateusbm.locmaq.repositories.DonoRepository;
import io.github.mateusbm.locmaq.repositories.EquipamentoRepository;
import io.github.mateusbm.locmaq.dto.EquipamentoDTO;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Cadastrar novo equipamento
    public void cadastrarEquipamento(EquipamentoDTO dto) {
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

        equipamentoRepository.save(equipamento);
    }

    // Listar todos os equipamentos
    public List<Equipamento> listarEquipamentos() {
        return equipamentoRepository.findAll();
    }

    // Buscar equipamento por ID
    public Optional<Equipamento> buscarPorId(Long id) {
        return equipamentoRepository.findById(id);
    }

    // Atualizar equipamento existente
    public Equipamento editarEquipamento(Long id, EquipamentoDTO dto) {
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

        return equipamentoRepository.save(equipamento);
    }

    // Remover equipamento por ID
    public void removerEquipamento(Long id) {
        equipamentoRepository.deleteById(id);
    }

    // Verificar disponibilidade por ID
    public boolean verificarDisponibilidade(Long id) {
        Equipamento equipamento = equipamentoRepository.findById(id).orElse(null);
        return equipamento != null && equipamento.isDisponibilidade();
    }
}