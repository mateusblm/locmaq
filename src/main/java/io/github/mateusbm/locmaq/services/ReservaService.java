package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.models.Reserva;
import io.github.mateusbm.locmaq.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    public List<Reserva> listarReservasPorEquipamento(Long equipamentoId) {
        return reservaRepository.findByEquipamentoId(equipamentoId);
    }

    public Reserva salvarReserva(Reserva reserva) {
        return reservaRepository.save(reserva);
    }
}
