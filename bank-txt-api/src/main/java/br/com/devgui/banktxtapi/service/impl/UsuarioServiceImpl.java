package br.com.devgui.banktxtapi.service.impl;

import br.com.devgui.banktxtapi.controller.response.TransacaoResponseDTO;
import br.com.devgui.banktxtapi.controller.response.UsuarioResumoResponseDTO;
import br.com.devgui.banktxtapi.controller.response.UsuarioTransacoesResponseDTO;
import br.com.devgui.banktxtapi.model.Transacao;
import br.com.devgui.banktxtapi.model.Usuario;
import br.com.devgui.banktxtapi.model.enums.TipoTransacao;
import br.com.devgui.banktxtapi.repository.TransacaoRepository;
import br.com.devgui.banktxtapi.repository.UsuarioRepository;
import br.com.devgui.banktxtapi.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository usuarioRepository;
    private TransacaoRepository transacaoRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, TransacaoRepository transacaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.transacaoRepository = transacaoRepository;
    }

    @Override
    @Transactional
    public void cadastrarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    @Override
    public UsuarioTransacoesResponseDTO listarTransacoes(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + idUsuario));

        List<TransacaoResponseDTO> transacaoResponseDTOS = transacaoRepository
                .findByUsuarioId(usuario.getId())
                .stream()
                .map(t -> new TransacaoResponseDTO(
                        t.getTipo(),
                        t.getValor(),
                        t.getData()))
                .toList();

        return new UsuarioTransacoesResponseDTO(usuario.getNome(), usuario.getEmail(), transacaoResponseDTOS);
    }

    @Override
    public UsuarioTransacoesResponseDTO listarTransacoesEntreDatas(Long idUsuario, LocalDate dataInicial, LocalDate dataFinal) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + idUsuario));

        List<Transacao> transacaos = transacaoRepository.buscarEntreDatas(usuario.getId(), dataInicial, dataFinal);
        List<TransacaoResponseDTO> transacaoResponseDTOS = transacaos.stream()
                .map(t -> new TransacaoResponseDTO(
                        t.getTipo(),
                        t.getValor(),
                        t.getData()))
                .toList();
        return new UsuarioTransacoesResponseDTO(usuario.getNome(), usuario.getEmail(), transacaoResponseDTOS);
    }

    @Override
    public UsuarioResumoResponseDTO obterResumo(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + idUsuario));

        List<Transacao> transacoes = transacaoRepository.findByUsuarioId(usuario.getId());

        Map<String, LocalDate> periodo = obterPeriodoTransacoes(idUsuario, transacoes);

        int totalTransacoes = transacoes.size();
        Map<TipoTransacao, Integer> quantidadePorTipo = inicializarMapaDeQuantidade();
        Map<TipoTransacao, BigDecimal> valoresPorTipo = inicializarMapaDeValores();

        BigDecimal saldoAtual = calcularResumo(transacoes, quantidadePorTipo, valoresPorTipo);

        BigDecimal mediaPorTransacao = totalTransacoes > 0
                ? saldoAtual.divide(BigDecimal.valueOf(totalTransacoes), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new UsuarioResumoResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                periodo,
                totalTransacoes,
                quantidadePorTipo,
                valoresPorTipo,
                saldoAtual,
                mediaPorTransacao,
                periodo.get("fim")
        );
    }


    private Map<String, LocalDate> obterPeriodoTransacoes(Long idUsuario, List<Transacao> transacaos) {
        Map<String, LocalDate> periodo = new HashMap<>();

        if (transacaos.isEmpty()) {
            periodo.put("inicio", null);
            periodo.put("fim", null);
        } else {
            LocalDate dataInicio = transacaoRepository.buscarPrimeiraTransacao(idUsuario).get().getData();
            LocalDate dataFim = transacaoRepository.buscarUltimaTransacao(idUsuario).get().getData();

            periodo.put("inicio", dataInicio);
            periodo.put("fim", dataFim);
        }
        return periodo;
    }

    private Map<TipoTransacao, Integer> inicializarMapaDeQuantidade() {
        Map<TipoTransacao, Integer> mapa = new EnumMap<>(TipoTransacao.class);
        for (TipoTransacao tipo : TipoTransacao.values()) {
            mapa.put(tipo, 0);
        }
        return mapa;
    }

    private Map<TipoTransacao, BigDecimal> inicializarMapaDeValores() {
        Map<TipoTransacao, BigDecimal> mapa = new EnumMap<>(TipoTransacao.class);
        for (TipoTransacao tipo : TipoTransacao.values()) {
            mapa.put(tipo, BigDecimal.ZERO);
        }
        return mapa;
    }

    private BigDecimal calcularResumo( List<Transacao> transacoes,
            Map<TipoTransacao, Integer> quantidadePorTipo,
            Map<TipoTransacao, BigDecimal> valoresPorTipo
    ) {
        BigDecimal saldo = BigDecimal.ZERO;

        for (Transacao transacao : transacoes) {
            TipoTransacao tipo = transacao.getTipo();
            BigDecimal valor = transacao.getValor();

            saldo = saldo.add(valor);
            quantidadePorTipo.put(tipo, quantidadePorTipo.get(tipo) + 1);
            valoresPorTipo.put(tipo, valoresPorTipo.get(tipo).add(valor));
        }

        return saldo;
    }
}
