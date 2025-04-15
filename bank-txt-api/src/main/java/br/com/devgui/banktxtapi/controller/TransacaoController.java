package br.com.devgui.banktxtapi.controller;

import br.com.devgui.banktxtapi.service.TransacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public void upload(@RequestParam("arquivo") MultipartFile arquivo, @RequestParam("usuarioId") Long usuarioId) {
        transacaoService.processarTransacoes(arquivo, usuarioId);
    }
}
