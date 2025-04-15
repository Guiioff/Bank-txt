package br.com.devgui.banktxtapi.model;

import br.com.devgui.banktxtapi.model.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Transacao(LocalDate data, BigDecimal valor, TipoTransacao tipo, Usuario usuario) {
        this.data = data;
        this.valor = valor;
        this.tipo = tipo;
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Transacao{");
        sb.append("id=").append(id);
        sb.append(", data=").append(data);
        sb.append(", valor=").append(valor);
        sb.append(", tipo=").append(tipo);
        sb.append(", usuario=").append(usuario);
        sb.append('}');
        return sb.toString();
    }
}
