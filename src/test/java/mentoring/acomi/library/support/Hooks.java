package mentoring.acomi.library.support;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.annotation.PostConstruct;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.testcontainers.mysql.MySQLContainer;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=test")
public class Hooks {

	private static MySQLContainer mysql;

	@Autowired
	private DataSource dataSource;

	@SuppressWarnings("resource")
	@BeforeAll
	public static void beforeAll() {

		mysql = new MySQLContainer("mysql:8.3").withDatabaseName("library_test").withUsername("library_user")
				.withPassword("library_password");

		mysql.start();

		System.setProperty("spring.datasource.url", mysql.getJdbcUrl());
		System.setProperty("spring.datasource.username", mysql.getUsername());
		System.setProperty("spring.datasource.password", mysql.getPassword());
	}

	@PostConstruct
	void initSchema() throws Exception {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("db/sql/schema.sql"));
		populator.execute(dataSource);
	}

	@AfterAll
	public static void afterAll() {
		if (mysql != null) {
			mysql.stop();
		}
	}

}