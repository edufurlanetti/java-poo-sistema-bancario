package br.edu.furlanetti.banco.modelo;

import br.edu.furlanetti.banco.excecao.OperacaoInvalidaException;
import br.edu.furlanetti.banco.excecao.SaldoInsuficienteException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Classe abstrata base de todas as contas.
 *
 * <p>Conceitos de POO demonstrados:</p>
 * <ul>
 *   <li><b>Encapsulamento</b>: o saldo é privado e só muda por métodos que validam as regras;</li>
 *   <li><b>Abstração</b>: não existe "conta genérica", por isso a classe é {@code abstract};</li>
 *   <li><b>Template Method</b>: {@link #sacar} define o algoritmo e delega a regra de limite
 *       às subclasses por meio de {@link #getLimiteDisponivel()};</li>
 *   <li><b>Composição</b>: a conta "tem um" cliente e "tem uma" lista de transações.</li>
 * </ul>
 *
 * <p>Valores monetários usam {@link BigDecimal}: {@code double} gera erros de arredondamento
 * (ex.: {@code 0.1 + 0.2 = 0.30000000000000004}).</p>
 */
public abstract class Conta {

    private static int proximoNumero = 1;

    private final int numero;
    private final Cliente titular;
    private BigDecimal saldo = BigDecimal.ZERO.setScale(2);
    private final List<Transacao> extrato = new ArrayList<>();

    protected Conta(Cliente titular) {
        this.titular = Objects.requireNonNull(titular, "Titular é obrigatório");
        this.numero = proximoNumero++;
    }

    public void depositar(BigDecimal valor) {
        validarValor(valor);
        creditar(TipoTransacao.DEPOSITO, valor, "Depósito");
    }

    public final void sacar(BigDecimal valor) {
        validarValor(valor);
        BigDecimal disponivel = saldo.add(getLimiteDisponivel());
        if (valor.compareTo(disponivel) > 0) {
            throw new SaldoInsuficienteException(disponivel, valor);
        }
        debitar(TipoTransacao.SAQUE, valor, "Saque");
    }

    /** Quanto a conta pode ficar negativa. Cada tipo de conta define a sua regra. */
    protected abstract BigDecimal getLimiteDisponivel();

    /** Rotina executada no fim do mês (tarifa, rendimento...). */
    public abstract void processarFimDoMes();

    public abstract String getTipo();

    // ---- operações internas: protegidas para uso das subclasses e do pacote ----

    protected void creditar(TipoTransacao tipo, BigDecimal valor, String descricao) {
        saldo = saldo.add(valor).setScale(2, RoundingMode.HALF_EVEN);
        extrato.add(new Transacao(tipo, valor.setScale(2, RoundingMode.HALF_EVEN), LocalDateTime.now(), descricao));
    }

    protected void debitar(TipoTransacao tipo, BigDecimal valor, String descricao) {
        saldo = saldo.subtract(valor).setScale(2, RoundingMode.HALF_EVEN);
        extrato.add(new Transacao(tipo, valor.setScale(2, RoundingMode.HALF_EVEN), LocalDateTime.now(), descricao));
    }

    /** Usado pela transferência: verifica e debita em um só passo. */
    void debitarTransferencia(BigDecimal valor, Conta destino) {
        validarValor(valor);
        BigDecimal disponivel = saldo.add(getLimiteDisponivel());
        if (valor.compareTo(disponivel) > 0) {
            throw new SaldoInsuficienteException(disponivel, valor);
        }
        debitar(TipoTransacao.TRANSFERENCIA_ENVIADA, valor, "Transferência para conta " + destino.getNumero());
    }

    void creditarTransferencia(BigDecimal valor, Conta origem) {
        creditar(TipoTransacao.TRANSFERENCIA_RECEBIDA, valor, "Transferência da conta " + origem.getNumero());
    }

    private static void validarValor(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new OperacaoInvalidaException("O valor deve ser positivo");
        }
    }

    // ---- getters: o extrato é devolvido como lista imutável ----

    public int getNumero() {
        return numero;
    }

    public Cliente getTitular() {
        return titular;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public List<Transacao> getExtrato() {
        return Collections.unmodifiableList(extrato);
    }

    @Override
    public String toString() {
        return String.format("%s nº %d | %s | saldo R$ %s", getTipo(), numero, titular.nome(), saldo);
    }

    @Override
    public boolean equals(Object outro) {
        return this == outro || (outro instanceof Conta conta && numero == conta.numero);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(numero);
    }
}
