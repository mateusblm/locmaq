package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.models.TipoUsuario;
import io.github.mateusbm.locmaq.models.Usuario;
import io.github.mateusbm.locmaq.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

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
        // Se quiser devolver só nome/tipo, pode criar um DTO, mas assim já funciona para o frontend
        return usuarioService.buscarPorNome(nome);
    }
}
