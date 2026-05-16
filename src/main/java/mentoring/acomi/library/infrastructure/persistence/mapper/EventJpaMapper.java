package mentoring.acomi.library.infrastructure.persistence.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import mentoring.acomi.library.domain.events.DomainEventType;
import mentoring.acomi.library.domain.events.books.BookCopiesAddedEvent;
import mentoring.acomi.library.domain.events.books.BookCopiesAddedPayload;
import mentoring.acomi.library.domain.events.books.BookCopiesRemovedEvent;
import mentoring.acomi.library.domain.events.books.BookCopiesRemovedPayload;
import mentoring.acomi.library.domain.events.books.BookEvent;
import mentoring.acomi.library.domain.events.books.BookRegisteredEvent;
import mentoring.acomi.library.domain.events.books.BookRegisteredPayload;
import mentoring.acomi.library.infrastructure.persistence.entity.EventEntity;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class EventJpaMapper {

	private final ObjectMapper objectMapper;

	public EventJpaMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public BookEvent toDomain(EventEntity event) {

		DomainEventType eventType = DomainEventType.valueOf(event.getEventType());

		return getEvent(eventType, event);
	}

	private BookEvent getEvent(DomainEventType eventType, EventEntity event) {

		return switch (eventType) {

			case BookRegistered -> {
	
				BookRegisteredPayload payload = objectMapper.treeToValue(event.getPayload(), BookRegisteredPayload.class);
				yield new BookRegisteredEvent(event.getAggregateType(), event.getAggregateId(), payload,
						event.getOccurredAt());
			}

			case BookCopiesAdded -> {
				BookCopiesAddedPayload payload = objectMapper.treeToValue(event.getPayload(), BookCopiesAddedPayload.class);
				yield new BookCopiesAddedEvent(event.getAggregateType(), event.getAggregateId(), payload,
						event.getOccurredAt());
			}
			
			case BookCopiesRemoved -> {
				BookCopiesRemovedPayload payload = objectMapper.treeToValue(event.getPayload(), BookCopiesRemovedPayload.class);
				yield new BookCopiesRemovedEvent(event.getAggregateType(), event.getAggregateId(), payload,
						event.getOccurredAt());
			}

		};

	}

	public EventEntity toEntity(BookEvent event) {
		return new EventEntity(event.aggregateType(), event.aggregateId(), event.type().name(),
				toJsonNode(event.payload()), Instant.now());

	}

	private JsonNode toJsonNode(Object payload) {
		return objectMapper.valueToTree(payload);
	}

}
