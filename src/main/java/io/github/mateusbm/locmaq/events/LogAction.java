package io.github.mateusbm.locmaq.events;

/**
 * Representa o evento que deve ser logado.
 * Record simples para carregar os dados do evento (Action, User e Details).
 */
public record LogAction(String action, String user, String details) {
}
