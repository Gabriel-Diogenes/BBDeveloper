package com.intercomex.api_bbdeveloper.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ExtratoUtil {

    private static final DateTimeFormatter SAIDA_BB = DateTimeFormatter.ofPattern("ddMMyyyy");

    private ExtratoUtil() {
    }

    public static String formatarAgencia(String agencia) {
        if (agencia == null || agencia.isBlank()) {
            throw new IllegalArgumentException("Agência é obrigatória.");
        }
        String numeros = agencia.replaceAll("\\D", "");
        if (numeros.isBlank()) {
            throw new IllegalArgumentException("Agência inválida.");
        }
        return String.valueOf(Integer.parseInt(numeros));
    }

    public static String formatarConta(String conta) {
        if (conta == null || conta.isBlank()) {
            throw new IllegalArgumentException("Conta é obrigatória.");
        }
        String numeros = conta.replaceAll("\\D", "");
        if (numeros.isBlank()) {
            throw new IllegalArgumentException("Conta inválida.");
        }
        return String.valueOf(Integer.parseInt(numeros));
    }

    /**
     * Converte datas comuns (dd.MM.yyyy, dd/MM/yyyy, yyyy-MM-dd ou DDMMAAAA) para o padrão do BB.
     */
    public static String formatarDataSolicitacao(String data) {
        if (data == null || data.isBlank()) {
            throw new IllegalArgumentException("Data é obrigatória.");
        }

        String valor = data.trim();
        if (valor.matches("\\d{8}")) {
            return valor;
        }

        LocalDate localDate = parseData(valor);
        return localDate.format(SAIDA_BB);
    }

    private static LocalDate parseData(String valor) {
        String normalizado = valor.replace('/', '.');
        String[] formatos = {"dd.MM.yyyy", "d.M.yyyy", "yyyy-MM-dd"};

        for (String formato : formatos) {
            try {
                return LocalDate.parse(normalizado, DateTimeFormatter.ofPattern(formato));
            } catch (DateTimeParseException ignored) {
                // tenta próximo formato
            }
        }

        throw new IllegalArgumentException(
                "Data inválida: '" + valor + "'. Use dd.MM.yyyy, dd/MM/yyyy, yyyy-MM-dd ou DDMMAAAA.");
    }
}
