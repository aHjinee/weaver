package com.sbproject.weaver.changelog.repository;

import com.sbproject.weaver.changelog.entity.EmployeeChangeDiff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChangeDiffRepository extends JpaRepository<EmployeeChangeDiff, UUID> {

    List<EmployeeChangeDiff> findByChangeLogId(UUID employeeChangeLogId);
}
