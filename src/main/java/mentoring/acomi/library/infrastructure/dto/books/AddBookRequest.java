package mentoring.acomi.library.infrastructure.dto.books;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddBookRequest(

		@NotBlank(message = "ISBN required")
		String isbn,

		@NotBlank(message = "author required")
		@Size(min = 3, max = 100)
		String author,

		@NotBlank(message = "title required")
		@Size(min = 3, max = 100)
		String title,

		String description

) {}
