package br.com.devgui.banktxtapi.repository;

import br.com.devgui.banktxtapi.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuarioId(Long id);

    @Query("SELECT t FROM Transacao t WHERE " +
            "t.usuario.id = :usuarioId AND " +
            "t.data BETWEEN :dataInicial AND :dataFinal")
    List<Transacao> buscarEntreDatas(@Param("usuarioId") Long usuarioId,
                                     @Param("dataInicial") LocalDate dataInicial,
                                     @Param("dataFinal") LocalDate dataFinal);

    @Query("SELECT t FROM Transacao t WHERE " +
            "t.usuario.id = :usuarioId " +
            "ORDER BY t.data DESC " +
            "LIMIT 1")
    Optional<Transacao> buscarUltimaTransacao(@Param("usuarioId") Long usuarioId);

    @Query("SELECT t FROM Transacao t WHERE " +
            "t.usuario.id = :usuarioId " +
            "ORDER BY t.data ASC " +
            "LIMIT 1")
    Optional<Transacao> buscarPrimeiraTransacao(@Param("usuarioId") Long usuarioId);
}
