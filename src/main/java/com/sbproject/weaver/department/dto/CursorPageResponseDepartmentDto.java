package com.sbproject.weaver.department.dto;

import com.sbproject.weaver.department.entity.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CursorPageResponseDepartmentDto {
    private List<DepartmentDto> content;
    private String nextCursor;
    private long nextIdAfter;
    private int size;
    private long totalElements;
    private boolean hasNext;

    public static CursorPageResponseDepartmentDto from(Slice<DepartmentDto> slice, int size, long totalElements) {

        List<DepartmentDto> content = slice.getContent();

        String nextCursor = (slice.hasNext() && !content.isEmpty())
                ? content.get(content.size() - 1).getId().toString()
                : null;

        return CursorPageResponseDepartmentDto.builder()
                .content(content)
                .size(size)
                .hasNext(slice.hasNext())
                .nextCursor(nextCursor)
                .nextIdAfter(0L)
                .totalElements(totalElements)
                .build();
    }
}


//content array<object>
//nextCursor string
//nextIdAfter integerint64
//size integerint32
//totalElements integerint64
//hasNext boolean