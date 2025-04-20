package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.models.TipoUsuario;
import io.github.mateusbm.locmaq.models.Usuario;
import io.github.mateusbm.locmaq.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/api/login")
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
}