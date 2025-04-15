package br.com.devgui.banktxtapi.service;

import br.com.devgui.banktxtapi.model.Transacao;
import br.com.devgui.banktxtapi.model.Usuario;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TransacaoService {

    void processarTransacoes(MultipartFile arquivo, Long usuarioId);

    List<Transacao> lerArquivo(MultipartFile arquivo, Usuario usuario);

    void salvarTransacoes(List<Transacao> transacoes, Usuario usuario);
}
