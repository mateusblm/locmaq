package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.dto.ContratoLocacaoDTO;
import io.github.mateusbm.locmaq.models.Cliente;
import io.github.mateusbm.locmaq.models.ContratoLocacao;
import io.github.mateusbm.locmaq.models.Equipamento;
import io.github.mateusbm.locmaq.models.Usuario;
import io.github.mateusbm.locmaq.repositories.ClienteRepository;
import io.github.mateusbm.locmaq.repositories.ContratoLocacaoRepository;
import io.github.mateusbm.locmaq.repositories.EquipamentoRepository;
import io.github.mateusbm.locmaq.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContratoLocacaoService {

    @Autowired
    private ContratoLocacaoRepository contratoRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private ClienteRepository clienteRepo;
    @Autowired
    private EquipamentoRepository equipamentoRepo;
    @Autowired
    private ActionLogService actionLogService;

    private String getUsuarioAutenticado() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public List<ContratoLocacao> listarTodos() {
        return contratoRepo.findAll();
    }

    public ContratoLocacao buscarPorId(Long id) {
        return contratoRepo.findById(id).orElse(null);
    }

    public ContratoLocacao cadastrar(ContratoLocacaoDTO dto) {
        Usuario usuario = usuarioRepo.findById(dto.getUsuarioLogisticaId()).orElse(null);
        Cliente cliente = clienteRepo.findById(dto.getClienteId()).orElse(null);
        Equipamento equipamento = equipamentoRepo.findById(dto.getEquipamentoId()).orElse(null);

        // Validação de conflito (não há id ainda, pois é novo)
        List<ContratoLocacao> conflitos = contratoRepo.findByEquipamentoIdAndPeriodExcluding(
                equipamento.getId(),
                dto.getDataInicio(),
                dto.getDataFim(),
                null // null para cadastro!
        );
        if (!conflitos.isEmpty()) {
            throw new IllegalStateException("O equipamento já está reservado para o período informado.");
        }

        ContratoLocacao contrato = dto.toEntity(usuario, cliente, equipamento);
        ContratoLocacao saved = contratoRepo.save(contrato);
        actionLogService.logAction("Cadastro de contrato de locação",
                getUsuarioAutenticado(),
                "Contrato ID: " + saved.getId() + ", Cliente: " + cliente.getNome() +
                        ", Equipamento: " + equipamento.getNome());
        return saved;
    }

    public ContratoLocacao atualizar(Long id, ContratoLocacaoDTO dto) {
        ContratoLocacao contrato = contratoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado"));

        Usuario usuario = usuarioRepo.findById(dto.getUsuarioLogisticaId()).orElse(null);
        Cliente cliente = clienteRepo.findById(dto.getClienteId()).orElse(null);
        Equipamento equipamento = equipamentoRepo.findById(dto.getEquipamentoId()).orElse(null);

        // Validação de conflito ignorando o próprio contrato
        List<ContratoLocacao> conflitos = contratoRepo.findByEquipamentoIdAndPeriodExcluding(
                equipamento.getId(),
                dto.getDataInicio(),
                dto.getDataFim(),
                id // ignora o próprio contrato!
        );
        if (!conflitos.isEmpty()) {
            throw new IllegalStateException("O equipamento já está reservado para o período informado.");
        }

        // Atualiza os campos
        contrato.setUsuarioLogistica(usuario);
        contrato.setCliente(cliente);
        contrato.setEquipamento(equipamento);
        contrato.setDataInicio(dto.getDataInicio());
        contrato.setDataFim(dto.getDataFim());
        contrato.setValorTotal(dto.getValorTotal());
        // ... outros campos, se houver

        ContratoLocacao saved = contratoRepo.save(contrato);
        actionLogService.logAction("Atualização de contrato de locação",
                getUsuarioAutenticado(),
                "Contrato ID: " + saved.getId() + ", Cliente: " + cliente.getNome() +
                        ", Equipamento: " + equipamento.getNome());
        return saved;
    }

    public void remover(Long id) {
        contratoRepo.deleteById(id);
        actionLogService.logAction("Remoção de contrato de locação", getUsuarioAutenticado(), "Contrato ID: " + id);
    }
}