package io.github.mateusbm.locmaq.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import io.github.mateusbm.locmaq.models.ContratoLocacao;
import io.github.mateusbm.locmaq.models.Orcamento;

public class ValidadorUtil {

    public static boolean isCpfOuCnpjValido(String documento) {
        if (documento == null || (documento.length() != 11 && documento.length() != 14)) return false;
        return documento.length() == 11 ? isCpfValido(documento) : isCnpjValido(documento);
    }

    public static boolean isCpfValido(String cpf) {
        if (cpf == null || cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int digito1 = 11 - (soma % 11);
            if (digito1 >= 10) digito1 = 0;

            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int digito2 = 11 - (soma % 11);
            if (digito2 >= 10) digito2 = 0;

            return digito1 == Character.getNumericValue(cpf.charAt(9)) && digito2 == Character.getNumericValue(cpf.charAt(10));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isCnpjValido(String cnpj) {
        if (cnpj == null || cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}")) return false;

        try {
            int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int soma = 0;

            for (int i = 0; i < 12; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * pesos1[i];
            }
            int digito1 = 11 - (soma % 11);
            if (digito1 >= 10) digito1 = 0;

            soma = 0;
            for (int i = 0; i < 13; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * pesos2[i];
            }
            int digito2 = 11 - (soma % 11);
            if (digito2 >= 10) digito2 = 0;

            return digito1 == Character.getNumericValue(cnpj.charAt(12)) && digito2 == Character.getNumericValue(cnpj.charAt(13));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isAgenciaValida(String agencia) {
        return agencia != null && agencia.matches("\\d{4}");
    }

    public static boolean isNumeroContaValido(String numeroConta) {
        return numeroConta != null && numeroConta.matches("\\d{6}-\\d{1}");
    }

    public static void validarDesconto(Orcamento orcamento, ContratoLocacao contrato) {
        if(orcamento.getDesconto() > orcamento.calcularValorCliente(contrato) - (orcamento.calcularValorCliente(contrato) * orcamento.getTaxaLucro() / 100.0)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O desconto não pode ser maior que o valor total do aluguel menos a taxa de lucro (R$ " +
                            String.format("%.2f", orcamento.calcularValorCliente(contrato) - (orcamento.calcularValorCliente(contrato) * orcamento.getTaxaLucro() / 100.0)) + ").");
        }
    }

    public static boolean isEmailValido(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}