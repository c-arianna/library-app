package mentoring.acomi.library.infrastructure.persistence.entity;

import java.time.Instant;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.JsonNode;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String aggregateId;

	private String aggregateType;

	private String eventType;

	private int eventVersion;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "json")
	private JsonNode payload;

	private Instant  occurredAt;

	public EventEntity(String aggregateType, String aggregateId, String eventType, JsonNode payload, Instant  occurredAt) {
		this.aggregateType = aggregateType;
		this.aggregateId = aggregateId;
		this.eventType = eventType;
		this.payload = payload;
		this.occurredAt = occurredAt;
	}

}
