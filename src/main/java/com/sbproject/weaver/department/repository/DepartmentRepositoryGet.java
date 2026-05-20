package com.sbproject.weaver.department.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.department.dto.DepartmentDto;
import com.sbproject.weaver.department.dto.DepartmentSearchRequest;
import com.sbproject.weaver.department.entity.QDepartment;
import com.sbproject.weaver.employee.entity.QEmployee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.querydsl.core.types.ExpressionUtils.as;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryGet implements DepartmentRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    private static final QDepartment d = QDepartment.department;
    private static final QEmployee e = QEmployee.employee; // TODO : employee를 join해야 부서에 인원 수 표기 가능!!!!!!!

    @Override
    public Optional<DepartmentDto> findById(UUID id) {
        DepartmentDto result = queryFactory
                .select(Projections.fields(DepartmentDto.class,
                        d.id,
                        d.name,
                        d.description,
                        d.establishedDate,
                        as(
                                JPAExpressions
                                        .select(e.count().intValue())
                                        .from(e)
                                        .where(e.department.id.eq(d.id)),
                                "employeeCount"
                        )
                ))
                .from(d)
                .where(d.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public CursorPageResponse<DepartmentDto> searchSlice(UUID cursor, int size, DepartmentSearchRequest search) {

        BooleanBuilder where = new BooleanBuilder();
        boolean isDesc = "desc".equals(search.getSortDirection());
        if (cursor != null) {
            if (isDesc){
                where.and(d.id.lt(cursor));
            } else {
                where.and(d.id.gt(cursor));
            }

        }
        String keyword = search.getNameOrDescription();
        if (keyword!= null && !keyword.isBlank()) {
            where.and(d.name.containsIgnoreCase(keyword)
                    .or(d.description.containsIgnoreCase(keyword)));
        }

        List<DepartmentDto> rows = queryFactory
                .select(Projections.fields(DepartmentDto.class,
                        d.id,
                        d.name,
                        d.description,
                        d.establishedDate,
                        as(
                                JPAExpressions
                                        .select(e.count().intValue())
                                        .from(e)
                                        .where(e.department.id.eq(d.id)),
                                "employeeCount"
                        )
                ))
                .from(d)
                .where(where)
                .orderBy(isDesc ? d.id.desc() : d.id.asc())
                .limit(size + 1L)
                .fetch();

        log.info("조회된 총 개수(size + 1): {}", rows.size());

        boolean hasNext = rows.size() > size;
        List<DepartmentDto> content = hasNext ? rows.subList(0, size) : rows;

        String nextCursor = (hasNext && !content.isEmpty())
                ? content.get(content.size()-1).getId().toString()
                : null;

        return CursorPageResponse.<DepartmentDto>builder()
                .content(content)
                .size(size)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
//                .nextIdAfter(nextCursor) 왜 있는지 모르겠음
                .totalElements(countSearch(search))
                .build();
    }

    @Override
    public long countSearch(DepartmentSearchRequest search) {
        BooleanBuilder where = new BooleanBuilder();
        String keyword = search.getNameOrDescription();
        if (keyword!= null && !keyword.isBlank()) {
            where.and(d.name.containsIgnoreCase(keyword)
                    .or(d.description.containsIgnoreCase(keyword)));
        }
        Long totalElements = queryFactory
                .select(d.count())
                .from(d)
                .where(where)
                .fetchOne();

        return totalElements != null ? totalElements : 0L;
    }



}
