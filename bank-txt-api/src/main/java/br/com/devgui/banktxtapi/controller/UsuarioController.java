package br.com.devgui.banktxtapi.controller;

import br.com.devgui.banktxtapi.controller.request.UsuarioCadastroRequestDTO;
import br.com.devgui.banktxtapi.controller.response.UsuarioResumoResponseDTO;
import br.com.devgui.banktxtapi.controller.response.UsuarioTransacoesResponseDTO;
import br.com.devgui.banktxtapi.model.Usuario;
import br.com.devgui.banktxtapi.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "Operações relacionadas à usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(
            summary = "Cadastro de usuário",
            description = "Cadastra um novo usuário no sistema a partir dos dados informados."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarUsuario(@RequestBody @Valid UsuarioCadastroRequestDTO usuarioDto) {
        Usuario usuario = usuarioDto.toEntity();
        usuarioService.cadastrarUsuario(usuario);
    }

    @Operation(
            summary = "Listar transações do usuário",
            description = "Retorna todas as transações financeiras associadas ao usuário informado."
    )
    @GetMapping("/{id}/transacoes")
    @ResponseStatus(HttpStatus.OK)
    public UsuarioTransacoesResponseDTO buscarTransacoes(@PathVariable Long id){
        return usuarioService.listarTransacoes(id);
    }

    @Operation(
            summary = "Listar transações do usuário por período",
            description = "Retorna as transações do usuário entre duas datas especificadas."
    )
    @GetMapping("/{id}/transacoes/por-data")
    @ResponseStatus(HttpStatus.OK)
    public UsuarioTransacoesResponseDTO buscarTransacoesPorData(@PathVariable Long id,
                                                                @RequestParam("inicio") LocalDate dataInicial,
                                                                @RequestParam("fim") LocalDate dataFinal){
        return usuarioService.listarTransacoesEntreDatas(id, dataInicial, dataFinal);
    }

    @Operation(
            summary = "Resumo financeiro do usuário",
            description = "Retorna um resumo com totais por tipo de transação, saldo final, média e outras informações agregadas."
    )
    @GetMapping("/{id}/resumo")
    @ResponseStatus(HttpStatus.OK)
    public UsuarioResumoResponseDTO buscarResumo(@PathVariable Long id){
        return usuarioService.obterResumo(id);
    }
}
