package io.github.mateusbm.locmaq.controllers;
import io.github.mateusbm.locmaq.models.TipoUsuario;
import io.github.mateusbm.locmaq.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class GestorController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/gestor")
    public String gestorHome() {
        return "gestor";
    }

    @GetMapping("/gestor/cadastrar")
    public String mostrarFormularioCadastro() {
        return "cadastrarUsuario";
    }

    @PostMapping("/gestor/cadastrar")
    public ResponseEntity<String> cadastrarUsuario(@RequestParam String nome,
                                                   @RequestParam String senha,
                                                   @RequestParam TipoUsuario tipoUsuario) {
        try {
            usuarioService.cadastrarUsuario(nome, senha, tipoUsuario);
            return ResponseEntity.ok("Usuário cadastrado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cadastrar o usuário.");
        }
    }
}