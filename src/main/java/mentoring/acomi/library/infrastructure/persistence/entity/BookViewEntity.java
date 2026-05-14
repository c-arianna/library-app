package mentoring.acomi.library.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "book_view")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookViewEntity {

	@Id
	private String isbn;

	private String author;
	private String title;
	private String description;
	private int totalCopies;
	private int borrowedCopies;
	private int availableCopies;
	private int reservedCopies;
	
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

	public BookViewEntity(String isbn, String author, String title, String description) {
		this.isbn = isbn;
		this.author = author;
		this.title = title;
		this.description = description;
	}

}
