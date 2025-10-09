package io.github.mateusbm.locmaq.state;

import io.github.mateusbm.locmaq.models.BoletimMedicao;

public class AssinadoState implements BoletimState {

    public static final String SITUACAO_NOME = "ASSINADO";

    @Override
    public void editar(BoletimMedicao boletim) {
        throw new IllegalStateException("Não é permitido editar um boletim de medição já assinado.");
    }

    @Override
    public void assinar(BoletimMedicao boletim) {
        throw new IllegalStateException("O boletim de medição já está assinado.");
    }

    @Override
    public void remover(BoletimMedicao boletim) {
        throw new IllegalStateException("Não é permitido remover um boletim de medição já assinado.");
    }
    
    @Override
    public String getSituacao() {
        return SITUACAO_NOME;
    }
}