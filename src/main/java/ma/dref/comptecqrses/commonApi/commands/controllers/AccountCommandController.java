package ma.dref.comptecqrses.commonApi.commands.controllers;


import lombok.AllArgsConstructor;
import ma.dref.comptecqrses.commonApi.commands.CreateAccountCommand;
import ma.dref.comptecqrses.commonApi.commands.CreditAccountCommand;
import ma.dref.comptecqrses.commonApi.commands.DebitAccountCommand;
import ma.dref.comptecqrses.commonApi.dtos.CreateAccountRequestDTO;
import ma.dref.comptecqrses.commonApi.dtos.CreditAccountRequestDTO;
import ma.dref.comptecqrses.commonApi.dtos.DebitAccountRequestDTO;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "/commands/account")
@AllArgsConstructor
public class AccountCommandController {
    private EventStore eventStore;
    private CommandGateway commandGateway;
    @PostMapping(path = "/create")
    public CompletableFuture<String> createAccount(@RequestBody CreateAccountRequestDTO request){
        CompletableFuture<String> commandResponse = commandGateway.send(new CreateAccountCommand(
                UUID.randomUUID().toString(),
                request.getInitialBalance(),
                request.getCurrency()
        ));
        return commandResponse;
    }

    @PutMapping(path = "/credit")
    public CompletableFuture<String> creditAccount(@RequestBody CreditAccountRequestDTO creditAccountRequestDTO) {
        CompletableFuture<String> response = commandGateway.send(
                new CreditAccountCommand(
                        creditAccountRequestDTO.getId(),
                        creditAccountRequestDTO.getAmount(),
                        creditAccountRequestDTO.getCurrency()
                )
        );
        return response;
    }

    @PutMapping(path = "/debit")
    public CompletableFuture<String> debitAccount(@RequestBody DebitAccountRequestDTO debitAccountRequestDTO) {
        CompletableFuture<String> response = commandGateway.send(
                new DebitAccountCommand(
                        debitAccountRequestDTO.getId(),
                        debitAccountRequestDTO.getAmount(),
                        debitAccountRequestDTO.getCurrency()
                )
        );
        return response;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception exception){
        ResponseEntity<String> entity = new ResponseEntity<>(
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
                );
        return entity;
    }

    @GetMapping("/eventStore/{accountId}")
    public Stream<? extends DomainEventMessage<?>> eventStore(@PathVariable String accountId){
        return eventStore.readEvents(accountId).asStream();
    }
}
