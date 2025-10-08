package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.models.TipoUsuario;
import io.github.mateusbm.locmaq.models.Usuario;
import io.github.mateusbm.locmaq.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final ActionLogService actionLogService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, ActionLogService actionLogService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.actionLogService = actionLogService;
        this.passwordEncoder = passwordEncoder;
    }

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

    @Transactional
    public void ativarUsuario(Long id, boolean ativo) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        usuario.setAtivo(ativo);
        usuarioRepository.save(usuario);
    }
}