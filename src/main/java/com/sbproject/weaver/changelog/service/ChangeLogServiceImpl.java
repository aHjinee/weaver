package com.sbproject.weaver.changelog.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.sbproject.weaver.changelog.dto.*;
import com.sbproject.weaver.changelog.entity.ChangeLogType;
import com.sbproject.weaver.changelog.entity.EmployeeChangeDiff;
import com.sbproject.weaver.changelog.entity.EmployeeChangeLog;
import com.sbproject.weaver.changelog.mapper.ChangeLogMapper;
import com.sbproject.weaver.changelog.repository.ChangeDiffRepository;
import com.sbproject.weaver.changelog.repository.ChangeLogRepository;
import com.sbproject.weaver.changelog.repository.ChangeLogRepositoryCustom;
import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.common.util.ipUtil;
import com.sbproject.weaver.employee.entity.Employee;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChangeLogServiceImpl implements ChangeLogService {

    private final ChangeLogRepositoryCustom changeLogRepositoryCustom;
    private final ChangeLogRepository changeLogRepository;
    private final ChangeDiffRepository changeDiffRepository;
    private final ChangeLogMapper changeLogMapper;

    @Transactional
    @Override
    public CursorPageResponse<ChangeLogDto> search(String cursor, int size, ChangeLogSearchRequest request) {
        ChangeLogSearchRequest req = request.withDefaults();

        ChangeLogType type = (req.getType() == null || req.getType().isBlank() || req.getType().equals("ALL"))
                ? null
                : ChangeLogType.valueOf(req.getType());

        Slice<EmployeeChangeLog> slice = changeLogRepositoryCustom.search(cursor, size, req, type);

        List<ChangeLogDto> content = slice.getContent().stream()
                .map(changeLogMapper::roResponse)
                .toList();
        String nextCursor = null;

        if (slice.hasNext()) {
            nextCursor = content.get(content.size() - 1).getId().toString();
        }

        Long totalElements = changeLogRepositoryCustom.count(req, type);

        return CursorPageResponse.<ChangeLogDto>builder()
                .content(content)
                .nextCursor(nextCursor)
                .nextIdAfter(null)
                .size(size)
                .totalElements(totalElements)
                .hasNext(slice.hasNext())
                .build();
    }

    @Transactional
    @Override
    public ChangeLogDetailDto findById(UUID id) {
        EmployeeChangeLog entity = changeLogRepository.findById((id))
                .orElseThrow(() -> new NoSuchElementException("post not found: " + id));

        List<EmployeeChangeDiff> diffs = changeDiffRepository.findByChangeLogId(id);
        List<DiffDto> diffDtos = new ArrayList<>(diffs.stream().map(changeLogMapper::toDiffDto).toList());

        List<String> order = List.of(
                "hireDate", "name", "position", "department", "email", "employeeNumber", "status"
        );
        diffDtos.sort(Comparator.comparingInt(d ->
                !order.contains(d.getPropertyName()) ? Integer.MAX_VALUE : order.indexOf(d.getPropertyName())
        ));

        return ChangeLogDetailDto.builder()
                .id(entity.getId())
                .type(entity.getType())
                .employeeNumber(entity.getEmployeeNumber())
                .memo(entity.getMemo())
                .ipAddress(entity.getIpAddress())
                .at(entity.getAt())
                .diffs(diffDtos).build();

    }

    @Transactional
    @Override
    public void save(ChangeLogType type,
                     Employee beforeEmployee,
                     Employee afterEmployee,
                     String memo,
                     HttpServletRequest request) {

        Employee target = afterEmployee != null ? afterEmployee : beforeEmployee;

        EmployeeChangeLog empLog = EmployeeChangeLog.builder()
                .employeeNumber(target.getEmployeeNumber())
                .type(type)
                .memo(memo)
                .ipAddress(ipUtil.getClientIp(request))
                .build();

        switch (type) {

            case CREATED -> {
                addDiff(empLog, "hireDate", null,
                        value(afterEmployee.getHireDate()));

                addDiff(empLog, "name", null,
                        afterEmployee.getName());

                addDiff(empLog, "position", null,
                        value(afterEmployee.getPosition()));

                addDiff(empLog, "department", null,
                        value(afterEmployee.getDepartment().getName()));

                addDiff(empLog, "email", null,
                        afterEmployee.getEmail());

                addDiff(empLog, "employeeNumber", null,
                        afterEmployee.getEmployeeNumber());

                addDiff(empLog, "status", null,
                        value(afterEmployee.getStatus()));
            }

            case UPDATED -> {

                compareAndAdd(empLog, "hireDate",
                        value(beforeEmployee.getHireDate()),
                        value(afterEmployee.getHireDate()));

                compareAndAdd(empLog, "name",
                        beforeEmployee.getName(),
                        afterEmployee.getName());

                compareAndAdd(empLog, "position",
                        value(beforeEmployee.getPosition()),
                        value(afterEmployee.getPosition()));

                compareAndAdd(empLog, "department",
                        value(beforeEmployee.getDepartment().getName()),
                        value(afterEmployee.getDepartment().getName()));

                compareAndAdd(empLog, "email",
                        beforeEmployee.getEmail(),
                        afterEmployee.getEmail());

                compareAndAdd(empLog, "employeeNumber",
                        beforeEmployee.getEmployeeNumber(),
                        afterEmployee.getEmployeeNumber());

                compareAndAdd(empLog, "status",
                        value(beforeEmployee.getStatus()),
                        value(afterEmployee.getStatus()));
            }

            case DELETED -> {

                addDiff(empLog, "hireDate",
                        value(beforeEmployee.getHireDate()),
                        null);

                addDiff(empLog, "name",
                        beforeEmployee.getName(),
                        null);

                addDiff(empLog, "position",
                        value(beforeEmployee.getPosition()),
                        null);

                addDiff(empLog, "department",
                        value(beforeEmployee.getDepartment().getName()),
                        null);

                addDiff(empLog, "email",
                        beforeEmployee.getEmail(),
                        null);

                addDiff(empLog, "employeeNumber",
                        beforeEmployee.getEmployeeNumber(),
                        null);

                addDiff(empLog, "status",
                        value(beforeEmployee.getStatus()),
                        null);
            }
        }

        changeLogRepository.save(empLog);
    }

    @Override
    @Transactional
    public Long count(ChangeLogCountCondition condition) {

        return changeLogRepositoryCustom.countLogs(condition);
    }

    private void addDiff(EmployeeChangeLog log,
                         String propertyName,
                         String beforeValue,
                         String afterValue) {

        EmployeeChangeDiff diff = EmployeeChangeDiff.builder()
                .changeLog(log)
                .propertyName(propertyName)
                .beforeValue(beforeValue)
                .afterValue(afterValue)
                .build();

        log.getDiffs().add(diff);
    }

    private void compareAndAdd(EmployeeChangeLog log,
                               String propertyName,
                               String before,
                               String after) {

        if (!Objects.equals(before, after)) {
            addDiff(log, propertyName, before, after);
        }
    }

    private String value(Object obj) {
        return obj != null ? obj.toString() : null;
    }
}
