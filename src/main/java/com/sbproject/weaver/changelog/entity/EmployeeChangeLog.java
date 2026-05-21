package com.sbproject.weaver.changelog.entity;


import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "employee_change_logs")
@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeChangeLog {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ChangeLogType type;

    @Column(name = "employee_number", nullable = false, length = 50)
    private String employeeNumber;

    @Column(columnDefinition = "text")
    private String memo;

    @Column(name = "ip_address", nullable = false, length = 50)
    private String ipAddress;

    @Column(nullable = false)
    private Instant at;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "changeLog",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EmployeeChangeDiff> diffs = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UuidCreator.getTimeOrderedEpoch();
        }

        if (at == null) {
            at = Instant.now().plusSeconds(60 * 60 * 9);
        }
    }
}
