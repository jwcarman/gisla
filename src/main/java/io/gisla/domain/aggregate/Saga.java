/*
 * Copyright (c) 2018 The Gisla Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gisla.domain.aggregate;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.gisla.domain.command.CompleteTransactionCommand;
import io.gisla.domain.command.CreateSagaCommand;
import io.gisla.domain.command.StartSagaCommand;
import io.gisla.domain.event.SagaCreatedEvent;
import io.gisla.domain.event.SagaStartedEvent;
import io.gisla.domain.event.TransactionCompletedEvent;
import io.gisla.domain.mapping.DomainMapper;
import io.gisla.domain.service.id.IdService;
import io.gisla.domain.service.tx.TransactionService;
import io.gisla.domain.value.CompletedTransaction;
import io.gisla.domain.value.PendingTransaction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static com.google.common.base.Verify.verify;
import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@Slf4j
@NoArgsConstructor
public class Saga {

//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    @AggregateIdentifier
    @Getter(AccessLevel.PACKAGE)
    private String sagaId;

    @Getter(AccessLevel.PACKAGE)
    private List<PendingTransaction> pendingTransactions;

    @Getter(AccessLevel.PACKAGE)
    private List<CompletedTransaction> completedTransactions;

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    @CommandHandler
    public Saga(CreateSagaCommand cmd, IdService idService, DomainMapper mapper) {
        final String newSagaId = idService.generateId();
        log.info("Creating saga \"{}\".", newSagaId);

        final List<PendingTransaction> pending = cmd.getTransactions().stream()
                .map(descriptor -> mapper.mapTransactionDescriptor(idService.generateId(), descriptor))
                .collect(Collectors.toCollection(LinkedList::new));

        apply(SagaCreatedEvent.builder()
                .sagaId(newSagaId)
                .pendingTransactions(pending)
                .build());
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @CommandHandler
    public void handle(StartSagaCommand cmd, TransactionService transactionService, DomainMapper mapper, UnitOfWork<?> unitOfWork) {
        log.info("Starting saga \"{}\".", sagaId);
        apply(SagaStartedEvent.builder().build());
        beginNextTransaction(transactionService, mapper, unitOfWork);
    }

    private void beginNextTransaction(TransactionService transactionService, DomainMapper mapper, UnitOfWork<?> unitOfWork) {
        if (!pendingTransactions.isEmpty()) {
            final PendingTransaction nextTransaction = pendingTransactions.get(0);
            log.info("Beginning transaction \"{}\" of saga \"{}\".", nextTransaction.getTransactionId(), sagaId);
            unitOfWork.afterCommit(uow -> transactionService.executeTransaction(mapper.mapPendingTransaction(sagaId, nextTransaction)));
        }
    }

    @CommandHandler
    public void handle(CompleteTransactionCommand cmd, TransactionService transactionService, DomainMapper domainMapper, UnitOfWork<?> unitOfWork) {
        final String expectedTransactionId = cmd.getTransactionId();
        verifyCurrentPendingTransaction(expectedTransactionId);
        apply(domainMapper.mapToEvent(cmd));
        beginNextTransaction(transactionService, domainMapper, unitOfWork);
    }

    private void verifyCurrentPendingTransaction(String expectedTransactionId) {
        verify(!pendingTransactions.isEmpty(), "No current transaction in progress.");
        final PendingTransaction currentTransaction = pendingTransactions.get(0);
        verify(currentTransaction.getTransactionId().equals(expectedTransactionId), "Transaction \"%s\" is not current transaction.", expectedTransactionId);
    }

    @EventSourcingHandler
    public void on(TransactionCompletedEvent event) {
        final PendingTransaction currentTransaction = pendingTransactions.remove(0);
        completedTransactions.add(0, CompletedTransaction.builder()
                .transactionId(currentTransaction.getTransactionId())
                .transactionType(currentTransaction.getTransactionType())
                .compensationSpec(event.getCompensationSpec())
                .build());
    }

    @EventSourcingHandler
    public void on(SagaCreatedEvent event) {
        this.sagaId = event.getSagaId();
        this.pendingTransactions = new LinkedList<>(event.getPendingTransactions());
        this.completedTransactions = new LinkedList<>();
    }
}
