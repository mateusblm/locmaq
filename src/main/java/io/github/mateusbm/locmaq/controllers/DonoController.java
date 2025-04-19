package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.models.Dono;
import io.github.mateusbm.locmaq.services.DonoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donos")
public class DonoController {
    @Autowired
    private DonoService donoService;

    @GetMapping
    public List<Dono> listarDonos() {
        return donoService.listarTodosDonos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dono> buscarPorId(@PathVariable Long id) {
        return donoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Dono dono) {
        donoService.salvar(dono);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Dono dono) {
        donoService.editar(id, dono);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Long id) {
        donoService.remover(id);
        return ResponseEntity.ok().build();
    }
}