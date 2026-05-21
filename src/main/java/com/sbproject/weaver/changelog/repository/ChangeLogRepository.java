package com.sbproject.weaver.changelog.repository;

import com.sbproject.weaver.changelog.entity.EmployeeChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChangeLogRepository extends JpaRepository<EmployeeChangeLog, UUID> {

}
