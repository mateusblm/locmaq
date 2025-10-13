package io.github.mateusbm.locmaq.adapter;

public interface DocumentValidator {
    //Valida um documento (CPF ou CNPJ) com base no formato e nos dígitos de verificação.
    void validateCpfCnpj(String documento);
}