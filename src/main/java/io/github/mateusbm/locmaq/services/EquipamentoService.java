package io.github.mateusbm.locmaq.services;


import io.github.mateusbm.locmaq.repositories.EquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EquipamentoService {

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    public void cadastrarEquipamento(){
    }

    public void editarEquipamento() {}

    public void verificarDisponibilidade() {}
}
