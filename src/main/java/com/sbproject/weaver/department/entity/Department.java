package com.sbproject.weaver.department.entity;

import com.sbproject.weaver.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "departments")
@Getter @SuperBuilder @ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column()
    private String description;

    @Column(name = "established_date", nullable = false)
    private LocalDate establishedDate;

    @Column()
    private int employeeCount;
}

//CREATE TABLE departments (
//        id UUID PRIMARY KEY,
//        name VARCHAR(100) NOT NULL UNIQUE,
//description TEXT,
//established_date DATE NOT NULL,
//created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
//updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
//);
