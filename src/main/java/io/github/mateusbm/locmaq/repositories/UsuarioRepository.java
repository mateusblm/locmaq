package io.github.mateusbm.locmaq.repositories;

import io.github.mateusbm.locmaq.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByNome(String nome);

}
