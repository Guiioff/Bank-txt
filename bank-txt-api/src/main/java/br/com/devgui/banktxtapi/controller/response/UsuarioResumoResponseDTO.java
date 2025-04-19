package br.com.devgui.banktxtapi.controller.response;

import br.com.devgui.banktxtapi.model.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record UsuarioResumoResponseDTO (
        Long usuarioId,

        String nome,

        Map<String, LocalDate> periodo,

        Integer totalTransacoes,

        Map<TipoTransacao, Integer> quantidadePorTipo,

        Map<TipoTransacao, BigDecimal> valoresPorTipo,

        BigDecimal saldoFinal,

        BigDecimal mediaPorTransacao,

        LocalDate dataUltimaTransacao

) {}
