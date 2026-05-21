package com.sbproject.weaver.backup.repository;

import com.sbproject.weaver.backup.entity.BackupEntity;
import com.sbproject.weaver.backup.entity.BackupStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BackupRepository extends JpaRepository<BackupEntity, UUID>, BackupRepositoryCustom {

    Optional<BackupEntity> findFirstByStatusOrderByStartedAtDesc(BackupStatus status);
}