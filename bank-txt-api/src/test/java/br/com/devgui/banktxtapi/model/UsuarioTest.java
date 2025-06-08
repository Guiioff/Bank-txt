package br.com.devgui.banktxtapi.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioTest {

    @Test
    void deveCriarUsuarioModelCorretamente() {
        Usuario usuario = new Usuario(1L, "Carlos", "carlos@email.com", "senha123");

        assertThat(usuario.getId()).isEqualTo(1L);
        assertThat(usuario.getNome()).isEqualTo("Carlos");
        assertThat(usuario.getEmail()).isEqualTo("carlos@email.com");
        assertThat(usuario.getSenha()).isEqualTo("senha123");
        assertThat(usuario.toString()).contains("Carlos", "carlos@email.com", "senha123");
    }

}