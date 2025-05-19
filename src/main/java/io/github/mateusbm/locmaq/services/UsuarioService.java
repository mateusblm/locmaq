package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.models.TipoUsuario;
import io.github.mateusbm.locmaq.models.Usuario;
import io.github.mateusbm.locmaq.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ActionLogService actionLogService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean existeUsuarioPorNome(String nome) {
        return usuarioRepository.findByNome(nome) != null;
    }

    @Transactional
    public void cadastrarUsuario(String nome, String senha, TipoUsuario tipoUsuario, String gestorNome) {
        Usuario novoUsuario = new Usuario(nome, passwordEncoder.encode(senha), tipoUsuario);
        Usuario saved = usuarioRepository.save(novoUsuario);
        actionLogService.logAction("Cadastro de usuário",
                gestorNome,
                "Usuário ID: " + saved.getId() + ", Nome: " + saved.getNome() + ", Tipo: " + saved.getTipoUsuario());
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

    public Usuario buscarPorNome(String nome) {
        return usuarioRepository.findByNome(nome);
    }
}