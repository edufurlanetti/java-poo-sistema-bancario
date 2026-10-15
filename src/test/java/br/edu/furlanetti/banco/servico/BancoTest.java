package br.edu.furlanetti.banco.servico;

import br.edu.furlanetti.banco.excecao.OperacaoInvalidaException;
import br.edu.furlanetti.banco.modelo.Cliente;
import br.edu.furlanetti.banco.modelo.Conta;
import br.edu.furlanetti.banco.modelo.ContaCorrente;
import br.edu.furlanetti.banco.modelo.ContaPoupanca;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BancoTest {

    private Banco banco;
    private ContaCorrente corrente;
    private ContaPoupanca poupanca;

    @BeforeEach
    void preparar() {
        banco = new Banco("Banco de Testes");
        Cliente ana = new Cliente("Ana Souza", "11122233344");
        Cliente bruno = new Cliente("Bruno Lima", "22233344455");

        corrente = banco.abrirContaCorrente(ana, new BigDecimal("200"));
        poupanca = banco.abrirContaPoupanca(bruno, new BigDecimal("0.01"));

        corrente.depositar(new BigDecimal("100"));
        poupanca.depositar(new BigDecimal("1000"));
    }

    @Test
    @DisplayName("Saldo total soma todas as contas")
    void saldoTotal() {
        assertEquals(new BigDecimal("1100.00"), banco.saldoTotal());
    }

    @Test
    @DisplayName("Transferência entre contas pelo número")
    void transferirPorNumero() {
        banco.transferir(poupanca.getNumero(), corrente.getNumero(), new BigDecimal("250"));

        assertEquals(new BigDecimal("350.00"), corrente.getSaldo());
        assertEquals(new BigDecimal("750.00"), poupanca.getSaldo());
    }

    @Test
    @DisplayName("Transferência para conta inexistente é rejeitada")
    void contaInexistente() {
        assertThrows(OperacaoInvalidaException.class,
                () -> banco.transferir(corrente.getNumero(), 9999, BigDecimal.TEN));
    }

    @Test
    @DisplayName("Ranking ordena do maior para o menor saldo")
    void ranking() {
        List<Conta> ranking = banco.rankingPorSaldo();

        assertEquals(poupanca, ranking.get(0));
        assertEquals(corrente, ranking.get(1));
    }

    @Test
    @DisplayName("Relatório de contas negativas após uso do cheque especial")
    void contasNegativas() {
        corrente.sacar(new BigDecimal("250"));

        assertEquals(List.of(corrente), banco.contasNegativas());
    }

    @Test
    @DisplayName("Saldo agrupado por tipo de conta")
    void saldoPorTipo() {
        Map<String, BigDecimal> porTipo = banco.saldoPorTipo();

        assertEquals(new BigDecimal("100.00"), porTipo.get("Conta Corrente"));
        assertEquals(new BigDecimal("1000.00"), porTipo.get("Conta Poupança"));
    }

    @Test
    @DisplayName("Fechar o mês aplica a regra de cada tipo de conta")
    void fecharMes() {
        banco.fecharMes();

        assertEquals(new BigDecimal("80.10"), corrente.getSaldo());
        assertEquals(new BigDecimal("1010.00"), poupanca.getSaldo());
        assertTrue(banco.buscar(corrente.getNumero()).isPresent());
    }
}
