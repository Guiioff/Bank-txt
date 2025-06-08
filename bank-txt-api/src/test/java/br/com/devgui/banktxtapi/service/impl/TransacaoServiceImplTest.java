package br.com.devgui.banktxtapi.service.impl;

import br.com.devgui.banktxtapi.exception.ArquivoInvalidoException;
import br.com.devgui.banktxtapi.model.Transacao;
import br.com.devgui.banktxtapi.model.Usuario;
import br.com.devgui.banktxtapi.model.enums.TipoTransacao;
import br.com.devgui.banktxtapi.repository.TransacaoRepository;
import br.com.devgui.banktxtapi.repository.UsuarioRepository;
import br.com.devgui.banktxtapi.validator.TransacaoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TransacaoServiceImplTest {

    private TransacaoServiceImpl service;
    private TransacaoValidator validator;

    @BeforeEach
    void setup() {
        validator = new TransacaoValidator();
        service = new TransacaoServiceImpl(
                Mockito.mock(TransacaoRepository.class),
                Mockito.mock(UsuarioRepository.class),
                validator
        );
    }

    private Usuario criarUsuarioFake() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setNome("Teste");
        u.setEmail("teste@email.com");
        return u;
    }

    @Test
    void deveLerArquivoComUmaTransacaoValida() {
        String conteudo = "2024-05-20;DEPOSITO;150.75\n";
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "teste.txt", "text/plain", conteudo.getBytes());

        List<Transacao> transacoes = service.lerArquivo(arquivo, criarUsuarioFake());

        assertEquals(1, transacoes.size());
        Transacao t = transacoes.get(0);
        assertEquals(LocalDate.of(2024, 5, 20), t.getData());
        assertEquals(TipoTransacao.DEPOSITO, t.getTipo());
        assertEquals(new BigDecimal("150.75"), t.getValor());
    }

    @Test
    void deveLancarExcecao_QuandoDataForInvalida() {
        String conteudo = "20-05-2024;DEPOSITO;100.00\n";
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", conteudo.getBytes());

        assertThatThrownBy(() -> service.lerArquivo(arquivo, criarUsuarioFake()))
                .isInstanceOf(ArquivoInvalidoException.class)
                .hasMessageContaining("data inválida");
    }

    @Test
    void deveLancarExcecao_QuandoTipoForInvalido() {
        String conteudo = "2024-05-20;INVALIDO;100.00\n";
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", conteudo.getBytes());

        assertThatThrownBy(() -> service.lerArquivo(arquivo, criarUsuarioFake()))
                .isInstanceOf(ArquivoInvalidoException.class)
                .hasMessageContaining("tipo de transação inválido");
    }

    @Test
    void deveLancarExcecao_QuandoValorForInvalido() {
        String conteudo = "2024-05-20;DEPOSITO;abc\n";
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", conteudo.getBytes());

        assertThatThrownBy(() -> service.lerArquivo(arquivo, criarUsuarioFake()))
                .isInstanceOf(ArquivoInvalidoException.class)
                .hasMessageContaining("valor inválido");
    }

    @Test
    void deveLancarExcecao_QuandoLinhaTiverFormatoIncorreto() {
        String conteudo = "2024-05-20;DEPOSITO\n"; // faltando valor
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", conteudo.getBytes());

        assertThatThrownBy(() -> service.lerArquivo(arquivo, criarUsuarioFake()))
                .isInstanceOf(ArquivoInvalidoException.class)
                .hasMessageContaining("mal formatada");
    }

    @Test
    void deveLancarExcecao_QuandoValorForZero() {
        String conteudo = "2024-05-20;DEPOSITO;0.00\n";
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", conteudo.getBytes());

        assertThatThrownBy(() -> service.lerArquivo(arquivo, criarUsuarioFake()))
                .isInstanceOf(ArquivoInvalidoException.class)
                .hasMessageContaining("valor 0 é inválida");
    }

    @Test
    void deveLancarExcecao_QuandoArquivoEstiverVazio() {
        String conteudo = "";
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", conteudo.getBytes());

        assertThatThrownBy(() -> service.lerArquivo(arquivo, criarUsuarioFake()))
                .isInstanceOf(ArquivoInvalidoException.class)
                .hasMessageContaining("não contém transações válidas");
    }
}
