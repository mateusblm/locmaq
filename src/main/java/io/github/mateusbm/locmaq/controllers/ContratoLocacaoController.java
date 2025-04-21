package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.dto.ContratoLocacaoDTO;
import io.github.mateusbm.locmaq.models.ContratoLocacao;
import io.github.mateusbm.locmaq.services.ContratoLocacaoService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ContratoLocacaoDTO cadastrar(@RequestBody ContratoLocacaoDTO dto) {
        ContratoLocacao c = contratoLocacaoService.cadastrar(dto);
        return ContratoLocacaoDTO.fromEntity(c);
    }

    @PutMapping("/{id}")
    public ContratoLocacaoDTO atualizar(@PathVariable Long id, @RequestBody ContratoLocacaoDTO dto) {
        dto.setId(id);
        ContratoLocacao c = contratoLocacaoService.cadastrar(dto);
        return ContratoLocacaoDTO.fromEntity(c);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        contratoLocacaoService.remover(id);
    }
}