package br.com.devgui.banktxtapi.service.impl;

import br.com.devgui.banktxtapi.model.Usuario;
import br.com.devgui.banktxtapi.repository.UsuarioRepository;
import br.com.devgui.banktxtapi.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public void cadastrarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }
}
