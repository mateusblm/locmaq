package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.models.Reserva;
import io.github.mateusbm.locmaq.services.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping("/equipamentos/{equipamentoId}")
    public List<Reserva> listarReservasPorEquipamento(@PathVariable Long equipamentoId) {
        return reservaService.listarReservasPorEquipamento(equipamentoId);
    }
}