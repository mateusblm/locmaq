package io.github.mateusbm.locmaq.services;


import io.github.mateusbm.locmaq.repositories.BoletimMedicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoletimMedicaoService {
    @Autowired
    private BoletimMedicaoRepository boletimMedicaoRepository;

    public void cadastrarBoletim() {}
    public void editarBoletim() {}
    public void consultarBoletim() {}
}
