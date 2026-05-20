package com.sbproject.weaver.backup.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.f4b6a3.uuid.UuidCreator;
import com.sbproject.weaver.file.entity.FileEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "backup_histories")
@Getter
@ToString(exclude = "file")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BackupEntity {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id = UuidCreator.getTimeOrderedEpoch();

    @Column(nullable = false, length = 100)
    private String worker;

    @Column(nullable = false)
    private Instant startedAt;

    private Instant endedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BackupStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = true)
    private FileEntity file;

    @Builder
    public BackupEntity(String worker, Instant startedAt) {
        this.worker = worker;
        this.startedAt = startedAt;
        this.status = BackupStatus.IN_PROGRESS;
    }

    public void complete(Instant endedAt, FileEntity file) {
        this.status = BackupStatus.COMPLETED;
        this.endedAt = endedAt;
        this.file = file;
    }

    public void fail(Instant endedAt, FileEntity file) {
        this.status = BackupStatus.FAILED;
        this.endedAt = endedAt;
        this.file = file;
    }

    public void skip(Instant endedAt) {
        this.status = BackupStatus.SKIPPED;
        this.endedAt = endedAt;
    }
}
