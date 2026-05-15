package mentoring.acomi.library.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import io.cucumber.java.Before;

public class ScenarioHooks {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Before
	public void beforeScenario() {
		jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");
		jdbcTemplate.execute("TRUNCATE TABLE events;");
		jdbcTemplate.execute("TRUNCATE TABLE book_view;");
		jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
	}

}
