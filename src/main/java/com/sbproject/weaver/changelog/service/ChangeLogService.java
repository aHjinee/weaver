package com.sbproject.weaver.changelog.service;

import com.sbproject.weaver.changelog.dto.ChangeLogCountCondition;
import com.sbproject.weaver.changelog.dto.ChangeLogDetailDto;
import com.sbproject.weaver.changelog.dto.ChangeLogDto;
import com.sbproject.weaver.changelog.dto.ChangeLogSearchRequest;
import com.sbproject.weaver.changelog.entity.ChangeLogType;
import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.employee.entity.Employee;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;


public interface ChangeLogService {

    CursorPageResponse<ChangeLogDto> search(String cursor, int size, ChangeLogSearchRequest searchRequest);

    ChangeLogDetailDto findById(UUID id);

    void save(ChangeLogType type,
              Employee beforeEmployee,
              Employee afterEmployee,
              String memo,
              HttpServletRequest request);

    Long count(ChangeLogCountCondition condition);
}
