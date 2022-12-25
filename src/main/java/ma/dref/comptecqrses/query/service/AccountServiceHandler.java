package ma.dref.comptecqrses.query.service;

import lombok.extern.slf4j.Slf4j;
import ma.dref.comptecqrses.commonApi.queries.GetAccountByIdQuery;
import ma.dref.comptecqrses.commonApi.queries.GetAllAccountsQuery;
import ma.dref.comptecqrses.commonApi.enums.AccountStatus;
import ma.dref.comptecqrses.commonApi.enums.TransactionType;
import ma.dref.comptecqrses.commonApi.events.AccountActivatedEvent;
import ma.dref.comptecqrses.commonApi.events.AccountCreatedEvent;
import ma.dref.comptecqrses.commonApi.events.AccountCreditedEvent;
import ma.dref.comptecqrses.commonApi.events.AccountDebitedEvent;
import ma.dref.comptecqrses.query.entities.Account;
import ma.dref.comptecqrses.query.entities.AccountTransaction;
import ma.dref.comptecqrses.query.repositories.AccountRepository;
import ma.dref.comptecqrses.query.repositories.TransactionRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@Transactional
public class AccountServiceHandler {
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    public AccountServiceHandler(AccountRepository accountRepository, TransactionRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = operationRepository;
    }

    @EventHandler
    public void on(AccountCreatedEvent event, EventMessage<AccountCreditedEvent> eventMessage)
    {
        log.info("******************************************");
        log.info("Handling AccountCreatedEvent: " + event.getId());
        log.info("AccountCreatedEvent recieved!");
        Account account = new Account();
        account.setId(event.getId());
        account.setAccountStatus(AccountStatus.ACTIVATED);
        account.setBalance(event.getInitialBalance());
        account.setCurrency(event.getCurrency());
        account.setCreatedAt(eventMessage.getTimestamp());

        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountActivatedEvent event)
    {
        log.info("AccountActivatedEvent recieved!");
        Account account = accountRepository.findById(event.getId()).get();
        account.setAccountStatus(event.getStatus());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountCreditedEvent event)
    {
        log.info("AccountCreditedEvent recieved!");
        Account account = accountRepository.findById(event.getId()).get();
        AccountTransaction operation = new AccountTransaction();
        operation.setTransactionType(TransactionType.CREDIT);
        operation.setAmount(event.getAmount());
        operation.setTimestamp(new Date());
        operation.setAccount(account);
        transactionRepository.save(operation);
        account.setBalance(account.getBalance() + event.getAmount());
        accountRepository.save(account);
    }


    @EventHandler
    public void on(AccountDebitedEvent event)
    {
        log.info("AccountDebitedEvent recieved!");
        Account account = accountRepository.findById(event.getId()).get();
        AccountTransaction operation = new AccountTransaction();
        operation.setTransactionType(TransactionType.DEBIT);
        operation.setAmount(event.getAmount());
        operation.setTimestamp(new Date());
        operation.setAccount(account);
        transactionRepository.save(operation);
        account.setBalance(account.getBalance() - event.getAmount());
        accountRepository.save(account);
    }

    @QueryHandler
    public List<Account> on(GetAllAccountsQuery query) {
        return accountRepository.findAll();
    }

    @QueryHandler
    public Account on(GetAccountByIdQuery query) {
        return accountRepository.findById(query.getId()).get();
    }

}