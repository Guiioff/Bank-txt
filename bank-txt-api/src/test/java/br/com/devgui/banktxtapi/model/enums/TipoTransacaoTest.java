package br.com.devgui.banktxtapi.model.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TipoTransacaoTest {

    @Test
    void deveValidarDescricaoEnumTipoTransacao() {
        assertThat(TipoTransacao.DEPOSITO.getDescricao()).isEqualTo("Depósito");
        assertThat(TipoTransacao.SAQUE.getDescricao()).isEqualTo("Saque");
        assertThat(TipoTransacao.TRANSFERENCIA.getDescricao()).isEqualTo("Transferência");
    }
}