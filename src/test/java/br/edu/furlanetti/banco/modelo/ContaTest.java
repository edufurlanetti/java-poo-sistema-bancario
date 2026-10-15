package br.edu.furlanetti.banco.modelo;

import br.edu.furlanetti.banco.excecao.OperacaoInvalidaException;
import br.edu.furlanetti.banco.excecao.SaldoInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContaTest {

    private Cliente cliente;

    @BeforeEach
    void preparar() {
        cliente = new Cliente("Ana Souza", "11122233344");
    }

    private static BigDecimal reais(String valor) {
        return new BigDecimal(valor);
    }

    @Test
    @DisplayName("Depósito aumenta o saldo e é registrado no extrato")
    void depositoAumentaSaldo() {
        ContaCorrente conta = new ContaCorrente(cliente, reais("0"));
        conta.depositar(reais("150.50"));

        assertEquals(reais("150.50"), conta.getSaldo());
        assertEquals(1, conta.getExtrato().size());
        assertEquals(TipoTransacao.DEPOSITO, conta.getExtrato().get(0).tipo());
    }

    @Test
    @DisplayName("Valores zero ou negativos são rejeitados")
    void valorInvalido() {
        ContaCorrente conta = new ContaCorrente(cliente, reais("0"));

        assertThrows(OperacaoInvalidaException.class, () -> conta.depositar(reais("0")));
        assertThrows(OperacaoInvalidaException.class, () -> conta.sacar(reais("-5")));
    }

    @Test
    @DisplayName("Conta corrente pode usar o cheque especial até o limite")
    void chequeEspecial() {
        ContaCorrente conta = new ContaCorrente(cliente, reais("500"));
        conta.depositar(reais("100"));

        conta.sacar(reais("600"));

        assertEquals(reais("-500.00"), conta.getSaldo());
        assertThrows(SaldoInsuficienteException.class, () -> conta.sacar(reais("0.01")));
    }

    @Test
    @DisplayName("Poupança não aceita saldo negativo")
    void poupancaSemLimite() {
        ContaPoupanca conta = new ContaPoupanca(cliente, reais("0.005"));
        conta.depositar(reais("100"));

        SaldoInsuficienteException erro =
                assertThrows(SaldoInsuficienteException.class, () -> conta.sacar(reais("100.01")));
        assertEquals(reais("100.00"), erro.getDisponivel());
        assertEquals(reais("100.00"), conta.getSaldo());
    }

    @Test
    @DisplayName("Fechamento do mês: poupança rende e corrente paga tarifa (polimorfismo)")
    void fechamentoDoMes() {
        ContaPoupanca poupanca = new ContaPoupanca(cliente, reais("0.005"));
        ContaCorrente corrente = new ContaCorrente(cliente, reais("0"));
        poupanca.depositar(reais("1000"));
        corrente.depositar(reais("100"));

        Conta[] contas = {poupanca, corrente};
        for (Conta conta : contas) {
            conta.processarFimDoMes();
        }

        assertEquals(reais("1005.00"), poupanca.getSaldo());
        assertEquals(reais("80.10"), corrente.getSaldo());
    }

    @Test
    @DisplayName("IOF incide apenas sobre saldo devedor")
    void iofSobreSaldoDevedor() {
        ContaCorrente conta = new ContaCorrente(cliente, reais("1000"));
        assertTrue(conta.isIsento());

        conta.sacar(reais("1000"));

        assertEquals(reais("3.80"), conta.calcularTributo());
    }

    @Test
    @DisplayName("Transferência movimenta as duas contas")
    void transferencia() {
        ContaCorrente origem = new ContaCorrente(cliente, reais("0"));
        ContaPoupanca destino = new ContaPoupanca(new Cliente("Bruno Lima", "22233344455"), reais("0.005"));
        origem.depositar(reais("300"));

        Transferencias.transferir(origem, destino, reais("120"));

        assertEquals(reais("180.00"), origem.getSaldo());
        assertEquals(reais("120.00"), destino.getSaldo());
    }

    @Test
    @DisplayName("Transferência sem saldo não altera nenhuma das contas")
    void transferenciaSemSaldo() {
        ContaPoupanca origem = new ContaPoupanca(cliente, reais("0.005"));
        ContaCorrente destino = new ContaCorrente(new Cliente("Bruno Lima", "22233344455"), reais("0"));
        origem.depositar(reais("50"));

        assertThrows(SaldoInsuficienteException.class,
                () -> Transferencias.transferir(origem, destino, reais("80")));
        assertEquals(reais("50.00"), origem.getSaldo());
        assertEquals(reais("0.00"), destino.getSaldo());
    }

    @Test
    @DisplayName("O extrato não pode ser alterado por fora da conta (encapsulamento)")
    void extratoImutavel() {
        ContaCorrente conta = new ContaCorrente(cliente, reais("0"));
        conta.depositar(reais("10"));

        assertThrows(UnsupportedOperationException.class, () -> conta.getExtrato().clear());
    }

    @Test
    @DisplayName("Cliente valida o CPF na criação (record com construtor compacto)")
    void clienteValidaCpf() {
        assertThrows(IllegalArgumentException.class, () -> new Cliente("Ana", "123"));
        assertThrows(IllegalArgumentException.class, () -> new Cliente(" ", "11122233344"));
    }
}
