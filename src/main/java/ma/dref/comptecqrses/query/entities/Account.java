package ma.dref.comptecqrses.query.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dref.comptecqrses.commonApi.enums.AccountStatus;

import javax.persistence.*;
import java.time.Instant;
import java.util.Collection;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {
    @Id
    private String id;
    private double balance;
    private Instant createdAt;
    private String currency;
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    @OneToMany(mappedBy = "account")
    private Collection<AccountTransaction> transactions;
}