package com.necrock.readingtracker.readingitem.persistence;

import com.necrock.readingtracker.readingitem.common.ReadingItemType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "items")
public class ReadingItemEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    private ReadingItemType type;

    private String author;

    private Integer numberChapters;

    private Instant createdAt;

    private ReadingItemEntity(Long id,
                              String title,
                              ReadingItemType type,
                              String author,
                              Integer numberChapters,
                              Instant createdAt) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.author = author;
        this.numberChapters = numberChapters;
        this.createdAt = createdAt;
    }

    @SuppressWarnings("unused") // Required for JPA
    protected ReadingItemEntity() {}

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    @Enumerated(EnumType.STRING)
    public ReadingItemType getType() {
        return type;
    }

    public String getAuthor() {
        return author;
    }

    public Integer getNumberChapters() {
        return numberChapters;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public static class Builder {
        private Long id;

        private String title;

        private ReadingItemType type;

        private String author;

        private Integer numberChapters;

        private Instant createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder type(ReadingItemType type) {
            this.type = type;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder numberChapters(Integer numberChapters) {
            this.numberChapters = numberChapters;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ReadingItemEntity build() {
            return new ReadingItemEntity(id, title, type, author, numberChapters, createdAt);
        }
    }
}
