package ma.dref.comptecqrses.query.repositories;

import ma.dref.comptecqrses.query.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
