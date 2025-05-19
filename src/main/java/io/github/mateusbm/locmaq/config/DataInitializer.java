package io.github.mateusbm.locmaq.config;

import io.github.mateusbm.locmaq.models.TipoUsuario;
import io.github.mateusbm.locmaq.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void run(String... args) throws Exception {
        if (!usuarioService.existeUsuarioPorNome("admin")) {
            usuarioService.cadastrarUsuario("admin", "root", TipoUsuario.GESTOR, "Administrador");
            System.out.println("Usuário admin criado com sucesso.");
        } else {
            System.out.println("Usuario admin já criado");
        }
    }
}