package io.gisla.domain.aggregate;

import io.gisla.domain.command.CreateSagaCommand;
import io.gisla.domain.command.StartSagaCommand;
import io.gisla.domain.event.SagaCreatedEvent;
import io.gisla.domain.event.SagaStartedEvent;
import io.gisla.domain.service.id.IdService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@Slf4j
@NoArgsConstructor
public class Saga {

//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    @AggregateIdentifier
    @Getter
    private String sagaId;

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    @CommandHandler
    public Saga(CreateSagaCommand cmd, IdService idService) {
        final String newSagaId = idService.generateId();
        log.info("Creating saga \"{}\".", newSagaId);
        apply(SagaCreatedEvent.builder()
                .sagaId(newSagaId)
                .transactions(cmd.getTransactions())
                .build());
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @CommandHandler
    public void handle(StartSagaCommand cmd) {
        log.info("Starting saga \"{}\".", sagaId);
        apply(SagaStartedEvent.builder()
                .build());
    }

    @EventSourcingHandler
    public void on(SagaCreatedEvent event) {
        this.sagaId = event.getSagaId();
    }

    @EventSourcingHandler
    public void on(SagaStartedEvent event) {
        // Do nothing for now.
    }
}
