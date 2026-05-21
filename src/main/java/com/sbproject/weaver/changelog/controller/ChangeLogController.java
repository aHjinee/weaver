package com.sbproject.weaver.changelog.controller;

import com.sbproject.weaver.changelog.dto.ChangeLogCountCondition;
import com.sbproject.weaver.changelog.dto.ChangeLogDetailDto;
import com.sbproject.weaver.changelog.dto.ChangeLogDto;
import com.sbproject.weaver.changelog.dto.ChangeLogSearchRequest;
import com.sbproject.weaver.changelog.service.ChangeLogService;
import com.sbproject.weaver.common.dto.CursorPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/change-logs")
public class ChangeLogController {

    private final ChangeLogService changeLogService;

    @GetMapping("")
    public ResponseEntity<CursorPageResponse<ChangeLogDto>> search(
            @RequestParam(required = false) String cursor,
            @RequestParam int size,
            @ModelAttribute ChangeLogSearchRequest searchRequest
    ) {
        return ResponseEntity.ok(changeLogService.search(cursor, size, searchRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChangeLogDetailDto> findById(@PathVariable UUID id){
        return ResponseEntity.ok(changeLogService.findById(id));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate
    ) {
        LocalDate resolvedTo = toDate != null ? toDate : LocalDate.now();
        LocalDate resolvedFrom = fromDate != null ? fromDate : resolvedTo.minusDays(7);

        ChangeLogCountCondition condition = new ChangeLogCountCondition(
                resolvedFrom,
                resolvedTo
        );

        Long response = changeLogService.count(condition);
        return ResponseEntity.ok(response);

    }

}
