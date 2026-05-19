package com.sbproject.weaver.backup.repository;

import com.sbproject.weaver.backup.dto.BackupDto;
import com.sbproject.weaver.backup.entity.BackupEntity;
import com.sbproject.weaver.backup.entity.BackupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface BackupRepository extends JpaRepository<BackupEntity, UUID> {

    Optional<BackupEntity> findFirstByStatusOrderByStartedAtDesc(BackupStatus status);

}
