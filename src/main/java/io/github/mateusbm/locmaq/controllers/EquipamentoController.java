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
        equipamentoService.cadastrarEquipamento(dto);
        return ResponseEntity.ok().build();
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
        equipamentoService.editarEquipamento(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerEquipamento(@PathVariable Long id) {
        equipamentoService.removerEquipamento(id);
        return ResponseEntity.ok().build();
    }
}