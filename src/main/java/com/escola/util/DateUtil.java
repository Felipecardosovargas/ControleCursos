package com.escola.util;

import java.time.LocalDate;
import java.time.Period;

/**
 * Classe utilitária para operações relacionadas a datas.
 * Fornece métodos para calcular a diferença entre datas e outras funcionalidades relacionadas a tempo.
 * Esta classe é final, pois não se pretende que seja estendida.
 */
public final class DateUtil {

    /**
     * Construtor privado para evitar a instanciação da classe utilitária.
     */
    private DateUtil() {
        // Classe utilitária não deve ser instanciada.
    }

    /**
     * Calcula a idade em anos entre uma data de nascimento e a data atual.
     *
     * @param dataNascimento A data de nascimento.
     * @return A idade em anos.
     * @throws IllegalArgumentException Se a data de nascimento for nula ou futura.
     */
    public static int calcularIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            throw new IllegalArgumentException("A data de nascimento não pode ser nula.");
        }
        LocalDate hoje = LocalDate.now();
        if (dataNascimento.isAfter(hoje)) {
            throw new IllegalArgumentException("A data de nascimento não pode ser futura.");
        }
        return Period.between(dataNascimento, hoje).getYears();
    }

    /**
     * Verifica se uma data está dentro dos últimos dias especificados a partir da data atual.
     *
     * @param data        A data a ser verificada.
     * @param diasAnteriores O número de dias anteriores a serem considerados.
     * @return {@code true} se a data estiver dentro do período, {@code false} caso contrário.
     * @throws IllegalArgumentException Se a data for nula ou o número de dias anteriores for negativo.
     */
    public static boolean isDentroDosUltimosDias(LocalDate data, int diasAnteriores) {
        if (data == null) {
            throw new IllegalArgumentException("A data não pode ser nula.");
        }
        if (diasAnteriores < 0) {
            throw new IllegalArgumentException("O número de dias anteriores não pode ser negativo.");
        }
        LocalDate hoje = LocalDate.now();
        LocalDate dataLimite = hoje.minusDays(diasAnteriores);
        return !data.isBefore(dataLimite) && !data.isAfter(hoje);
    }

    // Adicionar outros métodos utilitários relacionados a datas conforme necessário.
}