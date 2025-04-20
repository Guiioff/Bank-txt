package br.com.devgui.banktxtapi.controller;

import br.com.devgui.banktxtapi.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/transacoes")
@Tag(name = "Transações", description = "Operações relacionadas à transações")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @Operation(
            summary = "Upload de arquivo de transações",
            description = "Processa um arquivo .txt contendo transações no formato 'DATA;TIPO;VALOR' e as associa ao " +
                    "usuário informado. Transações inválidas são ignoradas ou rejeitadas conforme a validação."
    )
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public void upload(@RequestParam("arquivo") MultipartFile arquivo, @RequestParam("usuarioId") Long usuarioId) {
        transacaoService.processarTransacoes(arquivo, usuarioId);
    }
}
