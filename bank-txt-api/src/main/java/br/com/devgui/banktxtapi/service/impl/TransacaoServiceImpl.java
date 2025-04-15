package br.com.devgui.banktxtapi.service.impl;

import br.com.devgui.banktxtapi.model.enums.TipoTransacao;
import br.com.devgui.banktxtapi.service.TransacaoService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class TransacaoServiceImpl implements TransacaoService {

    @Override
    public void processarArquivo(MultipartFile arquivo, Long usuarioId) {
    try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(arquivo.getInputStream()));
        String linha;

        while ((linha = reader.readLine()) != null) {
            String[] partes = linha.split(";");

            LocalDate data = LocalDate.parse(partes[0]);
            TipoTransacao tipo = TipoTransacao.valueOf(partes[1]);
            BigDecimal valor = new BigDecimal(partes[2]);

            System.out.println(data);
            System.out.println(tipo);
            System.out.println(valor);
            System.out.println("---------------");
      }
    } catch (Exception e) {
        throw new RuntimeException("Erro ao processar o arquivo", e);
    }}
}
