package br.edu.furlanetti.banco.modelo;

/** Enumeração com atributo: cada tipo sabe se aumenta ou diminui o saldo. */
public enum TipoTransacao {
    DEPOSITO(+1),
    SAQUE(-1),
    TRANSFERENCIA_ENVIADA(-1),
    TRANSFERENCIA_RECEBIDA(+1),
    TARIFA(-1),
    RENDIMENTO(+1);

    private final int sinal;

    TipoTransacao(int sinal) {
        this.sinal = sinal;
    }

    public int getSinal() {
        return sinal;
    }
}
