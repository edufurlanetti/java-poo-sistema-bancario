package br.edu.furlanetti.banco.modelo;

import br.edu.furlanetti.banco.excecao.OperacaoInvalidaException;

import java.math.BigDecimal;

/**
 * Ponto de acesso às operações de transferência, que são package-private em {@link Conta}.
 * Mantém o encapsulamento: fora do pacote só é possível transferir por aqui.
 */
public final class Transferencias {

    private Transferencias() {
    }

    public static void transferir(Conta origem, Conta destino, BigDecimal valor) {
        if (origem.equals(destino)) {
            throw new OperacaoInvalidaException("Origem e destino devem ser contas diferentes");
        }
        origem.debitarTransferencia(valor, destino);   // lança exceção antes de creditar
        destino.creditarTransferencia(valor, origem);
    }
}
