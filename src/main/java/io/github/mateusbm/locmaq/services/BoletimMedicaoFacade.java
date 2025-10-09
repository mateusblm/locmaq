package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.dto.BoletimMedicaoDTO;
import io.github.mateusbm.locmaq.models.BoletimMedicao;
import io.github.mateusbm.locmaq.models.Usuario;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * Responsável por simplificar a interface para o subsistema Boletim de Medição,
 * encapsulando a lógica de coordenação entre BoletimMedicaoService e UsuarioService.
 */
@Service
public class BoletimMedicaoFacade {

    private final BoletimMedicaoService boletimService;
    private final UsuarioService usuarioService;

    public BoletimMedicaoFacade(BoletimMedicaoService boletimService, UsuarioService usuarioService) {
        this.boletimService = boletimService;
        this.usuarioService = usuarioService;
    }

    public List<BoletimMedicao> listarTodos() {
        return boletimService.listarTodos();
    }

    public BoletimMedicao buscarPorId(Long id) {
        return boletimService.buscarPorId(id);
    }

    public BoletimMedicao cadastrarBoletim(BoletimMedicaoDTO dto) {
        Usuario planejador = buscarPlanejador(dto.getPlanejadorId());
        return boletimService.cadastrar(dto, planejador);
    }

    public BoletimMedicao editarBoletim(Long id, BoletimMedicaoDTO dto) {
        BoletimMedicao boletimExistente = boletimService.buscarPorId(id);
        if (Boolean.TRUE.equals(boletimExistente.getAssinado())) {
            throw new IllegalStateException("Não é permitido editar um boletim de medição já assinado.");
        }
        Usuario planejador = buscarPlanejador(dto.getPlanejadorId());
        return boletimService.editar(id, dto, planejador);
    }

    public BoletimMedicao assinarBoletim(Long id) {
        return boletimService.assinarBoletim(id);
    }

    public void remover(Long id) {
        boletimService.remover(id);
    }
        
    private Usuario buscarPlanejador(Long planejadorId) {
        try {
            return usuarioService.buscarPorId(planejadorId);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Planejador com ID " + planejadorId + " não encontrado.");
        }
    }
}