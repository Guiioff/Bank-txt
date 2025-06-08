package br.com.devgui.banktxtapi.controller.request;

import br.com.devgui.banktxtapi.model.Usuario;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RecordDtoRequestTests {

    @Test
    void deveConverterUsuarioCadastroRequestDTOParaEntidade() {
        UsuarioCadastroRequestDTO dto = new UsuarioCadastroRequestDTO("Maria", "maria@email.com", "senha123");
        Usuario usuario = dto.toEntity();

        assertThat(usuario.getNome()).isEqualTo("Maria");
        assertThat(usuario.getEmail()).isEqualTo("maria@email.com");
        assertThat(usuario.getSenha()).isEqualTo("senha123");
    }
}