package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.dto.EquipamentoDTO;
import io.github.mateusbm.locmaq.models.Equipamento;
import io.github.mateusbm.locmaq.repositories.EquipamentoRepository;
import io.github.mateusbm.locmaq.services.EquipamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipamentos")
public class EquipamentoController {

    @Autowired
    private EquipamentoService equipamentoService;

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @PostMapping
    public ResponseEntity<?> cadastrarEquipamento(@RequestBody EquipamentoDTO dto) {
        try {
            equipamentoService.cadastrarEquipamento(dto);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            String mensagem = ex.getClass().getName() + ": " + ex.getMessage();
            return ResponseEntity.badRequest().body(new ErroResponse(mensagem));
        }
    }

    @GetMapping
    public List<Equipamento> listarTodos() {
        return equipamentoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipamento> buscarPorId(@PathVariable Long id) {
        return equipamentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarEquipamento(@PathVariable Long id,
                                               @RequestBody EquipamentoDTO dto) {
        try {
            equipamentoService.editarEquipamento(id, dto);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            String mensagem = ex.getClass().getName() + ": " + ex.getMessage();
            return ResponseEntity.badRequest().body(new ErroResponse(mensagem));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerEquipamento(@PathVariable Long id) {
        try {
            equipamentoService.removerEquipamento(id);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (msg != null && (msg.contains("ConstraintViolationException") || msg.contains("a foreign key constraint fails"))) {
                msg = "Não é possível excluir este equipamento pois ele está vinculado a um boletim de medição.";
            } else {
                msg = "Erro ao excluir equipamento.";
            }
            return ResponseEntity.badRequest().body(new ErroResponse(msg));
        }
    }

    // Classe auxiliar para resposta de erro
    class ErroResponse {
        private String mensagem;
        public ErroResponse(String mensagem) { this.mensagem = mensagem; }
        public String getMensagem() { return mensagem; }
        public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    }
}

