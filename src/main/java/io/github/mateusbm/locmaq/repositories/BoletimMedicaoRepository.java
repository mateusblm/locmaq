package io.github.mateusbm.locmaq.repositories;

import io.github.mateusbm.locmaq.models.BoletimMedicao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoletimMedicaoRepository extends JpaRepository<BoletimMedicao, Long> {
}
