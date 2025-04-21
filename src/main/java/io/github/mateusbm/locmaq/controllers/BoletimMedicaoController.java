package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.dto.BoletimMedicaoDTO;
import io.github.mateusbm.locmaq.models.BoletimMedicao;
import io.github.mateusbm.locmaq.models.Usuario;
import io.github.mateusbm.locmaq.services.BoletimMedicaoService;
import io.github.mateusbm.locmaq.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boletins")
public class BoletimMedicaoController {
    @Autowired
    private BoletimMedicaoService service;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<BoletimMedicaoDTO> listarTodos() {
        return service.listarTodos().stream().map(BoletimMedicaoDTO::fromEntity).toList();
    }

    @GetMapping("/{id}")
    public BoletimMedicaoDTO buscar(@PathVariable Long id) {
        return BoletimMedicaoDTO.fromEntity(service.buscarPorId(id));
    }

    @PostMapping
    public BoletimMedicaoDTO cadastrar(@RequestBody BoletimMedicaoDTO dto) {
        Usuario planejador = usuarioService.buscarPorId(dto.getPlanejadorId());
        BoletimMedicao b = service.cadastrar(dto, planejador);
        return BoletimMedicaoDTO.fromEntity(b);
    }

    @PostMapping("/{id}/assinar")
    public BoletimMedicaoDTO assinar(@PathVariable Long id) {
        return BoletimMedicaoDTO.fromEntity(service.assinarBoletim(id));
    }
}