package br.com.devgui.banktxtapi.service;

import br.com.devgui.banktxtapi.model.Transacao;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TransacaoService {

    void processarArquivo(MultipartFile arquivo, Long usuarioId);
}
