package mentoring.acomi.library.domain.model.books;

import java.util.regex.Pattern;

import mentoring.acomi.library.domain.books.errors.InvalidIsbn;

public abstract class ISBN {

	private String isbn;
	private Pattern format;
	private Pattern ruleRegex;
	private String ruleFormat;

	public ISBN(String rawIsbn, Pattern format, Pattern ruleRegex, String ruleFormat) {

		this.isbn = normalize(rawIsbn);
		this.format = format;
		this.ruleRegex = ruleRegex;
		this.ruleFormat = ruleFormat;

		if (!this.format.matcher(this.isbn).matches() || !this.checksum(this.isbn)) {
			throw InvalidIsbn.invalidFormat(this.isbn);
		}

	}

	public String formatted() {
		return this.ruleRegex.matcher(this.isbn).replaceAll(this.ruleFormat);
	}
	
	public String getValue() {
		return this.isbn;
	}

	private static String normalize(String input) {
		return input.toUpperCase().replaceAll("[^0-9X]", "");
	}

	abstract boolean checksum(String value);

	public static ISBN of(String value) {
		
		if (ISBN10.check(value)) {
			return new ISBN10(value);
		}
		
		if (ISBN13.check(value)) {
			return new ISBN13(value);
		}

		 throw InvalidIsbn.invalidFormat(value);
	}

}
