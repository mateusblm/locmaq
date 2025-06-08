package io.github.mateusbm.locmaq.config;

import io.github.mateusbm.locmaq.models.Usuario;
import io.github.mateusbm.locmaq.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNome(username);
        if (usuario == null) throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        if (!usuario.isAtivo()) throw new DisabledException("Usuario desativado");
        return new User(
                usuario.getNome(),
                usuario.getSenha(),
                Collections.singletonList(() -> "ROLE_" + usuario.getTipoUsuario().name())
        );
    }
}