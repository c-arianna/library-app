package mentoring.acomi.library.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import mentoring.acomi.library.application.aggregates.AggregateType;
import mentoring.acomi.library.application.errors.EventStoreInconsistencyException;
import mentoring.acomi.library.domain.events.BookCopiesAddedEvent;
import mentoring.acomi.library.domain.events.BookCopiesRemovedEvent;
import mentoring.acomi.library.domain.events.BookRegisteredEvent;
import mentoring.acomi.library.domain.events.BookReservedEvent;
import mentoring.acomi.library.domain.events.DomainEvent;
import mentoring.acomi.library.domain.events.DomainEventType;
import mentoring.acomi.library.domain.events.LoanFailedEvent;
import mentoring.acomi.library.domain.events.LoanRequestedEvent;
import mentoring.acomi.library.domain.events.payload.BookCopiesAddedPayload;
import mentoring.acomi.library.domain.events.payload.BookCopiesRemovedPayload;
import mentoring.acomi.library.domain.events.payload.BookLoanPayload;
import mentoring.acomi.library.domain.events.payload.BookRegisteredPayload;
import mentoring.acomi.library.domain.events.payload.LoanFailedPayload;
import mentoring.acomi.library.domain.events.payload.LoanRequestPayload;
import mentoring.acomi.library.infrastructure.persistence.entity.EventEntity;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class EventJpaMapper {

	private final ObjectMapper objectMapper;

	public EventJpaMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public DomainEvent toDomain(EventEntity event) {

		DomainEventType eventType = DomainEventType.valueOf(event.getEventType());

		DomainEvent domainEvent = getEvent(eventType, event);

		AggregateType entityType = AggregateType.valueOf(event.getAggregateType());

		if (domainEvent.aggregateType() != entityType) {
			throw new EventStoreInconsistencyException(
					String.format("Event aggregate type mismatch: entity=%s, event=%s, type=%s", entityType,
							domainEvent.aggregateType().name(), eventType.name()));
		}

		return domainEvent;
	}

	private DomainEvent getEvent(DomainEventType eventType, EventEntity event) {

		return switch (eventType) {

		case BookRegistered -> {
			BookRegisteredPayload payload = objectMapper.treeToValue(event.getPayload(), BookRegisteredPayload.class);
			yield new BookRegisteredEvent(event.getAggregateId(), payload, event.getOccurredAt());
		}

		case BookCopiesAdded -> {
			BookCopiesAddedPayload payload = objectMapper.treeToValue(event.getPayload(), BookCopiesAddedPayload.class);
			yield new BookCopiesAddedEvent(event.getAggregateId(), payload, event.getOccurredAt());
		}

		case BookCopiesRemoved -> {
			BookCopiesRemovedPayload payload = objectMapper.treeToValue(event.getPayload(),
					BookCopiesRemovedPayload.class);
			yield new BookCopiesRemovedEvent(event.getAggregateId(), payload, event.getOccurredAt());
		}

		case BookReserved -> {
			BookLoanPayload payload = objectMapper.treeToValue(event.getPayload(),
					BookLoanPayload.class);
			yield new BookReservedEvent(event.getAggregateId(), payload, event.getOccurredAt());
			
		}
		
		case LoanRequested -> {
			LoanRequestPayload payload = objectMapper.treeToValue(event.getPayload(), LoanRequestPayload.class);
			yield new LoanRequestedEvent(event.getAggregateId(), payload, event.getOccurredAt());
		}
		
		case LoanFailed -> {
			LoanFailedPayload payload = objectMapper.treeToValue(event.getPayload(), LoanFailedPayload.class);
			yield new LoanFailedEvent(event.getAggregateId(), payload, event.getOccurredAt());
		}

		};

	}

	public EventEntity toEntity(DomainEvent event) {
		return new EventEntity(event.aggregateType().name(), event.aggregateId(), event.type().name(),
				toJsonNode(event.payload()), event.occurredAt());

	}

	private JsonNode toJsonNode(Object payload) {
		return objectMapper.valueToTree(payload);
	}

}
