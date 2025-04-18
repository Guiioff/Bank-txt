package br.com.devgui.banktxtapi.controller.response;

import br.com.devgui.banktxtapi.model.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoResponseDTO(TipoTransacao tipo,
                                  BigDecimal valor,
                                  LocalDate data) {
}
