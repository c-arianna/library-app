package mentoring.acomi.library.support;

import org.springframework.stereotype.Component;

import io.cucumber.spring.ScenarioScope;

@Component
@ScenarioScope
public class TestContext {
	public String lastBody;
	public int lastStatus;
}
