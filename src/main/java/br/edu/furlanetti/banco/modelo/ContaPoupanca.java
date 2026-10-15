package br.edu.furlanetti.banco.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Conta poupança: não permite saldo negativo e rende juros mensais.
 * Demonstra <b>sobrescrita</b> ({@code @Override}) do comportamento de fim de mês.
 */
public class ContaPoupanca extends Conta {

    private final BigDecimal taxaMensal;

    /** @param taxaMensal ex.: 0.005 para 0,5% ao mês */
    public ContaPoupanca(Cliente titular, BigDecimal taxaMensal) {
        super(titular);
        this.taxaMensal = taxaMensal;
    }

    @Override
    protected BigDecimal getLimiteDisponivel() {
        return BigDecimal.ZERO;
    }

    @Override
    public void processarFimDoMes() {
        BigDecimal rendimento = getSaldo().multiply(taxaMensal).setScale(2, RoundingMode.HALF_EVEN);
        if (rendimento.signum() > 0) {
            creditar(TipoTransacao.RENDIMENTO, rendimento, "Rendimento mensal");
        }
    }

    @Override
    public String getTipo() {
        return "Conta Poupança";
    }
}
