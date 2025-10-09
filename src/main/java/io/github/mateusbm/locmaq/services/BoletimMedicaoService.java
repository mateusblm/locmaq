package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.dto.BoletimMedicaoDTO;
import io.github.mateusbm.locmaq.events.LogAction; // Import do Evento
import io.github.mateusbm.locmaq.models.BoletimMedicao;
import io.github.mateusbm.locmaq.models.Equipamento;
import io.github.mateusbm.locmaq.models.EquipamentoBoletimMedicao;
import io.github.mateusbm.locmaq.models.Usuario;
import io.github.mateusbm.locmaq.repositories.BoletimMedicaoRepository;
import io.github.mateusbm.locmaq.repositories.EquipamentoRepository;
import io.github.mateusbm.locmaq.state.BoletimStateFactory;
import io.github.mateusbm.locmaq.state.RascunhoState;
import org.springframework.context.ApplicationEventPublisher; // Novo Import
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BoletimMedicaoService {
    
    private final BoletimMedicaoRepository repo;
    private final EquipamentoRepository equipamentoRepository;
    private final ApplicationEventPublisher eventPublisher; 

    public BoletimMedicaoService(
            BoletimMedicaoRepository repo,
            EquipamentoRepository equipamentoRepository,
            ApplicationEventPublisher eventPublisher 
    ) {
        this.repo = repo;
        this.equipamentoRepository = equipamentoRepository;
        this.eventPublisher = eventPublisher;
    }

    private String getUsuarioAutenticado() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public List<BoletimMedicao> listarTodos() {
        return repo.findAll();
    }

    public BoletimMedicao buscarPorId(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Boletim não encontrado"));
    }

    public BoletimMedicao cadastrar(BoletimMedicaoDTO dto, Usuario planejador) {
        if (dto.getDataFim() != null && dto.getDataInicio() != null && dto.getDataFim().isBefore(dto.getDataInicio())) {
            throw new IllegalArgumentException("A data final não pode ser anterior à data inicial.");
        }

        List<EquipamentoBoletimMedicao> equipamentos = new ArrayList<>();
        if (dto.getEquipamentos() != null && !dto.getEquipamentos().isEmpty()) {
            for (var eqDTO : dto.getEquipamentos()) {
                Equipamento eq = equipamentoRepository.findById(eqDTO.getEquipamentoId())
                        .orElseThrow(() -> new EntityNotFoundException("Equipamento com ID " + eqDTO.getEquipamentoId() + " não encontrado."));
                
                Double valorMedido = eqDTO.getValorMedido();
                if (valorMedido == null || Double.isNaN(valorMedido)) valorMedido = 0.0;
                EquipamentoBoletimMedicao ebm = eqDTO.toEntity(eq);
                ebm.setValorMedido(valorMedido);
                equipamentos.add(ebm);
            }
        }

        BoletimMedicao boletim = dto.toEntity(planejador, equipamentos);
        for (EquipamentoBoletimMedicao e : equipamentos) {
            e.setBoletimMedicao(boletim);
        }
        boletim.setEquipamentos(equipamentos);

        if (boletim.getDataMedicao() == null) boletim.setDataMedicao(LocalDate.now());
        
        boletim.setSituacao(RascunhoState.SITUACAO_NOME); 
        
        BoletimMedicao saved = repo.save(boletim);
        
        eventPublisher.publishEvent(new LogAction(
                "Cadastro de boletim de medição",
                getUsuarioAutenticado(),
                "Boletim ID: " + saved.getId() + ", Situação: " + saved.getSituacao()
        ));
        return saved;
    }

    public BoletimMedicao editar(Long id, BoletimMedicaoDTO dto, Usuario planejador) {
        if (dto.getDataFim() != null && dto.getDataInicio() != null && dto.getDataFim().isBefore(dto.getDataInicio())) {
            throw new IllegalArgumentException("A data final não pode ser anterior à data inicial.");
        }

        BoletimMedicao boletim = buscarPorId(id);

        BoletimStateFactory.getState(boletim.getSituacao()).editar(boletim); 

        boletim.setDataInicio(dto.getDataInicio());
        boletim.setDataFim(dto.getDataFim());
        boletim.setPlanejador(planejador);

        boletim.getEquipamentos().clear();
        if (dto.getEquipamentos() != null && !dto.getEquipamentos().isEmpty()) {
            for (var eqDTO : dto.getEquipamentos()) {
                Equipamento eq = equipamentoRepository.findById(eqDTO.getEquipamentoId())
                        .orElseThrow(() -> new EntityNotFoundException("Equipamento com ID " + eqDTO.getEquipamentoId() + " não encontrado."));
                
                Double valorMedido = eqDTO.getValorMedido();
                if (valorMedido == null || Double.isNaN(valorMedido)) valorMedido = 0.0;
                EquipamentoBoletimMedicao ebm = eqDTO.toEntity(eq);
                ebm.setValorMedido(valorMedido);
                ebm.setBoletimMedicao(boletim);
                boletim.getEquipamentos().add(ebm);
            }
        }

        BoletimMedicao saved = repo.save(boletim);
        
        eventPublisher.publishEvent(new LogAction(
                "Edição de boletim de medição",
                getUsuarioAutenticado(),
                "Boletim ID: " + saved.getId() + ", Situação: " + saved.getSituacao()
        ));
        return saved;
    }

    public BoletimMedicao assinarBoletim(Long id) {
        BoletimMedicao b = buscarPorId(id);
        
        BoletimStateFactory.getState(b.getSituacao()).assinar(b);
        
        BoletimMedicao saved = repo.save(b);
        
        eventPublisher.publishEvent(new LogAction(
                "Assinatura de boletim de medição", 
                getUsuarioAutenticado(), 
                "Boletim ID: " + saved.getId()
        ));
        return saved;
    }

    public void remover(Long id) {
        BoletimMedicao b = buscarPorId(id);
        
        BoletimStateFactory.getState(b.getSituacao()).remover(b);
        
        repo.delete(b);
        
        eventPublisher.publishEvent(new LogAction(
                "Remoção de boletim de medição", 
                getUsuarioAutenticado(), 
                "Boletim ID: " + id + ", Situação Anterior: " + b.getSituacao()
        ));
    }
}