package br.edu.furlanetti.banco.modelo;

import java.util.Objects;

/**
 * Cliente do banco, imutável, implementado como {@code record} (Java 16+).
 * O construtor compacto valida os dados na criação do objeto.
 */
public record Cliente(String nome, String cpf) {

    public Cliente {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório");
        }
        Objects.requireNonNull(cpf, "CPF é obrigatório");
        if (!cpf.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos numéricos");
        }
    }
}
