package br.com.devgui.banktxtapi.validator;

import br.com.devgui.banktxtapi.exception.TransacaoInvalidaException;
import br.com.devgui.banktxtapi.model.Transacao;
import br.com.devgui.banktxtapi.model.Usuario;
import br.com.devgui.banktxtapi.model.enums.TipoTransacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


class TransacaoValidatorTest {

    private TransacaoValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TransacaoValidator();
    }

    private Transacao criarTransacaoValida() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setEmail("teste@teste.com");

        return new Transacao(
                LocalDate.now(),
                new BigDecimal("100.00"),
                TipoTransacao.DEPOSITO,
                usuario
        );
    }

    @Test
    void deveValidarTransacaoValida() {
        Transacao transacao = criarTransacaoValida();

        assertDoesNotThrow(() -> validator.validar(transacao));
    }

    @Test
    void deveLancarExcecao_QuandoTransacaoForNula() {
        assertThatThrownBy(() -> validator.validar((Transacao) null))
                .isInstanceOf(TransacaoInvalidaException.class)
                .hasMessageContaining("Transação nula encontrada.");
    }

    @Test
    void deveLancarExcecao_QuandoValorForNulo() {
        Transacao transacao = criarTransacaoValida();
        transacao.setValor(null);

        assertThatThrownBy(() -> validator.validar(transacao))
                .isInstanceOf(TransacaoInvalidaException.class)
                .hasMessageContaining("Valor da transação não pode ser nulo ou zero.");
    }

    @Test
    void deveLancarExcecao_QuandoValorForZero() {
        Transacao transacao = criarTransacaoValida();
        transacao.setValor(BigDecimal.ZERO);

        assertThatThrownBy(() -> validator.validar(transacao))
                .isInstanceOf(TransacaoInvalidaException.class)
                .hasMessageContaining("Valor da transação não pode ser nulo ou zero.");
    }

    @Test
    void deveLancarExcecao_QuandoDataForNula() {
        Transacao transacao = criarTransacaoValida();
        transacao.setData(null);

        assertThatThrownBy(() -> validator.validar(transacao))
                .isInstanceOf(TransacaoInvalidaException.class)
                .hasMessageContaining("Data da transação não pode ser nula.");
    }

    @Test
    void deveLancarExcecao_QuandoTipoForNulo() {
        Transacao transacao = criarTransacaoValida();
        transacao.setTipo(null);

        assertThatThrownBy(() -> validator.validar(transacao))
                .isInstanceOf(TransacaoInvalidaException.class)
                .hasMessageContaining("Tipo da transação não pode ser nulo.");
    }

    @Test
    void deveLancarExcecao_QuandoUsuarioForNulo() {
        Transacao transacao = criarTransacaoValida();
        transacao.setUsuario(null);

        assertThatThrownBy(() -> validator.validar(transacao))
                .isInstanceOf(TransacaoInvalidaException.class)
                .hasMessageContaining("Transação deve estar associada a um usuário.");
    }

    @Test
    void deveValidarListaDeTransacoes_QuandoTodasValidas() {
        List<Transacao> transacoes = List.of(
                criarTransacaoValida(),
                criarTransacaoValida()
        );

        assertDoesNotThrow(() -> validator.validar(transacoes));
    }

    @Test
    void deveLancarExcecao_QuandoUmaTransacaoDaListaForInvalida() {
        List<Transacao> transacoes = Arrays.asList(
                criarTransacaoValida(),
                null
        );

        assertThatThrownBy(() -> validator.validar(transacoes))
                .isInstanceOf(TransacaoInvalidaException.class)
                .hasMessageContaining("Transação nula encontrada.");
    }

}