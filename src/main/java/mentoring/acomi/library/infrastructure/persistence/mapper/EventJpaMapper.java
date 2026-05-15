package mentoring.acomi.library.infrastructure.persistence.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import mentoring.acomi.library.application.aggregates.BookAggregate;
import mentoring.acomi.library.domain.events.BookRegistered;
import mentoring.acomi.library.domain.model.books.Book;
import mentoring.acomi.library.infrastructure.persistence.entity.EventEntity;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class EventJpaMapper {

	private final ObjectMapper objectMapper;

	public EventJpaMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public BookRegistered toDomain(EventEntity event) {

		return new BookRegistered(BookAggregate.aggregateType, event.getAggregateId(), fromJsonNode(event.getPayload()), event.getOccurredAt());
	}

	public EventEntity toEntity(BookRegistered book) {

		return new EventEntity(book.getAggregateType(), book.getAggregateId(), book.getType(),
				toJsonNode(book.getPayload()), Instant.now());

	}

	private JsonNode toJsonNode(Object payload) {
		return objectMapper.valueToTree(payload);
	}

	private Book fromJsonNode(JsonNode node) {
		return objectMapper.treeToValue(node, Book.class);
	}

}
