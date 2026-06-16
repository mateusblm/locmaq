package io.github.mateusbm.locmaq.repositories;

import io.github.mateusbm.locmaq.models.RfidLeitura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RfidLeituraRepository extends JpaRepository<RfidLeitura, Long> {
    List<RfidLeitura> findTop20ByOrderByDataHoraDesc();
    List<RfidLeitura> findTop100ByOrderByDataHoraDesc();
}
