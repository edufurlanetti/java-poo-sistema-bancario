package br.edu.furlanetti.banco.servico;

import br.edu.furlanetti.banco.excecao.OperacaoInvalidaException;
import br.edu.furlanetti.banco.modelo.Cliente;
import br.edu.furlanetti.banco.modelo.Conta;
import br.edu.furlanetti.banco.modelo.ContaCorrente;
import br.edu.furlanetti.banco.modelo.ContaPoupanca;
import br.edu.furlanetti.banco.modelo.Transferencias;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Camada de serviço: orquestra as contas e oferece relatórios com a API de Streams.
 * Trabalha com o tipo abstrato {@link Conta} (<b>polimorfismo</b>): os métodos
 * funcionam para qualquer tipo de conta, inclusive tipos futuros.
 */
public class Banco {

    private final String nome;
    private final Map<Integer, Conta> contas = new LinkedHashMap<>();

    public Banco(String nome) {
        this.nome = nome;
    }

    public ContaCorrente abrirContaCorrente(Cliente cliente, BigDecimal limite) {
        ContaCorrente conta = new ContaCorrente(cliente, limite);
        contas.put(conta.getNumero(), conta);
        return conta;
    }

    public ContaPoupanca abrirContaPoupanca(Cliente cliente, BigDecimal taxaMensal) {
        ContaPoupanca conta = new ContaPoupanca(cliente, taxaMensal);
        contas.put(conta.getNumero(), conta);
        return conta;
    }

    public Optional<Conta> buscar(int numero) {
        return Optional.ofNullable(contas.get(numero));
    }

    public void transferir(int origem, int destino, BigDecimal valor) {
        Conta contaOrigem = buscar(origem)
                .orElseThrow(() -> new OperacaoInvalidaException("Conta de origem inexistente: " + origem));
        Conta contaDestino = buscar(destino)
                .orElseThrow(() -> new OperacaoInvalidaException("Conta de destino inexistente: " + destino));
        Transferencias.transferir(contaOrigem, contaDestino, valor);
    }

    /** Polimorfismo em ação: cada conta executa a SUA versão de processarFimDoMes(). */
    public void fecharMes() {
        contas.values().forEach(Conta::processarFimDoMes);
    }

    // ---------------- Relatórios com Streams ----------------

    public BigDecimal saldoTotal() {
        return contas.values().stream()
                .map(Conta::getSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Conta> contasNegativas() {
        return contas.values().stream()
                .filter(c -> c.getSaldo().signum() < 0)
                .toList();
    }

    public List<Conta> rankingPorSaldo() {
        return contas.values().stream()
                .sorted(Comparator.comparing(Conta::getSaldo).reversed())
                .toList();
    }

    public Map<String, BigDecimal> saldoPorTipo() {
        return contas.values().stream()
                .collect(Collectors.groupingBy(
                        Conta::getTipo,
                        Collectors.reducing(BigDecimal.ZERO, Conta::getSaldo, BigDecimal::add)));
    }

    public Map<Cliente, List<Conta>> contasPorCliente() {
        return contas.values().stream()
                .collect(Collectors.groupingBy(Conta::getTitular));
    }

    public String getNome() {
        return nome;
    }

    public int quantidadeDeContas() {
        return contas.size();
    }
}
