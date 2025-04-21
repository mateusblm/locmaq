package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.models.TipoUsuario;
import io.github.mateusbm.locmaq.models.Usuario;
import io.github.mateusbm.locmaq.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> cred) {
        String nome = cred.get("nome");
        String senha = cred.get("senha");
        Usuario usuario = usuarioService.autenticar(nome, senha);
        if (usuario != null) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("nome", usuario.getNome());
            resp.put("tipoUsuario", usuario.getTipoUsuario());
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.status(401).body("Usuário ou senha inválido");
    }

    @GetMapping
    public List<Usuario> listarPorTipo(@RequestParam(value = "tipo", required = false) TipoUsuario tipo) {
        if (tipo == null) {
            return usuarioService.listarTodos();
        } else {
            return usuarioService.listarPorTipo(tipo);
        }
    }

}
