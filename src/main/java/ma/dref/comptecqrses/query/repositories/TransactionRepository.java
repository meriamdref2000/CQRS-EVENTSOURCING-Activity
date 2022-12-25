package ma.dref.comptecqrses.query.repositories;

import ma.dref.comptecqrses.query.entities.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<AccountTransaction, Long> {
}
