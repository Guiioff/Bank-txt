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

        List<Transacao> transacaos = transacaoRepository.findByUsuarioId(usuario.getId());

        LocalDate dataInicio = null;
        LocalDate dataFim = null;

        if (!transacaos.isEmpty()) {
            Transacao primeiraTransacao = transacaoRepository.buscarPrimeiraTransacao(idUsuario).get();
            Transacao ultimaTransacao = transacaoRepository.buscarUltimaTransacao(idUsuario).get();

            dataInicio = primeiraTransacao.getData();
            dataFim = ultimaTransacao.getData();
        }

        Map<String, LocalDate> periodo = new HashMap<>();
        periodo.put("inicio", dataInicio);
        periodo.put("fim", dataInicio);

        int totalTransacoes = transacaos.size();
        Map<TipoTransacao, Integer> quantidadePorTipo = new EnumMap<>(TipoTransacao.class);
        Map<TipoTransacao, BigDecimal> valoresPorTipo = new EnumMap<>(TipoTransacao.class);
        BigDecimal saldoAtual = BigDecimal.ZERO;

        for (TipoTransacao tipo : TipoTransacao.values()) {
            quantidadePorTipo.put(tipo, 0);
            valoresPorTipo.put(tipo, BigDecimal.ZERO);
        }

        for (Transacao transacao : transacaos) {
            TipoTransacao tipo = transacao.getTipo();
            BigDecimal valor = transacao.getValor();

            saldoAtual = saldoAtual.add(valor);

            quantidadePorTipo.put(tipo, quantidadePorTipo.get(tipo) + 1);
            valoresPorTipo.put(tipo, valoresPorTipo.get(tipo).add(valor));
        }

        BigDecimal mediaPorTransacao = saldoAtual.divide(BigDecimal.valueOf(totalTransacoes), 2, RoundingMode.HALF_UP);

    return new UsuarioResumoResponseDTO(
            usuario.getId(),
            usuario.getNome(),
            periodo,
            totalTransacoes,
            quantidadePorTipo,
            valoresPorTipo,
            saldoAtual,
            mediaPorTransacao,
            dataFim
    );
  }








}
