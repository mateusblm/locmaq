package io.github.mateusbm.locmaq.strategy;

import io.github.mateusbm.locmaq.models.ContratoLocacao;

/**
 * Define a interface para todas as estratégias de validação.
 */
public interface ContratoValidationStrategy {
    
    /**
     * @param contrato O contrato a ser validado.
     * @param isUpdate Indica se a validação é para uma atualização (true) ou cadastro (false).
     */
    void validate(ContratoLocacao contrato, boolean isUpdate);
}