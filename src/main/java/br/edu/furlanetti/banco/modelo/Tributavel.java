package br.edu.furlanetti.banco.modelo;

import java.math.BigDecimal;

/**
 * Interface: um contrato que classes sem parentesco podem cumprir.
 * Tanto contas quanto, futuramente, seguros ou investimentos podem ser tributáveis.
 */
public interface Tributavel {

    BigDecimal calcularTributo();

    /** Método default (Java 8+): comportamento comum já implementado. */
    default boolean isIsento() {
        return calcularTributo().signum() == 0;
    }
}
