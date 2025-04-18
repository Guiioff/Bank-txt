package br.com.devgui.banktxtapi.service;

import br.com.devgui.banktxtapi.controller.response.UsuarioTransacoesResponseDTO;
import br.com.devgui.banktxtapi.model.Usuario;

public interface UsuarioService {

    void cadastrarUsuario(Usuario usuario);

    UsuarioTransacoesResponseDTO listarTransacoes(Long idUsuario);
}
