package io.github.mateusbm.locmaq.services;


import io.github.mateusbm.locmaq.repositories.ContratoLocacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContratoLocacaoService {

    @Autowired
    private ContratoLocacaoRepository contratoLocacaoRepository



    public void criarContrato() {}
    public void editarContrato() {}
    public void cancelarContrato() {}
    public void calcularValorTotal() {}
}
