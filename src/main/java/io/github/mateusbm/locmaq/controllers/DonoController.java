// src/main/java/io/github/mateusbm/locmaq/controllers/DonoController.java
package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.models.Dono;
import io.github.mateusbm.locmaq.services.DonoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
        try {
            donoService.salvar(dono);
            return ResponseEntity.ok("Proprietário cadastrado com sucesso!");
        }catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Já existe um proprietário com esse CPF ou CNPJ.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Dono dono) {
        try {
            donoService.editar(id, dono);
            return ResponseEntity.ok("Proprietário atualizado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Long id) {
        try {
            donoService.remover(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}