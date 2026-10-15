package br.edu.furlanetti.banco;

import br.edu.furlanetti.banco.excecao.OperacaoInvalidaException;
import br.edu.furlanetti.banco.excecao.SaldoInsuficienteException;
import br.edu.furlanetti.banco.modelo.Cliente;
import br.edu.furlanetti.banco.modelo.Conta;
import br.edu.furlanetti.banco.modelo.ContaCorrente;
import br.edu.furlanetti.banco.modelo.ContaPoupanca;
import br.edu.furlanetti.banco.modelo.Transacao;
import br.edu.furlanetti.banco.servico.Banco;

import java.math.BigDecimal;

/** Demonstração de uso do sistema bancário. */
public class App {

    public static void main(String[] args) {
        Banco banco = new Banco("Banco Didático");

        Cliente ana = new Cliente("Ana Souza", "11122233344");
        Cliente bruno = new Cliente("Bruno Lima", "22233344455");

        ContaCorrente ccAna = banco.abrirContaCorrente(ana, new BigDecimal("500.00"));
        ContaPoupanca cpAna = banco.abrirContaPoupanca(ana, new BigDecimal("0.005"));
        ContaCorrente ccBruno = banco.abrirContaCorrente(bruno, new BigDecimal("1000.00"));

        ccAna.depositar(new BigDecimal("1200.00"));
        cpAna.depositar(new BigDecimal("3000.00"));
        ccBruno.depositar(new BigDecimal("150.00"));

        banco.transferir(ccAna.getNumero(), ccBruno.getNumero(), new BigDecimal("300.00"));
        ccBruno.sacar(new BigDecimal("800.00"));          // usa o cheque especial

        titulo("Tratamento de exceções");
        try {
            cpAna.sacar(new BigDecimal("5000.00"));        // poupança não tem limite
        } catch (SaldoInsuficienteException e) {
            System.out.println("Erro específico: " + e.getMessage());
        }
        try {
            ccAna.depositar(new BigDecimal("-10"));
        } catch (OperacaoInvalidaException e) {
            System.out.println("Erro genérico: " + e.getMessage());
        }

        banco.fecharMes();

        titulo("Contas após o fechamento do mês");
        banco.rankingPorSaldo().forEach(System.out::println);

        titulo("Extrato da conta corrente de Bruno");
        for (Transacao t : ccBruno.getExtrato()) {
            System.out.printf("%-24s %10s  %s%n", t.tipo(), t.valorComSinal(), t.descricao());
        }

        titulo("Relatórios");
        System.out.println("Saldo total do banco: R$ " + banco.saldoTotal());
        System.out.println("Saldo por tipo: " + banco.saldoPorTipo());
        System.out.println("Contas negativas: " + banco.contasNegativas().stream().map(Conta::getNumero).toList());
        System.out.println("IOF devido por Bruno: R$ " + ccBruno.calcularTributo());
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + texto + " ===");
    }
}
