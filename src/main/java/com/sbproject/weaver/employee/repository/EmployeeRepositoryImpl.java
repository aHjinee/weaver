package com.sbproject.weaver.employee.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.department.entity.QDepartment;
import com.sbproject.weaver.employee.dto.*;
import com.sbproject.weaver.employee.entity.Employee;
import com.sbproject.weaver.employee.entity.QEmployee;
import com.sbproject.weaver.file.entity.QFileEntity;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        BooleanBuilder baseCondition = new BooleanBuilder();

        if (condition.getNameOrEmail() != null && !condition.getNameOrEmail().isBlank()) {
            baseCondition.and(
                    employee.name.containsIgnoreCase(condition.getNameOrEmail())
                            .or(employee.email.containsIgnoreCase(condition.getNameOrEmail()))
            );
        }

        if (condition.getEmployeeNumber() != null && !condition.getEmployeeNumber().isBlank()) {
            baseCondition.and(employee.employeeNumber.containsIgnoreCase(condition.getEmployeeNumber()));
        }

        if (condition.getDepartmentName() != null && !condition.getDepartmentName().isBlank()) {
            baseCondition.and(department.name.containsIgnoreCase(condition.getDepartmentName()));
        }

        if (condition.getPosition() != null && !condition.getPosition().isBlank()) {
            baseCondition.and(employee.position.containsIgnoreCase(condition.getPosition()));
        }

        if (condition.getHireDateFrom() != null) {
            baseCondition.and(employee.hireDate.goe(condition.getHireDateFrom()));
        }

        if (condition.getHireDateTo() != null) {
            baseCondition.and(employee.hireDate.loe(condition.getHireDateTo()));
        }

        if (condition.getStatus() != null) {
            baseCondition.and(employee.status.eq(condition.getStatus()));
        }

        Order order = "desc".equalsIgnoreCase(condition.getSortDirection())
                ? Order.DESC
                : Order.ASC;

        String sortField = condition.getSortField() == null
                ? "name"
                : condition.getSortField();

        BooleanBuilder pageCondition = new BooleanBuilder();
        pageCondition.and(baseCondition);

        if (condition.getCursor() != null && !condition.getCursor().isBlank()) {
            UUID cursorId = UUID.fromString(condition.getCursor());

            Employee cursorEmployee = queryFactory
                    .selectFrom(employee)
                    .where(employee.id.eq(cursorId))
                    .fetchOne();

            if (cursorEmployee != null) {
                pageCondition.and(cursorCondition(employee, cursorEmployee, sortField, order));
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
                .where(pageCondition)
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
                .where(baseCondition)
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
    public List<EmployeeTrendDto> getTrend(EmployeeTrendCondition condition) {
        QEmployee employee = QEmployee.employee;

        LocalDate from = condition.getFrom();
        LocalDate to = condition.getTo();
        String unit = condition.getUnit() == null ? "month" : condition.getUnit();

        List<EmployeeTrendDto> result = new ArrayList<>();

        long previousCount = 0L;
        boolean first = true;

        LocalDate current = alignStartDate(from, unit);

        while (!current.isAfter(to)) {
            LocalDate periodEnd = getPeriodEnd(current, unit);

            if (periodEnd.isAfter(to)) {
                periodEnd = to;
            }

            Long countResult = queryFactory
                    .select(employee.count())
                    .from(employee)
                    .where(employee.hireDate.loe(periodEnd))
                    .fetchOne();

            long count = countResult != null ? countResult : 0L;

            long change = first ? 0L : count - previousCount;

            double changeRate = first || previousCount == 0
                    ? 0.0
                    : ((double) change / previousCount) * 100;

            result.add(new EmployeeTrendDto(
                    formatPeriod(current, unit),
                    count,
                    change,
                    changeRate
            ));

            previousCount = count;
            first = false;
            current = getNextPeriod(current, unit);
        }

        return result;
    }

    private LocalDate alignStartDate(LocalDate date, String unit) {
        return switch (unit) {
            case "day" -> date;

            case "week" -> date.with(WeekFields.ISO.dayOfWeek(), 1);

            case "month" -> date.withDayOfMonth(1);

            case "quarter" -> {
                int quarterStartMonth = ((date.getMonthValue() - 1) / 3) * 3 + 1;
                yield LocalDate.of(date.getYear(), quarterStartMonth, 1);
            }

            case "year" -> LocalDate.of(date.getYear(), 1, 1);

            default -> date.withDayOfMonth(1);
        };
    }

    private LocalDate getPeriodEnd(LocalDate date, String unit) {
        return switch (unit) {
            case "day" -> date;

            case "week" -> date.plusDays(6);

            case "month" -> date.withDayOfMonth(date.lengthOfMonth());

            case "quarter" -> {
                int quarterEndMonth = ((date.getMonthValue() - 1) / 3) * 3 + 3;
                LocalDate quarterEnd = LocalDate.of(date.getYear(), quarterEndMonth, 1);
                yield quarterEnd.withDayOfMonth(quarterEnd.lengthOfMonth());
            }

            case "year" -> LocalDate.of(date.getYear(), 12, 31);

            default -> date.withDayOfMonth(date.lengthOfMonth());
        };
    }

    private LocalDate getNextPeriod(LocalDate date, String unit) {
        return switch (unit) {
            case "day" -> date.plusDays(1);
            case "week" -> date.plusWeeks(1);
            case "month" -> date.plusMonths(1);
            case "quarter" -> date.plusMonths(3);
            case "year" -> date.plusYears(1);
            default -> date.plusMonths(1);
        };
    }

    private String formatPeriod(LocalDate date, String unit) {
        return switch (unit) {
            case "day" -> date.toString();

            case "week" -> {
                int week = date.get(WeekFields.ISO.weekOfWeekBasedYear());
                int year = date.get(WeekFields.ISO.weekBasedYear());
                yield "%d-W%02d".formatted(year, week);
            }

            case "month" -> "%d-%02d".formatted(date.getYear(), date.getMonthValue());

            case "quarter" -> {
                int quarter = ((date.getMonthValue() - 1) / 3) + 1;
                yield "%d-Q%d".formatted(date.getYear(), quarter);
            }

            case "year" -> String.valueOf(date.getYear());

            default -> "%d-%02d".formatted(date.getYear(), date.getMonthValue());
        };
    }

    @Override
    public Long countEmployees(EmployeeCountCondition condition) {
        QEmployee employee = QEmployee.employee;

        BooleanBuilder builder = new BooleanBuilder();

        if (condition.getStatus() != null) {
            builder.and(employee.status.eq(condition.getStatus()));
        }

        if (condition.getFrom() != null) {
            builder.and(employee.hireDate.goe(condition.getFrom()));
        }

        if (condition.getTo() != null) {
            builder.and(employee.hireDate.loe(condition.getTo()));
        }

        Long count = queryFactory
                .select(employee.count())
                .from(employee)
                .where(builder)
                .fetchOne();

        return count != null ? count : 0L;
    }
}