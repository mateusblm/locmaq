package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.dto.ContratoLocacaoDTO;
import io.github.mateusbm.locmaq.models.ContratoLocacao;
import io.github.mateusbm.locmaq.services.ContratoLocacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contrato-locacoes")
public class ContratoLocacaoController {

    @Autowired
    private ContratoLocacaoService contratoLocacaoService;

    @GetMapping
    public List<ContratoLocacaoDTO> listarTodos() {
        return contratoLocacaoService.listarTodos().stream()
                .map(ContratoLocacaoDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ContratoLocacaoDTO buscarPorId(@PathVariable Long id) {
        ContratoLocacao c = contratoLocacaoService.buscarPorId(id);
        if (c == null) return null;
        return ContratoLocacaoDTO.fromEntity(c);
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody ContratoLocacaoDTO dto) {
        try {
            ContratoLocacao c = contratoLocacaoService.cadastrar(dto);
            return ResponseEntity.ok(ContratoLocacaoDTO.fromEntity(c));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody ContratoLocacaoDTO dto) {
        try {
            dto.setId(id);
            ContratoLocacao c = contratoLocacaoService.cadastrar(dto);
            return ResponseEntity.ok(ContratoLocacaoDTO.fromEntity(c));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        contratoLocacaoService.remover(id);
    }
}