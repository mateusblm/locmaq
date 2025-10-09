package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.dto.ContratoLocacaoDTO;
import io.github.mateusbm.locmaq.events.LogAction;
import io.github.mateusbm.locmaq.models.Cliente;
import io.github.mateusbm.locmaq.models.ContratoLocacao;
import io.github.mateusbm.locmaq.models.Equipamento;
import io.github.mateusbm.locmaq.models.Usuario;
import io.github.mateusbm.locmaq.repositories.ClienteRepository;
import io.github.mateusbm.locmaq.repositories.ContratoLocacaoRepository;
import io.github.mateusbm.locmaq.repositories.EquipamentoRepository;
import io.github.mateusbm.locmaq.repositories.UsuarioRepository;
import io.github.mateusbm.locmaq.strategy.ContratoValidationStrategy; 
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContratoLocacaoService {

    private final ContratoLocacaoRepository contratoRepo;
    private final UsuarioRepository usuarioRepo;
    private final ClienteRepository clienteRepo;
    private final EquipamentoRepository equipamentoRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final List<ContratoValidationStrategy> validationStrategies; 

    public ContratoLocacaoService(
            ContratoLocacaoRepository contratoRepo,
            UsuarioRepository usuarioRepo,
            ClienteRepository clienteRepo,
            EquipamentoRepository equipamentoRepo,
            ApplicationEventPublisher eventPublisher,
            List<ContratoValidationStrategy> validationStrategies 
    ) {
        this.contratoRepo = contratoRepo;
        this.usuarioRepo = usuarioRepo;
        this.clienteRepo = clienteRepo;
        this.equipamentoRepo = equipamentoRepo;
        this.eventPublisher = eventPublisher;
        this.validationStrategies = validationStrategies;
    }

    private String getUsuarioAutenticado() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public List<ContratoLocacao> listarTodos() {
        return contratoRepo.findAll();
    }

    public ContratoLocacao buscarPorId(Long id) {
        return contratoRepo.findById(id).orElse(null);
    }

    /**
     * Loop principal que executa todas as estratégias de validação.
     */
    private void executeValidations(ContratoLocacao contrato, boolean isUpdate) {
        // Executa cada estratégia injetada
        for (ContratoValidationStrategy strategy : validationStrategies) {
            strategy.validate(contrato, isUpdate);
        }
    }

    @Transactional
    public ContratoLocacao cadastrar(ContratoLocacaoDTO dto) {
        Usuario usuario = usuarioRepo.findById(dto.getUsuarioLogisticaId()).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        Cliente cliente = clienteRepo.findById(dto.getClienteId()).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        Equipamento equipamento = equipamentoRepo.findById(dto.getEquipamentoId()).orElseThrow(() -> new IllegalArgumentException("Equipamento não encontrado."));

        ContratoLocacao contrato = dto.toEntity(usuario, cliente, equipamento);
        if (contrato.getStatusPagamento() == null) {
            contrato.setStatusPagamento("PENDENTE");
        }
        
        // Strategy chama o executor de estratégias
        executeValidations(contrato, false); // false = não é atualização

        ContratoLocacao saved = contratoRepo.save(contrato);
        
        eventPublisher.publishEvent(new LogAction(
                "Cadastro de contrato de locação",
                getUsuarioAutenticado(),
                "Contrato ID: " + saved.getId() + ", Cliente: " + cliente.getNome() +
                ", Equipamento: " + equipamento.getNome()
        ));
        return saved;
    }

    @Transactional
    public ContratoLocacao atualizar(Long id, ContratoLocacaoDTO dto) {
        ContratoLocacao contrato = contratoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado"));

        Usuario usuario = usuarioRepo.findById(dto.getUsuarioLogisticaId()).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        Cliente cliente = clienteRepo.findById(dto.getClienteId()).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        Equipamento equipamento = equipamentoRepo.findById(dto.getEquipamentoId()).orElseThrow(() -> new IllegalArgumentException("Equipamento não encontrado."));

        contrato.setUsuarioLogistica(usuario);
        contrato.setCliente(cliente);
        contrato.setEquipamento(equipamento);
        contrato.setDataInicio(dto.getDataInicio());
        contrato.setDataFim(dto.getDataFim());
        contrato.setValorTotal(dto.getValorTotal());
        contrato.setStatusPagamento(dto.getStatusPagamento() != null ? dto.getStatusPagamento() : "PENDENTE");
        
        // Strategy chama o executor de estratégias
        executeValidations(contrato, true); // true = é atualização

        ContratoLocacao saved = contratoRepo.save(contrato);
        
        eventPublisher.publishEvent(new LogAction(
                "Atualização de contrato de locação",
                getUsuarioAutenticado(),
                "Contrato ID: " + saved.getId() + ", Cliente: " + cliente.getNome() +
                ", Equipamento: " + equipamento.getNome()
        ));
        return saved;
    }

    public void remover(Long id) {
        contratoRepo.deleteById(id);
        
        eventPublisher.publishEvent(new LogAction(
                "Remoção de contrato de locação",
                getUsuarioAutenticado(),
                "Contrato ID: " + id
        ));
    }
}