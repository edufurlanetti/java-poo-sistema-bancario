package br.edu.furlanetti.banco.excecao;

/**
 * Exceção de negócio lançada quando uma operação bancária não pode ser realizada.
 * É uma exceção verificada? Não: herda de {@link RuntimeException}, seguindo a
 * prática moderna de usar exceções não verificadas para regras de negócio.
 */
public class OperacaoInvalidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OperacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
