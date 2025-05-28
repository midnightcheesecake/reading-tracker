package com.necrock.readingtracker.readingitem.persistence;

import com.necrock.readingtracker.readingitem.common.ReadingItemType;
import com.necrock.readingtracker.readingprogress.persistence.ReadingProgressEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "items")
public class ReadingItemEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private ReadingItemType type;

    private String author;

    private Integer totalChapters;

    private Instant createdAt;

    @OneToMany(mappedBy = "readingItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "id")
    private Map<Long, ReadingProgressEntity> progressSet = new HashMap<>();

    private ReadingItemEntity(Long id,
                              String title,
                              ReadingItemType type,
                              String author,
                              Integer totalChapters,
                              Instant createdAt) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.author = author;
        this.totalChapters = totalChapters;
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

    public ReadingItemType getType() {
        return type;
    }

    public String getAuthor() {
        return author;
    }

    public Integer getTotalChapters() {
        return totalChapters;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void addProgress(ReadingProgressEntity progress) {
        progressSet.put(progress.getId(), progress);
    }

    public void removeProgress(ReadingProgressEntity progress) {
        progressSet.remove(progress.getId());
    }

    public boolean containsProgress(ReadingProgressEntity progress) {
        return progressSet.containsKey(progress.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ReadingItemEntity that = (ReadingItemEntity) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getTitle(), that.getTitle())
                && getType() == that.getType()
                && Objects.equals(getAuthor(), that.getAuthor())
                && Objects.equals(getTotalChapters(), that.getTotalChapters())
                && Objects.equals(getCreatedAt(), that.getCreatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getId(),
                getTitle(),
                getType(),
                getAuthor(),
                getTotalChapters(),
                getCreatedAt());
    }

    public static class Builder {
        private Long id;
        private String title;
        private ReadingItemType type;
        private String author;
        private Integer totalChapters;
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

        public Builder totalChapters(Integer totalChapters) {
            this.totalChapters = totalChapters;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ReadingItemEntity build() {
            return new ReadingItemEntity(id, title, type, author, totalChapters, createdAt);
        }
    }
}
