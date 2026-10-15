package br.edu.furlanetti.banco.excecao;

import java.math.BigDecimal;

/** Exceção específica: herança permite tratar de forma genérica ou específica. */
public class SaldoInsuficienteException extends OperacaoInvalidaException {

    private static final long serialVersionUID = 1L;

    private final BigDecimal disponivel;

    public SaldoInsuficienteException(BigDecimal disponivel, BigDecimal solicitado) {
        super("Saldo insuficiente: disponível R$ " + disponivel + ", solicitado R$ " + solicitado);
        this.disponivel = disponivel;
    }

    public BigDecimal getDisponivel() {
        return disponivel;
    }
}
