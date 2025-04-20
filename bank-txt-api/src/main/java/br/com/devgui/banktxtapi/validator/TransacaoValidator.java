package br.com.devgui.banktxtapi.validator;

import br.com.devgui.banktxtapi.exception.TransacaoInvalidaException;
import br.com.devgui.banktxtapi.model.Transacao;
import br.com.devgui.banktxtapi.model.enums.TipoTransacao;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class TransacaoValidator {

    public void validar(List<Transacao> transacoes) {
        for (Transacao transacao : transacoes) {
            this.validar(transacao);
        }
    }

    public void validar(Transacao transacao) {

        if (transacao == null) {
            throw new TransacaoInvalidaException("Transação nula encontrada.");
        }

        if (transacao.getValor() == null || transacao.getValor().compareTo(BigDecimal.ZERO) == 0) {
            throw new TransacaoInvalidaException("Valor da transação não pode ser nulo ou zero.");
        }

        if (transacao.getData() == null) {
            throw new TransacaoInvalidaException("Data da transação não pode ser nula.");
        }

        if (transacao.getTipo() == null) {
            throw new TransacaoInvalidaException("Tipo da transação não pode ser nulo.");
        }

        if (!tipoValido(transacao.getTipo())) {
            throw new TransacaoInvalidaException("Tipo de transação inválido: " + transacao.getTipo());
        }

        if (transacao.getUsuario() == null) {
            throw new TransacaoInvalidaException("Transação deve estar associada a um usuário.");
        }
    }

    private boolean tipoValido(TipoTransacao tipo) {
        for (TipoTransacao tipoValido : TipoTransacao.values()) {
            if (tipoValido.equals(tipo)) {
                return true;
            }
        }
        return false;
    }
}
