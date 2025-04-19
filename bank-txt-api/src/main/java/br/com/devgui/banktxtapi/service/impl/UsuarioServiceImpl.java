package br.com.devgui.banktxtapi.service.impl;

import br.com.devgui.banktxtapi.controller.response.TransacaoResponseDTO;
import br.com.devgui.banktxtapi.controller.response.UsuarioTransacoesResponseDTO;
import br.com.devgui.banktxtapi.model.Transacao;
import br.com.devgui.banktxtapi.model.Usuario;
import br.com.devgui.banktxtapi.repository.TransacaoRepository;
import br.com.devgui.banktxtapi.repository.UsuarioRepository;
import br.com.devgui.banktxtapi.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;
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
}
