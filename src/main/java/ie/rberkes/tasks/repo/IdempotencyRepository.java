package ie.rberkes.tasks.repo;

import ie.rberkes.tasks.domain.Idempotency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface IdempotencyRepository extends JpaRepository<Idempotency, String> {

    @Modifying
    @Query(value = """
                INSERT INTO idempotency_keys (key)
                VALUES (:key)
                ON CONFLICT DO NOTHING
            """, nativeQuery = true)
    int insertIfNotExists(String key);
}