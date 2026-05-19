package com.sbproject.weaver.employee.repository;

import com.sbproject.weaver.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID>, EmployeeRepositoryCustom {

    Optional<Employee> findByEmployeeNumber(String employeeNumber);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    boolean existsByEmployeeNumber(String employeeNumber);

    boolean existsByUpdatedAtAfter(Instant updatedAt);
}