package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.models.TipoUsuario;
import io.github.mateusbm.locmaq.models.Usuario;
import io.github.mateusbm.locmaq.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String nome, @RequestParam String senha) {
        Usuario usuario = usuarioService.autenticar(nome, senha);
        if (usuario != null) {
            if (usuario.getTipoUsuario() == TipoUsuario.GESTOR) {
                return "redirect:/html/cadastrarusuario.html";
            } else if (usuario.getTipoUsuario() == TipoUsuario.PLANEJADOR) {
                return "redirect:/html/planejador.html";
            } else if (usuario.getTipoUsuario() == TipoUsuario.LOGISTICA){
                return "redirect:/html/equipamento.html"; }
        }
        return "redirect:/login?error";
    }
}