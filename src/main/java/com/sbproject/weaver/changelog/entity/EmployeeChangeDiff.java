package com.sbproject.weaver.changelog.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Entity
@Table(name = "employee_change_diffs")
@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeChangeDiff {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_log_id", nullable = false)
    private EmployeeChangeLog changeLog;

    @Column(name = "property_name", nullable = false, length = 100)
    private String propertyName;

    @Column(name = "before_value", columnDefinition = "text")
    private String beforeValue;

    @Column(name = "after_value", columnDefinition = "text")
    private String afterValue;

        @PrePersist
        public void prePersist() {
            if (id == null) {
                id = UuidCreator.getTimeOrderedEpoch();
            }
        }

    }
