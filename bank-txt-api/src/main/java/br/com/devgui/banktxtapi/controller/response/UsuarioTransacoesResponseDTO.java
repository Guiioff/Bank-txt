package br.com.devgui.banktxtapi.controller.response;

import java.util.List;

public record UsuarioTransacoesResponseDTO (
        String nome,
        String email,
        List<TransacaoResponseDTO> transacoes
) {

    public UsuarioTransacoesResponseDTO (String nome, String email, List<TransacaoResponseDTO> transacoes) {
        this.nome = nome;
        this.email = email;
        this.transacoes = transacoes;
    }
}
