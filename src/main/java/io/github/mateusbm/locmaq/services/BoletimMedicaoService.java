package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.dto.BoletimMedicaoDTO;
import io.github.mateusbm.locmaq.models.BoletimMedicao;
import io.github.mateusbm.locmaq.models.Equipamento;
import io.github.mateusbm.locmaq.models.EquipamentoBoletimMedicao;
import io.github.mateusbm.locmaq.models.Usuario;
import io.github.mateusbm.locmaq.repositories.BoletimMedicaoRepository;
import io.github.mateusbm.locmaq.repositories.EquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BoletimMedicaoService {
    @Autowired
    private BoletimMedicaoRepository repo;
    @Autowired
    private EquipamentoRepository equipamentoRepository;
    @Autowired
    private ActionLogService actionLogService;

    private String getUsuarioAutenticado() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public List<BoletimMedicao> listarTodos() {
        return repo.findAll();
    }

    public BoletimMedicao buscarPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public BoletimMedicao cadastrar(BoletimMedicaoDTO dto, Usuario planejador) {
        List<EquipamentoBoletimMedicao> equipamentos = new ArrayList<>();
        if (dto.getEquipamentos() != null && !dto.getEquipamentos().isEmpty()) {
            for (var eqDTO : dto.getEquipamentos()) {
                Equipamento eq = equipamentoRepository.findById(eqDTO.getEquipamentoId()).orElseThrow();
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
        BoletimMedicao saved = repo.save(boletim);
        actionLogService.logAction(
                "Cadastro de boletim de medição",
                getUsuarioAutenticado(),
                "Boletim ID: " + saved.getId() +
                        ", Período: " + saved.getPeriodo() +
                        ", Situação: " + saved.getSituacao()
        );
        return saved;
    }

    public BoletimMedicao assinarBoletim(Long id) {
        BoletimMedicao b = buscarPorId(id);
        b.setAssinado(true);
        b.setSituacao("ASSINADO");
        BoletimMedicao saved = repo.save(b);
        actionLogService.logAction("Assinatura de boletim de medição", getUsuarioAutenticado(), "Boletim ID: " + saved.getId());
        return saved;
    }
}