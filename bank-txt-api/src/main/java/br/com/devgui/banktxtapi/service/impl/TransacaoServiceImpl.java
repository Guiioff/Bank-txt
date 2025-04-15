package br.com.devgui.banktxtapi.service.impl;

import br.com.devgui.banktxtapi.model.Transacao;
import br.com.devgui.banktxtapi.model.Usuario;
import br.com.devgui.banktxtapi.model.enums.TipoTransacao;
import br.com.devgui.banktxtapi.repository.TransacaoRepository;
import br.com.devgui.banktxtapi.repository.UsuarioRepository;
import br.com.devgui.banktxtapi.service.TransacaoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransacaoServiceImpl implements TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public TransacaoServiceImpl(TransacaoRepository transacaoRepository, UsuarioRepository usuarioRepository) {
        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void processarTransacoes(MultipartFile arquivo, Long usuarioId) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);

        if (usuarioOptional.isEmpty()) throw new RuntimeException("Usuário não encontrado");

        Usuario usuario = usuarioOptional.get();
        List<Transacao> transacaos = this.lerArquivo(arquivo, usuario);
        this.salvarTransacoes(transacaos);
    }

    @Override
    public List<Transacao> lerArquivo(MultipartFile arquivo, Usuario usuario) {
        List<Transacao> transacoes = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(arquivo.getInputStream()));
            String linha;

            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");

                LocalDate data = LocalDate.parse(partes[0]);
                TipoTransacao tipo = TipoTransacao.valueOf(partes[1]);
                BigDecimal valor = new BigDecimal(partes[2]);

                transacoes.add(new Transacao(data, valor, tipo, usuario));
            }
            return transacoes;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar o arquivo", e);
        }
    }

    @Override
    @Transactional
    public void salvarTransacoes(List<Transacao> transacoes) {

        List<Transacao> transacoesValidas = new ArrayList<>();

        for (Transacao transacao : transacoes) {
            if (transacao == null) {
                throw new IllegalArgumentException("Transação nula encontrada.");
            }

            if (transacao.getValor() == null || transacao.getValor().compareTo(BigDecimal.ZERO) == 0 ) {
                throw new IllegalArgumentException("Valor da transação não pode ser nulo/zero.");
            }

            if (transacao.getData() == null) {
                throw new IllegalArgumentException("Data da transação não pode ser nula.");
            }

            transacoesValidas.add(transacao);
        }
        this.transacaoRepository.saveAll(transacoesValidas);
    }
}
