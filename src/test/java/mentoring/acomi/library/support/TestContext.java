package mentoring.acomi.library.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import io.cucumber.spring.ScenarioScope;

@Component
@ScenarioScope
public class TestContext {
	public String lastBody;
	public int lastStatus;

	private Map<String, Object> context = new HashMap<>();

	public void put(String key, Object value) {
		context.put(key, value);
	}

	public <T> T get(String key, Class<T> clazz) {
		return clazz.cast(context.get(key));
	}

	public Set<String> keys() {
		return context.keySet();
	}

}
