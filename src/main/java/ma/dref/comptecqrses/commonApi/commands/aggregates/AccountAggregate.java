package ma.dref.comptecqrses.commonApi.commands.aggregates;

import ma.dref.comptecqrses.commonApi.commands.CreateAccountCommand;
import ma.dref.comptecqrses.commonApi.commands.CreditAccountCommand;
import ma.dref.comptecqrses.commonApi.commands.DebitAccountCommand;
import ma.dref.comptecqrses.commonApi.enums.AccountStatus;
import ma.dref.comptecqrses.commonApi.events.AccountActivatedEvent;
import ma.dref.comptecqrses.commonApi.events.AccountCreatedEvent;
import ma.dref.comptecqrses.commonApi.events.AccountCreditedEvent;
import ma.dref.comptecqrses.commonApi.events.AccountDebitedEvent;
import ma.dref.comptecqrses.commonApi.exceptions.BalanceInSufficientException;
import ma.dref.comptecqrses.commonApi.exceptions.NegativeAmountException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;


@Aggregate
public class AccountAggregate {
    @AggregateIdentifier
    private String accountId;
    private double balance;
    private String currency;
    private AccountStatus status;

    public AccountAggregate() {
        //Required by AXON
    }

    @CommandHandler
    public AccountAggregate(CreateAccountCommand createAccountCommand) {
        if(createAccountCommand.getInitialBalance()<0) throw new RuntimeException("Impossible de créer un compte avec un solde négatif");
        AggregateLifecycle.apply(new AccountCreatedEvent(
                createAccountCommand.getId(),
                createAccountCommand.getInitialBalance(),
                createAccountCommand.getCurrency()
        ));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event){
        this.accountId=event.getId();
        this.balance= event.getInitialBalance();
        this.currency= event.getCurrency();
        this.status=AccountStatus.CREATED;
        AggregateLifecycle.apply(new AccountActivatedEvent(
                event.getId(),
                AccountStatus.ACTIVATED
        ));
    }

    @EventSourcingHandler
    public void on(AccountActivatedEvent event){
        this.status=event.getStatus();
    }


    @CommandHandler
    public void handle(CreditAccountCommand command) {
        if(command.getAmount() <0) throw new NegativeAmountException("Amount should not be negative");

        AggregateLifecycle.apply(new AccountCreditedEvent(
                command.getId(),
                command.getAmount(),
                command.getCurrency()
        ));
    }

    @EventSourcingHandler
    public void on(AccountCreditedEvent event) {
        this.balance += event.getAmount();
    }


    @CommandHandler
    public void handle(DebitAccountCommand command) {
        if(command.getAmount() <0) throw new NegativeAmountException("Amount should not be negative");
        if(balance < command.getAmount() ) throw new BalanceInSufficientException("Balance is InSufficient !" + balance);

        AggregateLifecycle.apply(new AccountDebitedEvent(
                command.getId(),
                command.getAmount(),
                command.getCurrency()
        ));
    }

    @EventSourcingHandler
    public void on(AccountDebitedEvent event) {
        this.balance -= event.getAmount();
    }



}
