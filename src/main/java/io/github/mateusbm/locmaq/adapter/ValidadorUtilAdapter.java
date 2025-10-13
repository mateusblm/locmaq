package io.github.mateusbm.locmaq.adapter;

import io.github.mateusbm.locmaq.utils.ValidadorUtil;
import org.springframework.stereotype.Component;


 //Adapta a classe ValidadorUtil para a interface DocumentValidator.
@Component
public class ValidadorUtilAdapter implements DocumentValidator {

    @Override
    public void validateCpfCnpj(String documento) {
        String doc = documento.replaceAll("\\D", "");
        
        if (doc.length() == 11) {
            if (!ValidadorUtil.isCpfValido(doc)) {
                throw new IllegalArgumentException("CPF inválido");
            }
        } else if (doc.length() == 14) {
            if (!ValidadorUtil.isCnpjValido(doc)) {
                throw new IllegalArgumentException("CNPJ inválido");
            }
        } else {
            throw new IllegalArgumentException("CPF/CNPJ deve ter 11 ou 14 dígitos");
        }
    }
}