package br.com.devgui.banktxtapi.controller.response;

import br.com.devgui.banktxtapi.model.enums.TipoTransacao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RecordDtoResponseTests {

    @Test
    void deveCriarErrorResponseDTOCorretamente() {
        ErrorResponseDTO dto = new ErrorResponseDTO(404, "Not Found", "Recurso inexistente");

        assertThat(dto.status()).isEqualTo(404);
        assertThat(dto.error()).isEqualTo("Not Found");
        assertThat(dto.message()).isEqualTo("Recurso inexistente");
    }

    @Test
    void deveCriarTransacaoResponseDTOCorretamente() {
        TipoTransacao tipo = TipoTransacao.DEPOSITO;
        BigDecimal valor = new BigDecimal("250.00");
        LocalDate data = LocalDate.of(2024, 6, 1);

        TransacaoResponseDTO dto = new TransacaoResponseDTO(tipo, valor, data);

        assertThat(dto.tipo()).isEqualTo(tipo);
        assertThat(dto.valor()).isEqualByComparingTo("250.00");
        assertThat(dto.data()).isEqualTo(data);
    }

    @Test
    void deveCriarUsuarioResumoResponseDTOCorretamente() {
        UsuarioResumoResponseDTO dto = new UsuarioResumoResponseDTO(
                1L,
                "João",
                Map.of("inicio", LocalDate.of(2024, 6, 1), "fim", LocalDate.of(2024, 6, 10)),
                3,
                Map.of(TipoTransacao.DEPOSITO, 2, TipoTransacao.SAQUE, 1),
                Map.of(TipoTransacao.DEPOSITO, new BigDecimal("200.00"), TipoTransacao.SAQUE, new BigDecimal("-50.00")),
                new BigDecimal("150.00"),
                new BigDecimal("50.00"),
                LocalDate.of(2024, 6, 10)
        );

        assertThat(dto.usuarioId()).isEqualTo(1L);
        assertThat(dto.nome()).isEqualTo("João");
        assertThat(dto.periodo().get("inicio")).isEqualTo(LocalDate.of(2024, 6, 1));
        assertThat(dto.totalTransacoes()).isEqualTo(3);
        assertThat(dto.quantidadePorTipo().get(TipoTransacao.DEPOSITO)).isEqualTo(2);
        assertThat(dto.valoresPorTipo().get(TipoTransacao.SAQUE)).isEqualByComparingTo("-50.00");
        assertThat(dto.saldoFinal()).isEqualByComparingTo("150.00");
        assertThat(dto.mediaPorTransacao()).isEqualByComparingTo("50.00");
        assertThat(dto.dataUltimaTransacao()).isEqualTo(LocalDate.of(2024, 6, 10));
    }

    @Test
    void deveCriarUsuarioTransacoesResponseDTOCorretamente() {
        TransacaoResponseDTO transacao = new TransacaoResponseDTO(
                TipoTransacao.DEPOSITO,
                new BigDecimal("100.00"),
                LocalDate.of(2024, 6, 1)
        );

        UsuarioTransacoesResponseDTO dto = new UsuarioTransacoesResponseDTO(
                "João",
                "joao@email.com",
                List.of(transacao)
        );

        assertThat(dto.nome()).isEqualTo("João");
        assertThat(dto.email()).isEqualTo("joao@email.com");
        assertThat(dto.transacoes()).hasSize(1);
        assertThat(dto.transacoes().get(0).valor()).isEqualByComparingTo("100.00");
    }
}
