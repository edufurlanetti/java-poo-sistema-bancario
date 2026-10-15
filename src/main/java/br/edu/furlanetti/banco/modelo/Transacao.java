package br.edu.furlanetti.banco.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Registro imutável de uma movimentação no extrato. */
public record Transacao(TipoTransacao tipo, BigDecimal valor, LocalDateTime dataHora, String descricao) {

    /** Valor com sinal: positivo para créditos, negativo para débitos. */
    public BigDecimal valorComSinal() {
        return valor.multiply(BigDecimal.valueOf(tipo.getSinal()));
    }
}
