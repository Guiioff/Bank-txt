package br.com.devgui.banktxtapi.controller;

import br.com.devgui.banktxtapi.controller.request.UsuarioCadastroRequestDTO;
import br.com.devgui.banktxtapi.model.Usuario;
import br.com.devgui.banktxtapi.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarUsuario(@RequestBody @Valid UsuarioCadastroRequestDTO usuarioDto) {
        Usuario usuario = usuarioDto.toEntity();
        usuarioService.cadastrarUsuario(usuario);
    }
}
