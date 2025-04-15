package br.com.devgui.banktxtapi.repository;

import br.com.devgui.banktxtapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {}
