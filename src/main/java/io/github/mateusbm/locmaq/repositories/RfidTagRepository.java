package io.github.mateusbm.locmaq.repositories;

import io.github.mateusbm.locmaq.models.RfidTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RfidTagRepository extends JpaRepository<RfidTag, Long> {
    boolean existsByUid(String uid);
    Optional<RfidTag> findByUid(String uid);
    Optional<RfidTag> findByUidAndAtivoTrue(String uid);
}
