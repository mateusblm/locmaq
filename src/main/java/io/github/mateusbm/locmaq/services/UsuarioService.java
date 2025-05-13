package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.models.TipoUsuario;
import io.github.mateusbm.locmaq.models.Usuario;
import io.github.mateusbm.locmaq.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario autenticar(String nome, String senha) {
        Usuario usuario = usuarioRepository.findByNome(nome);
        if (usuario != null) {
            System.out.println("Senha fornecida: " + senha);
            System.out.println("Senha armazenada (hash): " + usuario.getSenha());
            if (passwordEncoder.matches(senha, usuario.getSenha())) {
                return usuario;
            }
        }
        return null;
    }

    @Transactional
    public void cadastrarUsuario(String nome, String senha, TipoUsuario tipoUsuario) {
        String senhaHash = passwordEncoder.encode(senha);
        Usuario novoUsuario = new Usuario(nome, senhaHash, tipoUsuario);
        usuarioRepository.save(novoUsuario);
    }

    public boolean existeUsuarioPorNome(String nome) {
        return usuarioRepository.findByNome(nome) != null;
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarPorTipo(TipoUsuario tipo) {
        return usuarioRepository.findByTipoUsuario(tipo);
    }
}