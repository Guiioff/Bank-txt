package br.com.devgui.banktxtapi.service.impl;

import br.com.devgui.banktxtapi.exception.ArquivoInvalidoException;
import br.com.devgui.banktxtapi.exception.UsuarioNaoEncontradoException;
import br.com.devgui.banktxtapi.model.Transacao;
import br.com.devgui.banktxtapi.model.Usuario;
import br.com.devgui.banktxtapi.model.enums.TipoTransacao;
import br.com.devgui.banktxtapi.repository.TransacaoRepository;
import br.com.devgui.banktxtapi.repository.UsuarioRepository;
import br.com.devgui.banktxtapi.service.TransacaoService;
import br.com.devgui.banktxtapi.validator.TransacaoValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransacaoServiceImpl implements TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TransacaoValidator transacaoValidator;

    public TransacaoServiceImpl(TransacaoRepository transacaoRepository,
                                UsuarioRepository usuarioRepository,
                                TransacaoValidator transacaoValidator) {
        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.transacaoValidator = transacaoValidator;
    }

    @PersistenceContext
    private EntityManager entityManager;

    private static final int BATCH_SIZE = 100;

    @Override
    @Transactional
    public void processarTransacoes(MultipartFile arquivo, Long usuarioId) {
        Usuario usuario = buscarUsuarioOuLancar(usuarioId);
        List<Transacao> transacoes = lerArquivo(arquivo, usuario);
        salvarTransacoes(transacoes);
    }

    private Usuario buscarUsuarioOuLancar(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado com o ID: " + usuarioId));
    }

    @Override
    public List<Transacao> lerArquivo(MultipartFile arquivo, Usuario usuario) {
        List<Transacao> transacoes = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(arquivo.getInputStream()))) {
            String linha;
            int linhaAtual = 1;

            while ((linha = reader.readLine()) != null) {
                Transacao transacao = converterLinhaParaTransacao(linha, linhaAtual, usuario);
                transacoes.add(transacao);
                linhaAtual++;
            }

            if (transacoes.isEmpty()) {
                throw new ArquivoInvalidoException("O arquivo não contém transações válidas.");
            }

            return transacoes;

        } catch (IOException e) {
            throw new ArquivoInvalidoException("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    private Transacao converterLinhaParaTransacao(String linha, int linhaAtual, Usuario usuario) {
        String[] partes = linha.split(";");

        if (partes.length != 3) {
            throw new ArquivoInvalidoException("Linha " + linhaAtual + " está mal formatada: '" + linha + "'");
        }

        LocalDate data = parseData(partes[0], linhaAtual);
        TipoTransacao tipo = parseTipoTransacao(partes[1], linhaAtual);
        BigDecimal valor = parseValor(partes[2], linhaAtual);

        if (valor.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArquivoInvalidoException("Linha " + linhaAtual + ": transação com valor 0 é inválida.");
        }

        return new Transacao(data, valor, tipo, usuario);
    }

    private LocalDate parseData(String dataStr, int linhaAtual) {
        try {
            return LocalDate.parse(dataStr);
        } catch (DateTimeParseException e) {
            throw new ArquivoInvalidoException("Linha " + linhaAtual + ": data inválida: '" + dataStr + "'. Formato esperado: yyyy-MM-dd");
        }
    }

    private TipoTransacao parseTipoTransacao(String tipoStr, int linhaAtual) {
        try {
            return TipoTransacao.valueOf(tipoStr);
        } catch (IllegalArgumentException e) {
            throw new ArquivoInvalidoException("Linha " + linhaAtual + ": tipo de transação inválido: '" + tipoStr + "'");
        }
    }

    private BigDecimal parseValor(String valorStr, int linhaAtual) {
        try {
            return new BigDecimal(valorStr);
        } catch (NumberFormatException e) {
            throw new ArquivoInvalidoException("Linha " + linhaAtual + ": valor inválido: '" + valorStr + "'");
        }
    }

    @Override
    public void salvarTransacoes(List<Transacao> transacoes) {
        transacaoValidator.validar(transacoes);
        for (int i = 0; i < transacoes.size(); i++) {
            entityManager.persist(transacoes.get(i));

            if (i % BATCH_SIZE == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }
}
