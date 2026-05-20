package com.sbproject.weaver.employee.entity;

import com.sbproject.weaver.common.entity.BaseEntity;
import com.sbproject.weaver.department.entity.Department;
import com.sbproject.weaver.file.entity.FileEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Entity
@Table(name = "employees")
public class Employee extends BaseEntity {

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "employee_number", length = 50, nullable = false, unique = true)
    private String employeeNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id", unique = true)
    private FileEntity profileImage;

    @Column(name = "position", length = 100, nullable = false)
    private String position;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private EmployeeStatus status;

    public void updateInfo(String name, String email, String position,
                           LocalDate hireDate, Department department, EmployeeStatus status) {
        if (name != null) {
            this.name = name;
        }

        if (email != null) {
            this.email = email;
        }

        if (position != null) {
            this.position = position;
        }

        if (hireDate != null) {
            this.hireDate = hireDate;
        }

        if (department != null) {
            this.department = department;
        }

        if (status != null) {
            this.status = status;
        }
    }


    public void updateProfileImage(FileEntity profileImage) {
        this.profileImage = profileImage;
    }
}