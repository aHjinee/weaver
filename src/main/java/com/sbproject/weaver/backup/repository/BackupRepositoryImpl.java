package com.sbproject.weaver.backup.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sbproject.weaver.backup.dto.BackupDto;
import com.sbproject.weaver.backup.entity.BackupStatus;
import com.sbproject.weaver.backup.entity.QBackupEntity;
import com.sbproject.weaver.file.entity.QFileEntity;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class BackupRepositoryImpl implements BackupRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BackupDto> findBackups(
            String worker,
            BackupStatus status,
            Instant from,
            Instant to,
            String cursor,
            String direction,
            String sortField,
            int limit
    ) {
        QBackupEntity backup = QBackupEntity.backupEntity;
        QFileEntity file = QFileEntity.fileEntity;

        BooleanBuilder condition = new BooleanBuilder();

        if (worker != null && !worker.isBlank()) {
            condition.and(backup.worker.contains(worker));
        }

        if (status != null) {
            condition.and(backup.status.eq(status));
        }

        if (from != null) {
            condition.and(backup.startedAt.goe(from));
        }

        if (to != null) {
            condition.and(backup.startedAt.loe(to));
        }

        Order order = "ASC".equalsIgnoreCase(direction) ? Order.ASC : Order.DESC;

        DateTimePath<Instant> sortPath = "endedAt".equals(sortField)
                ? backup.endedAt
                : backup.startedAt;

        if ("endedAt".equals(sortField)) {
            condition.and(backup.endedAt.isNotNull());
        }

        if (cursor != null && !cursor.isBlank()) {
            UUID cursorId = UUID.fromString(cursor);

            BackupCursor backupCursor = queryFactory
                    .select(Projections.constructor(
                            BackupCursor.class,
                            backup.id,
                            sortPath
                    ))
                    .from(backup)
                    .where(backup.id.eq(cursorId))
                    .fetchOne();

            if (backupCursor != null && backupCursor.sortValue() != null) {
                if (order == Order.ASC) {
                    condition.and(
                            sortPath.gt(backupCursor.sortValue())
                                    .or(sortPath.eq(backupCursor.sortValue())
                                            .and(backup.id.gt(backupCursor.id())))
                    );
                } else {
                    condition.and(
                            sortPath.lt(backupCursor.sortValue())
                                    .or(sortPath.eq(backupCursor.sortValue())
                                            .and(backup.id.lt(backupCursor.id())))
                    );
                }
            }
        }

        return queryFactory
                .select(Projections.constructor(
                        BackupDto.class,
                        backup.id,
                        backup.worker,
                        backup.startedAt,
                        backup.endedAt,
                        backup.status,
                        file.id
                ))
                .from(backup)
                .leftJoin(backup.file, file)
                .where(condition)
                .orderBy(
                        new OrderSpecifier<>(order, sortPath),
                        new OrderSpecifier<>(order, backup.id)
                )
                .limit(limit)
                .fetch();
    }

    public static class BackupCursor {
        private final UUID id;
        private final Instant sortValue;

        public BackupCursor(UUID id, Instant sortValue) {
            this.id = id;
            this.sortValue = sortValue;
        }

        public UUID id() {
            return id;
        }

        public Instant sortValue() {
            return sortValue;
        }
    }

    @Override
    public long countBackups(
            String worker,
            BackupStatus status,
            Instant from,
            Instant to
    ) {
        QBackupEntity backup = QBackupEntity.backupEntity;

        BooleanBuilder condition = new BooleanBuilder();

        if (worker != null && !worker.isBlank()) {
            condition.and(backup.worker.contains(worker));
        }

        if (status != null) {
            condition.and(backup.status.eq(status));
        }

        if (from != null) {
            condition.and(backup.startedAt.goe(from));
        }

        if (to != null) {
            condition.and(backup.startedAt.loe(to));
        }

        Long count = queryFactory
                .select(backup.count())
                .from(backup)
                .where(condition)
                .fetchOne();

        return count == null ? 0L : count;
    }
}