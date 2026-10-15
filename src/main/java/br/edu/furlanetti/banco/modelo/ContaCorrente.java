package br.edu.furlanetti.banco.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Conta corrente: possui cheque especial (limite) e paga tarifa mensal.
 * Implementa {@link Tributavel}: incide IOF simplificado sobre o saldo devedor.
 */
public class ContaCorrente extends Conta implements Tributavel {

    public static final BigDecimal TARIFA_MENSAL = new BigDecimal("19.90");
    private static final BigDecimal ALIQUOTA_IOF = new BigDecimal("0.0038");

    private final BigDecimal limite;

    public ContaCorrente(Cliente titular, BigDecimal limite) {
        super(titular);
        if (limite == null || limite.signum() < 0) {
            throw new IllegalArgumentException("Limite não pode ser negativo");
        }
        this.limite = limite;
    }

    @Override
    protected BigDecimal getLimiteDisponivel() {
        return limite;
    }

    @Override
    public void processarFimDoMes() {
        debitar(TipoTransacao.TARIFA, TARIFA_MENSAL, "Tarifa de manutenção");
        BigDecimal iof = calcularTributo();
        if (iof.signum() > 0) {
            debitar(TipoTransacao.TARIFA, iof, "IOF sobre saldo devedor");
        }
    }

    @Override
    public BigDecimal calcularTributo() {
        if (getSaldo().signum() >= 0) {
            return BigDecimal.ZERO;
        }
        return getSaldo().abs().multiply(ALIQUOTA_IOF).setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public String getTipo() {
        return "Conta Corrente";
    }

    public BigDecimal getLimite() {
        return limite;
    }
}
