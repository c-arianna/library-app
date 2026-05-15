package mentoring.acomi.library.domain.events;

import java.time.Instant;

import lombok.Getter;
import mentoring.acomi.library.domain.model.books.Book;

@Getter
public class BookRegistered  {
	
	private String aggregateType;
	private String type = "BookRegistered";
	
	private String aggregateId;
	private Book payload;
	
	private Instant occurredAt;
	
	public BookRegistered(String aggregateType, String aggregateId, Book payload, Instant occurredAt) {
		this.aggregateType = aggregateType;
		this.aggregateId = aggregateId;
		this.payload = payload;
		this.occurredAt = occurredAt;
		
	}
	
}
