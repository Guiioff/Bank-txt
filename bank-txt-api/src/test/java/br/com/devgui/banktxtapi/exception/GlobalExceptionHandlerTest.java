package br.com.devgui.banktxtapi.exception;

import br.com.devgui.banktxtapi.controller.response.ErrorResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveTratarUsuarioNaoEncontradoException() {
        UsuarioNaoEncontradoException ex = new UsuarioNaoEncontradoException("Usuário não encontrado");
        ResponseEntity<ErrorResponseDTO> response = handler.usuarioNaoEncontradoException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().error()).isEqualTo("Recurso não encontrado");
        assertThat(response.getBody().message()).isEqualTo("Usuário não encontrado");
    }

    @Test
    void deveTratarArquivoInvalidoException() {
        ArquivoInvalidoException ex = new ArquivoInvalidoException("Formato inválido");
        ResponseEntity<ErrorResponseDTO> response = handler.arquivoInvalidoException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().error()).isEqualTo("Arquivo inválido");
        assertThat(response.getBody().message()).isEqualTo("Formato inválido");
    }

    @Test
    void deveTratarTransacaoInvalidaException() {
        TransacaoInvalidaException ex = new TransacaoInvalidaException("Valor inválido");
        ResponseEntity<ErrorResponseDTO> response = handler.transacaoInvalidaException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().error()).isEqualTo("Transação inválida");
        assertThat(response.getBody().message()).isEqualTo("Valor inválido");
    }
}
