package io.github.mateusbm.locmaq.state;

import io.github.mateusbm.locmaq.models.BoletimMedicao;

public class RascunhoState implements BoletimState {
    public static final String SITUACAO_NOME = "RASCUNHO";
    @Override
    public void editar(BoletimMedicao boletim) {
    }

    @Override
    public void assinar(BoletimMedicao boletim) {
        boletim.setAssinado(true);
        boletim.setSituacao(AssinadoState.SITUACAO_NOME); 
    }

    @Override
    public void remover(BoletimMedicao boletim) {
    }

    @Override
    public String getSituacao() {
        return "RASCUNHO";
    }
}