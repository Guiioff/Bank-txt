package br.com.devgui.banktxtapi.controller.request;

import br.com.devgui.banktxtapi.model.Usuario;

public record UsuarioCadastroRequestDTO(String nome,
                                        String email,
                                        String senha) {

    public Usuario toEntity() {
        return new Usuario(nome, email, senha);
    }
}
