package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.models.TipoUsuario;
import io.github.mateusbm.locmaq.models.Usuario;
import io.github.mateusbm.locmaq.repositories.EquipamentoRepository;
import io.github.mateusbm.locmaq.repositories.UsuarioRepository;
import io.github.mateusbm.locmaq.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Usuario> listarPorTipo(@RequestParam(value = "tipo", required = false) TipoUsuario tipo) {
        if (tipo == null) {
            return usuarioService.listarTodos();
        } else {
            return usuarioService.listarPorTipo(tipo);
        }
    }

    @GetMapping("/me")
    public Usuario getUsuarioLogado() {
        String nome = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioService.buscarPorNome(nome);
    }

    @PostMapping("/me/desativar")
    public ResponseEntity<?> desativarProprioUsuario() {
        String nome = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByNome(nome);
        if (usuario == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        usuarioService.ativarUsuario(usuario.getId(), false);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}
