package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.dto.EspelhoFaturamentoDTO;
import io.github.mateusbm.locmaq.services.EspelhoFaturamentoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/faturamento")
public class EspelhoFaturamentoController {

    private final EspelhoFaturamentoService service;

    public EspelhoFaturamentoController(EspelhoFaturamentoService service) {
        this.service = service;
    }

    @GetMapping("/espelho")
    public List<EspelhoFaturamentoDTO> getEspelho() {
        return service.gerarEspelho();
    }
}