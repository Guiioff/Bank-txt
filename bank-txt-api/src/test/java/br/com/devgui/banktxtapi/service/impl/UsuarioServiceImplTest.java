package br.com.devgui.banktxtapi.service.impl;

import br.com.devgui.banktxtapi.controller.response.UsuarioResumoResponseDTO;
import br.com.devgui.banktxtapi.controller.response.UsuarioTransacoesResponseDTO;
import br.com.devgui.banktxtapi.exception.UsuarioNaoEncontradoException;
import br.com.devgui.banktxtapi.model.Transacao;
import br.com.devgui.banktxtapi.model.Usuario;
import br.com.devgui.banktxtapi.model.enums.TipoTransacao;
import br.com.devgui.banktxtapi.repository.TransacaoRepository;
import br.com.devgui.banktxtapi.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UsuarioServiceImplTest {

    private UsuarioRepository usuarioRepository;
    private TransacaoRepository transacaoRepository;
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        transacaoRepository = mock(TransacaoRepository.class);
        usuarioService = new UsuarioServiceImpl(usuarioRepository, transacaoRepository);

        usuario = new Usuario(1L, "João", "joao@email.com", "123456");
    }

    @Test
    void deveListarTransacoesDoUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(transacaoRepository.findByUsuarioId(1L)).thenReturn(List.of(
                new Transacao(LocalDate.of(2024, 5, 1), new BigDecimal("100.00"), TipoTransacao.DEPOSITO, usuario),
                new Transacao(LocalDate.of(2024, 5, 2), new BigDecimal("-50.00"), TipoTransacao.SAQUE, usuario)
        ));

        UsuarioTransacoesResponseDTO dto = usuarioService.listarTransacoes(1L);

        assertThat(dto.nome()).isEqualTo("João");
        assertThat(dto.transacoes()).hasSize(2);
    }

    @Test
    void deveListarTransacoesEntreDatas() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(transacaoRepository.buscarEntreDatas(eq(1L), any(), any())).thenReturn(List.of(
                new Transacao(LocalDate.of(2024, 5, 1), new BigDecimal("100.00"), TipoTransacao.DEPOSITO, usuario)
        ));

        UsuarioTransacoesResponseDTO dto = usuarioService.listarTransacoesEntreDatas(1L,
                LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 10));

        assertThat(dto.transacoes()).hasSize(1);
    }

    @Test
    void deveObterResumoComDadosCalculados() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(transacaoRepository.findByUsuarioId(1L)).thenReturn(List.of(
                new Transacao(LocalDate.of(2024, 5, 1), new BigDecimal("100.00"), TipoTransacao.DEPOSITO, usuario),
                new Transacao(LocalDate.of(2024, 5, 2), new BigDecimal("-40.00"), TipoTransacao.SAQUE, usuario)
        ));
        when(transacaoRepository.buscarPrimeiraTransacao(1L))
                .thenReturn(Optional.of(new Transacao(LocalDate.of(2024, 5, 1), BigDecimal.ONE, TipoTransacao.DEPOSITO, usuario)));
        when(transacaoRepository.buscarUltimaTransacao(1L))
                .thenReturn(Optional.of(new Transacao(LocalDate.of(2024, 5, 2), BigDecimal.ONE, TipoTransacao.SAQUE, usuario)));

        UsuarioResumoResponseDTO resumo = usuarioService.obterResumo(1L);

        assertThat(resumo.totalTransacoes()).isEqualTo(2);
        assertThat(resumo.saldoFinal()).isEqualByComparingTo("60.00");
        assertThat(resumo.mediaPorTransacao()).isEqualByComparingTo("30.00");
        assertThat(resumo.quantidadePorTipo().get(TipoTransacao.DEPOSITO)).isEqualTo(1);
        assertThat(resumo.quantidadePorTipo().get(TipoTransacao.SAQUE)).isEqualTo(1);
    }

    @Test
    void deveLancarExcecao_QuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.listarTransacoes(99L))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessageContaining("Usuário não encontrado");
    }
}
