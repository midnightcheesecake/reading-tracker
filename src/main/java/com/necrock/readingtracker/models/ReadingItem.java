package com.necrock.readingtracker.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.joda.time.Instant;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "items")
public class ReadingItem {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    private String type;

    private String author;

    private Integer numberChapters;

    private Instant createdAt;

    private ReadingItem(Long id, String title, String type, String author, Integer numberChapters) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.author = author;
        this.numberChapters = numberChapters;
        this.createdAt = Instant.now();
    }

    // Required for JPA
    protected ReadingItem() {}

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

    public String getType() {
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
        private String type;
        private String author;
        private Integer numberChapters;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder type(String type) {
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

        public ReadingItem build() {
            return new ReadingItem(id, title, type, author, numberChapters);
        }
    }
}
