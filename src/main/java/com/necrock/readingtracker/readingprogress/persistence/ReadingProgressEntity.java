package com.necrock.readingtracker.readingprogress.persistence;

import com.necrock.readingtracker.readingitem.persistence.ReadingItemEntity;
import com.necrock.readingtracker.user.persistence.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(
        name = "progress",
        uniqueConstraints = @UniqueConstraint(
                name = "reading_progress_unique_user_reading_item",
                columnNames = {"user_id", "reading_item_id"}))
public class ReadingProgressEntity {

    public static final String UNIQUE_USER_READING_ITEM =
            "reading_progress_unique_user_reading_item";
    public static final String FK_USER =
            "reading_progress_fk_user";
    public static final String FK_READING_ITEM =
            "reading_progress_fk_reading_item";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "reading_progress_fk_user"))
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reading_item_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "reading_progress_fk_reading_item"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ReadingItemEntity readingItem;

    private Integer lastReadChapter;

    private ReadingProgressEntity(Long id,
                                  UserEntity user,
                                  ReadingItemEntity readingItem,
                                  Integer lastReadChapter) {
        this.id = id;
        this.user = user;
        this.readingItem = readingItem;
        this.lastReadChapter = lastReadChapter;
    }

    @SuppressWarnings("unused") // Required for JPA
    protected ReadingProgressEntity() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public ReadingItemEntity getReadingItem() {
        return readingItem;
    }

    public Integer getLastReadChapter() {
        return lastReadChapter;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ReadingProgressEntity that = (ReadingProgressEntity) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getUser(), that.getUser())
                && Objects.equals(getReadingItem(), that.getReadingItem())
                && Objects.equals(getLastReadChapter(), that.getLastReadChapter());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getId(),
                getUser(),
                getReadingItem(),
                getLastReadChapter());
    }

    public static class Builder {
        private Long id;
        private UserEntity user;
        private ReadingItemEntity readingItem;
        private Integer lastReadChapter;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder user(UserEntity user) {
            this.user = user;
            return this;
        }

        public Builder user(UserEntity.Builder userBuilder) {
            this.user = userBuilder.build();
            return this;
        }

        public Builder readingItem(ReadingItemEntity readingItem) {
            this.readingItem = readingItem;
            return this;
        }

        public Builder readingItem(ReadingItemEntity.Builder readingItemBuilder) {
            this.readingItem = readingItemBuilder.build();
            return this;
        }

        public Builder lastReadChapter(Integer lastReadChapter) {
            this.lastReadChapter = lastReadChapter;
            return this;
        }

        public ReadingProgressEntity build() {
            return new ReadingProgressEntity(id, user, readingItem, lastReadChapter);
        }
    }
}
