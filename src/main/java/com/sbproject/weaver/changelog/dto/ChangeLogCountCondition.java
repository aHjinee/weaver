package com.sbproject.weaver.changelog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ChangeLogCountCondition {
    private LocalDate from;
    private LocalDate to;
}
