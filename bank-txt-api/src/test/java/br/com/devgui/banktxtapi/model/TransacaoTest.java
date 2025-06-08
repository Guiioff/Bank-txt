package br.com.devgui.banktxtapi.model;

import br.com.devgui.banktxtapi.model.enums.TipoTransacao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TransacaoTest {

    @Test
    void deveCriarTransacaoModelCorretamente() {
        Usuario usuario = new Usuario(2L, "Ana", "ana@email.com", "senha456");
        Transacao transacao = new Transacao(
                LocalDate.of(2024, 6, 5),
                new BigDecimal("300.00"),
                TipoTransacao.DEPOSITO,
                usuario
        );

        assertThat(transacao.getData()).isEqualTo(LocalDate.of(2024, 6, 5));
        assertThat(transacao.getValor()).isEqualByComparingTo("300.00");
        assertThat(transacao.getTipo()).isEqualTo(TipoTransacao.DEPOSITO);
        assertThat(transacao.getUsuario()).isEqualTo(usuario);
        assertThat(transacao.toString()).contains("300.00", "DEPOSITO", "Ana");
    }
}