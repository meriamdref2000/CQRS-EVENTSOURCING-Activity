package ma.dref.comptecqrses.query.repositories;

import ma.dref.comptecqrses.query.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation, Long> {
}
