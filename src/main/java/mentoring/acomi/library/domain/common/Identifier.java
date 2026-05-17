package mentoring.acomi.library.domain.common;

import lombok.Getter;

@Getter
public class Identifier {
	
	protected String value;
	
	protected Identifier(String value) {
	    this.value = value;
    }
}
