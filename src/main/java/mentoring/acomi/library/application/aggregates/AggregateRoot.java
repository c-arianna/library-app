package mentoring.acomi.library.application.aggregates;

import java.util.List;
import java.util.function.Consumer;

import mentoring.acomi.library.domain.common.Identifier;
import mentoring.acomi.library.domain.events.DomainEvent;

public abstract class AggregateRoot<ID extends Identifier, E extends DomainEvent> {

	protected ID id;
	protected Consumer<E> dispatcher;

	protected AggregateRoot(ID id, Consumer<E> dispatcher) {
		this.id = id;
		this.dispatcher = dispatcher;
	}

	protected void replay(List<E> events) {
		for (E event : events) {
			apply(event);
		}
	}

	abstract void apply(E event);

	protected void manageEvent(E event) {
		apply(event);
		dispatcher.accept(event);
	}
}
