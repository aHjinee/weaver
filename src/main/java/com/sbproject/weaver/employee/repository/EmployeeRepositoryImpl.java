package com.sbproject.weaver.employee.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.department.entity.QDepartment;
import com.sbproject.weaver.employee.dto.EmployeeDistributionDto;
import com.sbproject.weaver.employee.dto.EmployeeDto;
import com.sbproject.weaver.employee.dto.EmployeeSearchCondition;
import com.sbproject.weaver.employee.dto.EmployeeSearchDistribution;
import com.sbproject.weaver.employee.entity.Employee;
import com.sbproject.weaver.employee.entity.EmployeeStatus;
import com.sbproject.weaver.employee.entity.QEmployee;
import com.sbproject.weaver.file.entity.QFileEntity;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import com.querydsl.core.types.dsl.StringExpression;

@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public CursorPageResponse<EmployeeDto> search(EmployeeSearchCondition condition) {
        QEmployee employee = QEmployee.employee;
        QDepartment department = QDepartment.department;
        QFileEntity profileImage = QFileEntity.fileEntity;

        int size = condition.getSize() != null ? condition.getSize() : 10;
        int limit = size + 1;

        BooleanBuilder builder = new BooleanBuilder();

        if (condition.getNameOrEmail() != null && !condition.getNameOrEmail().isBlank()) {
            builder.and(
                    employee.name.containsIgnoreCase(condition.getNameOrEmail())
                            .or(employee.email.containsIgnoreCase(condition.getNameOrEmail()))
            );
        }

        if (condition.getEmployeeNumber() != null && !condition.getEmployeeNumber().isBlank()) {
            builder.and(employee.employeeNumber.containsIgnoreCase(condition.getEmployeeNumber()));
        }

        if (condition.getDepartmentName() != null && !condition.getDepartmentName().isBlank()) {
            builder.and(department.name.containsIgnoreCase(condition.getDepartmentName()));
        }

        if (condition.getPosition() != null && !condition.getPosition().isBlank()) {
            builder.and(employee.position.containsIgnoreCase(condition.getPosition()));
        }

        if (condition.getHireDateFrom() != null) {
            builder.and(employee.hireDate.goe(condition.getHireDateFrom()));
        }

        if (condition.getHireDateTo() != null) {
            builder.and(employee.hireDate.loe(condition.getHireDateTo()));
        }

        if (condition.getStatus() != null) {
            builder.and(employee.status.eq(condition.getStatus()));
        }

        Order order = "desc".equalsIgnoreCase(condition.getSortDirection())
                ? Order.DESC
                : Order.ASC;

        String sortField = condition.getSortField() == null
                ? "name"
                : condition.getSortField();

        if (condition.getCursor() != null && !condition.getCursor().isBlank()) {
            UUID cursorId = UUID.fromString(condition.getCursor());

            Employee cursorEmployee = queryFactory
                    .selectFrom(employee)
                    .where(employee.id.eq(cursorId))
                    .fetchOne();

            if (cursorEmployee != null) {
                builder.and(cursorCondition(employee, cursorEmployee, sortField, order));
            }
        }

        OrderSpecifier<?> sortSpecifier = getSortSpecifier(employee, sortField, order);

        List<EmployeeDto> results = queryFactory
                .select(Projections.constructor(
                        EmployeeDto.class,
                        employee.id,
                        employee.name,
                        employee.email,
                        employee.employeeNumber,
                        department.id,
                        department.name,
                        employee.position,
                        employee.hireDate,
                        employee.status,
                        profileImage.id
                ))
                .from(employee)
                .join(employee.department, department)
                .leftJoin(employee.profileImage, profileImage)
                .where(builder)
                .orderBy(
                        sortSpecifier,
                        new OrderSpecifier<>(order, employee.id)
                )
                .limit(limit)
                .fetch();

        boolean hasNext = results.size() > size;

        if (hasNext) {
            results.remove(size);
        }

        String nextCursor = null;

        if (hasNext && !results.isEmpty()) {
            EmployeeDto last = results.get(results.size() - 1);
            nextCursor = last.getId().toString();
        }

        Long totalElements = queryFactory
                .select(employee.count())
                .from(employee)
                .join(employee.department, department)
                .where(builder)
                .fetchOne();

        return new CursorPageResponse<>(
                results,
                nextCursor,
                null,
                size,
                totalElements != null ? totalElements : 0L,
                hasNext
        );
    }

    private OrderSpecifier<?> getSortSpecifier(QEmployee employee, String sortField, Order order) {
        return switch (sortField) {
            case "employeeNumber" -> new OrderSpecifier<>(order, employee.employeeNumber);
            case "hireDate" -> new OrderSpecifier<>(order, employee.hireDate);
            case "name" -> new OrderSpecifier<>(order, employee.name);
            default -> new OrderSpecifier<>(order, employee.name);
        };
    }

    private BooleanBuilder cursorCondition(
            QEmployee employee,
            Employee cursorEmployee,
            String sortField,
            Order order
    ) {
        BooleanBuilder cursorBuilder = new BooleanBuilder();

        UUID cursorId = cursorEmployee.getId();

        switch (sortField) {
            case "employeeNumber" -> {
                String cursorValue = cursorEmployee.getEmployeeNumber();

                if (order == Order.ASC) {
                    cursorBuilder.and(
                            employee.employeeNumber.gt(cursorValue)
                                    .or(employee.employeeNumber.eq(cursorValue)
                                            .and(employee.id.gt(cursorId)))
                    );
                } else {
                    cursorBuilder.and(
                            employee.employeeNumber.lt(cursorValue)
                                    .or(employee.employeeNumber.eq(cursorValue)
                                            .and(employee.id.lt(cursorId)))
                    );
                }
            }

            case "hireDate" -> {
                LocalDate cursorValue = cursorEmployee.getHireDate();

                if (order == Order.ASC) {
                    cursorBuilder.and(
                            employee.hireDate.gt(cursorValue)
                                    .or(employee.hireDate.eq(cursorValue)
                                            .and(employee.id.gt(cursorId)))
                    );
                } else {
                    cursorBuilder.and(
                            employee.hireDate.lt(cursorValue)
                                    .or(employee.hireDate.eq(cursorValue)
                                            .and(employee.id.lt(cursorId)))
                    );
                }
            }

            default -> {
                String cursorValue = cursorEmployee.getName();

                if (order == Order.ASC) {
                    cursorBuilder.and(
                            employee.name.gt(cursorValue)
                                    .or(employee.name.eq(cursorValue)
                                            .and(employee.id.gt(cursorId)))
                    );
                } else {
                    cursorBuilder.and(
                            employee.name.lt(cursorValue)
                                    .or(employee.name.eq(cursorValue)
                                            .and(employee.id.lt(cursorId)))
                    );
                }
            }
        }

        return cursorBuilder;
    }

    @Override
    public List<EmployeeDistributionDto> distribution(EmployeeSearchDistribution searchDistribution) {
        QEmployee employee = QEmployee.employee;
        QDepartment department = QDepartment.department;

        String groupBy = (searchDistribution.getGroupBy() == null || searchDistribution.getGroupBy().isBlank())
                ? "department" : searchDistribution.getGroupBy();

        StringExpression groupPath = "position".equals(groupBy)
                ? employee.position
                : department.name;

        EmployeeStatus status = searchDistribution.getStatus() != null
                ? searchDistribution.getStatus()
                : EmployeeStatus.ACTIVE;

        Long totalCount = queryFactory
                .select(employee.count())
                .from(employee)
                .join(employee.department, department)
                .where(employee.status.eq(status))
                .fetchOne();

        if (totalCount == null || totalCount == 0) return List.of();

        return queryFactory
                .select(Projections.constructor(
                        EmployeeDistributionDto.class,
                        groupPath,
                        employee.count(),
                        employee.count().doubleValue().multiply(100).divide(totalCount)
                ))
                .from(employee)
                .join(employee.department, department)
                .where(employee.status.eq(status))
                .groupBy(groupPath)
                .fetch();
    }
}