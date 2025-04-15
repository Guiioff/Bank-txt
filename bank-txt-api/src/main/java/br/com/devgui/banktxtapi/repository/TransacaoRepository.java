package br.com.devgui.banktxtapi.repository;

import br.com.devgui.banktxtapi.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {}
