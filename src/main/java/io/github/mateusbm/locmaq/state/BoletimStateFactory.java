package io.github.mateusbm.locmaq.state;

import java.util.HashMap;
import java.util.Map;

public class BoletimStateFactory {
    private static final Map<String, BoletimState> STATES = new HashMap<>();

    static {
        STATES.put(AssinadoState.SITUACAO_NOME, new AssinadoState());
        STATES.put(RascunhoState.SITUACAO_NOME, new RascunhoState());
    }

    public static BoletimState getState(String situacao) {
        return switch (situacao.toUpperCase()) {
            case "ASSINADO" -> new AssinadoState();
            case "RASCUNHO" -> new RascunhoState();
            default -> new RascunhoState(); 
        };
    }
}