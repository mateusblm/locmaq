package io.github.mateusbm.locmaq.state;

import io.github.mateusbm.locmaq.models.BoletimMedicao;

/**
 * Define a interface para as operações baseadas no estado atual do Boletim.
 */
public interface BoletimState {
    
    void editar(BoletimMedicao boletim); 
    
    void assinar(BoletimMedicao boletim); 
    
    void remover(BoletimMedicao boletim);

    String getSituacao();
}