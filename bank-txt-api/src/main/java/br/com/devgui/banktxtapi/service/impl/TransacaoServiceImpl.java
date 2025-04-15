package br.com.devgui.banktxtapi.service.impl;

import br.com.devgui.banktxtapi.model.Transacao;
import br.com.devgui.banktxtapi.model.Usuario;
import br.com.devgui.banktxtapi.model.enums.TipoTransacao;
import br.com.devgui.banktxtapi.repository.TransacaoRepository;
import br.com.devgui.banktxtapi.repository.UsuarioRepository;
import br.com.devgui.banktxtapi.service.TransacaoService;
import org.springframework.stereotype.Service;
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
    public List<Transacao> processarArquivo(MultipartFile arquivo, Long usuarioId) {

        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);

        if (usuario.isEmpty()) throw new RuntimeException("Usuário não encontrado");

        List<Transacao> transacoes = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(arquivo.getInputStream()));
            String linha;

            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");

                LocalDate data = LocalDate.parse(partes[0]);
                TipoTransacao tipo = TipoTransacao.valueOf(partes[1]);
                BigDecimal valor = new BigDecimal(partes[2]);

                transacoes.add(new Transacao(data, valor, tipo, usuario.get()));
            }

            return transacoes;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar o arquivo", e);
        }
    }
}
