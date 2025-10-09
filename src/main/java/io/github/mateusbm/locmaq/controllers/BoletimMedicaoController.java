package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.dto.BoletimMedicaoDTO;
import io.github.mateusbm.locmaq.models.BoletimMedicao;
import io.github.mateusbm.locmaq.services.BoletimMedicaoFacade; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boletins")
public class BoletimMedicaoController {
    
    private final BoletimMedicaoFacade facade;

    public BoletimMedicaoController(BoletimMedicaoFacade facade) {
        this.facade = facade;
    }

    @GetMapping
    public ResponseEntity<List<BoletimMedicaoDTO>> listarTodos() {
        List<BoletimMedicaoDTO> lista = facade.listarTodos().stream()
                .map(BoletimMedicaoDTO::fromEntity)
                .toList();
        
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoletimMedicaoDTO> buscar(@PathVariable Long id) {
        BoletimMedicaoDTO dto = BoletimMedicaoDTO.fromEntity(facade.buscarPorId(id));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<BoletimMedicaoDTO> cadastrar(@RequestBody BoletimMedicaoDTO dto) {
        BoletimMedicao b = facade.cadastrarBoletim(dto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(BoletimMedicaoDTO.fromEntity(b));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoletimMedicaoDTO> editar(@PathVariable Long id, @RequestBody BoletimMedicaoDTO dto) {
        BoletimMedicao atualizado = facade.editarBoletim(id, dto);
        
        return ResponseEntity.ok(BoletimMedicaoDTO.fromEntity(atualizado));
    }

    @PostMapping("/{id}/assinar")
    public ResponseEntity<BoletimMedicaoDTO> assinar(@PathVariable Long id) {
        BoletimMedicaoDTO dto = BoletimMedicaoDTO.fromEntity(facade.assinarBoletim(id));
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        facade.remover(id);
        return ResponseEntity.ok().build();
    }
}