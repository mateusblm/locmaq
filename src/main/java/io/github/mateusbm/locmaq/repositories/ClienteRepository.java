package io.github.mateusbm.locmaq.repositories;

import io.github.mateusbm.locmaq.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
