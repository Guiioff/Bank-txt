package br.com.devgui.banktxtapi.repository;

import br.com.devgui.banktxtapi.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuarioId(Long id);
}
