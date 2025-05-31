package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.dto.BoletimMedicaoDTO;
import io.github.mateusbm.locmaq.models.BoletimMedicao;
import io.github.mateusbm.locmaq.models.Usuario;
import io.github.mateusbm.locmaq.services.BoletimMedicaoService;
import io.github.mateusbm.locmaq.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/boletins")
public class BoletimMedicaoController {
    @Autowired
    private BoletimMedicaoService service;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<?> listarTodos() {
        try {
            List<BoletimMedicaoDTO> lista = service.listarTodos().stream().map(BoletimMedicaoDTO::fromEntity).toList();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao listar boletins.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            BoletimMedicaoDTO dto = BoletimMedicaoDTO.fromEntity(service.buscarPorId(id));
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Boletim não encontrado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar boletim.");
        }
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody BoletimMedicaoDTO dto) {
        try {
            Usuario planejador = usuarioService.buscarPorId(dto.getPlanejadorId());
            BoletimMedicao b = service.cadastrar(dto, planejador);
            return ResponseEntity.status(HttpStatus.CREATED).body(BoletimMedicaoDTO.fromEntity(b));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Planejador não encontrado.");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos ou violação de integridade.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cadastrar boletim.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody BoletimMedicaoDTO dto) {
        try {
            BoletimMedicao boletim = service.buscarPorId(id);
            if (Boolean.TRUE.equals(boletim.getAssinado())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Não é permitido editar um boletim de medição já assinado.");
            }
            Usuario planejador = usuarioService.buscarPorId(dto.getPlanejadorId());
            BoletimMedicao atualizado = service.editar(id, dto, planejador);
            return ResponseEntity.ok(BoletimMedicaoDTO.fromEntity(atualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Boletim ou planejador não encontrado.");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos ou violação de integridade.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao editar boletim.");
        }
    }

    @PostMapping("/{id}/assinar")
    public ResponseEntity<?> assinar(@PathVariable Long id) {
        try {
            BoletimMedicaoDTO dto = BoletimMedicaoDTO.fromEntity(service.assinarBoletim(id));
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Boletim não encontrado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao assinar boletim.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Long id) {
        try {
            service.remover(id);
            return ResponseEntity.ok("Boletim removido com sucesso!");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Boletim não encontrado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao remover boletim.");
        }
    }
}