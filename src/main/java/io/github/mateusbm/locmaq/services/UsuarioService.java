package io.github.mateusbm.locmaq.services;


import io.github.mateusbm.locmaq.models.TipoUsuario;
import io.github.mateusbm.locmaq.models.Usuario;
import io.github.mateusbm.locmaq.repositories.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario autenticar(String nome, String senha) {
        Usuario usuario = usuarioRepository.findByNome(nome);
        if (usuario != null && usuario.getSenha().equals(senha)) {
            return usuario;
        }
        return null;
    }
    @Transactional
    public void cadastrarUsuario(String nome, String senha, TipoUsuario tipoUsuario) {
        Usuario novoUsuario = new Usuario(nome, senha, tipoUsuario);
        usuarioRepository.save(novoUsuario);
    }
    public boolean existeUsuarioPorNome(String nome) {
        return usuarioRepository.findByNome(nome) != null;
    }
}

