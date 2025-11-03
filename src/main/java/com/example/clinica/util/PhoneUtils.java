package com.example.clinica.util;

public class PhoneUtils {
    /** Retorna apenas os dígitos de uma string (ou null se input for null) */
    public static String onlyDigits(String input) {
        if (input == null) return null;
        return input.replaceAll("\\D", "");
    }

    /**
     * Formata string de dígitos para (##) ####-#### ou (##) #####-#### dependendo do tamanho (10 ou 11).
     * Se entrada for null ou vazia, retorna string vazia.
     */
    public static String format(String digits) {
        if (digits == null) return "";
        String d = onlyDigits(digits);
        if (d.isEmpty()) return "";
        if (d.length() == 10) {
            return String.format("(%s) %s-%s", d.substring(0,2), d.substring(2,6), d.substring(6));
        } else if (d.length() == 11) {
            return String.format("(%s) %s-%s", d.substring(0,2), d.substring(2,7), d.substring(7));
        } else {
            // se tamanho inesperado, retorna apenas os dígitos
            return d;
        }
    }
}
