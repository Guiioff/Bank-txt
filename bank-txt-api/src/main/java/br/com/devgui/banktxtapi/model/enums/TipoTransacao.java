package br.com.devgui.banktxtapi.model.enums;

public enum TipoTransacao {
    DEPOSITO("Depósito"),
    SAQUE("Saque"),
    TRANSFERENCIA("Transferência");

    private final String descricao;

    TipoTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return this.descricao;
    }
}
