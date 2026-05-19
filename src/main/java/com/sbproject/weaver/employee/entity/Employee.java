package com.sbproject.weaver.employee.entity;

import com.sbproject.weaver.common.entity.BaseEntity;
import com.sbproject.weaver.department.entity.Department;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employees")
@Getter
@SuperBuilder
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee extends BaseEntity {
    // TODO : 테스트용 임시 엔티티! 나중에 머지따라 달라짐!!!
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "employee_number", length = 50, nullable = false, unique = true)
    private String employeeNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "profile_image_id", unique = true)
    private UUID profileImageId;

    @Column(name = "position", length = 100, nullable = false)
    private String position;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "status", length = 30, nullable = false)
    private String status;
}
