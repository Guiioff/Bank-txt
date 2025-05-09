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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public TransacaoServiceImpl(TransacaoRepository transacaoRepository, UsuarioRepository usuarioRepository, TransacaoValidator transacaoValidator) {
        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.transacaoValidator = transacaoValidator;
    }

    @Override
    @Transactional
    public void processarTransacoes(MultipartFile arquivo, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado com o ID: " + usuarioId));

        List<Transacao> transacaos = this.lerArquivo(arquivo, usuario);
        this.salvarTransacoes(transacaos);
    }

    @Override
    public List<Transacao> lerArquivo(MultipartFile arquivo, Usuario usuario) {
        List<Transacao> transacoes = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(arquivo.getInputStream()));

            String linha;
            int linhaAtual = 1;

            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");

                if (partes.length != 3) {
                    throw new ArquivoInvalidoException("Linha " + linhaAtual + " está mal formatada: '" + linha + "'");
                }

                LocalDate data;
                try {
                    data = LocalDate.parse(partes[0]);
                } catch (DateTimeParseException e) {
                    throw new ArquivoInvalidoException("Linha " + linhaAtual + ": data inválida: '" + partes[0] + "'. Formato esperado: yyyy-MM-dd");
                }

                TipoTransacao tipo;
                try {
                    tipo = TipoTransacao.valueOf(partes[1]);
                } catch (IllegalArgumentException e) {
                    throw new ArquivoInvalidoException("Linha " + linhaAtual + ": tipo de transação inválido: '" + partes[1] + "'");
                }

                BigDecimal valor;
                try {
                    valor = new BigDecimal(partes[2]);
                } catch (NumberFormatException e) {
                    throw new ArquivoInvalidoException("Linha " + linhaAtual + ": valor inválido: '" + partes[2] + "'");
                }

                if (valor.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArquivoInvalidoException("Linha " + linhaAtual + ": transação com valor 0 é inválida.");
                }

                transacoes.add(new Transacao(data, valor, tipo, usuario));
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

    @Override
    public void salvarTransacoes(List<Transacao> transacoes) {
        transacaoValidator.validar(transacoes);
        this.transacaoRepository.saveAll(transacoes);
    }
}
